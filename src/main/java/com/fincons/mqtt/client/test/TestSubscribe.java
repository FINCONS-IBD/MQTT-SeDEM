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

import com.fincons.mqtt.client.service.MessageSubscribeService;

/**
 * Tests the subscribe operations to a specific topic based on the MQTT protocol. 
 * In this case the main method registers a MQTTCalback object containing the operations that the client will perform when an event message arrived.
 * The encrypted message delivered will be decrypted and presented by the library in clean format.
 * 
 * @see SimpleMqttCallback
 * 
 * @author leonardo.straniero
 *
 */
public class TestSubscribe {

	public static void main(String[] args) {

		SimpleMqttCallback listener = new SimpleMqttCallback();

		String thng_id = "UKaFaQraeXswtKwwwH8QMHrd";
		String property_id = "temperature";
		String topic = "/thngs/"+thng_id+"/properties/"+property_id;

//		String topic = "/thngs/UnNtBWqqeDswtKwwagAmBsdp/properties/#";
		
		MessageSubscribeService.getInstance().subscribe(topic, listener);
		
	}

}