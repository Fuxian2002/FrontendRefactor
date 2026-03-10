package com.example.demo.bean;

public class Requirement extends PdNode{
	private int requirement_no;	//需求编号
	private String requirement_context;	//需求描述
	private String requirement_shortname;
	private int requirement_x;	//位置信息
	private int requirement_y;
	private int requirement_h;
	private int requirement_w;
	public Requirement(Requirement item) {
		requirement_no = item.requirement_no;
		requirement_context = item.requirement_context;
		requirement_shortname = item.requirement_shortname;
		requirement_x = item.requirement_x;
		requirement_y = item.requirement_y;
		requirement_h = item.requirement_h;
		requirement_w = item.requirement_w;
	}

	public Requirement() {
		super();
	}

	public int getRequirement_no() {
		return requirement_no;
	}
	public void setRequirement_no(int requirement_no) {
		this.requirement_no = requirement_no;
	}
	public String getRequirement_context() {
		return requirement_context;
	}
	public void setRequirement_context(String requirement_context) {
		this.requirement_context = requirement_context;
	}
	public String getRequirement_shortname() {
		return requirement_shortname;
	}
	public void setRequirement_shortname(String requirement_shortname) {
		this.requirement_shortname = requirement_shortname;
	}
	public int getRequirement_x() {
		return requirement_x;
	}
	public void setRequirement_x(int requirement_x) {
		this.requirement_x = requirement_x;
	}
	public int getRequirement_y() {
		return requirement_y;
	}
	public void setRequirement_y(int requirement_y) {
		this.requirement_y = requirement_y;
	}
	public int getRequirement_h() {
		return requirement_h;
	}
	public void setRequirement_h(int requirement_h) {
		this.requirement_h = requirement_h;
	}
	public int getRequirement_w() {
		return requirement_w;
	}
	public void setRequirement_w(int requirement_w) {
		this.requirement_w = requirement_w;
	}
	public int getNo() {
		return requirement_no;
	}

	public void setNo(int no) {
		this.requirement_no = no;
	}

	public String getName() {
		return requirement_context;
	}

	public void setName(String name) {
		this.requirement_context = name;
	}

	public String getShortname() {
		return requirement_shortname;
	}

	public void setShortname(String shortname) {
		this.requirement_shortname = shortname;
	}

	public int getX() {
		return requirement_x;
	}

	public void setX(int x) {
		this.requirement_x = x;
	}

	public int getY() {
		return requirement_y;
	}

	public void setY(int y) {
		this.requirement_y = y;
	}

	public int getH() {
		return requirement_h;
	}

	public void setH(int h) {
		this.requirement_h = h;
	}

	public int getW() {
		return requirement_w;
	}

	public void setW(int w) {
		this.requirement_w = w;
	}
}
