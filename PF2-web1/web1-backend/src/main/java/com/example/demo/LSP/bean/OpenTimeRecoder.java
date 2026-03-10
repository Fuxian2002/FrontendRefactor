package com.example.demo.LSP.bean;

public class OpenTimeRecoder {
	String uri;
	long T0;
	long T1;
	int nodeNumber;
	int linkNumber;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public long getT0() {
		return T0;
	}

	public void setT0(long t0) {
		T0 = t0;
	}

	public long getT1() {
		return T1;
	}

	public void setT1(long t1) {
		T1 = t1;
	}

	public OpenTimeRecoder(String uri, long t0, long t1) {
		super();
		this.uri = uri;
		T0 = t0;
		T1 = t1;
	}

	public OpenTimeRecoder(String uri, long t0, long t1, int nodeNumber) {
		super();
		this.uri = uri;
		T0 = t0;
		T1 = t1;
		this.nodeNumber = nodeNumber;
	}

	public OpenTimeRecoder(String uri, long t0, long t1, int nodeNumber, int linkNumber) {
		super();
		this.uri = uri;
		T0 = t0;
		T1 = t1;
		this.nodeNumber = nodeNumber;
		this.linkNumber = linkNumber;
	}

	public String toString() {
		long time = T1 - T0;
		return uri + ",\t" + nodeNumber + ",\t" + linkNumber + ",\t" + T0 + ",\t" + T1 + ",\t" + time;
	}

	public String toString2() {
		long time = T1 - T0;
		return uri + ",\t" + nodeNumber + ",\t" + T0 + ",\t" + T1 + ",\t" + time;
	}

	public String toString1() {
		long time = T1 - T0;
		return uri + ",\t" + T0 + ",\t" + T1 + ",\t" + time;
	}
}
