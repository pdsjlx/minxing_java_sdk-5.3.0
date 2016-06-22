package com.minxing.client.ocu;

public class HtmlMessage extends TextMessage {

	public HtmlMessage(String body) {
		super(body);
	}
	@Override
	public int messageType() {
		// TODO Auto-generated method stub
		return HTML_MESSAGE;
	}
}
