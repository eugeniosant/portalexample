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
package org.entando.entando.aps.system;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author eu
 */
public class JNDIThreadLocalContainer {
	
	private static final Logger _logger = LoggerFactory.getLogger(JNDIThreadLocalContainer.class);
	
	private static final ThreadLocal<Map<String, Object>> sessionThreadLocal = new ThreadLocal<Map<String, Object>>();
	/*
	static {
		sessionThreadLocal.set(new HashMap<String, Object>());
	}
	*/
	public static void set(String key, Object value) {
		Map<String, Object> map = sessionThreadLocal.get();
		if (null == map) {
			sessionThreadLocal.set(new HashMap<String, Object>());
			map = sessionThreadLocal.get();
		}
		map.put(key, value);
    }
	
	public static Connection getConnection(String dataSourceName) throws Exception {
		//System.out.println("Get connection - " + dataSourceName);
		Connection conn = null;
		try {
			Map<String, Object> map = sessionThreadLocal.get();
			if (null == map) {
				sessionThreadLocal.set(new HashMap<String, Object>());
				map = sessionThreadLocal.get();
			}
			//System.out.println("MAP CODE - " + System.identityHashCode(map));
			String connThreadLocalName = dataSourceName+"@connection";
			conn = (Connection) map.get(connThreadLocalName);
			if (null == conn) {
				DataSource ds = (DataSource) map.get(dataSourceName);
				conn = ds.getConnection();
				conn.setAutoCommit(false);
				map.put(connThreadLocalName, conn);
			}
			//System.out.println("CONNECTION - " + System.identityHashCode(conn));
		} catch (Throwable t) {
			_logger.error("Error extractiong connection", t);
			throw new Exception("Error extractiong connection", t);
		}
		return conn;
	}
	
	public static void closeConnections() throws Exception {
		try {
			Map<String, Object> map = sessionThreadLocal.get();
			Iterator<String> iter = map.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				if (key.endsWith("@connection")) {
					Connection conn = (Connection) map.get(key);
					commitAndCloseConnection(conn);
					map.remove(key);
				}
			}
		} catch (Throwable t) {
			_logger.error("Error extractiong connection", t);
			throw new Exception("Error extractiong connection", t);
		}
	}
	
	private static void commitAndCloseConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.commit();
			}
		} catch (Throwable t) {
			executeRollback(conn);
			_logger.error("Error committing data", t);			
		} finally {
			closeConnection(conn);
		}
	}
	
	private static void closeConnection(Connection conn) {
		try {
			if (conn != null) conn.close();
		} catch (Throwable t) {
			_logger.error("Error closing the connection", t);			
		}
	}
	
	private static void executeRollback(Connection conn) {
		try {
			if (conn != null) conn.rollback();
		} catch (SQLException e) {
			_logger.error("Error on connection rollback", e);			
		}
	}
	
}
