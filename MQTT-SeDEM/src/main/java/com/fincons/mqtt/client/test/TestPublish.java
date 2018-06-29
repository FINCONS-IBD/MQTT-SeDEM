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

import org.json.JSONObject;

import com.fincons.mqtt.client.service.MessagePublisherService;

/**
 * Tests the creation and publishing operations, based on MQTT protocol, for a specific topic. 
 * In this case the main method emulates the reading of a temperature sensor every 30 seconds and 
 * the event is represented by a temperature value in JSON format. The message will be published on 
 * MQTT Broker Server in an encrypted form.
 * 
 * @author leonardo.straniero
 *
 */
public class TestPublish {

	public static void main(String[] args) {
		
		String thng_id = "UKaFaQraeXswtKwwwH8QMHrd";
		String property_id = "temperature";
		String topic = "/thngs/"+thng_id+"/properties/"+property_id;

		JSONObject msg_obj = new JSONObject();
		msg_obj.put("temperature", 28);
//		msg_obj.put("humidity", 84);
//		msg_obj.put("motion", true);
		
//		MessagePublisherService.getInstance().publishMessage(topic, msg_obj);

		while(true){
			MessagePublisherService.getInstance().publishMessage(topic, msg_obj);
			try {
				Thread.sleep(29000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
