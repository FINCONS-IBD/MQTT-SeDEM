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
 * 	Gaetano Giordano (FINCONS GROUP)
 * Contributors:
 * 	Domenico Rotondi (FINCONS GROUP)
 ******************************************************************************/
package com.fincons.message_viewer.mqtt;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fincons.message_viewer.net.WsSocket;
import com.fincons.mqtt.client.util.DecryptionUtils;

/**
 * Provides MQTT SeDEM Library subscribe callback functionalities 
 * 
 * @author Fincons Group
 *
 */
public class SimpleMqttCallback implements MqttCallback {

	private WsSocket socketServer;
	private Logger logger = Logger.getLogger(SimpleMqttCallback.class);
	//messages counter
	private int i=0;
	boolean encrypted;
	
	/**
	 * The constructor returns a new instance of SimpleMqttCallback.
	 */
	public SimpleMqttCallback(WsSocket socketServer, boolean encrypted){
		super();
		this.socketServer=socketServer;
		this.encrypted = encrypted;
		this.i=0;
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
	 * It is possible to handle the message event reading the MqttMessage instance. In particular it provides the data (in JSON format) to the Web Socket components.
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		i++;
		logger.info("Gotten Event: " + i);
		
		logger.info("New message with id " + message.getId() + " arrived...");
		String clean_payload;
		if(this.encrypted)
			clean_payload = DecryptionUtils.getInstance().decrypt_payload(message);
		else
			clean_payload = new String(message.getPayload());
		
		
		//if the clean_payload is empty some errors are occured during the decryption process 
		//or the message sent doesn't join the estabilished JSON Format (a node with headers and payload attributes)
		JSONObject obj_to_show = null;
		if(!clean_payload.isEmpty()){
				
			Object json = new JSONTokener(clean_payload).nextValue();
			if (json instanceof JSONObject){
				//you have an object
				JSONArray jsn = new JSONArray(new String(message.getPayload()));
				JSONObject msg = ((JSONArray)jsn).getJSONObject(0);
			
				obj_to_show = new JSONObject();
				obj_to_show.put("createdAt", msg.get("createdAt"));
				obj_to_show.put("sensor", msg.get("key"));
				obj_to_show.put("payload", new JSONObject(clean_payload));
				obj_to_show.put("numMessage", i);
				
			}else if (json instanceof JSONArray){
				//you have an array
				JSONObject msg = ((JSONArray)json).getJSONObject(0);
				String value = msg.getString("value");
				JSONObject content = new JSONObject(value);
				JSONObject headers = content.getJSONObject("headers");
				String payload = content.getString("payload");
				
				obj_to_show = new JSONObject();
				obj_to_show.put("createdAt", msg.get("createdAt"));
				obj_to_show.put("sensor", msg.get("key"));
				obj_to_show.put("headers", headers);
				obj_to_show.put("payload", payload);
				obj_to_show.put("numMessage", i);
			}
			
			logger.info("Ready to send in socket message #"+i);
			socketServer.sendMessageToAllSession(obj_to_show.toString());

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
