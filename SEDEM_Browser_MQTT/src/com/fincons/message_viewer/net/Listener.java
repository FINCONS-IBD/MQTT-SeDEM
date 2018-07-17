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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.fincons.message_viewer.mqtt.ThreadInitSubscribers;

/**
 * This class provides methods for receiving notification events about ServletContext lifecycle changes.
 * 
 * @author leonardo.straniero
 *
 */
public class Listener implements ServletContextListener {
	
    private ThreadInitSubscribers myThread = null;

	final static Logger logger = Logger.getLogger(Listener.class);
	public static String config_enc;
	public static String thing_id;
	
	/**
	 * This method receives notification that the web application initialization process is starting.
	 * @param sce - the ServletContextEvent containing the ServletContext that is being initialized
	 * 
	 */
    public void contextInitialized(ServletContextEvent sce) {
    	logger.info("Start contextInitialized");
    	String mqtt_conf = sce.getServletContext().getInitParameter("mqtt_conf_dec_subscriber");
		String log4jconf = sce.getServletContext().getInitParameter("log4j.properties");
		System.getProperties().setProperty("mqtt.properties", mqtt_conf);
		
		config_enc = sce.getServletContext().getInitParameter("mqtt_conf_enc_subscriber");
		thing_id = sce.getServletContext().getInitParameter("thing_id");
		
		logger.info("Loaded log4jproperties from:"+log4jconf);
		PropertyConfigurator.configure(log4jconf);
		
        if ((myThread == null) || (!myThread.isAlive())) {
            myThread = new ThreadInitSubscribers();
            myThread.start();
        }
    }

    /**
     * Receives notification that the ServletContext is about to be shut down.
     * 
     * @param sce - the ServletContextEvent containing the ServletContext that is being destroyed
     */
    public void contextDestroyed(ServletContextEvent sce){
    	logger.info("Destroing the context...");
        try {
        	logger.info("Closed");
        	if(myThread!=null){
	            myThread.interrupt();
        	}
        } catch (Exception ex) {
        	logger.error("Error in shutdown",ex);
        	ex.printStackTrace();
        }
    }
    
    /**
     * Returns the current ThreadInitSubscribers thread.
     *  
     */
    public ThreadInitSubscribers getMyThread(){
    	return myThread;
    }
    
}
