package com.example.demo;

public class MyJSONWrapper {
	private String status;
	private String message;
	
	
	public MyJSONWrapper(String message) {
		this.status = "success";
		this.message = message;
	}
	
	public MyJSONWrapper(String status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
