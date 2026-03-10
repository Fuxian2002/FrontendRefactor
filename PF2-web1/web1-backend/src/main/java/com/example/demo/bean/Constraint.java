package com.example.demo.bean;

import java.util.List;

public class Constraint extends Shape {
	private int constraint_no;	//约束编号
	private String constraint_name;	//约束名称
	private String constraint_description;	//约束详情
//	private String constraint_intentdescription;	//约束详情
	private String constraint_constraint;	
	private String constraint_from;	
	private String constraint_to;
	private int constraint_x1;	//位置信息
	private int constraint_y1;
	private int constraint_x2;
	private int constraint_y2;
	private boolean isintent;
	private List<RequirementPhenomenon> phenomenonList;	//现象列表
//	private List<IntentPhenomenon> intentphenomenonList;	//intent/context现象列表
//	private List<RequirementPhenomenon> intentphenomenonList;	//intent/context现象列表
	public Constraint() {
		super();
	}
	public Constraint(Constraint item) {
		constraint_no = item.constraint_no;
		constraint_name = item.constraint_name;
		constraint_description = item.constraint_description;
//		constraint_intentdescription = item.constraint_intentdescription;
		constraint_from = item.constraint_from;
		constraint_to = item.constraint_to;
		constraint_x1 = item.constraint_x1;
		constraint_y1 = item.constraint_y1;
		constraint_x1 = item.constraint_x1;
		constraint_y1 = item.constraint_y1;
		phenomenonList = item.phenomenonList;
//		intentphenomenonList = item.intentphenomenonList;
		isintent = item.isintent;
	}

	public boolean isIsintent() {
		return isintent;
	}

	public void setIsintent(boolean isintent) {
		this.isintent = isintent;
	}

	public int getConstraint_no() {
		return constraint_no;
	}
	public void setConstraint_no(int constraint_no) {
		this.constraint_no = constraint_no;
	}
	public String getConstraint_name() {
		return constraint_name;
	}
	public void setConstraint_name(String constraint_name) {
		this.constraint_name = constraint_name;
	}
	public String getConstraint_description() {
		return constraint_description;
	}
	public void setConstraint_description(String constraint_description) {
		this.constraint_description = constraint_description;
	}

//	public String getConstraint_intentdescription() {
//		return constraint_intentdescription;
//	}
//
//	public void setConstraint_intentdescription(String constraint_intentdescription) {
//		this.constraint_intentdescription = constraint_intentdescription;
//	}

	public String getConstraint_from() {
		return constraint_from;
	}
	public void setConstraint_from(String constraint_from) {
		this.constraint_from = constraint_from;
	}
	public String getConstraint_to() {
		return constraint_to;
	}
	public void setConstraint_to(String constraint_to) {
		this.constraint_to = constraint_to;
	}
	public int getConstraint_x1() {
		return constraint_x1;
	}
	public void setConstraint_x1(int constraint_x1) {
		this.constraint_x1 = constraint_x1;
	}
	public int getConstraint_y1() {
		return constraint_y1;
	}
	public void setConstraint_y1(int constraint_y1) {
		this.constraint_y1 = constraint_y1;
	}
	public int getConstraint_x2() {
		return constraint_x2;
	}
	public void setConstraint_x2(int constraint_x2) {
		this.constraint_x2 = constraint_x2;
	}
	public int getConstraint_y2() {
		return constraint_y2;
	}
	public void setConstraint_y2(int constraint_y2) {
		this.constraint_y2 = constraint_y2;
	}
	public List<RequirementPhenomenon> getPhenomenonList() {
		return phenomenonList;
	}
	public void setPhenomenonList(List<RequirementPhenomenon> phenomenonList) {
		this.phenomenonList = phenomenonList;
	}
//	public List<RequirementPhenomenon> getIntentPhenomenonList() {
//		return intentphenomenonList;
//	}
//	public void setIntentPhenomenonList(List<RequirementPhenomenon> intentPhenomenonList) {
//		this.intentphenomenonList = intentPhenomenonList;
//	}
	public String getConstraint_constraint() {
		return constraint_constraint;
	}
	public void setConstraint_constraint(String constraint_constraint) {
		this.constraint_constraint = constraint_constraint;
	}
	
	@Override    
	public Object clone() {        
		Constraint constraint = null;        
		try {            
			constraint = (Constraint) super.clone();        
		} catch (CloneNotSupportedException e) {            
			e.printStackTrace();        
			}        
		return constraint;    
	}
	public int getNo() {
		return constraint_no;
	}

	public void setNo(int no) {
		this.constraint_no = no;
	}

	public String getName() {
		return constraint_name;
	}

	public void setName(String name) {
		this.constraint_name = name;
	}
	public void refreshPhenomenonList(String oldShortName, String newShortName) {
		for (RequirementPhenomenon phenomenon : phenomenonList) {
			if (phenomenon.getPhenomenon_from()!=null && phenomenon.getPhenomenon_from().equals(oldShortName))
				phenomenon.setPhenomenon_from(newShortName);
			else if (phenomenon.getPhenomenon_to()!=null && phenomenon.getPhenomenon_to().equals(oldShortName))
				phenomenon.setPhenomenon_to(newShortName);
		}
	}
}
