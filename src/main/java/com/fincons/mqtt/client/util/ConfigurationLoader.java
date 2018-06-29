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

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;

/**
 * This class loads in memory the properties configuration file called conf.properties. 
 * The class provides the 'g'et and 'setter' methods for each configuration parameter.
 * 
 * @author Fincons Group
 *
 */
public class ConfigurationLoader {

	Logger logger = Logger.getLogger(ConfigurationLoader.class);

	public static final ConfigurationLoader service = new ConfigurationLoader();

	private AuthCredentials credentials = null;
	private String mqtt_client_id = null;
	private int mqtt_qos;
	private String mqtt_broker;
	private int mqtt_port;
	private String mqtt_protocol;
	private boolean mqtt_clearsession;
	private boolean mqtt_retained;
	private int timeToWait;
	private int connectionTimeout;
	private int keepAliveInterval;
	private boolean cpabe_aes_encryption;
	private String cpabe_policy;
	private boolean cpabe_anticipated_key;
	private int cpabe_anticipated_key_in_sec;
	
	private String key_type;
	private String cryptographic_curve;
	private String alg;
	private String enc;
	
	private String proxy_protocol;
	private String proxy_ip;
	private String proxy_port;
	private String proxy_id;
	private String storage_type;
	
	private String db_ip;
	private String db_port;
	private String db_auth_user;
	private String db_auth_pwd;
	private String db_database;
	private String db_table;

	private String payload_pattern;
	private String placeholder_pattern;

	private String consumer_public_param;
	private String consumer_cpabe_key;
	
