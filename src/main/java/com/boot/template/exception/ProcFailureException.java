package com.boot.template.exception;

public class ProcFailureException extends RuntimeException {

	private String redirectUrl;
	
	public ProcFailureException(String msg) {
		super(msg);
	}
	
	public ProcFailureException(String msg, String redirectUrl) {
		super(msg);
		this.redirectUrl = redirectUrl;
	}
	
	public String getRedirectUrl() {
		return this.redirectUrl;
	}

}
