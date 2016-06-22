package com.minxing.client.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class StringUtil {

		public static String convertContentToHtml(String content){
			content = content.replaceAll("(\r\n|\n)", "<br/>");
			content = content.replaceAll(" ", "&nbsp;");
			return content;
		}
		
		public static String pathDecode(String path) throws UnsupportedEncodingException{
			if(path != null){
				path = path.replace("+", "%2b");
				path = URLDecoder.decode(path,"UTF-8");
			}
			return path;
		}
		//图文内容里带\n \r \r\n  会造成图文消息结构混乱，导致客户端crash、web显示消息异常
		public static String convertContent(String content){
			if(content!=null){
				content = content.replaceAll("(\n)", "\\\\n");
				content = content.replaceAll("(\r)", "\\\\r");
			}
			return content;
		}
		
		final protected static char[] hexArray = "0123456789abcdef".toCharArray();
		public static String bytesToHex(byte[] bytes) {
		    char[] hexChars = new char[bytes.length * 2];
		    for ( int j = 0; j < bytes.length; j++ ) {
		        int v = bytes[j] & 0xFF;
		        hexChars[j * 2] = hexArray[v >>> 4];
		        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		    }
		    return new String(hexChars);
		}
}
