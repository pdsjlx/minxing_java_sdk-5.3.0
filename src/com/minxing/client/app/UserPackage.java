package com.minxing.client.app;

import java.util.Iterator;
import java.util.List;

import com.minxing.client.model.User;

public class UserPackage implements Iterable<UserSet> {

	int userSize = 100;
	private AppAccount account;

	UserPackage(AppAccount _account, int _userSize) {
		this.account = _account;
		this.userSize = _userSize;
	}

	@Override
	public Iterator<UserSet> iterator() {
		return new UserSetIterator(account,userSize);
	}
	
}
