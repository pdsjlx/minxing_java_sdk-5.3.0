package com.minxing.client.utils;

import java.net.URLEncoder;

public class UrlEncoder {

	public static String encode(String data) {  
		String result = null;
		try {
			result = URLEncoder.encode(data, "UTF-8");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}  
	
	public static void main(String[] args) throws Exception {
	}
}
