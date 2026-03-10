package com.example.demo.bean;

import java.util.List;

public class CCSLSet {
	private String id;
	private List<String> ccslList;
	
	public CCSLSet(String id, List<String> ccslList) {
		this.id = id;
		this.ccslList = ccslList;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getCcslList() {
		return ccslList;
	}
	public void setCcslList(List<String> ccslList) {
		this.ccslList = ccslList;
	}
}
