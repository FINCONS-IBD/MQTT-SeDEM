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
package com.fincons.mqtt.client.test;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fincons.mqtt.client.util.ConfigurationLoader;
import com.fincons.mqtt.client.util.DecryptionUtils;

/**
 * Provides a test case about the use of SeDEM Library subscribe callback functionalities 
 * 
 * @author Fincons Group
 *
 */
public class SimpleMqttCallback implements MqttCallback {

	private String clientId;
	private Logger logger = Logger.getLogger(SimpleMqttCallback.class);

	/**
	 * The constructor return a new instance of SimpleMqttCallback.
	 */
	public SimpleMqttCallback() {
		this.clientId = ConfigurationLoader.getInstance().getCredentials().getClientId();
	}

	/**
	 * @see MqttCallback
	 */
	@Override
	public void connectionLost(Throwable me) {
		logger.info("Connection lost", me);
		System.out.println("msg "+me.getMessage());
		System.out.println("loc "+me.getLocalizedMessage());
		System.out.println("cause "+me.getCause());
		System.out.println("excep "+me);
	}

	/**
	 * This method is called every time a new event has been received by the subscriber on the specified topic.
	 * It is possible to handle the message event reading the MqttMessage instance.
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		logger.info("New message with id " + message.getId() + " arrived...");
		String clean_payload = DecryptionUtils.getInstance().decrypt_payload(message);

		//if the clean_payload is empty some errors are occured during the decryption process 
		//or the message sent doesn't join the estabilished JSON Format (a node with headers and payload attributes)
		if(!clean_payload.isEmpty()){
			String time = new Timestamp(System.currentTimeMillis()).toString();

			System.out.println("Time:\t" +time +
					"  Topic:\t" + topic +
					"  Message:\t" + clean_payload +
					"  QoS:\t" + message.getQos());
		}
	}

	/**
	 * @see MqttCallback
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}

}
