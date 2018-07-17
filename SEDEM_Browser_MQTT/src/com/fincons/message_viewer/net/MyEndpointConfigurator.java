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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.server.ServerEndpointConfig;

import org.apache.log4j.Logger;

/**
 * This class extends the ServerEndpointConfig.Configurator class to provide custom configuration algorithms.
 * 
 * @author leonardo.straniero
 *
 */
public class MyEndpointConfigurator extends ServerEndpointConfig.Configurator{
	
    private Set<WsSocket> endpoints = Collections.synchronizedSet(new HashSet<>());
	final static Logger logger = Logger.getLogger(MyEndpointConfigurator.class);
	
	/**
	 * This method is called by the container each time a new client connects to the logical endpoint this configurator configures.
	 */
    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        try {
        	logger.info("Endpoint Configuration");
            T endpoint = endpointClass.newInstance();
            WsSocket myEndpoint = (WsSocket) endpoint;
            endpoints.add(myEndpoint);
            return endpoint;
        } catch (IllegalAccessException e) {
        	logger.error("Endpoint Configuration Error", e);
            throw new InstantiationException(e.getMessage());
        }
    }

    /**
     * This method remove the passed WsSocket from the session collection
     * 
     * @param endpoint - the WsSocket to remove
     */
    public void removeInstance(WsSocket endpoint) {
    	logger.info("Endpoint Removed");
        endpoints.remove(endpoint);
    }
}
