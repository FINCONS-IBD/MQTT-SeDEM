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

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fincons.mqtt.client.util.AuthCredentials;
import com.fincons.mqtt.client.util.ConfigurationLoader;
import com.fincons.mqtt.client.util.Dms;

/**
 * This class instantiates an MQTT Client that can be used to communicate with an MQTT server. 
 * The configuration parameters about the MQTT Broker Server and all the connection options
 * are specified in an external configuration file. 
 * 
 * @author leonardo.straniero
 *
 */
public class SessionService {

	private Logger logger = Logger.getLogger(SessionService.class);
	
	private static SessionService service = new SessionService();
	private MqttClient mqttClient;
	private AuthCredentials credentials;
	private String broker_url = ConfigurationLoader.getInstance().getMqtt_broker();
	private int broker_port = ConfigurationLoader.getInstance().getMqtt_port();
	private String broker_protocol = ConfigurationLoader.getInstance().getMqtt_protocol();

	private final Dms dms = new Dms(broker_url, broker_port);
	private String broker = broker_protocol+"://" + dms.getDomain() + ":" + dms.getPort();
	private MqttConnectOptions connOpts; 

	private SessionService() {
		
		MemoryPersistence persistence = new MemoryPersistence();
		credentials = ConfigurationLoader.getInstance().getCredentials();
		try {
			mqttClient = new MqttClient(broker, credentials.getClientId(), persistence);

			connOpts = new MqttConnectOptions();
			connOpts.setPassword(credentials.getPassword());
			connOpts.setUserName(credentials.getUsername());
			connOpts.setCleanSession( ConfigurationLoader.getInstance().isMqtt_clearsession());
			connOpts.setConnectionTimeout(ConfigurationLoader.getInstance().getConnectionTimeout());		
			connOpts.setKeepAliveInterval(ConfigurationLoader.getInstance().getKeepAliveInterval());

			mqttClient.setTimeToWait(ConfigurationLoader.getInstance().getTimeToWait());
		} catch (MqttException e) {
			logger.error("MqttException on connect", e);
		}
	}

	/**
	 * Return the single instance of the SessionService class respecting the singleton pattern and providing an 
	 * optimized handing of the connections with the Broker Server.
	 * This method call internally the class constructor when the class is used the first time.
	 * @return the instance of SessionService
	 */
	public static SessionService getInstance() {
		return service;
	}

	/**
	 * Connects to an MQTT server using the specified options. 
	 * N.B. The disconnect operation is handled by the keep-alive mechanisms then is not provided
	 * a disconnect() method.
	 * 
	 * @return the boolean flag that specifies if the client is currently connected to the server.
	 */
	public synchronized boolean connect() {

		try {
			logger.info("Connecting to broker: " + broker);
			mqttClient.connect(connOpts);

		} catch (MqttException e) {
			logger.error("MqttException on connect", e);
		}

		return mqttClient.isConnected();
	}

//	public void disconnect() {
//		try {
//			mqttClient.disconnect();
//		} catch (MqttException e) {
//			logger.error("MqttException on disconnect", e);
//		}
//	}

	/**
	 * Gets the instance of the MQTT Client created by the SessionService class.
	 * @return the instance of {@link MqttClient}
	 */
	public MqttClient getMqttClient() {
		return mqttClient;
	}

	/**
	 * Specify if the client is currently connected to the server
	 * @return true if the client is connected to the server, false otherwise. 
	 */
	public synchronized boolean isConnected() {
		return mqttClient.isConnected();
	}
}
