package com.example.demo.bean;

import java.util.ArrayList;

public class ExternalEntity extends PdNode{
	private int externalentity_no;
	private String externalentity_name;
	private String externalentity_shortname;
	private int externalentity_x;
	private int externalentity_y;
	private int externalentity_h;
	private int externalentity_w;
	private ArrayList<Phenomenon> phes;
//	private ArrayList<IntentPhenomenon> phes;
	public ExternalEntity() {
		super();
	}

	public ExternalEntity(ExternalEntity item) {
		externalentity_no = item.externalentity_no;
		externalentity_name = item.externalentity_name;
		externalentity_shortname = item.externalentity_shortname;
		externalentity_x = item.externalentity_x;
		externalentity_y = item.externalentity_y;
		externalentity_h = item.externalentity_h;
		externalentity_w = item.externalentity_w;
	}
	public void setPhes(ArrayList<Phenomenon> phes){
		this.phes = phes;
	}
	public ArrayList<Phenomenon> getPhes(){
		return this.phes;
	}
	public int getExternalentity_no() {
		return externalentity_no;
	}
	public void setExternalentity_no(int externalentity_no) {
		this.externalentity_no = externalentity_no;
	}
	public String getExternalentity_name() {
		return externalentity_name;
	}
	public void setExternalentity_name(String externalentity_name) {
		this.externalentity_name = externalentity_name;
	}
	public String getExternalentity_shortname() {
		return externalentity_shortname;
	}
	public void setExternalentity_shortname(String externalentity_shortname) {
		this.externalentity_shortname = externalentity_shortname;
	}
	public int getExternalentity_x() {
		return externalentity_x;
	}
	public void setExternalentity_x(int externalentity_x) {
		this.externalentity_x = externalentity_x;
	}
	public int getExternalentity_y() {
		return externalentity_y;
	}
	public void setExternalentity_y(int externalentity_y) {
		this.externalentity_y = externalentity_y;
	}
	public int getExternalentity_h() {
		return externalentity_h;
	}
	public void setExternalentity_h(int externalentity_h) {
		this.externalentity_h = externalentity_h;
	}
	public int getExternalentity_w() {
		return externalentity_w;
	}
	public void setExternalentity_w(int externalentity_w) {
		this.externalentity_w = externalentity_w;
	}
	public int getNo() {
		return externalentity_no;
	}

	public void setNo(int no) {
		this.externalentity_no = no;
	}

	public String getName() {
		return externalentity_name;
	}

	public void setName(String name) {
		this.externalentity_name = name;
	}

	public String getShortname() {
		return externalentity_shortname;
	}

	public void setShortname(String shortname) {
		this.externalentity_shortname = shortname;
	}

	public int getX() {
		return externalentity_x;
	}

	public void setX(int x) {
		this.externalentity_x = x;
	}

	public int getY() {
		return externalentity_y;
	}

	public void setY(int y) {
		this.externalentity_y = y;
	}

	public int getH() {
		return externalentity_h;
	}

	public void setH(int h) {
		this.externalentity_h = h;
	}

	public int getW() {
		return externalentity_w;
	}

	public void setW(int w) {
		this.externalentity_w = w;
	}
}
