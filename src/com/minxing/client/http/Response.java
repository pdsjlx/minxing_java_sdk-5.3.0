package com.minxing.client.http;

import com.minxing.client.json.JSONArray;
import com.minxing.client.json.JSONException;
import com.minxing.client.json.JSONObject;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.model.MxException;

public class Response {

	private int statusCode;
	private String responseAsString = null;

	public Response() {

	}

	Response(String content) {
		this.responseAsString = content;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public JSONObject asJSONObject() throws MxException {
		try {
			return new JSONObject(responseAsString);
		} catch (JSONException jsone) {
			throw new MxException("Server Error,return:" + this.responseAsString,
					jsone);
		}
	}

	public JSONArray asJSONArray() throws MxException {
		try {
			return new JSONArray(responseAsString);
		} catch (Exception jsone) {
			throw new MxException(jsone.getMessage() + ":"
					+ this.responseAsString, jsone);
		}
	}

	public ApiErrorException getApiError() {

		if (this.getStatusCode() > 500) {
			return new ApiErrorException(500, this.getResponseAsString());
		}
		JSONObject json_result = this.asJSONObject();
		int code;
		try {
			code = this.asJSONObject().getInt("code");
			String msg = json_result.getString("message");
			return new ApiErrorException(code, msg);
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return new ApiErrorException(500, e.getMessage());
		}

		

	}

	@Override
	public String toString() {
		if (null != responseAsString) {
			return responseAsString;
		}
		return "Response{" + "statusCode=" + statusCode + ", responseString='"
				+ responseAsString + '\'' + '}';
	}

	public String getResponseAsString() {
		return responseAsString;
	}

	public void setResponseAsString(String responseAsString) {
		this.responseAsString = responseAsString;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
