package com.example.demo.bean;

import java.util.ArrayList;
import java.util.List;

public class IntentDiagram {
	private String title;	//文件名
	private ESystem system;	//机器
	private List<ExternalEntity> externalEntityList;	//领域列表

	private List<Intent> intentList;
	private List<Interface> interfaceList;	//交互列表

	private List<Reference> referenceList;

	private List<Constraint> constraintList;
	static public IntentDiagram copyIntentDiagram(IntentDiagram ID) {
		IntentDiagram intentDiagram = new IntentDiagram();
		if(ID == null){
			return intentDiagram;
		}
		intentDiagram.setTitle(ID.getTitle());
		intentDiagram.setSystem(new ESystem(ID.getSystem()));
		intentDiagram.copyExternalEntityList(ID.getExternalEntityList());
		intentDiagram.copyInterfaceList(ID.getInterfaceList());
		intentDiagram.copyIntentList(ID.getIntentList());
		intentDiagram.copyReferenceList(ID.getReferenceList());
		intentDiagram.copyConstraintList(ID.getConstraintList());
		return intentDiagram;
	}
	public boolean copyExternalEntityList(List<ExternalEntity> pdList) {
		if(externalEntityList == null){
			externalEntityList = new ArrayList<ExternalEntity>();
		}
		if(pdList !=null) {
			for (ExternalEntity pd : pdList) {
				externalEntityList.add(new ExternalEntity(pd));
			}
		}
		return true;
	}
	public boolean copyIntentList(List<Intent> reqList) {
		if(intentList == null) {
			intentList = new ArrayList<Intent>();
		}
		for (Intent req : reqList)
			intentList.add(new Intent(req));
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ESystem getSystem() {
		if(system == null){
			return null;
		}
		return system;
	}
	public void setSystem(ESystem system) {
		this.system = system;
	}
	public List<Intent> getIntentList() {
		return intentList;
	}

	public void setIntentList(List<Intent> intentList) {
		this.intentList = intentList;
	}

	public List<ExternalEntity> getExternalEntityList() {
		return externalEntityList;
	}
	public void setExternalEntityList(List<ExternalEntity> externalEntityList) {
		this.externalEntityList = externalEntityList;
	}
	public List<Interface> getInterfaceList() {
		return interfaceList;
	}
	public void setInterfaceList(List<Interface> interfaceList) {
		this.interfaceList = interfaceList;
	}
	public List<Reference> getReferenceList() {
		return referenceList;
	}
	public void setReferenceList(List<Reference> referenceList) {
		this.referenceList = referenceList;
	}
	public List<Constraint> getConstraintList() {
		return constraintList;
	}
	public void setConstraintList(List<Constraint> constraintList) {
		this.constraintList = constraintList;
	}

}
