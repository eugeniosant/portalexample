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

import java.io.IOException;
import java.rmi.ServerException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.sql.DataSource;

import org.entando.entando.aps.system.JNDIThreadLocalContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author E.Santoboni
 */
public class ThreadLocalFilter implements javax.servlet.Filter {
	
	private static final Logger _logger = LoggerFactory.getLogger(ThreadLocalFilter.class);
	
	@Override
	public void init(FilterConfig fc) throws ServletException {
		//System.out.println("INIT " + this);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			/*
			System.out.println("*********************");
			System.out.println("*** " + request.getServerName());
			System.out.println("*********************");
			*/
			
			//System.out.println("############CREATING ThreadLocal Container################");
			
			Context context = new InitialContext();
			DataSource portDataSource = (DataSource) context.lookup("java:/comp/env/jdbc/portDataSource");
			JNDIThreadLocalContainer.set("portDataSource", portDataSource);
			
			//if (request.getServerName().equals("localhost")) {
				DataSource servDataSource = (DataSource) context.lookup("java:/comp/env/jdbc/servDataSource");
				JNDIThreadLocalContainer.set("servDataSource", servDataSource);
			//} else {
			//	DataSource servDataSource = (DataSource) context.lookup("java:/comp/env/jdbc/servDataSourceTest");
			//	JNDIThreadLocalContainer.set("servDataSource", servDataSource);
			//}
			
			chain.doFilter(request, response);
			
		} catch (Throwable t) {
			_logger.error("Error", t);
			throw new ServerException("Error", new Exception(t));
		} finally {
			//System.out.println("###################Closing connections####################");
			try {
				JNDIThreadLocalContainer.closeConnections();
			} catch (Exception ex) {
				_logger.error("Error closing connections", ex);
				throw new ServerException("Error closing connections", ex);
			}
		}
	}
	
	@Override
	public void destroy() {
		System.out.println("DESTROY " + this);
	}
	
}
