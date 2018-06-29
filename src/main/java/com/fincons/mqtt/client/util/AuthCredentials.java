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
package com.fincons.mqtt.client.util;


/**
 * Provides a representation of a Credential Data Object used to perform the authentication
 * on the MQTT Broker Server
 * 
 * @author leonardo.straniero
 *
 */
public class AuthCredentials {

	private String clientId;
	private String username;
	private char[] password;
	
	/**
	 * The constructor returns an instance of AuthCredentials based on the parameters specified.
	 * 
	 * @param clientId - The unique string that identify the client. N.B: the client identifier must be unique across all clients connecting to the same MQTT Broker Server.
	 * @param username - The username string that identify the user authorized to perform MQTT operation on the server
	 * @param password - The secret word associated with the username
	 */
	public AuthCredentials(String clientId, String username, String password) {
		this.clientId = clientId;
		this.username = username;
		this.password = password.toCharArray();
	}

	public String getClientId() {
		return clientId;
	}

	public String getUsername() {
		return username;
	}

	public char[] getPassword() {
		return password;
	}
}
