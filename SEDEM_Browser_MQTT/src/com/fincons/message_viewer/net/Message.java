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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class Message extends HttpServlet {

	private static final long serialVersionUID = 902702403663250306L;
	private String socketServerEnc="";
	private String socketServerDec="";
	final static Logger logger = Logger.getLogger(Message.class);	

	public void init(ServletConfig config) throws ServletException {
		socketServerEnc = config.getServletContext().getInitParameter("SocketServerEncrypt");
		socketServerDec = config.getServletContext().getInitParameter("SocketServerDecrypt");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("socketServerEnc", socketServerEnc);
		request.setAttribute("socketServerDec", socketServerDec);
		logger.info("start show_message");
		logger.debug("socketServerEnc:"+ socketServerEnc);
		logger.debug("socketServerDec: "+socketServerDec);
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	public String getServletInfo() {
		return super.getServletInfo();
	}
}