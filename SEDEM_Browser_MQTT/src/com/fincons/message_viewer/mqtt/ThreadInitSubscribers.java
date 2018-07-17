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

import com.fincons.message_viewer.net.Listener;
import com.fincons.message_viewer.net.WsSocketDecryptedMessage;
import com.fincons.message_viewer.net.WsSocketEncryptedMessage;
import com.fincons.mqtt.client.service.MessageSubscribeService;

/**
 * This class defines two consumers of a specific set of events dispatched by another MQTT client on a specific topic.
 * The actual processing of the events is performed by the registered instances of {@link MqttCallback}. See the class {@link SimpleMqttCallback} for more
 * details about the subscriber business logic.
 * 
 * @see SimpleMqttCallback
 *  
 * @author leonardo.straniero
 *
 */  
public class ThreadInitSubscribers extends Thread {
	
	private Logger logger = Logger.getLogger(ThreadInitSubscribers.class);

	String thng_id = Listener.thing_id;
	String topic = "/thngs/"+thng_id+"/properties";

	/**
	 * Causes this thread to begin execution, creating and configuring two instances of two MQTT SeDEM subscribers.  
	 */
	@Override
	public synchronized void start() {
		
		logger.info("Start the tread to manage the two instances of MQTT Subscriber");
		
		super.start();

		SimpleMqttCallback decryptedListener = new SimpleMqttCallback(new WsSocketDecryptedMessage(), true);		
		MessageSubscribeService decryptedClient = MessageSubscribeService.getInstance();
		decryptedClient.subscribe(topic, decryptedListener);
		System.out.println("start first subscriber");

		System.getProperties().setProperty("mqtt.properties", Listener.config_enc);
		SimpleMqttCallback encryptedListener = new SimpleMqttCallback(new WsSocketEncryptedMessage(), false);		
		MessageSubscribeServiceNew encryptedClient = new MessageSubscribeServiceNew();	
		encryptedClient.subscribe(topic, encryptedListener);
		System.out.println("start second subscriber");

	}

	/**
	 * Interrupts this thread. 
	 */
	@Override
	public void interrupt() {
		super.interrupt();
	}

}
