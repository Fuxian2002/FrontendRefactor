package com.example.demo.bean;

public class PfPhenomenon {
	private String type;
	private String name;
	private String description;
	private String from;
	private String to;
	public PfPhenomenon(String type, String name, String description, String from, String to) {
		super();
		this.type = type;
		this.name = name;
		this.description = description;
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		String res = "phenomenon " + type + " " + name;
		if(from != null){
			res += " " + from;
		}else{
			res += " *";
		}
		if(to != null){
			res += " " + to;
		}else{
			res += " *";
		}
		if (description != null && !description.contentEquals(""))
			res += " \"" + description + "\"";
		return res;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

}
