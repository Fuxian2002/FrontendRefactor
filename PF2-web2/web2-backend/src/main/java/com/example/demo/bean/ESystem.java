package com.example.demo.bean;

public class ESystem extends PdNode{
	private String system_name;	//机器名称
	private String system_shortName;	//名称缩写
	private int system_x;	//位置信息
	private int system_y;
	private int system_h;
	private int system_w;
	public ESystem() {
		super();
	}

	public ESystem(String name, String shortname, int x, int y, int w, int h) {
		this.system_name = name;
		this.system_shortName = shortname;
		this.system_x = x;
		this.system_y = y;
		this.system_w = w;
		this.system_h = h;
	}

	public ESystem(ESystem old) {
		if (old == null)
			return;
		this.system_name = old.system_name;
		this.system_shortName = old.system_shortName;
		this.system_x = old.system_x;
		this.system_y = old.system_y;
		this.system_w = old.system_w;
		this.system_h = old.system_h;
	}
	public String getSystem_name() {
		return system_name;
	}
	public void setSystem_name(String system_name) {
		this.system_name = system_name;
	}
	public String getSystem_shortName() {
		return system_shortName;
	}
	public void setSystem_shortName(String system_shortName) {
		this.system_shortName = system_shortName;
	}
	public int getSystem_x() {
		return system_x;
	}
	public void setSystem_x(int system_x) {
		this.system_x = system_x;
	}
	public int getSystem_y() {
		return system_y;
	}
	public void setSystem_y(int system_y) {
		this.system_y = system_y;
	}
	public int getSystem_h() {
		return system_h;
	}
	public void setSystem_h(int system_h) {
		this.system_h = system_h;
	}
	public int getSystem_w() {
		return system_w;
	}
	public void setSystem_w(int system_w) {
		this.system_w = system_w;
	}
	public String getShortname() {
		return system_shortName;
	}

	public void setShortname(String shortname) {
		this.system_shortName = shortname;
	}

	public int getX() {
		return system_x;
	}

	public void setX(int x) {
		this.system_x = x;
	}

	public int getY() {
		return system_y;
	}

	public void setY(int y) {
		this.system_y = y;
	}

	@Override
	public int getNo() {
		return 0;
	}

	@Override
	public void setNo(int node_no) {
	}

	public String getName() {
		return system_name;
	}

	public void setName(String name) {
		this.system_name = name;
	}

	public int getH() {
		return system_h;
	}

	public void setH(int h) {
		this.system_h = h;
	}

	public int getW() {
		return system_w;
	}

	public void setW(int w) {
		this.system_w = w;
	}
}
