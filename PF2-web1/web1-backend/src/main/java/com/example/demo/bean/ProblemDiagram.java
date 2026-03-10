package com.example.demo.bean;

import java.util.ArrayList;
import java.util.List;

public class ProblemDiagram {
	private String title;	//文件名
	private ContextDiagram contextDiagram;	//上下文图
	private List<Requirement> requirementList;	//需求列表
	private List<Constraint> constraintList;	//约束列表
	private List<Reference> referenceList;	//引用列表
	static public ProblemDiagram copyProblemDiagram(ProblemDiagram pd) {
		ProblemDiagram problemDiagram = new ProblemDiagram();
		problemDiagram.setTitle(pd.getTitle());
		problemDiagram.copyRequirementList(pd.getRequirementList());
		problemDiagram.copyReferenceList(pd.getReferenceList());
		problemDiagram.copyConstraintList(pd.getConstraintList());

		return problemDiagram;
	}

	public boolean copyConstraintList(List<Constraint> conList) {
		if(constraintList == null){
			constraintList = new ArrayList<Constraint>();
		}
		for (Constraint con : conList)
			constraintList.add(new Constraint(con));
		return true;
	}

	public boolean copyReferenceList(List<Reference> refList) {
		if(referenceList == null){
			referenceList = new ArrayList<Reference>();
		}
		for (Reference ref : refList)
			referenceList.add(new Reference(ref));
		return true;
	}

	public boolean copyRequirementList(List<Requirement> reqList) {
		if(requirementList == null) {
			requirementList = new ArrayList<Requirement>();
		}
		for (Requirement req : reqList)
			requirementList.add(new Requirement(req));
		return true;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ContextDiagram getContextDiagram() {
		return contextDiagram;
	}
	public void setContextDiagram(ContextDiagram contextDiagram) {
		this.contextDiagram = contextDiagram;
	}
	public List<Requirement> getRequirementList() {
		return requirementList;
	}
	public void setRequirementList(List<Requirement> requirementList) {
		this.requirementList = requirementList;
	}
	public List<Constraint> getConstraintList() {
		return constraintList;
	}
	public void setConstraintList(List<Constraint> constraintList) {
		this.constraintList = constraintList;
	}
	public List<Reference> getReferenceList() {
		return referenceList;
	}
	public void setReferenceList(List<Reference> referenceList) {
		this.referenceList = referenceList;
	}
}
