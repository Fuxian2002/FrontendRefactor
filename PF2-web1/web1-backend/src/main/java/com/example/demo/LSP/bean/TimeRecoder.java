package com.example.demo.LSP.bean;

public class TimeRecoder {

	String uri;
	String lastVersion;
	long T0;
	long T1;
	long T2;
	long T3;
	long T4;
	long Flag1;
	long Flag2;
	long Flag3;
	int nodeNumber;
	int linkNumber;
	private int pfLen;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLastVersion() {
		return lastVersion;
	}

	public void setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
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

	public long getT2() {
		return T2;
	}

	public void setT2(long t2) {
		T2 = t2;
	}

	public long getT3() {
		return T3;
	}

	public void setT3(long t3) {
		T3 = t3;
	}

	public long getT4() {
		return T4;
	}

	public void setT4(long t4) {
		T4 = t4;
	}

	public TimeRecoder(String uri, String lastVersion, long t0, long t1, long t2, long t3, long t4) {
		super();
		this.uri = uri;
		this.lastVersion = lastVersion;
		T0 = t0;
		T1 = t1;
		T2 = t2;
		T3 = t3;
		T4 = t4;
	}

	public TimeRecoder(String uri, String lastVersion, long t0, long t1, long t2, long t3, long t4, long flag1,
			long flag2, long flag3) {
		this(uri, lastVersion, t0, t1, t2, t3, t4);
		Flag1 = flag1;
		Flag2 = flag2;
		Flag3 = flag3;
	}

	public TimeRecoder(String uri, String lastVersion, long t0, long t1, long t2, long t3, long t4, int nodeNumber) {
		super();
		this.uri = uri;
		this.lastVersion = lastVersion;
		T0 = t0;
		T1 = t1;
		T2 = t2;
		T3 = t3;
		T4 = t4;
		this.nodeNumber = nodeNumber;
	}

	public TimeRecoder(String uri, String lastVersion, long t0, long t1, long t2, long t3, long t4, int nodeNumber,
			int linkNumber) {
		super();
		this.uri = uri;
		this.lastVersion = lastVersion;
		T0 = t0;
		T1 = t1;
		T2 = t2;
		T3 = t3;
		T4 = t4;
		this.nodeNumber = nodeNumber;
		this.linkNumber = linkNumber;
	}

	public TimeRecoder(String uri, String lastVersion, int nodeNumber, int linkNumber, int pfLen, long t0, long t1,
			long t2, long t3, long t4) {
		super();
		this.uri = uri;
		this.lastVersion = lastVersion;
		this.nodeNumber = nodeNumber;
		this.linkNumber = linkNumber;
		this.pfLen = pfLen;
		T0 = t0;
		T1 = t1;
		T2 = t2;
		T3 = t3;
		T4 = t4;
	}

	public String toString() {
		long time = T4 - T0;
		return uri + ",\t" + lastVersion + ",\t" + nodeNumber + ",\t" + linkNumber + ",\t" + pfLen + ",\t" + T0 + ",\t"
				+ T4 + ",\t" + time;
	}

	public String toString3() {
		long time = T4 - T0;
		return uri + ",\t" + lastVersion + ",\t" + nodeNumber + ",\t" + linkNumber + ",\t" + T0 + ",\t" + T4 + ",\t"
				+ time;
	}

	public String toString2() {
		long time = T4 - T0;
		return uri + ",\t" + lastVersion + ",\t" + T0 + ",\t" + T1 + ",\t" + T2 + ",\t" + T3 + ",\t" + T4 + ",\t"
				+ time;
	}

	public String toString1() {
		long time = T4 - T0;
		return uri + "," + lastVersion + "同步共用" + time + "ms\n" + T0 + "," + T1 + "," + T2 + "," + T3 + "," + T4;
	}
}
