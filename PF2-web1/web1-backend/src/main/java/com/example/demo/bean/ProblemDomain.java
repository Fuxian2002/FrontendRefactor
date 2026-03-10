package com.example.demo.bean;

import java.util.ArrayList;

public class ProblemDomain extends PdNode{
	private int problemdomain_no;	//领域编号
	private String problemdomain_name;	//领域名称
	private String problemdomain_shortname;	//名称缩写
	private String problemdomain_type;	//领域类型
	private String problemdomain_property;	//物理特性
	private int problemdomain_x;	//位置信息
	private int problemdomain_y;	
	private int problemdomain_h;	
	private int problemdomain_w;
	private String state;
	private ArrayList<Phenomenon> phes;
	public ProblemDomain() {
		super();
	}

	public ProblemDomain(ProblemDomain item) {
		problemdomain_no = item.problemdomain_no;
		problemdomain_name = item.problemdomain_name;
		problemdomain_shortname = item.problemdomain_shortname;
		problemdomain_type = item.problemdomain_type;
		problemdomain_property = item.problemdomain_property;
		problemdomain_x = item.problemdomain_x;
		problemdomain_y = item.problemdomain_y;
		problemdomain_h = item.problemdomain_h;
		problemdomain_w = item.problemdomain_w;
		state = item.state;
	}
	public void setPhes(ArrayList<Phenomenon> phes){
		this.phes = phes;
	}
	public ArrayList<Phenomenon> getPhes(){
		return this.phes;
	}
	public int getProblemdomain_no() {
		return problemdomain_no;
	}
	public void setProblemdomain_no(int problemdomain_no) {
		this.problemdomain_no = problemdomain_no;
	}
	public String getProblemdomain_name() {
		return problemdomain_name;
	}
	public void setProblemdomain_name(String problemdomain_name) {
		this.problemdomain_name = problemdomain_name;
	}
	public String getProblemdomain_shortname() {
		return problemdomain_shortname;
	}
	public void setProblemdomain_shortname(String problemdomain_shortname) {
		this.problemdomain_shortname = problemdomain_shortname;
	}
	public String getProblemdomain_type() {
		return problemdomain_type;
	}
	public void setProblemdomain_type(String problemdomain_type) {
		this.problemdomain_type = problemdomain_type;
	}
	public String getProblemdomain_property() {
		if(problemdomain_property==null) {
			problemdomain_property="GivenDomain";
		}
		return problemdomain_property;
	}
	public void setProblemdomain_property(String problemdomain_property) {
		this.problemdomain_property = problemdomain_property;
	}
	public int getProblemdomain_x() {
		return problemdomain_x;
	}
	public void setProblemdomain_x(int problemdomain_x) {
		this.problemdomain_x = problemdomain_x;
	}
	public int getProblemdomain_y() {
		return problemdomain_y;
	}
	public void setProblemdomain_y(int problemdomain_y) {
		this.problemdomain_y = problemdomain_y;
	}
	public int getProblemdomain_h() {
		return problemdomain_h;
	}
	public void setProblemdomain_h(int problemdomain_h) {
		this.problemdomain_h = problemdomain_h;
	}
	public int getProblemdomain_w() {
		return problemdomain_w;
	}
	public void setProblemdomain_w(int problemdomain_w) {
		this.problemdomain_w = problemdomain_w;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getNo() {
		return problemdomain_no;
	}

	public void setNo(int no) {
		this.problemdomain_no = no;
	}

	public String getName() {
		return problemdomain_name;
	}

	public void setName(String name) {
		this.problemdomain_name = name;
	}

	public String getShortname() {
		return problemdomain_shortname;
	}

	public void setShortname(String shortname) {
		this.problemdomain_shortname = shortname;
	}

	public String getType() {
		return problemdomain_type;
	}

	public void setType(String type) {
		this.problemdomain_type = type;
	}

	public String getProperty() {
		return problemdomain_property;
	}

	public void setProperty(String property) {
		this.problemdomain_property = property;
	}

	public int getX() {
		return problemdomain_x;
	}

	public void setX(int x) {
		this.problemdomain_x = x;
	}

	public int getY() {
		return problemdomain_y;
	}

	public void setY(int y) {
		this.problemdomain_y = y;
	}

	public int getH() {
		return problemdomain_h;
	}

	public void setH(int h) {
		this.problemdomain_h = h;
	}

	public int getW() {
		return problemdomain_w;
	}

	public void setW(int w) {
		this.problemdomain_w = w;
	}
}