	/**
	/**
	 * Return the single instance of the ConfigurationLoader class respecting the singleton pattern and providing an 
	 * optimized handing of the client configuration. 
	 * This method call internally the class constructor when the class is used the first time.
	 * @return the instance of SessionService
	 */
	public static ConfigurationLoader getInstance() {
		return service;
	}

	
	private ConfigurationLoader() {
		Configurations configs = new Configurations();
		Configuration config = null;
		try {
			File configurationFile = new File(System.getProperty("mqtt.properties"));
			config = configs.properties(configurationFile);
			if (config.getString(Constants.MQTT_PASSWORD) == null 
					|| config.getString(Constants.MQTT_USERNAME) == null 
					|| config.getString(Constants.MQTT_CLIENT_ID) == null) {
				logger.error("Crendentials not found.");
				System.exit(0);
			}
			
			credentials = new AuthCredentials(config.getString(Constants.MQTT_CLIENT_ID), 
					config.getString(Constants.MQTT_USERNAME), 
					config.getString(Constants.MQTT_PASSWORD));
			mqtt_client_id = config.getString(Constants.MQTT_CLIENT_ID);
			
			if(config.getString(Constants.MQTT_QOS) == null || config.getString(Constants.MQTT_BROKER)  == null|| 
			   config.getString(Constants.MQTT_PORT)  == null|| config.getString(Constants.MQTT_PROTOCOL) == null || 
			   config.getString(Constants.MQTT_CLEARSESSION)  == null|| config.getString(Constants.MQTT_RETAINED) == null ||
			   config.getString(Constants.MQTT_TIME_TO_WAIT) == null || config.getString(Constants.MQTT_TIMEOUT) == null ||
			   config.getString(Constants.MQTT_KEEP_ALIVE) == null ){
				
				logger.error("Missing mandatory mqtt configuration parameters: MQTT_QOS, MQTT_BROKER, "
						+ "MQTT_PORT, MQTT_PROTOCOL, MQTT_CLEARSESSION, MQTT_RETAINED, MQTT_TIME_TO_WAIT, "
						+ "MQTT_TIMEOUT, MQTT_KEEP_ALIVE.");
				System.exit(0);
			}else{
				mqtt_qos = config.getInt(Constants.MQTT_QOS);
				mqtt_broker = config.getString(Constants.MQTT_BROKER);
				mqtt_port = config.getInt(Constants.MQTT_PORT);
				mqtt_protocol = config.getString(Constants.MQTT_PROTOCOL);
				mqtt_clearsession = config.getBoolean(Constants.MQTT_CLEARSESSION);
				mqtt_retained = config.getBoolean(Constants.MQTT_RETAINED);
				timeToWait = config.getInt(Constants.MQTT_TIME_TO_WAIT);
				connectionTimeout = config.getInt(Constants.MQTT_TIMEOUT);;
				keepAliveInterval = config.getInt(Constants.MQTT_KEEP_ALIVE);
			}
			
			
			if(config.getString(Constants.CPABE_AES_ENCRYPTION) == null){
				logger.error("Missing mandatory security configuration parameter: CPABE_AES_ENCRYPTION");
				System.exit(0);
			}
			
			if(config.getString(Constants.PAYLOAD_PATTERN) == null || config.getString(Constants.PLACEHOLDER_PATTERN) == null){
				logger.error("Missing mandatory configuration parameters: PAYLOAD_PATTERN, PLACEHOLDER_PATTERN.");
				System.exit(0);					
			}else{
				payload_pattern = config.getString(Constants.PAYLOAD_PATTERN);
				placeholder_pattern = config.getString(Constants.PLACEHOLDER_PATTERN);
			}
						
			if(config.getBoolean(Constants.CPABE_AES_ENCRYPTION)){
				
				if(config.getString(Constants.CPABE_POLICY) == null ||
					config.getString(Constants.CPABE_ANTICIPATED_KEY) == null ||
					config.getString(Constants.KEY_TYPE) == null ||
					config.getString(Constants.CRYPTOGRAPHIC_CURVE) == null ||
					config.getString(Constants.ALG) == null ||
					config.getString(Constants.ENC) == null ||
					config.getString(Constants.PROXY_PROTOCOL) == null ||							
					config.getString(Constants.PROXY_IP) == null ||
					config.getString(Constants.PROXY_PORT) == null ||
					config.getString(Constants.PROXY_ID) == null ||
					config.getString(Constants.STORAGE_TYPE) == null||
					config.getString(Constants.DB_IP) == null || 
					config.getString(Constants.DB_PORT) == null ||
					config.getString(Constants.DB_AUTH_USER) == null ||
					config.getString(Constants.DB_AUTH_PWD) == null ||
					config.getString(Constants.DB_DATABASE) == null ||
					config.getString(Constants.DB_TABLE) == null
						){
					
					logger.error("Missing mandatory security configuration parameters: CPABE_POLICY, CPABE_ANTICIPATED_KEY, KEY_TYPE, "
							+ "CRYPTOGRAPHIC_CURVE, ALG, ENC, PROXY_PROTOCOL, PROXY_IP, PROXY_PORT, PROXY_ID, STORAGE_TYPE, "
							+ "DB_IP, DB_AUTH_USE, DB_AUTH_PWD, DB_DATABASE, DB_TABLE.");
					
					System.exit(0);
					
				}else{
					cpabe_aes_encryption = config.getBoolean(Constants.CPABE_AES_ENCRYPTION);
					cpabe_policy = config.getString(Constants.CPABE_POLICY);
					key_type =  config.getString(Constants.KEY_TYPE);
					cryptographic_curve = config.getString(Constants.CRYPTOGRAPHIC_CURVE);
					alg = config.getString(Constants.ALG);
					enc = config.getString(Constants.ENC);
					proxy_protocol = config.getString(Constants.PROXY_PROTOCOL);
					proxy_ip = config.getString(Constants.PROXY_IP);
					proxy_port = config.getString(Constants.PROXY_PORT);
					proxy_id = config.getString(Constants.PROXY_ID);
					storage_type = config.getString(Constants.STORAGE_TYPE);
					db_ip = config.getString(Constants.DB_IP);
					db_port = config.getString(Constants.DB_PORT);
					db_auth_user = config.getString(Constants.DB_AUTH_USER);
					db_auth_pwd = config.getString(Constants.DB_AUTH_PWD);
					db_database = config.getString(Constants.DB_DATABASE);
					db_table = config.getString(Constants.DB_TABLE);
					
				}
				
				if(config.getBoolean(Constants.CPABE_ANTICIPATED_KEY) && config.getString(Constants.CPABE_ANTICIPATED_KEY_IN_SECONDS) == null){
					logger.error("Missing mandatory security configuration parameters: CPABE_ANTICIPATED_KEY_IN_SECONDS.");
					System.exit(0);
				}else
					this.cpabe_anticipated_key_in_sec =  config.getInt(Constants.CPABE_ANTICIPATED_KEY_IN_SECONDS);

				if(config.getString(Constants.CONSUMER_PUBLIC_PARAM) == null ||
						config.getString(Constants.CONSUMER_CPABE_KEY) == null){
					logger.error("Missing mandatory security configuration parameters: CONSUMER_PUBLIC_PARAM, CONSUMER_CPABE_KEY.");
					System.exit(0);
				}else{
					this.consumer_public_param = config.getString(Constants.CONSUMER_PUBLIC_PARAM);
					this.consumer_cpabe_key = config.getString(Constants.CONSUMER_CPABE_KEY);
				}
					
			}
			
		} catch (ConfigurationException e) {
			logger.error("ConfigurationLoaderException - mqtt.properties not found or a problem occoured", e);
		} catch (Exception ex ){
			logger.error("ConfigurationLoaderException - A general problem occours during the configuration steps.", ex);
		}

	}

