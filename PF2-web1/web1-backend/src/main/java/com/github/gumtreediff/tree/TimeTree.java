package com.github.gumtreediff.tree;

public class TimeTree extends Tree {

	long time;
	boolean isDelete;

	String label;

	// Begin position of the tree in terms of absolute character index and length
	int pos;
	int length;
	// End position

	AssociationMap metadata;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public TimeTree(int type, String label, long time) {
		super(type, label);
		this.time = time;
	}

	public TimeTree(int type, String label, long time, int id) {
		super(type, label);
		this.time = time;
		this.id = id;
	}

	// Only used for cloning ...
	protected TimeTree(Tree other, long time) {
		super(other);
		this.time = time;
	}

	// Only used for cloning ...
	public TimeTree(TimeTree other) {
		super(other);
		this.time = other.time;
		this.isDelete = other.isDelete;
	}

	@Override
	public TimeTree deepCopy() {
		TimeTree copy = new TimeTree(this);
		for (ITree child : getChildren())
			copy.addChild(child.deepCopy());
		return copy;
	}

	public static TimeTree deepCopy(Tree tree, long time) {
		TimeTree copy = new TimeTree(tree, time);
		for (ITree child : tree.getChildren())
			copy.addChild(deepCopy((Tree) child, time));
		return copy;
	}

	@Override
	public String toString() {
		return toShortString();
	}

	public String toPrettyString1(TreeContext ctx) {
		String str = "";
		if (hasLabel())
			str = ctx.getTypeLabel(this) + ", " + getLabel() + ", " + getId() + ", " + getTime() + ", " + isDelete();
		else
			str = ctx.getTypeLabel(this) + ", " + getId() + ", " + getTime() + ", " + isDelete();
		str += ",	depth:" + this.getDepth();
		str += ",	height:" + this.getHeight();
		return str;
	}

	public String toPrettyString2(TreeContext ctx) {
		String str = "";
		if (hasLabel())
			str = ctx.getTypeLabel(this) + ", " + getLabel() + ", " + getId() + ", " + getTime() + ", " + isDelete();
		else
			str = ctx.getTypeLabel(this) + ", " + getId() + ", " + getTime() + ", " + isDelete();

		return str;
	}
	public String toPrettyString(TreeContext ctx) {
		String str = "";
		if (hasLabel())
			str = ctx.getTypeLabel(this) + ", " + getLabel() + ", " + getId();
		else
			str = ctx.getTypeLabel(this) + ", " + getId();

		return str;
	}
	public String toPrettyString3(TreeContext ctx) {
		String str = "";
		if (hasLabel())
			str = ctx.getTypeLabel(this) + ", " + getLabel() + ", " + getId() + ", " + getTime();
		else
			str = ctx.getTypeLabel(this) + ", " + getId() + ", " + getTime();

		return str;
	}

	// used for git in subject
	@Override
	public String toTreeString() {
		StringBuilder b = new StringBuilder();
		for (ITree t : TreeUtils.preOrder(this))
			b.append(indent(t) + ((TimeTree) t).toFileString() + "\n");
		return b.toString();
	}

	private String indent(ITree t) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < t.getDepth(); i++)
			b.append("\t");
		return b.toString();
	}

	/**
	 * @Title: toFileString
	 * @Description: to string for writing to file,
	 * TypeLabel，Label，id , time, parentid \n符
	 * use these ,can rebuild Tree
	 * @param @param ctx
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public String toFileString() {
		String str = "";
		str = getType() + ",	" + getLabel() + ",	" + getId() + ",	" + getTime() + ",	";
		if (getParent() != null)
			str += getParent().getId();
		else
			str += -1;
		str += ", " + isDelete();
		return str;
	}
}
