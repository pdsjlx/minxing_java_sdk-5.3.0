package com.minxing.client.organization;

import java.util.HashMap;

public class Network extends Organization {
	private long id;
	private String name;
	private String display_name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public HashMap<String, String> toHash() {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("name", this.getName());
		params.put("display_name", this.getDisplay_name());
		return params;

	}

	public void setId(long _id) {
		// TODO Auto-generated method stub
		this.id = _id;
	}
	
	public long getId() {
		return this.id;
	}

}
