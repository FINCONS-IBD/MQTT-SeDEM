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
package com.fincons.message_viewer.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import com.fincons.mqtt.client.test.SimpleMqttCallback;

/**
 * This class declares the web socket endpoint on which the encrypted message will be sent without starting the decryption process
 * See the class {@link WsSocketDecryptedMessage} for more details about the methods implemented in this class.
 * @author leonardo.straniero
 *
 */
@ServerEndpoint("/encryptedMessage")
public class WsSocketEncryptedMessage implements WsSocket {

	private static List<Session> allSession;
	
	final static Logger logger = Logger.getLogger(WsSocketEncryptedMessage.class);

	public WsSocketEncryptedMessage(){
		allSession = new ArrayList<Session>();
	}
	
	@OnOpen
	public void onOpen(Session session) {

		logger.info("Encrypted - Open new session");
		if (allSession == null || allSession.isEmpty()) {
			allSession = new ArrayList<Session>();
		}
		allSession.add(session);
//		System.out.println("Open conn");
	}

	@OnClose
	public void onClose() {
		logger.info("Encrypted - Close session");
//		System.out.println("Close Connection ...");
	}

	@OnMessage
	public String onMessage(String message) throws IOException {
//		System.out.println("Message from the client: " + message);
		logger.debug("Encrypted -  Message from the client: " + message);
		String echoMsg = "Echo from the server : " + message;
		return echoMsg;
	}

	@OnError
	public void onError(Throwable e) {
		logger.error("Encrypted - Error in socket ", e);
		e.printStackTrace();
	}

	public void sendMessageToAllSession(String string) throws IOException {
		logger.debug("Encrypted - SendMessage");
//		System.out.println("SendMessage");
		for (Session session : allSession) {
			if (session.isOpen()) {
				session.getBasicRemote().sendText(string);
			}
		}
	}
}
