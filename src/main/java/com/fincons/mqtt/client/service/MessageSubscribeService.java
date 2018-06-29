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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;


import com.fincons.mqtt.client.test.SimpleMqttCallback;
import com.fincons.mqtt.client.util.ConfigurationLoader;

import cpabe.Common;
import cpabe.Cpabe;
import messages.Key_Storage;
import messages.Storage_Parameters;

/**
 * This class defines a consumer of a specific set of events dispatched by another 
 * MQTT client on a specific topic. The actual processing of the events is performed by the
 * registered instances of {@link MqttCallback}. See the class {@link SimpleMqttCallback} for more
 * details about the subscriber business logic.
 * 
 * @see SimpleMqttCallback
 * 
 * @author leonardo.straniero
 *
 */
public class MessageSubscribeService  implements MqttCallback {

	private static MessageSubscribeService service = new MessageSubscribeService();
	private Logger logger = Logger.getLogger(MessageSubscribeService.class);

	/**
	 * Return the single instance of this class respecting the singleton pattern. 
	 * This method calls internally the class constructor when the class is instanciated for the first time.
	 * @return the instance of MessageSubscribeService
	 */
	public static MessageSubscribeService getInstance() {
		return service;
	}

	private MessageSubscribeService(){
	}

	/**
	 * Starts the subscription to a specific branch, subset or node of a specific resource pattern. 
	 * 
	 * @param topic - topic to subscribe, for example "finance/stock/ibm"
	 * @param listener - the listener containing the business logic executed when a new message arrived on the specified topic
	 */
	public void subscribe(String topic, MqttCallback listener) {

		logger.info("Calling the subscribe() method...");

		int QoS = ConfigurationLoader.getInstance().getMqtt_qos();

		try {
			if(!SessionService.getInstance().isConnected())
				SessionService.getInstance().connect();
			
			SessionService.getInstance().getMqttClient().setCallback(listener);
			SessionService.getInstance().getMqttClient().subscribe(topic, QoS);

			logger.info("Connection estabilished!");

		} catch (MqttSecurityException e) {
			logger.error("MqttSecurityException", e);

		} catch (MqttException ex) {
			logger.error("MqttException", ex);
		}
	}

	/**
	 * Ends the subscription.
	 * @param topicFilter - the topic to unsubscribe
	 */
	public void unsubscribe(String topicFilter) {
		logger.info("Calling the unsubscribe() method...");

		if(!SessionService.getInstance().isConnected())
			SessionService.getInstance().connect();

		try {
			SessionService.getInstance().getMqttClient().unsubscribe(topicFilter);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method implements the business logic of the client when it loses the connection with the MQTT Broker Server.
	 */
	@Override
	public void connectionLost(Throwable cause) {
		logger.info("Connection lost");
		System.out.println("msg "+cause.getMessage());
		System.out.println("loc "+cause.getLocalizedMessage());
		System.out.println("cause "+cause.getCause());
		System.out.println("excep "+cause);
	}
	
	/**
	 * This method defines the business logic implemented in the client subscriber when a new message is delivered to the topic selected.
	 * N.B. To permit the customization of the subscriber business logic, is preferable to register a {@link MqttCallback} externally (e.g. during the use of the library, at client side).
	 * For more details about the subscriber business logic please see the class {@link SimpleMqttCallback}.
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String time = new Timestamp(System.currentTimeMillis()).toString();

		System.out.println("Time:\t" +time +
				"  Topic:\t" + topic +
				"  Message:\t" + new String(message.getPayload()) +
				"  QoS:\t" + message.getQos());

	}

	/**
	 * This method defines the business logic implemented in the client publisher when the message is delivered to the MQTT Broker Server. 
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

}
