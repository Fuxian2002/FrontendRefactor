package com.example.demo.bean;

public class AlternativeFlow extends BasicFlow{
	private String type;	//specific/global/bounded
	private String RFS;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRFS() {
		return RFS;
	}
	public void setRFS(String RFS) {
		this.RFS = RFS;
	}
}
