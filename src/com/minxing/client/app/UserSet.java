package com.minxing.client.app;

import com.minxing.client.organization.User;



public class UserSet  {

	int userSize = 100;
	private User[] users;
	
	public UserSet(User[] users2) {
		this.users = users2;
	}

	
	public User[] getUsers() {
		return this.users;
	}
	
	
}
