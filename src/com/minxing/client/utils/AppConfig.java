package com.minxing.client.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
		public AppConfig(){}
		private static Properties appProps = new Properties(); 
		static{
			try {
				appProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mx_app.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public static String getValue(String key){
			return appProps.getProperty(key);
		}
	
	    public static void updateProperties(String key,String value) {    
	    	appProps.setProperty(key, value); 
	    } 
}
