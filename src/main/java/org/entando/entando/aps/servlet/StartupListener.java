/*
*
* Copyright 2014 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software;
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2014 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.servlet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import org.entando.entando.aps.system.JNDIThreadLocalContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Init the system when the web application is started
 * @author E.Santoboni
 */
public class StartupListener extends org.springframework.web.context.ContextLoaderListener {
	
	private static final Logger _logger = LoggerFactory.getLogger(StartupListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			
			//INIT DATASOURCES
			Context context = new InitialContext();
			DataSource portDataSource = (DataSource) context.lookup("java:/comp/env/jdbc/portDataSource");
			JNDIThreadLocalContainer.set("portDataSource", portDataSource);
			DataSource servDataSource = (DataSource) context.lookup("java:/comp/env/jdbc/servDataSource");
			JNDIThreadLocalContainer.set("servDataSource", servDataSource);
			
			ServletContext svCtx = event.getServletContext();
			String msg = this.getClass().getName()+ ": INIT " + svCtx.getServletContextName();
			System.out.println(msg);
			
			//System.out.println("CREATING ThreadLocal Container");
			
			super.contextInitialized(event);
			msg = this.getClass().getName() + ": INIT DONE "+ svCtx.getServletContextName();
			System.out.println(msg);
			
			
		} catch (Throwable t) {
			//System.out.println("ERRORRRRRRRRRRRRRRRRRRRR");
			_logger.error("Error initializating context connection", t);
		} finally {
			try {
				//CLOSE CONNECTIONS
				//System.out.println("Closing connections");
				JNDIThreadLocalContainer.closeConnections();
			} catch (Exception ex) {
				_logger.error("Error closing connections", ex);
			}
		}
	}
	
}
