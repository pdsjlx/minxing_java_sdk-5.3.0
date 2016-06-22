package com.minxing.client.model;

public class ApiErrorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int errorCode;
	private String errorMessage;
	
	public ApiErrorException(int errorCode,String errorMessage) {
		super(errorCode + ": " + errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public ApiErrorException(String errorMessage,int errorCode, Throwable e) {
		super(errorMessage,e);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
