package com.example.demo.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntClass;

public class MyOntClass {
	Integer id; // 唯一标识符id
	String name; // class名称
	String type; // class类型
	Boolean isdynamic;// 是否有状态机
	String TA_name;// 状态机名称
	ArrayList<String> states;// 状态机状态集合
	//	ArrayList<String> opts;// 状态机操作集合
	ArrayList<String> instructions;
	ArrayList<String> signals;
	ArrayList<String> values; // 静态属性值
	//	private List<OntClass> stateMachine;	//状态机
	private List<OntClass> TimedAutomaton;
//	private List<OntClass> IOAutomata;	//输入输出状态机

	public MyOntClass() {
		this.name = null;
		this.type = null;
		this.isdynamic = false;
		this.TA_name = null;
		this.states = new ArrayList<String>();
		//this.opts = new ArrayList<String>();
		this.values = new ArrayList<String>();
		this.instructions = new ArrayList<String>();
		this.signals = new ArrayList<String>();
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}
	public void addValues(ArrayList<String> values) {
		for (String value : values)
			this.values.add(value);
	}
	public ArrayList<String> getValues() {
		return values;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsdynamic() {
		return isdynamic;
	}

	public void setIsdynamic(Boolean isdynamic) {
		this.isdynamic = isdynamic;
	}

	public String getTA_name() {
		return TA_name;
	}

	public void setTA_name(String sM_name) {
		TA_name = sM_name;
	}

	public ArrayList<String> getStates() {
		return states;
	}

	public void setStates(ArrayList<String> states) {
		this.states = states;
	}
	public void addStates(ArrayList<String> states) {
		for (String state : states)
			this.states.add(state);
	}
//	public ArrayList<String> getOpts() {
//		return opts;
//	}
//
//	public void setOpts(ArrayList<String> opts) {
//		this.opts = opts;
//	}

	public ArrayList<String> getInstructions() {
		return instructions;
	}

	public void setInstructions(ArrayList<String> instructions) {
		this.instructions = instructions;
	}
	public ArrayList<String> getSignals() {
		return signals;
	}

	public void setSignals(ArrayList<String> signals) {
		this.signals = signals;
	}
	//	public void addOpts(ArrayList<String> opts) {
//		for (String opt : opts)
//			this.opts.add(opt);
//	}
	public void addSignals(ArrayList<String> signals) {
		for (String signal : signals)
			this.signals.add(signal);
	}
	public void addInstructions(ArrayList<String> instructions) {
		for (String instruction : instructions)
			this.instructions.add(instruction);
	}
	public boolean hasValues() {
		if (this.values.size() == 0)
			return false;
		else
			return true;
	}

	public ArrayList<String> getPhes() {
		ArrayList<String> phes = new ArrayList<String>();
		for (int i = 0; i < this.getValues().size(); i++) {
			phes.add(this.getValues().get(i));
		}
		for (int i = 0; i < this.getStates().size(); i++) {
			phes.add(this.getStates().get(i));
		}
//		for (int i = 0; i < this.getOpts().size(); i++) {
//			phes.add(this.getOpts().get(i));
//		}
		for (int i = 0; i < this.getInstructions().size(); i++) {
			phes.add(this.getInstructions().get(i));
		}
		for (int i = 0; i < this.getSignals().size(); i++) {
			phes.add(this.getSignals().get(i));
		}
		return phes;
	}

	//	public List<OntClass> getStateMachine() {
//		return stateMachine;
//	}
	public List<OntClass> getTimedAutomaton() {
		return TimedAutomaton;
	}
//	public List<OntClass> getIOAutomata() {
//		return IOAutomata;
//	}

	public void setTimedAutomaton(List<OntClass> timedAutomaton) {
		TimedAutomaton = timedAutomaton;
	}
}
