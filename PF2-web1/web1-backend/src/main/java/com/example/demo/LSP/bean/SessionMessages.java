package com.example.demo.LSP.bean;

import java.util.ArrayList;

import javax.websocket.Session;

public class SessionMessages {
	Session session;
	ArrayList<TempMessage> messages = new ArrayList<TempMessage>();

	public SessionMessages(Session session) {
		super();
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public ArrayList<TempMessage> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<TempMessage> messages) {
		this.messages = messages;
	}

}
