package com.example.demo.bean;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.Session;

public class LSP {
	private String titel;
	private String version;
	private String project = null;
	private CopyOnWriteArraySet<Session> sessionSet = new CopyOnWriteArraySet<Session>();
	private ArrayList<String> message = new ArrayList<String>();
	private int id = 0;

	public ArrayList<String> getMessages() {
		return message;
	}

	public String getMessage(int index) {
		return message.get(index);
	}

	public int getMessagesLen() {
		return message.size();
	}

	public void addMessage(String mes) {
		System.out.println("addMessage, id = " + id + "message=" + mes);
		message.add(mes);
		id++;
	}

	public int getId() {
		return id;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	// private Project project=new Project();
	// private PFText pf_text;
	public LSP(String titel, String version, Session session) {
		super();
		this.titel = titel;
		this.version = version;
		// this.pf_text = pf_text;
		this.sessionSet.add(session);
		// System.out.println("new lsp");
		System.out.println(this.sessionSet);
	}

	// public Project getProject() {
	// return project;
	// }

	public CopyOnWriteArraySet<Session> getSessionSet() {
		return sessionSet;
	}

	public void setSessionSet(CopyOnWriteArraySet<Session> sessionSet) {
		this.sessionSet = sessionSet;
	}

	public void addSession(Session session) {
		this.sessionSet.add(session);
		System.out.println("addSession");
		System.out.println(this.sessionSet);
	}

	public void removeSession(Session session) {
		this.sessionSet.remove(session);
		System.out.println("removeSession");
		System.out.println(this.sessionSet);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	// public void setProject(Project project) {
	// this.project = project;
	// }
	// public PFText getPf_text() {
	// return pf_text;
	// }
	// public void setPf_text(PFText pf_text) {
	// this.pf_text = pf_text;
	// }

}
