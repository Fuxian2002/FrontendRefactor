package com.example.demo.bean;

import java.util.List;

public class Project {
	private long time = 0;
	private String title;	//项目名
	private IntentDiagram intentDiagram;  //任务意图
	private ContextDiagram contextDiagram;	//上下文图
	private ProblemDiagram problemDiagram;	//问题图
	private List<ScenarioGraph> scenarioGraphList; 	//情景图
	private ScenarioGraph fullScenarioGraph; 	//完整的情景图（貌似没有使用，一直为空）
	private List<ScenarioGraph> newScenarioGraphList; 	//分解得到的情景图（貌似没有使用，一直为空）
	private List<SubProblemDiagram> subProblemDiagramList;	//子问题图
	private List<Trace> traceList;  // 追溯关系
	private List<Dependence> dataDependenceList;  // 数据依赖
	private List<Dependence> controlDependenceList;  // 控制依赖
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public IntentDiagram getIntentDiagram() {
		return intentDiagram;
	}
	public void setIntentDiagram(IntentDiagram intentDiagram) {
		this.intentDiagram = intentDiagram;
	}
	public ContextDiagram getContextDiagram() {
		return contextDiagram;
	}
	public void setContextDiagram(ContextDiagram contextDiagram) {
		this.contextDiagram = contextDiagram;
	}
	public ProblemDiagram getProblemDiagram() {
		return problemDiagram;
	}
	public void setProblemDiagram(ProblemDiagram problemDiagram) {
		this.problemDiagram = problemDiagram;
	}
	public List<ScenarioGraph> getScenarioGraphList() {
		return scenarioGraphList;
	}
	public void setScenarioGraphList(List<ScenarioGraph> scenarioGraphList) {
		this.scenarioGraphList = scenarioGraphList;
	}
	public List<SubProblemDiagram> getSubProblemDiagramList() {
		return subProblemDiagramList;
	}
	public void setSubProblemDiagramList(List<SubProblemDiagram> subProblemDiagramList) {
		this.subProblemDiagramList = subProblemDiagramList;
	}
	public ScenarioGraph getFullScenarioGraph() {
		return fullScenarioGraph;
	}
	public void setFullScenarioGraph(ScenarioGraph fullScenarioGraph) {
		this.fullScenarioGraph = fullScenarioGraph;
	}
	public List<ScenarioGraph> getNewScenarioGraphList() {
		return newScenarioGraphList;
	}
	public void setNewScenarioGraphList(List<ScenarioGraph> newScenarioGraphList) {
		this.newScenarioGraphList = newScenarioGraphList;
	}
	public List<Trace> getTraceList() {
		return traceList;
	}

	public void setTraceList(List<Trace> traceList) {
		this.traceList = traceList;
	}

	public List<Dependence> getDataDependenceList() {
		return dataDependenceList;
	}

	public void setDataDependenceList(List<Dependence> dataDependenceList) {
		this.dataDependenceList = dataDependenceList;
	}

	public List<Dependence> getControlDependenceList() {
		return controlDependenceList;
	}

	public void setControlDependenceList(List<Dependence> controlDependenceList) {
		this.controlDependenceList = controlDependenceList;
	}
	
}
