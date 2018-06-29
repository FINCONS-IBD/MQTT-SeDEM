/*******************************************************************************
 * Copyright 2018 FINCONS GROUP AG
 * Licensed under the Eclipse Public License v1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This work was provided within the TagITSecure! activity of the European H2020 
 * TagItSmart! project (http://tagitsmart.eu/) and partially funded by the European 
 * Commission's under the Grant Agreement 688061. 
 * 
 * Authors:
 * 	Leonardo Straniero (FINCONS GROUP)
 * 	Salvador Pérez (University of Murcia)
 * Contributors:
 * 	Domenico Rotondi (FINCONS GROUP)
 ******************************************************************************/
package com.fincons.mqtt.client.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.JSONObject;

import com.fincons.mqtt.client.util.ConfigurationLoader;

import entities.Cph;
import messages.CPABE_Policy;
import messages.Key_Id;
import messages.Key_Storage;
import messages.Metadata;
import messages.Storage_Parameters;


/**This class implements the interface {@link MqttCallback} and provides the method to perform an MQTT message publish operation
 * in a secure way or in clear. This class includes the CP-ABE/AES security aspects; in particular the encryption keys
 * generation and the encryption message features.  
 * 
 * @author leonardo.straniero
 *
 */
public class MessagePublisherService implements MqttCallback {

	private static MessagePublisherService service = new MessagePublisherService();
	private Logger logger = Logger.getLogger(MessagePublisherService.class);

	/* CP-ABE-AES aproach */
	private Cph cph;
	private String policy;
	private String key_type;
	private String cryptographic_curve;
	private String proxy_protocol;
	private String proxy_ip;
	private String proxy_port;
	private String proxy_id;
	private String alg;
	private String enc;
	private String specs;

	/* For "anticipated keys" features */
	private boolean is_anticipated_key;
	private int anticipatedKeySeconds;
	private Cph anticipated_cph;

	private AnticipationThread anticipationThread;

	private Key_Storage key_storage; 

	private class AnticipationThread implements Runnable{
		public void run() {
			anticipated_cph = setup_symmetric_key();
		}
	}

	/**
	 * Returns the single instance of this class implemented according to the singleton pattern. 
	 * This method calls internally the class constructor when the class is instanciated for the first time.
	 * @return the instance of MessagePublisherService
	 */
	public static MessagePublisherService getInstance() {
		return service;
	}

	private MessagePublisherService() {
		
		logger.info("Create a new instance of MessagePublishService...");

		//crypto enabled
		if(ConfigurationLoader.getInstance().isCpabe_aes_encryption()){
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			this.policy = ConfigurationLoader.getInstance().getCpabe_policy();
			is_anticipated_key = ConfigurationLoader.getInstance().isCpabe_anticipated_key();
			if(is_anticipated_key){
				this.anticipatedKeySeconds = ConfigurationLoader.getInstance().getCpabe_anticipated_key_in_sec();
				anticipationThread = new AnticipationThread();
			}
			/* Firstly, it is necessary to get the storages in which the symmetric key will be saved */
			getParamsCph();

			this.cph = setup_symmetric_key();
		}

	}

	/**
	 * This method publishes a message to a topic on the MQTT Broker Server and returns once it is delivered.
	 * 
	 * @param topic - topic to deliver the message to, for example "finance/stock/ibm"
	 * @param message - the string to use as the payload
	 */
	public void publishMessage(String topic, JSONObject message) {
		boolean enc_enabled = ConfigurationLoader.getInstance().isCpabe_aes_encryption();

		//encryption enabled: retry to setup the symmetric key if 
		//a problem during the key generation (including the renew of an expired key) occurred
		if(enc_enabled){
			if(this.cph == null || this.cph.getSymmetricKeyId() == null){
				logger.debug("Value of CPH is not VALID! Retrying to setup a new symmetric key!");
				this.cph = setup_symmetric_key();			
			}
		}
						
		JSONObject event = new JSONObject();

		String msg_to_publish = "";
		//encryption enabled
		if(enc_enabled){
			try{
				/* Before encrypting, it is necessary to know if the current symmetric key is expired. In this case, a new shared symmetric key is generated. */
				/** BY DATE **/
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				logger.debug("Expiration Date: "+ cph.getExpirationDate());
				Date expiration_date = df.parse(cph.getExpirationDate());
				Date current_date = new Date();
				if(expiration_date.compareTo(current_date) <= 0){
					logger.debug("The CHP Key is expired, try to use the new calculated key!");
					if(anticipated_cph != null){						
						this.cph = this.anticipated_cph;
						this.anticipated_cph = null;
						logger.debug("The new CHP key is online!");
						logger.debug("New key id: " + this.cph.getId());
						if(this.cph.getSymmetricKeyId() == null)
							logger.debug("New SymmetricKeyId is NULL!");
						else{
							logger.debug("New key Enc_sym_key_id: " + this.cph.getSymmetricKeyId().getEnc_sym_key_id());	
						}
					}else{
						this.cph = setup_symmetric_key();
					}
				}else{
					/* Anticipated key */
					if(is_anticipated_key){
						Date anticipation_date = expiration_date;
						anticipation_date.setSeconds(expiration_date.getSeconds() - this.anticipatedKeySeconds); //When to start to calculate next key
						/* A new anticipated key is calculated if there is none */
						if((anticipation_date.compareTo(current_date) <= 0) && this.anticipated_cph == null){
							logger.debug("The expiration key time is near! Start the anticipated cph calucaltion");
							Thread t = new Thread(anticipationThread);
							t.run();
						}
					}
				}

				String msg_string = message.toString();
				logger.info("Ready to encrypt the following message: " + msg_string);
				event = encrypt_data(msg_string);
				msg_to_publish = buildMessageToPublish(event.toString());

			} catch (ParseException ex) {
				logger.error("ParseException", ex);
			}

		}else{
			JSONObject headers = new JSONObject();

			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date current_date = new Date();
			headers.put("timestamp", current_date.toString());

			event.put("payload", message.toString());
			event.put("headers", headers);
			msg_to_publish = buildMessageToPublish(event.toString());

		}

		try {

			logger.info(msg_to_publish);
			if(!SessionService.getInstance().isConnected())
				SessionService.getInstance().connect();

			SessionService.getInstance().getMqttClient().setCallback(this);
			SessionService.getInstance().getMqttClient().publish(topic, msg_to_publish.toString().getBytes(), 
					ConfigurationLoader.getInstance().getMqtt_qos(), ConfigurationLoader.getInstance().isMqtt_retained());

			//N.B.: NON VA FATTA OGNI VOLTA LA DISCONNECT! 
			//viene gestito tutto implicitamente tramite il parametro keep alive
			//SessionService.getInstance().disconnect();
		} catch (MqttPersistenceException ex) {
			logger.error("MqttPersistenceException", ex);
		} catch (MqttException ee) {
			logger.error("MqttException", ee);
		}

	}

