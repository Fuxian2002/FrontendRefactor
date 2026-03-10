package com.example.demo.LSP.bean;

import com.example.demo.bean.Shape;

public class DiagramContentChangeEvent {
	String shapeType;
	String changeType;
	Shape oldShape;
	Shape newShape;

	public DiagramContentChangeEvent(String shapeType, String changeType, Shape oldShape, Shape newShape) {
		super();
		this.shapeType = shapeType;
		this.changeType = changeType;
		this.oldShape = oldShape;
		this.newShape = newShape;
		System.out.println("DiagramContentChangeEvent: " +
				"shapeType: "+shapeType+"\n----------------changeType: "+changeType);
	}

	public String getShapeType() {
		return shapeType;
	}

	public void setShapeType(String shapeType) {
		this.shapeType = shapeType;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public Shape getOldShape() {
		return oldShape;
	}

	public void setOldShape(Shape oldShape) {
		this.oldShape = oldShape;
	}

	public Shape getNewShape() {
		return newShape;
	}

	public void setNewShape(Shape newShape) {
		this.newShape = newShape;
	}

}
