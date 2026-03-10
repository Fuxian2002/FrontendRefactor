package com.example.demo.bean;

import java.util.List;

public class Transition {
	private String name;
	private String from;
	private String to;
	private List<String> sync;

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public List<String> getSync() {
		return sync;
	}
	public void setSync(List<String> trigger) {
		this.sync = trigger;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
