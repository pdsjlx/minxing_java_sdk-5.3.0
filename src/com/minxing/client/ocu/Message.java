package com.minxing.client.ocu;

public interface Message {

	final public static int TEXT_MESSAGE = 0;
	final public static int RICH_TEXT_MESSAGE = 1;
	final public static int NOTIFICATION_MESSAGE = 2;
	final public static int SYSTEM_MESSAGE = 3;
	final public static int HTML_MESSAGE = 4;
	final public static int PLUGIN_MESSAGE = 5;
	final public static int APP_MESSAGE = 6;

	int messageType();

	String getBody();

}
