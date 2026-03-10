package com.example.demo.bean;

import java.util.List;

public class RUCM {
	private String useCaseName;
	private String briefDescription;
	private String precondition;
	private String primaryActor;
	private List<String> secondaryActors;
	private String dependency;
	private String generalization;
	private BasicFlow basicflow;
	private List<AlternativeFlow> alternativeFlows;
	
	public String getUseCaseName() {
		return useCaseName;
	}
	public void setUseCaseName(String useCaseName) {
		this.useCaseName = useCaseName;
	}
	public String getBriefDescription() {
		return briefDescription;
	}
	public void setBriefDescription(String briefDescription) {
		this.briefDescription = briefDescription;
	}
	public String getPrecondition() {
		return precondition;
	}
	public void setPrecondition(String precondition) {
		this.precondition = precondition;
	}
	public String getPrimaryActor() {
		return primaryActor;
	}
	public void setPrimaryActor(String primaryActor) {
		this.primaryActor = primaryActor;
	}
	public List<String> getSecondaryActors() {
		return secondaryActors;
	}
	public void setSecondaryActors(List<String> secondaryActors) {
		this.secondaryActors = secondaryActors;
	}
	public String getDependency() {
		return dependency;
	}
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
	public String getGeneralization() {
		return generalization;
	}
	public void setGeneralization(String generalization) {
		this.generalization = generalization;
	}
	public BasicFlow getBasicflow() {
		return basicflow;
	}
	public void setBasicflow(BasicFlow basicflow) {
		this.basicflow = basicflow;
	}
	public List<AlternativeFlow> getAlternativeFlows() {
		return alternativeFlows;
	}
	public void setAlternativeFlows(List<AlternativeFlow> alternativeFlows) {
		this.alternativeFlows = alternativeFlows;
	}
}
