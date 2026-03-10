package com.example.demo.bean;

import java.io.Serializable;
import java.util.List;

public class Interface implements Cloneable,Serializable {
	private static final long serialVersionUID = 1L;
	private int interface_no;	//交互编号
	private String interface_name;	//交互名称
	private String interface_description;	//交互内容
	private String interface_from;
	private String interface_to;
	private List<Phenomenon> phenomenonList;	//现象列表
	private int interface_x1;	//位置信息
	private int interface_y1;
	private int interface_x2;
	private int interface_y2;
	private boolean isintent;

	public Interface() {

	}

	public Interface(Interface item) {
		interface_no = item.interface_no;
		interface_name = item.interface_name;
		interface_description = item.interface_description;
//		interface_intentdescription = item.interface_intentdescription;
		interface_from = item.interface_from;
		interface_to = item.interface_to;
		phenomenonList = item.phenomenonList;
//		intentphenomenonList = item.intentphenomenonList;
		interface_x1 = item.interface_x1;
		interface_y1 = item.interface_y1;
		interface_x2 = item.interface_x2;
		interface_y2 = item.interface_y2;
		isintent = item.isintent;
	}
	
	public int getInterface_no() {
		return interface_no;
	}
	public void setInterface_no(int interface_no) {
		this.interface_no = interface_no;
	}
	public boolean isIsintent() {
		return isintent;
	}
	public void setIsintent(boolean isintent) {
		this.isintent = isintent;
	}
	public String getInterface_name() {
		return interface_name;
	}
	public void setInterface_name(String interface_name) {
		this.interface_name = interface_name;
	}
	public String getInterface_description() {
		return interface_description;
	}
	public void setInterface_description(String interface_description) {
		this.interface_description = interface_description;
	}
	public String getInterface_from() {
		return interface_from;
	}
	public void setInterface_from(String interface_from) {
		this.interface_from = interface_from;
	}
	public String getInterface_to() {
		return interface_to;
	}
	public void setInterface_to(String interface_to) {
		this.interface_to = interface_to;
	}
	public List<Phenomenon> getPhenomenonList() {
		return phenomenonList;
	}
	public void setPhenomenonList(List<Phenomenon> phenomenonList) {
		this.phenomenonList = phenomenonList;
	}
	public int getInterface_x1() {
		return interface_x1;
	}
	public void setInterface_x1(int interface_x1) {
		this.interface_x1 = interface_x1;
	}
	public int getInterface_y1() {
		return interface_y1;
	}
	public void setInterface_y1(int interface_y1) {
		this.interface_y1 = interface_y1;
	}
	public int getInterface_x2() {
		return interface_x2;
	}
	public void setInterface_x2(int interface_x2) {
		this.interface_x2 = interface_x2;
	}
	public int getInterface_y2() {
		return interface_y2;
	}
	public void setInterface_y2(int interface_y2) {
		this.interface_y2 = interface_y2;
	}
	@Override    
	public Object clone() {        
		Interface inte = null;        
		try {            
			inte = (Interface) super.clone();        
		} catch (CloneNotSupportedException e) {            
			e.printStackTrace();        
			}        
		return inte;    
	}
}
