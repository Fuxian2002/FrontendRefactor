package com.example.demo.bean;

import java.util.List;

public class SubProblemDiagram {
	private String title;	//文件名
	private Machine machine;	//机器
	private List<ProblemDomain> problemDomainList;	//领域列表
	private Requirement requirement;	//需求
	private List<Interface> interfaceList;	//交互列表
	private List<Constraint> constraintList;	//约束列表
	private List<Reference> referenceList;	//引用列表
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Machine getMachine() {
		return machine;
	}
	public void setMachine(Machine machine) {
		this.machine = machine;
	}
	public List<ProblemDomain> getProblemDomainList() {
		return problemDomainList;
	}
	public void setProblemDomainList(List<ProblemDomain> problemDomainList) {
		this.problemDomainList = problemDomainList;
	}
	public Requirement getRequirement() {
		return requirement;
	}
	public void setRequirement(Requirement requirement) {
		this.requirement = requirement;
	}
	public List<Interface> getInterfaceList() {
		return interfaceList;
	}
	public void setInterfaceList(List<Interface> interfaceList) {
		this.interfaceList = interfaceList;
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
