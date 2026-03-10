package com.example.demo.bean;

public class Machine extends PdNode{
	private String machine_name;	//机器名称
	private String machine_shortName;	//名称缩写
	private int machine_x;	//位置信息
	private int machine_y;
	private int machine_h;
	private int machine_w;
	public Machine() {
		super();
	}

	public Machine(String name, String shortname, int x, int y, int w, int h) {
		this.machine_name = name;
		this.machine_shortName = shortname;
		this.machine_x = x;
		this.machine_y = y;
		this.machine_w = w;
		this.machine_h = h;
	}

	public Machine(Machine old) {
		if (old == null)
			return;
		this.machine_name = old.machine_name;
		this.machine_shortName = old.machine_shortName;
		this.machine_x = old.machine_x;
		this.machine_y = old.machine_y;
		this.machine_w = old.machine_w;
		this.machine_h = old.machine_h;
	}
	public String getMachine_name() {
		return machine_name;
	}
	public void setMachine_name(String machine_name) {
		this.machine_name = machine_name;
	}
	public String getMachine_shortName() {
		return machine_shortName;
	}
	public void setMachine_shortName(String machine_shortName) {
		this.machine_shortName = machine_shortName;
	}
	public int getMachine_x() {
		return machine_x;
	}
	public void setMachine_x(int machine_x) {
		this.machine_x = machine_x;
	}
	public int getMachine_y() {
		return machine_y;
	}
	public void setMachine_y(int machine_y) {
		this.machine_y = machine_y;
	}
	public int getMachine_h() {
		return machine_h;
	}
	public void setMachine_h(int machine_h) {
		this.machine_h = machine_h;
	}
	public int getMachine_w() {
		return machine_w;
	}
	public void setMachine_w(int machine_w) {
		this.machine_w = machine_w;
	}
	public String getShortname() {
		return machine_shortName;
	}

	public void setShortname(String shortname) {
		this.machine_shortName = shortname;
	}

	public int getX() {
		return machine_x;
	}

	public void setX(int x) {
		this.machine_x = x;
	}

	public int getY() {
		return machine_y;
	}

	public void setY(int y) {
		this.machine_y = y;
	}

	@Override
	public int getNo() {
		return 0;
	}

	@Override
	public void setNo(int node_no) {
	}

	public String getName() {
		return machine_name;
	}

	public void setName(String name) {
		this.machine_name = name;
	}

	public int getH() {
		return machine_h;
	}

	public void setH(int h) {
		this.machine_h = h;
	}

	public int getW() {
		return machine_w;
	}

	public void setW(int w) {
		this.machine_w = w;
	}
}
