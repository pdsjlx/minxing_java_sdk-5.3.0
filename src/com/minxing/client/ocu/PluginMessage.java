package com.minxing.client.ocu;

import com.minxing.client.json.JSONArray;
import com.minxing.client.json.JSONException;
import com.minxing.client.json.JSONObject;
import com.minxing.client.model.MxException;

public class PluginMessage implements Message {

	private String body;
	private long id = 0;

	public PluginMessage(String body) {
		this.body = body;
	}

	public PluginMessage(long _id, String body) {
		this.body = body;
		this.id = _id;
	}
	
	public PluginMessage(JSONObject data) {
		try {
			JSONArray message_item = data.getJSONArray("items");
			JSONObject message = message_item.getJSONObject(0);
			this.id = message.getLong("id");
			this.body = message.getString("body");
			
		} catch (JSONException e) {
			throw new MxException(e);
		}
	}
	

	public String getBody() {
		return body;
	}

	public static void main(String[] args) {
		PluginMessage m = new PluginMessage("abc");
		System.out.println(m.getBody());
	}

	@Override
	public int messageType() {
		return PLUGIN_MESSAGE;
	}

	@Override
	public String toString() {
		return "Message<id:" + id + ",body:" + body + ">";
	}

	public static PluginMessage fromJSON(JSONObject data) {
		return new PluginMessage(data);

	}
}
