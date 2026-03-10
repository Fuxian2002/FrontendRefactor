package com.example.demo.bean;

public class PfNode {
	private String shortname;
	private String type;
	private String name;
	private String prodomaintype;
	public PfNode(String shortname, String type, String name, String prodomaintype) {
		super();
		this.shortname = shortname;
		this.type = type;
		this.name = name;
		this.prodomaintype = prodomaintype;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
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

	public String getProdomaintype() {
		return prodomaintype;
	}

	public void setProdomaintype(String prodomaintype) {
		this.prodomaintype = prodomaintype;
	}

	public String toString() {
		String res = type;
		if (prodomaintype != null )
			res += " " + prodomaintype;
		res += " " + shortname;
		if (name != null && !name.contentEquals(""))
			res += " \"" + name + "\"";
		return res;
	}
}
