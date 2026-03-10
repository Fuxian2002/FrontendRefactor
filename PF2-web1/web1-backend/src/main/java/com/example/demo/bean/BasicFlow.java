package com.example.demo.bean;

import java.util.List;

public class BasicFlow {
	private String ID;
	private List<Step> steps;
	private String postcondition;
	
	
	public String getPostcondition() {
		return postcondition;
	}
	public void setPostcondition(String postcondition) {
		this.postcondition = postcondition;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public List<Step> getSteps() {
		return steps;
	}
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
}
