package com.example.demo.bean;

public class Intent extends PdNode{
	private int intent_no;	//需求编号
	private String intent_context;	//需求描述
	private String intent_shortname;
	private int intent_x;	//位置信息
	private int intent_y;
	private int intent_h;
	private int intent_w;
	public Intent(Intent item) {
		intent_no = item.intent_no;
		intent_context = item.intent_context;
		intent_shortname = item.intent_shortname;
		intent_x = item.intent_x;
		intent_y = item.intent_y;
		intent_h = item.intent_h;
		intent_w = item.intent_w;
	}

	public Intent() {
		super();
	}

	public int getIntent_no() {
		return intent_no;
	}
	public void setIntent_no(int intent_no) {
		this.intent_no = intent_no;
	}
	public String getIntent_context() {
		return intent_context;
	}
	public void setIntent_context(String intent_context) {
		this.intent_context = intent_context;
	}
	public String getIntent_shortname() {
		return intent_shortname;
	}
	public void setIntent_shortname(String intent_shortname) {
		this.intent_shortname = intent_shortname;
	}
	public int getIntent_x() {
		return intent_x;
	}
	public void setIntent_x(int intent_x) {
		this.intent_x = intent_x;
	}
	public int getIntent_y() {
		return intent_y;
	}
	public void setIntent_y(int intent_y) {
		this.intent_y = intent_y;
	}
	public int getIntent_h() {
		return intent_h;
	}
	public void setIntent_h(int intent_h) {
		this.intent_h = intent_h;
	}
	public int getIntent_w() {
		return intent_w;
	}
	public void setIntent_w(int intent_w) {
		this.intent_w = intent_w;
	}
	public int getNo() {
		return intent_no;
	}

	public void setNo(int no) {
		this.intent_no = no;
	}

	public String getName() {
		return intent_context;
	}

	public void setName(String name) {
		this.intent_context = name;
	}

	public String getShortname() {
		return intent_shortname;
	}

	public void setShortname(String shortname) {
		this.intent_shortname = shortname;
	}

	public int getX() {
		return intent_x;
	}

	public void setX(int x) {
		this.intent_x = x;
	}

	public int getY() {
		return intent_y;
	}

	public void setY(int y) {
		this.intent_y = y;
	}

	public int getH() {
		return intent_h;
	}

	public void setH(int h) {
		this.intent_h = h;
	}

	public int getW() {
		return intent_w;
	}

	public void setW(int w) {
		this.intent_w = w;
	}
}
