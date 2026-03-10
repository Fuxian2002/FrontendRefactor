package com.example.demo.bean;

import java.io.Serializable;
import java.util.Objects;

public class Phenomenon implements Cloneable,Serializable {
	private static final long serialVersionUID = 1L;
	private int phenomenon_no;	//现象编号
	private String phenomenon_shortname;	//现象名称
	private String phenomenon_name;	//现象名称
	private String phenomenon_type;	//现象类型
	private String phenomenon_from;	//发送方
	private String phenomenon_to;	//接收方

	public void cope(Phenomenon phe){
		this.phenomenon_no = phe.getPhenomenon_no();
		this.phenomenon_name = phe.getPhenomenon_name();
		this.phenomenon_shortname = phe.getPhenomenon_shortname();
		this.phenomenon_from = phe.getPhenomenon_from();
		this.phenomenon_to = phe.getPhenomenon_to();
		this.phenomenon_type = phe.getPhenomenon_type();
	}
	public int getPhenomenon_no() {
		return phenomenon_no;
	}
	public void setPhenomenon_no(int phenomenon_no) {
		this.phenomenon_no = phenomenon_no;
	}
	public String getPhenomenon_name() {
		return phenomenon_name;
	}
	public void setPhenomenon_name(String phenomenon_name) {
		this.phenomenon_name = phenomenon_name;
	}
	public String getPhenomenon_shortname() {
		return phenomenon_shortname;
	}
	public void setPhenomenon_shortname(String phenomenon_shortname) {
		this.phenomenon_shortname = phenomenon_shortname;
	}
	public String getPhenomenon_type() {
		return phenomenon_type;
	}
	public void setPhenomenon_type(String phenomenon_type) {
		this.phenomenon_type = phenomenon_type;
	}
	public String getPhenomenon_from() {
		return phenomenon_from;
	}
	public void setPhenomenon_from(String phenomenon_from) {
		this.phenomenon_from = phenomenon_from;
	}
	public String getPhenomenon_to() {
		return phenomenon_to;
	}
	public void setPhenomenon_to(String phenomenon_to) {
		this.phenomenon_to = phenomenon_to;
	}
	@Override    
	public Object clone() {        
		Phenomenon phenomenon = null;        
		try {            
			phenomenon = (Phenomenon) super.clone();        
		} catch (CloneNotSupportedException e) {            
			e.printStackTrace();        
			}        
		return phenomenon;    
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Phenomenon that = (Phenomenon) o;
		return phenomenon_no == that.phenomenon_no && phenomenon_shortname.equals(that.phenomenon_shortname) && phenomenon_name.equals(that.phenomenon_name) && phenomenon_type.equals(that.phenomenon_type) && Objects.equals(phenomenon_from, that.phenomenon_from) && Objects.equals(phenomenon_to, that.phenomenon_to);
	}

	@Override
	public int hashCode() {
		return Objects.hash(phenomenon_no, phenomenon_shortname, phenomenon_name, phenomenon_type, phenomenon_from, phenomenon_to);
	}
	public String phe2String(){
		return "phe:"+phenomenon_no+" "+phenomenon_shortname +" "+phenomenon_name;
	}
}
