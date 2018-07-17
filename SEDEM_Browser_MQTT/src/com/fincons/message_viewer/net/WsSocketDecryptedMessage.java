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
/**
 * This class declares the web socket endpoint on which the decrypted messages will be sent starting the standard CP-ABE/AES decryption process,
 * defining the URL where this endpoint will be published at, and other important properties of the endpoint to the websocket runtime.
 * 
 * @author leonardo.straniero
 *
 */
@ServerEndpoint("/decryptedMessage")
public class WsSocketDecryptedMessage implements WsSocket {

	private static List<Session> allSession;

	final static Logger logger = Logger.getLogger(WsSocketDecryptedMessage.class);
	
	/**
	 * The WsSocketDecryptedMessage constructor, instanciates the new Sessions collection. 
	 */
	public WsSocketDecryptedMessage(){
		allSession = new ArrayList<Session>();
	}
	
	/**
	 * This method decorates a Java method that wishes to be called when a new web socket session is opened, 
	 * adding the session to the sessions collection.
	 * @param session - A Web Socket session 
	 */
	@OnOpen
	public void onOpen(Session session) {
		logger.info("Decrypted - Open new session");

		allSession.add(session);
	}
	
	/**
	 * This method decorates a Java method that wishes to be called when a new web socket session is closed.
	 */
	@OnClose
	public void onClose() {

		logger.info("Decrypted - Close session");
	}

	/**
	 * This method implements the logic executed when incoming web socket messages are received.
	 * 
	 * @param message
	 * @return
	 * @throws IOException
	 */
	@OnMessage
	public String onMessage(String message) throws IOException {
		logger.info("Decrypted - Message from the client: " + message);
		String echoMsg = "Echo from the server : " + message;
		return echoMsg;
	}

	/**
	 * This method is called in order to handle errors.
	 * 
	 * @param e - the Exception throwed
	 */
	@OnError
	public void onError(Throwable e) {
		logger.error("Decrypted - Error in socket ", e);
		e.printStackTrace();
	}

	/**
	 * This method implements the functionalities to send the incoming message to all the open Web Socket in the Sessions collection.
	 * 
	 * @param string - the incoming message
	 */
	public void sendMessageToAllSession(String string) throws IOException {
		logger.debug("Decrypted - Send message in broadcast");
		for (Session session : allSession) {
			if (session.isOpen()) {
				session.getBasicRemote().sendText(string);
			}
		}
	}
}
