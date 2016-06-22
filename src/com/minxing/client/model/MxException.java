package com.minxing.client.model;

import com.minxing.client.json.JSONObject;

public class MxException extends RuntimeException {

	private static final long serialVersionUID = -6535179057555940528L;
	private int statusCode = -1;
	private int errorCode = -1;
	private String request;
	private String error;
	private int code = -1;
	private String message;

	public MxException(String msg) {
		super(msg);
	}

	public MxException(Exception cause) {
		super(cause);

	}

	public MxException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}

	public MxException(String msg, JSONObject json, int statusCode) {

		try {

			int t_code = json.getInt("code");
			if (t_code == 0) {
				JSONObject errors_json = json.getJSONObject("errors");
				if (errors_json != null) {
					this.code = errors_json.getInt("code");
					this.message = "HTTP: " + statusCode + " "
							+ errors_json.getString("message") + ", erro code:" + this.code;
				}
			} else {
				this.message = json.getString("message");
				this.code = t_code;
			}

			this.statusCode = statusCode;
			this.errorCode = json.getInt("error_code");
			this.error = json.getString("error");
			this.request = json.getString("request");

		} catch (Exception e) {

		}

	}

	public MxException(String msg, Exception cause) {
		super(msg, cause);
	}

	public MxException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;

	}

	public MxException(String msg, Throwable cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getRequest() {
		return request;
	}

	public String getError() {
		return error;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		if (message == null || message.trim().equals("")) {
			message = super.getMessage();
		}
		return message;
	}

}
