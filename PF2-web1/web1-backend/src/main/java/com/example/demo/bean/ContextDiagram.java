package com.example.demo.bean;

import java.util.ArrayList;
import java.util.List;

public class ContextDiagram {
	private String title;	//文件名
	private Machine machine;	//机器
	private List<ProblemDomain> problemDomainList;	//领域列表
	private List<Interface> interfaceList;	//交互列表
	static public ContextDiagram copyContextDiagram(ContextDiagram CD) {
		ContextDiagram contextDiagram = new ContextDiagram();
		contextDiagram.setTitle(CD.getTitle());
		contextDiagram.setMachine(new Machine(CD.getMachine()));
		contextDiagram.copyProblemDomainList(CD.getProblemDomainList());
		contextDiagram.copyInterfaceList(CD.getInterfaceList());
		return contextDiagram;
	}
	public boolean copyProblemDomainList(List<ProblemDomain> pdList) {
		if(problemDomainList == null){
			problemDomainList = new ArrayList<ProblemDomain>();
		}
		if(pdList !=null) {
			for (ProblemDomain pd : pdList) {
				problemDomainList.add(new ProblemDomain(pd));
			}
		}
		return true;
	}

	public boolean copyInterfaceList(List<Interface> intList) {
		if(interfaceList == null){
			interfaceList = new ArrayList<Interface>();
		}
		if(intList != null) {
			for (Interface iint : intList) {
				interfaceList.add(new Interface(iint));
			}
		}
		return true;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Machine getMachine() {
		if(machine == null){
			return null;
		}
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
	public List<Interface> getInterfaceList() {
		return interfaceList;
	}
	public void setInterfaceList(List<Interface> interfaceList) {
		this.interfaceList = interfaceList;
	}
}