	private void getParamsCph(){
		key_type = ConfigurationLoader.getInstance().getKey_type();
		cryptographic_curve = ConfigurationLoader.getInstance().getCryptographic_curve();
		proxy_protocol = ConfigurationLoader.getInstance().getProxy_protocol();
		proxy_ip = ConfigurationLoader.getInstance().getProxy_ip();
		proxy_port = ConfigurationLoader.getInstance().getProxy_port();
		proxy_id = ConfigurationLoader.getInstance().getProxy_id();
		alg = ConfigurationLoader.getInstance().getAlg();
		enc = ConfigurationLoader.getInstance().getEnc();
		specs = ConfigurationLoader.getInstance().getCpabe_policy();

		String storage_type = ConfigurationLoader.getInstance().getStorage_type();
		switch(storage_type){
		case "database":
			Storage_Parameters param1 = new Storage_Parameters("db_ip", ConfigurationLoader.getInstance().getDb_ip());
			Storage_Parameters param2 = new Storage_Parameters("db_port", ConfigurationLoader.getInstance().getDb_port());
			Storage_Parameters param3 = new Storage_Parameters("db_auth_user", ConfigurationLoader.getInstance().getDb_auth_user());
			Storage_Parameters param4 = new Storage_Parameters("db_auth_pwd", ConfigurationLoader.getInstance().getDb_auth_pwd());
			Storage_Parameters param5 = new Storage_Parameters("db_database", ConfigurationLoader.getInstance().getDb_database());
			Storage_Parameters param6 = new Storage_Parameters("db_table", ConfigurationLoader.getInstance().getDb_table());
			ArrayList<Storage_Parameters> storage_parameters = new ArrayList<Storage_Parameters>();
			storage_parameters.add(param1);storage_parameters.add(param2);storage_parameters.add(param3);
			storage_parameters.add(param4);storage_parameters.add(param5);storage_parameters.add(param6);
			key_storage = new Key_Storage(storage_type, storage_parameters);
			break;
		default:
			logger.error("The storage type is not allowed");
			throw new IllegalStateException("The storage type is not allowed");
		}
	}

