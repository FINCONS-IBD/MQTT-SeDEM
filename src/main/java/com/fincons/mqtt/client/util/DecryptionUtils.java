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
package com.fincons.mqtt.client.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import algorithms.AES;
import cpabe.Common;
import cpabe.Cpabe;
import messages.Key_Storage;
import messages.OrientDB_Recovered_Key;
import messages.Storage_Parameters;

/**
* This class contains the Decryption features based on CPABE-AES approach. This class interacts with the Key Storage Service to recover the CP-ABE encrypted AES key useful to decrypt the secret message.
* 
 * @author Fincons Group
*/
public class DecryptionUtils {

	private Logger logger = Logger.getLogger(DecryptionUtils.class);

	private Cpabe cpabe;
	private Map<String, byte[]> sym_keys; //Set of decrypted symmetric keys

	//At the moment, only one storage
	private Key_Storage key_storage; 

	private byte[] cpabe_private_key;
	private byte[] public_parameters;


	private static DecryptionUtils service = new DecryptionUtils();


	private DecryptionUtils(){
		try {
			if(ConfigurationLoader.getInstance().isCpabe_aes_encryption()){
				this.cpabe = new Cpabe();
				this.sym_keys = new HashMap<String, byte[]>();

				/* Firstly, it is necessary to get the storages in which the symmetric key will be saved */
				getParamsCph();
				String public_parameters_file = ConfigurationLoader.getInstance().getConsumer_public_param(); //example
				this.public_parameters = Common.suckFile(public_parameters_file);
				String cpabe_key_file = ConfigurationLoader.getInstance().getConsumer_cpabe_key(); //example
				this.cpabe_private_key = Common.suckFile(cpabe_key_file);
			}

		} catch (Exception e) {
			logger.error("Exception",e);
		}

	}

	/**
	 * This method return an unique instance of the DecryptionUtils class in line with the singleton pattern.
	 * This method calls internally the class constructor when the class is instanciated for the first time.
	 * @return the instance of DecryptionUtils class
	 */
	public static DecryptionUtils getInstance(){
		return service;
	}

