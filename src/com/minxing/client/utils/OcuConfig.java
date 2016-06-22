package com.minxing.client.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class OcuConfig {
		public OcuConfig(){}
		private static Properties ocuProps = new Properties();
		static{
			try {
				ocuProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("mx_ocu.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public static String getValue(String key){
			return ocuProps.getProperty(key);
		}
	
	    public static void updateProperties(String key,String value) {    
	    	ocuProps.setProperty(key, value); 
	    } 
}
