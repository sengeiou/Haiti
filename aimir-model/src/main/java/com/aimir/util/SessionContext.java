package com.aimir.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.aimir.model.system.User;

@Scope(value = "session")
@Component("sessionContext")
public class SessionContext {
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return String.format("SessionContext [user=%s]", user);
	}

}
