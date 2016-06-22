package com.minxing.client.model;

public class Conversation {

	Long id;

	public Conversation(Long _id) {
		id = _id;
	}
	
	public Long getId() {
		return id;
	}

	public String toString() {
		return "<Conversation id:" + id + ">";
	}

}