	private void getParamsCph(){
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


	/**
	 * This method accepts as input the encrypted MQTT message and, through the metadata contained in the message, retrieves the key from the Key Storage Service and performs the decryption operation.
	 * 
	 * @param body - The encrypted MQTT message
	 * @return the string containing the decrypted message event
	 */
	public String decrypt_payload(MqttMessage body){
		logger.info("Called the decrypt_payload on the message with id " + body.getId());

		StringBuffer encrypted_val = new StringBuffer();

		try{
			JSONArray message = new JSONArray(new String(body.getPayload()));
			String msg = message.getJSONObject(0).getString("value");

			JSONObject content = new JSONObject(msg);

			JSONObject headers = content.getJSONObject("headers");
			String payload = content.getString("payload");


			//encryption enabled
			if(cpabe!=null){
				for(String key : headers.keySet()){

					if(key.equals("key-id")){
						String decrypted_value = decrypt_data(headers.getString("key-id"), payload.getBytes());
						encrypted_val.append(decrypted_value);
					}else if(key.equals("next-key-id")){
						anticipate_key(headers.getString("next-key-id"));
					}
				}
				if(encrypted_val.length()==0)
					throw new JSONException("Problems occured during the payload decryption!");
				
			}else
				//encryption disabled...return the payload
				encrypted_val.append(payload);

		}catch(JSONException ex){
			String json_format = ConfigurationLoader.getInstance().getPayload_pattern();
			logger.error("JSONException: the message doesn't respect the estabilished JSON format "+json_format+" and will be ignored!", ex);
		}


		return encrypted_val.toString();
	}

	private String decrypt_data(String key_id, byte[] body){
		try{
			/* Check if this data consumer has the key associated with the identifier */
			byte[] symmetric_key;
			if(!this.sym_keys.containsKey(key_id)){
				//				logger.info("--- IT'S NECESSARY TO RECOVER AND DECRYPT THE KEY ---");
				String encrypted_symmetric_key = getEncSymKey(key_id);
				/* Decrypt the key by CP-ABE using its CP-ABE private key */
				int index = encrypted_symmetric_key.indexOf(" ");
				String cph_b64 = encrypted_symmetric_key.substring(0, index);
				String aes_b64 = encrypted_symmetric_key.substring(index +  1);
				byte[] cph = Base64.getUrlDecoder().decode(cph_b64);
				byte[] aes = Base64.getUrlDecoder().decode(aes_b64);
				byte[][] enc_sym_key = new byte[2][];
				enc_sym_key[0] = cph;
				enc_sym_key[1] = aes;

				symmetric_key = this.cpabe.dec(this.public_parameters, this.cpabe_private_key, enc_sym_key);

				if(symmetric_key==null)
					throw new Exception("Error decrypting symmetric key");

				/* Locally store the key for future decryption operations */
				this.sym_keys.put(key_id, symmetric_key);
			}else{
				//				logger.info("--- THE KEY WAS STORED ---");
				symmetric_key = this.sym_keys.get(key_id);
			}
			/* Decrypt the encrypted data */
			String encrypted_data = new String(body);
			logger.debug("decrypt String "+ encrypted_data);
			byte[] encrypted_data_byte = Base64.getUrlDecoder().decode(encrypted_data);

			AES aes = new AES();
			String decrypted_data = new String(aes.AES_decrypt(encrypted_data_byte, symmetric_key, new byte[16]));

			return decrypted_data; 
		}catch(Exception e){
			logger.error("Exception: The decryption operation failed!", e);
			return "The decryption operation failed!"; 
		}
	}

	private String getEncSymKey(String key_id) throws Exception{
		String storage_type = key_storage.getStorage_type();
		switch(storage_type){
		case "database":
			String db_ip = "", db_port = "", db_auth_user = "", db_auth_pwd = "", db_database = "", db_table = "";
			ArrayList<Storage_Parameters> storage_parameters = key_storage.getStorage_parameters();
			for(Storage_Parameters sp : storage_parameters){
				if(sp.getName().equals("db_ip")) db_ip = sp.getValue();
				else if(sp.getName().equals("db_port")) db_port = sp.getValue();
				else if(sp.getName().equals("db_auth_user")) db_auth_user = sp.getValue();
				else if(sp.getName().equals("db_auth_pwd")) db_auth_pwd = sp.getValue();
				else if(sp.getName().equals("db_database")) db_database = sp.getValue();
				else if(sp.getName().equals("db_table")) db_table = sp.getValue();
			}

			String credentials = db_auth_user + ":" + db_auth_pwd;
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(credentials.getBytes()));

			String urlGet = "http://" + db_ip + ":" + db_port + "/query/" + db_database + "/sql/" +
					"select%20value%20from%20" + db_table + "%20where%20id=\"" + key_id + "\"";
			URL url = new URL(urlGet);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Authorization", basicAuth);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			if(connection.getResponseCode()==200){
				logger.info("Connection with KSS sucessfull estabilished");
				String json_response = "";
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String text = "";
				while ((text = br.readLine()) != null) {
					json_response += text;
				}
				Gson gson = new Gson();
				OrientDB_Recovered_Key message_class = gson.fromJson(json_response, OrientDB_Recovered_Key.class);

				return message_class.getResult().get(0).getValue(); //There will only be one result, since the key id is unique
			}else	
				throw new Exception("The encrypted symmetric key has not been recovered");		
		default:
			throw new Exception("The encrypted symmetric key has not been recovered");
		}
	}


	private void anticipate_key(String key_id){
		try{
			/* Check if this data consumer has the key associated with the identifier */
			if(!this.sym_keys.containsKey(key_id)){
				logger.info("--- IT'S NECESSARY TO RECOVER AND DECRYPT THE ANTICIPATED KEY ---");
				String encrypted_symmetric_key = getEncSymKey(key_id);
				/* Decrypt the key by CP-ABE using its CP-ABE private key */
				int index = encrypted_symmetric_key.indexOf(" ");
				String cph_b64 = encrypted_symmetric_key.substring(0, index);
				String aes_b64 = encrypted_symmetric_key.substring(index +  1);
				byte[] cph = Base64.getUrlDecoder().decode(cph_b64);
				byte[] aes = Base64.getUrlDecoder().decode(aes_b64);
				byte[][] enc_sym_key = new byte[2][];
				enc_sym_key[0] = cph;
				enc_sym_key[1] = aes;
				byte[] symmetric_key = this.cpabe.dec(this.public_parameters, this.cpabe_private_key, enc_sym_key);
				if(symmetric_key==null)
					throw new Exception("Error decrypting symmetric key");
				/* Locally store the key for future decryption operations */
				this.sym_keys.put(key_id, symmetric_key);
			}
		}catch(Exception e){
			logger.error("Exception",e);
			e.printStackTrace();
		}
	}

}
