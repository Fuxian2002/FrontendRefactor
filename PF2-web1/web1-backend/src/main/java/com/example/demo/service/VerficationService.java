package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.example.demo.bean.CCSLSet;
import com.example.demo.bean.Constraint;
import com.example.demo.bean.CtrlNode;
import com.example.demo.bean.Interface;
import com.example.demo.bean.Line;
import com.example.demo.bean.Node;
import com.example.demo.bean.Phenomenon;
import com.example.demo.bean.Project;
import com.example.demo.bean.ScenarioGraph;

@Service
public class VerficationService {

	public List<CCSLSet> toCCSL(Project project, String address) {
		List<CCSLSet> ccslset = new ArrayList<CCSLSet>();
		// 测试接口
//		List<String> ccslList = new ArrayList<String>();
//		ccslList.add("abc");
//		ccslList.add("def");
//		CCSLSet ccsl = new CCSLSet("CCSL_1", ccslList);
//		ccslset.add(ccsl);
		List<Phenomenon> phenomenonList = getPhenomenonList(project);
		List<ScenarioGraph> scenarioGraphList = project.getNewScenarioGraphList();
		// 每一张情景图转换成一个CCSLSet
		int ccslNo = 1;
		for(ScenarioGraph scenarioGraph: scenarioGraphList) {
			List<CtrlNode> ctrlNodeList = scenarioGraph.getCtrlNodeList();
			List<Line> lineList = scenarioGraph.getLineList();
			List<String> ccslList = new ArrayList<String>();
			String ccslText = null;
			String behStartNode = null;
			String behEndNode = null;
			String expStartNode = null;
			String expEndNode = null;
			int tmpNo = 1;
			// 遍历情景图的lineList，根据线段来构造CCSL约束
			// TODO 1.8 新建tmp变量时先判断ccslList中有没有，如果有的话找到已有的并使用该变量，没有的时候再新建
			// TODO 1.8 加入有关于Delay的处理
			for(int i = 0; i < lineList.size(); i++) {
				Line line = lineList.get(i);
				String LineType = line.getLine_type();
				// 绿线：不做转换
				if(LineType.equals("Synchrony")) {
					continue;
				} else {
					Node fromNode = line.getFromNode();
					Node toNode = line.getToNode();
					String fromType = fromNode.getNode_type();
					String toType = toNode.getNode_type();
					String from = null;
					String to = null;
					Phenomenon fromPhenomenon = null;
					Phenomenon toPhenomenon = null;
					// 获取线段两端的节点
					if(fromType.equals("BehInt") || fromType.equals("ConnInt") || fromType.equals("ExpInt")) {
						for(Phenomenon phenomenon: phenomenonList) {
							if(fromNode.getNode_no() == phenomenon.getPhenomenon_no()) {
								fromPhenomenon = phenomenon;
								from = phenomenon.getPhenomenon_name();
								break;
							}
						}
					} else {
						if(fromType.equals("Delay")) {
							for(CtrlNode ctrlNode: ctrlNodeList) {
								if(fromNode.getNode_no() == ctrlNode.getNode_no() && fromType.equals(ctrlNode.getNode_type())) {
									from = ctrlNode.getNode_type() + ctrlNode.getNode_no();
									break;
								}
							}
						}
					}
					if(toType.equals("BehInt") || toType.equals("ConnInt") || toType.equals("ExpInt")) {
						for(Phenomenon phenomenon: phenomenonList) {
							if(toNode.getNode_no() == phenomenon.getPhenomenon_no()) {
								toPhenomenon = phenomenon;
								to = phenomenon.getPhenomenon_name();
								break;
							}
						}
					} else {
						if(toType.equals("Delay")) {
							for(CtrlNode ctrlNode: ctrlNodeList) {
								if(toNode.getNode_no() == ctrlNode.getNode_no() && toType.equals(ctrlNode.getNode_type())) {
									to = ctrlNode.getNode_type() + ctrlNode.getNode_no();
									break;
								}
							}
						}
					}
					// 蓝线：
					if(LineType.equals("BehOrder")) {
						if(fromType.equals("Start")) {
							behStartNode = to;
						} else if(toType.equals("End")) {
							// 最后一个事件到第一个事件：event1（最后一个事件） < event2$1（第一个事件）
							behEndNode = from;
							List<String> ccsls = new ArrayList<String>();
							ccsls.add("tmp" + tmpNo + "=" + behStartNode + "$1");
							ccsls.add(behEndNode + "<tmp" + tmpNo);
							ccslList.addAll(ccsls);
							tmpNo++;
						} else {
							// 事件 -- 事件：event1 < event2
							ccslText = from + "<" + to;
							ccslList.add(ccslText);
						}
					} else if(LineType.equals("ExpOrder")) {
						// 橙线：
						if(fromType.equals("Start")) {
							expStartNode = to;
						} else if(toType.equals("End")) {
							expEndNode = from;
							for(Phenomenon phenomenon: phenomenonList) {
								if(expStartNode.equals(phenomenon.getPhenomenon_name())) {
									toPhenomenon = phenomenon;
									break;
								}
							}
							// 最后一个事件到第一个事件：event1（最后一个事件） < event2$1（第一个事件）
							if(fromPhenomenon.getPhenomenon_type().equals("event") && toPhenomenon.getPhenomenon_type().equals("event")) {
								List<String> ccsls = new ArrayList<String>();
								ccsls.add("tmp" + tmpNo + "=" + expStartNode + "$1");
								ccsls.add(expEndNode + "<tmp" + tmpNo);
								ccslList.addAll(ccsls);
								tmpNo++;
							} else if(fromPhenomenon.getPhenomenon_type().equals("state") && toPhenomenon.getPhenomenon_type().equals("event")) {
								// 最后一个状态到第一个事件：state.f < event$1（第一个事件）
								List<String> ccsls = new ArrayList<String>();
								ccsls.add("tmp" + tmpNo + "=" + expStartNode + "$1");
								ccsls.add(expEndNode + ".f<tmp" + tmpNo);
								ccslList.addAll(ccsls);
								tmpNo++;
							}
						} else {
							if(fromPhenomenon != null && toPhenomenon != null) {
								// 事件 -- 事件：event1 < event2
								if(fromPhenomenon.getPhenomenon_type().equals("event") && toPhenomenon.getPhenomenon_type().equals("event")) {
									ccslText = from + "<" + to;
									ccslList.add(ccslText);
								} else if(fromPhenomenon.getPhenomenon_type().equals("event") && toPhenomenon.getPhenomenon_type().equals("state")) {
									// 事件 -- 状态：event trigger state in 1
									List<String> ccsls = new ArrayList<String>();
									ccsls.add(from + '<' + to + ".s");
									ccsls.add("tmp" + tmpNo + "=" + from + "$1 on idealClock");
									ccsls.add(to + ".s≤" + "tmp" + tmpNo);
									tmpNo++;
									ccsls.add(to + ".s<" + to + ".f");
									ccsls.add("tmp" + tmpNo + "=" + to + ".s$1");
									ccsls.add(to + ".f<tmp" + tmpNo);
									tmpNo++;
									ccslList.addAll(ccsls);
								} else if(fromPhenomenon.getPhenomenon_type().equals("state") && toPhenomenon.getPhenomenon_type().equals("event")) {
									// 状态 -- 事件：state.f < event
									ccslText = from + ".f<" + to;
									ccslList.add(ccslText);
								} else if(fromPhenomenon.getPhenomenon_type().equals("state") && toPhenomenon.getPhenomenon_type().equals("state")) {
									// 状态 -- 状态：state1.f < state2.s
									ccslText = from + ".f<" + to + ".s";
									ccslList.add(ccslText);
								}
							}
						}
					} else if(LineType.equals("BehEnable")) {
						// 红线：
						// 事件 -- 状态：event trigger state in 1
						if(fromPhenomenon.getPhenomenon_type().equals("event") && toPhenomenon.getPhenomenon_type().equals("state")) {
							List<String> ccsls = new ArrayList<String>();
							ccsls.add(from + '<' + to + ".s");
							ccsls.add("tmp" + tmpNo + "=" + from + "$1 on idealClock");
							ccsls.add(to + ".s≤" + "tmp" + tmpNo);
							tmpNo++;
							ccsls.add(to + ".s<" + to + ".f");
							ccsls.add("tmp" + tmpNo + "=" + to + ".s$1");
							ccsls.add(to + ".f<tmp" + tmpNo);
							tmpNo++;
							ccslList.addAll(ccsls);
						}
					}
				}
			}
			CCSLSet ccsl = new CCSLSet("CCSL_" + ccslNo, ccslList);
			ccslset.add(ccsl);
			ccslNo++;
		}
		return ccslset;
	}
	
	private List<Phenomenon> getPhenomenonList(Project project){
		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
		List<Interface> interfaceList = project.getContextDiagram().getInterfaceList();
		for(Interface interfaceElement: interfaceList) {
			phenomenonList.addAll(interfaceElement.getPhenomenonList());
		}
		List<Constraint> constraintList = project.getProblemDiagram().getConstraintList();
		for(Constraint constraint: constraintList) {
			phenomenonList.addAll(constraint.getPhenomenonList());
		}
		
		for(int i = phenomenonList.size() - 1; i >= 0; i--) {
			for(int j = i - 1; j >= 0; j--) {
				Phenomenon phenomenon1 = phenomenonList.get(i);
				Phenomenon phenomenon2 = phenomenonList.get(j);
				if(phenomenon1.getPhenomenon_no() == phenomenon2.getPhenomenon_no()) {
					phenomenonList.remove(phenomenon1);
					break;
				}
			}
		}
		return phenomenonList;
	}
}
