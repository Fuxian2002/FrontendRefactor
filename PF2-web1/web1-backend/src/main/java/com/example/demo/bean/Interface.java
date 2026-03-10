package com.example.demo.bean;

import java.io.Serializable;
import java.util.List;

public class Interface extends Shape implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private int interface_no;	//交互编号
	private String interface_name;	//交互名称
	private String interface_description;	//交互内容
//	private String interface_intentdescription;	//intent交互内容
	private String interface_from;
	private String interface_to;
	private List<Phenomenon> phenomenonList;	//现象列表
//	private List<Phenomenon> intentphenomenonList;	//intent/context现象列表
//	private List<IntentPhenomenon> intentphenomenonList;	//intent/context现象列表
	private int interface_x1;	//位置信息
	private int interface_y1;
	private int interface_x2;
	private int interface_y2;
	private boolean isintent;
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

	public Interface() {
		super();
	}
	public boolean isIsintent() {
		return isintent;
	}
	public void setIsintent(boolean isintent) {
		this.isintent = isintent;
	}
	public int getInterface_no() {
		return interface_no;
	}
	public void setInterface_no(int interface_no) {
		this.interface_no = interface_no;
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

//	public String getInterface_intentdescription() {
//		return interface_intentdescription;
//	}
//
//	public void setInterface_intentdescription(String interface_intentdescription) {
//		this.interface_intentdescription = interface_intentdescription;
//	}
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
//	public List<Phenomenon> getIntentPhenomenonList() {
//		return intentphenomenonList;
//	}
//	public void setIntentPhenomenonList(List<Phenomenon> intentPhenomenonList) {
//		this.intentphenomenonList = intentPhenomenonList;
//	}
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

	@Override
	public int getNo() {
		return interface_no;
	}

	@Override
	public void setNo(int node_no) {
		this.interface_no=node_no;
	}

	@Override
	public String getName() {
		return interface_name;
	}

	@Override
	public void setName(String node_no) {
		this.interface_name=node_no;
	}
	public void refreshPhenomenonList(String oldShortName, String newShortName) {
		for (Phenomenon phenomenon : phenomenonList) {
			if (phenomenon.getPhenomenon_from().equals(oldShortName))
				phenomenon.setPhenomenon_from(newShortName);
			else if (phenomenon.getPhenomenon_to().equals(oldShortName))
				phenomenon.setPhenomenon_to(newShortName);
		}
//		if(intentphenomenonList != null) {
//			for (Phenomenon phenomenon : intentphenomenonList) {
//				if (phenomenon.getPhenomenon_from().equals(oldShortName))
//					phenomenon.setPhenomenon_from(newShortName);
//				else if (phenomenon.getPhenomenon_to().equals(oldShortName))
//					phenomenon.setPhenomenon_to(newShortName);
//			}
//		}
	}

}
