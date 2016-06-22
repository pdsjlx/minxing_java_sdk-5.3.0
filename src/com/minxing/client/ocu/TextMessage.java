package com.minxing.client.ocu;

import com.minxing.client.json.JSONException;
import com.minxing.client.json.JSONObject;
import com.minxing.client.model.MxException;

public class TextMessage implements Message {

	private String body;
	private long id = 0;

	public TextMessage(String body) {
		this.body = body;
	}

	public TextMessage(long _id, String body) {
		this.body = body;
		this.id = _id;
	}
	
	public TextMessage(JSONObject message) {
		try {
//			JSONArray message_item = data.getJSONArray("items");
//			JSONObject message = message_item.getJSONObject(0);
			this.id = message.getLong("id");
			String group_id = message.getString("group_id");
			if(group_id == null||"".equals(group_id)){
				this.body = message.getString("body");
			}else{
				JSONObject message_body = message.getJSONObject("body");
				this.body = message_body.getString("plain");
			}
			
			
			
		} catch (JSONException e) {
			throw new MxException(e);
		}
	}
	

	public String getBody() {
		return body;
	}

	public static void main(String[] args) {
		TextMessage m = new TextMessage("abc");
		System.out.println(m.getBody());
	}

	@Override
	public int messageType() {
		return TEXT_MESSAGE;
	}

	@Override
	public String toString() {
		return "Message<id:" + id + ",body:" + body + ">";
	}

	public static TextMessage fromJSON(JSONObject data) {
		return new TextMessage(data);

	}
}
