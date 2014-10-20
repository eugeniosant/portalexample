/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.entando.entando.aps.servlet;

/**
 * @author eu
 */
public class MyThreadLocal {
	
	public static final ThreadLocal<String> sessionThreadLocal = new ThreadLocal<String>();
	
	public static void set(String sessionid) {
        sessionThreadLocal.set(sessionid);
    }
	
    public static String get() {
        return sessionThreadLocal.get();
    }
	
}
