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
 *  Gaetano Giordano (FINCONS GROUP)
 * Contributors:
 * 	Domenico Rotondi (FINCONS GROUP)
 ******************************************************************************/
package com.fincons.message_viewer.mqtt;

import org.apache.log4j.Logger;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fincons.mqtt.client.util.AuthCredentials;
import com.fincons.mqtt.client.util.Dms;

public class MessageSubscribeServiceNew {

	private Logger logger = Logger.getLogger(MessageSubscribeServiceNew.class);
	
	private MqttClient mqttClient;
	private ConfigurationLoaderNew configLoader = new ConfigurationLoaderNew();
	private MqttConnectOptions connOpts;
	
	public MessageSubscribeServiceNew(){

		String broker_url = configLoader.getMqtt_broker();
		int broker_port = configLoader.getMqtt_port();
		String broker_protocol = configLoader.getMqtt_protocol();
		Dms dms = new Dms(broker_url, broker_port);
		String broker = broker_protocol+"://" + dms.getDomain() + ":" + dms.getPort();
		
		MemoryPersistence persistence = new MemoryPersistence();
		AuthCredentials credentials = configLoader.getCredentials();
		try {
			
			connOpts = new MqttConnectOptions();			
			connOpts.setPassword(credentials.getPassword());
			connOpts.setUserName(credentials.getUsername());
			connOpts.setCleanSession( configLoader.isMqtt_clearsession());
			connOpts.setConnectionTimeout(configLoader.getConnectionTimeout());		
			connOpts.setKeepAliveInterval(configLoader.getKeepAliveInterval());
			
			mqttClient = new MqttClient(broker, credentials.getClientId(), persistence);
			mqttClient.setTimeToWait(configLoader.getTimeToWait());
		} catch (MqttException e) {
			logger.error("MqttException on connect", e);
		}
	}
	
	public void subscribe(String topic, MqttCallback listener) {

		logger.info("Calling the subscribe() method...");
		

		try {
			if(!mqttClient.isConnected())
				mqttClient.connect(connOpts);
			
			int QoS = configLoader.getMqtt_qos();
			
			mqttClient.setCallback(listener);
			mqttClient.subscribe(topic, QoS);

			logger.info("Connection estabilished!");

		} catch (MqttSecurityException e) {
			logger.error("MqttSecurityException", e);

		} catch (MqttException ex) {
			logger.error("MqttException", ex);
		}
	}


}