	private Cph setup_symmetric_key(){
		logger.debug("Begin the setup symmetric key process...");
		try{
			String userID = ConfigurationLoader.getInstance().getMqtt_client_id();
			Cph new_cph = new Cph(userID);
			/* 1.1. Generate an ephemeral elliptic curve key pair */
			new_cph.Generate_ekeys(key_type, cryptographic_curve);
			/* 1.2. Send its ephemeral public key as well as the selected ECC curve to ABE proxy. The shared symmetric key is calculated */
			new_cph.sendMessageProxy(proxy_protocol, proxy_ip, proxy_port, "/ABE-Proxy/generate_shared_secret", proxy_id, alg, enc);

			/* 2.1. Encrypt CP-ABE related information by AES (shared_sym_key) */
			/* Get cpabe_info */
			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
			df.setTimeZone(tz);
			Date date = new Date();
			String creation_date = df.format(date);
			Metadata creation_date_metadata = new Metadata("creation-date", creation_date);
			String name = "policy_" + userID;
			Metadata name_metadata = new Metadata("name", name);
			String version = "1.0";
			Metadata version_metadata = new Metadata("version", version);
			String description = "Policy to encrypt symmetric key of " + userID;
			Metadata description_metadata = new Metadata("description", description);
			String author = userID;
			Metadata author_metadata = new Metadata("author", author);
			String library = "junwei-wang/cpabe";
			Metadata libray_metadata = new Metadata("library", library);
			ArrayList<Metadata> policy_metadatas = new ArrayList<Metadata>();
			policy_metadatas.add(creation_date_metadata);policy_metadatas.add(name_metadata);policy_metadatas.add(version_metadata);
			policy_metadatas.add(description_metadata);policy_metadatas.add(author_metadata);policy_metadatas.add(libray_metadata);
			String url = null;
			CPABE_Policy policy = new CPABE_Policy(specs, url, policy_metadatas);
			ArrayList<Key_Storage> encrypted_symmetric_key_storages = new ArrayList<Key_Storage>();
			encrypted_symmetric_key_storages.add(this.key_storage);
			String cpabe_info = new_cph.get_cpabe_info(policy, this.key_storage);
			logger.debug("Setting the new cpabe info: " + cpabe_info);
			
			/* Get encrypted cpabe_info */
			String enc_cpabe_info = new_cph.encrypt_cpabe_info(cpabe_info);			
			if(enc_cpabe_info != null)
				logger.debug("Setting the new cpabe info: " + cpabe_info);
			else
				logger.debug("Waringn! enc_cpabe_info IS NULL!");
			
			/* 2.2. Send the encrypt CP-ABE related information to ABE proxy */
			new_cph.sendMessageProxy(proxy_protocol, proxy_ip, proxy_port, "/ABE-Proxy/cpabe_information", enc_cpabe_info);

			logger.debug("New CPH key produced! Following the key info:");
			logger.debug("Id: " + new_cph.getId());
			logger.debug("Expiration date: " + new_cph.getExpirationDate());
			if(new_cph.getSymmetricKeyId() == null)
				logger.debug("the new generated key has the Symmetric Key Id NULL!" );
			else
				logger.debug("Enc_sym_key_id: " + new_cph.getSymmetricKeyId().getEnc_sym_key_id() );
				
			return new_cph;

		}catch(Exception e){
			logger.error("Exception", e);
			return null;
		}
	}    

	private JSONObject encrypt_data(String message){
		JSONObject headers = new JSONObject();
		logger.debug("cph is null: " + cph == null );
		
		Key_Id symmetric_key_id = cph.getSymmetricKeyId();
		logger.debug("symmetric_key_id is null: " + symmetric_key_id == null );

		String id = symmetric_key_id.getEnc_sym_key_id();
		logger.debug("enc_sym_key_id is null: " + id == null );

		Key_Id a_symmetric_key_id = null;
		String a_id = "";
		if(anticipated_cph != null){
			a_symmetric_key_id = anticipated_cph.getSymmetricKeyId();
			a_id = a_symmetric_key_id.getEnc_sym_key_id();
		}


		/* Add the encrypted symmetric key id */
		String encryption_algorithms = "";
		String expiration_date = "";
		for(Metadata m : symmetric_key_id.getMetadata()){
			if(m.getName().equals("protection-mechanism"))
				encryption_algorithms = m.getValue();
			else if(m.getName().equals("expiration-date"))
				expiration_date = m.getValue();
		}
		headers.put("key-id", id);
		headers.put("key-expiration-date", expiration_date);
		headers.put("protection-mechanism", encryption_algorithms);
		if(anticipated_cph != null){
			String ant_expiration_date = "";
			for(Metadata m : anticipated_cph.getSymmetricKeyId().getMetadata()){
				if(m.getName().equals("expiration-date")){
					ant_expiration_date = m.getValue();
					break;
				}
			}
			headers.put("next-key-id", a_id);
			headers.put("next-key-expiration-date", ant_expiration_date);
		}

		String encrypted_value = cph.encrypt_value(message, new byte[16]);
		JSONObject encrypted_msg = new JSONObject();
		encrypted_msg.put("payload", encrypted_value);
		encrypted_msg.put("headers", headers);
		return encrypted_msg;

	}


	private static String buildMessageToPublish(String measurements){
		String pattern = ConfigurationLoader.getInstance().getPayload_pattern();
		String placeholder = ConfigurationLoader.getInstance().getPlaceholder_pattern();

		String message = pattern.replace(placeholder, JSONObject.quote(measurements));

		return message;
	}

	/**
	 * This method implements the business logic of the client when it loses the connection with the MQTT Broker Server.
	 */
	@Override
	public void connectionLost(Throwable me) {
		logger.error("Connection lost", me);
		System.out.println("msg "+me.getMessage());
		System.out.println("loc "+me.getLocalizedMessage());
		System.out.println("cause "+me.getCause());
		System.out.println("excep "+me);
	}

	/**
	 * This method provides the business logic implemented in the client subscriber when a new message is delivered to the topic selected.
	 */
	@Override
	public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
		logger.info("message arrived-------");
	}

	/**
	 * This method implements the business logic of the publisher client when the message is delivered to the MQTT Broker Server.
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		logger.info("message deliverd--------");
	}

}
