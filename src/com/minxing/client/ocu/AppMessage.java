package com.minxing.client.ocu;

public class AppMessage implements Message {

	private String content;
	private int badge;
	private String custom;
	private boolean enable_badge_in_app_store = false;

	public AppMessage(int badge, String content, String custom,
			boolean disable_badge_in_app_store) {
		this.content = content;
		this.badge = badge;
		this.enable_badge_in_app_store = disable_badge_in_app_store;
		this.custom = custom;
	}

	public AppMessage(int badge, String content, String custom) {
		this.content = content;
		this.badge = badge;
		this.custom = custom;
	}

	public String getBody() {

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"badge\":").append(badge).append(",");
		
		if (enable_badge_in_app_store == true) {
			sb.append("\"enable_badge_in_app_store\":").append(enable_badge_in_app_store).append(",");
		}

		sb.append("\"content\":\"").append(content).append("\"");

		if (custom != null && custom.length() > 0) {
			sb.append(",");
			sb.append("\"custom\":").append(custom);
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public int messageType() {

		return APP_MESSAGE;
	}
}