	public AuthCredentials getCredentials() {
		return credentials;
	}

	public String getMqtt_client_id() {
		return mqtt_client_id;
	}

	public int getMqtt_qos() {
		return mqtt_qos;
	}

	public String getMqtt_broker() {
		return mqtt_broker;
	}

	public int getMqtt_port() {
		return mqtt_port;
	}

	public String getMqtt_protocol() {
		return mqtt_protocol;
	}

	public boolean isMqtt_clearsession() {
		return mqtt_clearsession;
	}

	public boolean isMqtt_retained() {
		return mqtt_retained;
	}

	public int getTimeToWait() {
		return timeToWait;
	}
	
	public String getCpabe_policy() {
		return cpabe_policy;
	}

	public boolean isCpabe_aes_encryption() {
		return cpabe_aes_encryption;
	}

	public boolean isCpabe_anticipated_key() {
		return cpabe_anticipated_key;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}
	
	public int getCpabe_anticipated_key_in_sec() {
		return cpabe_anticipated_key_in_sec;
	}
	
	public String getKey_type() {
		return key_type;
	}

	public String getCryptographic_curve() {
		return cryptographic_curve;
	}

	public String getAlg() {
		return alg;
	}

	public String getEnc() {
		return enc;
	}

	public String getProxy_protocol() {
		return proxy_protocol;
	}

	public String getProxy_ip() {
		return proxy_ip;
	}

	public String getProxy_port() {
		return proxy_port;
	}

	public String getProxy_id() {
		return proxy_id;
	}

	public String getStorage_type() {
		return storage_type;
	}

	public String getDb_ip() {
		return db_ip;
	}

	public String getDb_port() {
		return db_port;
	}

	public String getDb_auth_user() {
		return db_auth_user;
	}

	public String getDb_auth_pwd() {
		return db_auth_pwd;
	}

	public String getDb_database() {
		return db_database;
	}

	public String getDb_table() {
		return db_table;
	}

	public String getPayload_pattern() {
		return payload_pattern;
	}

	public String getPlaceholder_pattern() {
		return placeholder_pattern;
	}

	public String getConsumer_public_param() {
		return consumer_public_param;
	}

	public String getConsumer_cpabe_key() {
		return consumer_cpabe_key;
	}
}
