package com.example.demo.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.example.demo.bean.CtrlNode;
import com.example.demo.bean.Line;
import com.example.demo.bean.Node;
import com.example.demo.bean.Project;
import com.example.demo.bean.ScenarioGraph;

public class FullScenarioGraphServiceImpl implements FullScenarioGraphService {
	public EnumMap<FSGProperty, Object> getNodeList(Project project) {
        EnumMap<FSGProperty,Object> retMap = new EnumMap<FSGProperty, Object>(FSGProperty.class);
		
		List<ScenarioGraph> SGList = project.getScenarioGraphList();
		List<CtrlNode> ctrlNodeList = getCtrlNodeListBySG(SGList);
		List<Node> intNodeList = getIntNodeListBySG(SGList);
		List<Line> lineList = getLineListBySG(SGList);
		List<CtrlNode> ctrlNodes = new ArrayList<CtrlNode>();
		List<CtrlNode> behCtrlNodes = new ArrayList<CtrlNode>();
		List<CtrlNode> expCtrlNodes = new ArrayList<CtrlNode>();
		List<Line> lines = new ArrayList<Line>();

		// 若有相同的现象，只保留其中一个现象。
		// 若相同现象的fromList/toList一样，则删除其中一个现象即可
		List<Node> sameNodes = new ArrayList<Node>();
		for(int i = intNodeList.size() - 1; i >= 0; i--) {
			Node intNode1 = intNodeList.get(i);
			for(int j = i - 1; j >= 0; j--) {
				Node intNode2 = intNodeList.get(j);
				if(intNode1.getNode_no() == intNode2.getNode_no() && intNode1.getNode_type().equals(intNode2.getNode_type())) {
					List<Node> intNode1FromList = intNode1.getNode_fromList();
					List<Node> intNode1ToList = intNode1.getNode_toList();
					List<Node> intNode2FromList = intNode2.getNode_fromList();
					List<Node> intNode2ToList = intNode2.getNode_toList();
					boolean result1 = compareList(intNode1FromList, intNode2FromList);
					boolean result2 = compareList(intNode1ToList, intNode2ToList);
					if(!result1 && !result2) {
						intNode1FromList = addIntNodeFromList(intNode2FromList, intNode1FromList);
						intNode1ToList = addIntNodeToList(intNode2ToList, intNode1ToList);
						intNode1.setNode_fromList(intNode1FromList);
						intNode1.setNode_toList(intNode1ToList);
					} else if(!result1 && result2) {
						intNode1FromList = addIntNodeFromList(intNode2FromList, intNode1FromList);
						intNode1.setNode_fromList(intNode1FromList);
					} else if(result1 && !result2) {
						intNode1ToList = addIntNodeToList(intNode2ToList, intNode1ToList);
						intNode1.setNode_toList(intNode1ToList);
					}
					sameNodes.add(intNode2);
//					intNodeList.remove(j);
					// 如果有大于两个情景图中都存在相同的现象时，此时这里就不能用break
					break;
				}
			}
		}
		intNodeList.removeAll(sameNodes);
		// 合并子情景图中的控制节点从而获取情景图中的控制节点列表（合并节点和分支节点除外）
		// 将合并（分支）节点的fromList中的现象的toList设置为该合并（分支）节点的toList
		// 将合并（分支）节点的toList中的现象的fromList设置为该合并（分支）节点的fromList
		// 将合并（分支）节点从控制节点列表中删除
		for(int i = ctrlNodeList.size() - 1; i >= 0; i--) {
			CtrlNode SGCtrlNode = ctrlNodeList.get(i);
			List<Node> fromList = SGCtrlNode.getNode_fromList();
			System.out.println("FullScenarioGraphServiceImpl.getNodeList.fromList:" + fromList);
			List<Node> toList = SGCtrlNode.getNode_toList();
			if(SGCtrlNode.getNode_type().equals("Merge") || SGCtrlNode.getNode_type().equals("Branch")) {
				for(int m = 0; m < fromList.size(); m++) {
					Node fromNode = fromList.get(m);
					String fromNodeType = fromNode.getNode_type();
					boolean flag1 = false;
					boolean flag2 = false;
					for(Node sameNode: sameNodes) {
						if(fromNode.getNode_no() == sameNode.getNode_no() && fromNode.getNode_type().equals(sameNode.getNode_type())) {
							flag1 = true;
						}
					}
					for(Node toNode: toList) {
						if(toNode.getNode_type().equals("End")) {
							flag2 = true;
						}
					}
					if(flag1 && flag2) {
						continue;
					}
					if(fromNodeType.equals("BehInt") || fromNodeType.equals("ConnInt") || fromNodeType.equals("ExpInt")) {
						for(int n = 0; n < intNodeList.size(); n++) {
							Node intnode = intNodeList.get(n);
							if(intnode.getNode_no() == fromNode.getNode_no() && intnode.getNode_type().equals(fromNode.getNode_type())) {
								List<Node> tmpToList = intnode.getNode_toList();
								for(Node tmpToNode: tmpToList) {
									if(tmpToNode.getNode_no() == SGCtrlNode.getNode_no() && tmpToNode.getNode_type().equals(SGCtrlNode.getNode_type())) {
										tmpToList.remove(tmpToNode);
										break;
									}
								}
								tmpToList.addAll(toList);
								intnode.setNode_toList(tmpToList);
								intNodeList.set(n, intnode);
								break;
							}
						}
					} else {
						for(int n = 0; n < ctrlNodeList.size(); n++) {
							CtrlNode ctrlnode = ctrlNodeList.get(n);
							if(ctrlnode.getNode_no() == fromNode.getNode_no() && ctrlnode.getNode_type().equals(fromNode.getNode_type())) {
								List<Node> tmpToList = ctrlnode.getNode_toList();
								for(Node tmpToNode: tmpToList) {
									if(tmpToNode.getNode_no() == SGCtrlNode.getNode_no() && tmpToNode.getNode_type().equals(SGCtrlNode.getNode_type())) {
										tmpToList.remove(tmpToNode);
										break;
									}
								}
								tmpToList.addAll(toList);
								ctrlnode.setNode_toList(tmpToList);
								ctrlNodeList.set(n, ctrlnode);
								break;
							}
						}
					}
				}
				loop:
				for(int m = 0; m < toList.size(); m++) {
					Node toNode = toList.get(m);
					String toNodeType = toNode.getNode_type();
					for(Node sameNode: sameNodes) {
						if(toNode.getNode_no() == sameNode.getNode_no() && toNode.getNode_type().equals(sameNode.getNode_type())) {
							continue loop;
						}
					}
					if(toNodeType.equals("BehInt") || toNodeType.equals("ConnInt") || toNodeType.equals("ExpInt")) {
						for(int n = 0; n < intNodeList.size(); n++) {
							Node intnode = intNodeList.get(n);
							if(intnode.getNode_no() == toNode.getNode_no() && intnode.getNode_type().equals(toNode.getNode_type())) {
								List<Node> tmpFromList = intnode.getNode_fromList();
								for(Node tmpFromNode: tmpFromList) {
									if(tmpFromNode.getNode_no() == SGCtrlNode.getNode_no() && tmpFromNode.getNode_type().equals(SGCtrlNode.getNode_type())) {
										tmpFromList.remove(tmpFromNode);
										break;
									}
								}
								tmpFromList.addAll(fromList);
								intnode.setNode_fromList(tmpFromList);
								intNodeList.set(n, intnode);
								break;
							}
						}
					} else {
						for(int n = 0; n < ctrlNodeList.size(); n++) {
							CtrlNode ctrlnode = ctrlNodeList.get(n);
							if(ctrlnode.getNode_no() == toNode.getNode_no() && ctrlnode.getNode_type().equals(toNode.getNode_type())) {
								List<Node> tmpFromList = ctrlnode.getNode_fromList();
								for(Node tmpFromNode: tmpFromList) {
									if(tmpFromNode.getNode_no() == SGCtrlNode.getNode_no() && tmpFromNode.getNode_type().equals(SGCtrlNode.getNode_type())) {
										tmpFromList.remove(tmpFromNode);
										break;
									}
								}
								tmpFromList.addAll(fromList);
								ctrlnode.setNode_fromList(fromList);
								ctrlNodeList.set(n, ctrlnode);
								break;
							}
						}
					}
				}
				continue;
			} else if(SGCtrlNode.getNode_type().equals("Start") || SGCtrlNode.getNode_type().equals("End")) {
				continue;
			} else {
				// 把选择节点和时间节点加入ctrlNodes中（区分左右两边，注意去重）
				for(Node fromNode: fromList) {
					if(fromNode.getNode_type().equals("ExpInt")) {
						expCtrlNodes.add(SGCtrlNode);
					} else if(fromNode.getNode_type().equals("BehInt") || fromNode.getNode_type().equals("ConnInt")) {
						behCtrlNodes.add(SGCtrlNode);
					}
				}
				for(Node toNode: toList) {
					if(toNode.getNode_type().equals("ExpInt")) {
						expCtrlNodes.add(SGCtrlNode);
					} else if(toNode.getNode_type().equals("BehInt") || toNode.getNode_type().equals("ConnInt")) {
						behCtrlNodes.add(SGCtrlNode);
					}
				}
				ctrlNodes.add(SGCtrlNode);
			}
		}
		// 获取情景图中的现象列表
		// 检索含有precondition的现象，将该现象的fromList设置为precondition所指向的现象
		// 将precondition所指向的现象的toList设置为该现象
		for(int i = intNodeList.size() - 1; i >= 0; i--) {
			Node intNode = intNodeList.get(i);
			if(intNode.getPre_condition() != null) {
				for(int j = intNodeList.size() - 1; j >= 0; j--) {
					Node intNode2 = intNodeList.get(j);
					if(intNode2.getPost_condition() != null) {
						List<Node> fromList = new ArrayList<Node>();
						List<Node> toList = new ArrayList<Node>();
						if(intNode.getPre_condition().getPhenomenon_no() == intNode2.getNode_no() 
								&& intNode2.getPost_condition().getPhenomenon_no() == intNode.getNode_no()) {
							fromList.add(intNode2);
							toList.add(intNode);
							Node copyIntNode = new Node();
							Node copyIntNode2 = new Node();
							copyIntNode = copyIntNode(copyIntNode, intNode);
							copyIntNode2 = copyIntNode(copyIntNode2, intNode2);
							copyIntNode.setNode_fromList(fromList);
							copyIntNode2.setNode_toList(toList);
							intNodeList.set(i, copyIntNode);
							intNodeList.set(j, copyIntNode2);
							break;
						}
					}
				}
			}
		}
		List<Node> startNodeToListL = new ArrayList<Node>();
		List<Node> startNodeToListR = new ArrayList<Node>();
		List<Node> startNodeFromListL = new ArrayList<Node>();
		List<Node> startNodeFromListR = new ArrayList<Node>();
		List<Node> endNodeFromListL = new ArrayList<Node>();
		List<Node> endNodeFromListR = new ArrayList<Node>();
		List<Node> endNodeToListL = new ArrayList<Node>();
		List<Node> endNodeToListR = new ArrayList<Node>();
		//控制节点列表中只保留两个开始节点和结束节点（左右两边各一个）
		for(int i = lineList.size() - 1; i >= 0; i--) {
			Line line = lineList.get(i);
			Node lineFromNode = line.getFromNode();
			Node lineToNode = line.getToNode();
			if(line.getLine_type().equals("BehOrder")) {
				if(lineFromNode.getNode_type().equals("Start")) {
					startNodeToListL = addNodeList(startNodeToListL, lineToNode);
				} else if(lineToNode.getNode_type().equals("Start")) {
					startNodeFromListL = addNodeList(startNodeFromListL, lineFromNode);
				} else if(lineFromNode.getNode_type().equals("End")) {
					endNodeToListL = addNodeList(endNodeToListL, lineToNode);
				} else if(lineToNode.getNode_type().equals("End")) {
					endNodeFromListL = addNodeList(endNodeFromListL, lineFromNode);
				}
			} 
			if(line.getLine_type().equals("ExpOrder")) {
				if(lineFromNode.getNode_type().equals("Start")) {
					startNodeToListR = addNodeList(startNodeToListR, lineToNode);
				} else if(lineToNode.getNode_type().equals("Start")) {
					startNodeFromListR = addNodeList(startNodeFromListR, lineFromNode);
				} else if(lineFromNode.getNode_type().equals("End")) {
					endNodeToListR = addNodeList(endNodeToListR, lineToNode);
				} else if(lineToNode.getNode_type().equals("End")) {
					endNodeFromListR = addNodeList(endNodeFromListR, lineFromNode);
				}
			} 
		}
		startNodeToListL = getStartNodeList(startNodeToListL, ctrlNodeList, intNodeList, sameNodes);
		startNodeToListR = getStartNodeList(startNodeToListR, ctrlNodeList, intNodeList, sameNodes);
		startNodeFromListL = getStartNodeList(startNodeFromListL, ctrlNodeList, intNodeList, sameNodes);
		startNodeFromListR = getStartNodeList(startNodeFromListR, ctrlNodeList, intNodeList, sameNodes);
		endNodeFromListL = getEndNodeList(endNodeFromListL, ctrlNodeList, intNodeList, sameNodes);
		endNodeFromListR = getEndNodeList(endNodeFromListR, ctrlNodeList, intNodeList, sameNodes);
		endNodeToListL = getEndNodeList(endNodeToListL, ctrlNodeList, intNodeList, sameNodes);
		endNodeToListR = getEndNodeList(endNodeToListR, ctrlNodeList, intNodeList, sameNodes);
		
		CtrlNode startNodeL = addStartNode(startNodeFromListL, startNodeToListL, 1); 
		ctrlNodes.add(startNodeL);
		behCtrlNodes.add(startNodeL);
		List<Node> tmpStartNodeToListL = new ArrayList<Node>();
		tmpStartNodeToListL.add(startNodeL);
		CtrlNode startNodeR = addStartNode(startNodeFromListR, startNodeToListR, 2); 
		ctrlNodes.add(startNodeR);
		expCtrlNodes.add(startNodeR);
		List<Node> tmpStartNodeToListR = new ArrayList<Node>();
		tmpStartNodeToListR.add(startNodeR);
		
		CtrlNode endNodeL = addEndNode(endNodeFromListL, endNodeToListL, 1);
		ctrlNodes.add(endNodeL);
		behCtrlNodes.add(endNodeL);
		List<Node> tmpEndNodeFromListL = new ArrayList<Node>();
		tmpEndNodeFromListL.add(endNodeL);
		CtrlNode endNodeR = addEndNode(endNodeFromListR, endNodeToListR, 2);
		ctrlNodes.add(endNodeR);
		expCtrlNodes.add(endNodeR);
		List<Node> tmpEndNodeFromListR = new ArrayList<Node>();
		tmpEndNodeFromListR.add(endNodeR);
		for(Node startNode: startNodeToListL) {
			String startNodeType = startNode.getNode_type();
			if(startNodeType.equals("BehInt") || startNodeType.equals("ConnInt") || startNodeType.equals("ExpInt")) {
				for(int i = 0; i < intNodeList.size(); i++) {
					Node node = intNodeList.get(i);
					if(startNode.getNode_no() == node.getNode_no() && startNodeType.equals(node.getNode_type())) {
						Node tmpNode = new Node();
						tmpNode = copyIntNode(tmpNode, node);
						tmpNode.setNode_fromList(tmpStartNodeToListL);
						intNodeList.set(i, tmpNode);
						break;
					}
				}
			} else {
				for(int i = 0; i < ctrlNodes.size(); i++) {
					CtrlNode node = ctrlNodes.get(i);
					if(startNode.getNode_no() == node.getNode_no() && startNodeType.equals(node.getNode_type())) {
						CtrlNode tmpNode = new CtrlNode();
						tmpNode = copyCtrlNode(tmpNode, node);
						tmpNode.setNode_fromList(tmpStartNodeToListL);
						ctrlNodes.set(i, tmpNode);
						break;
					}
				}
			}
		}
		for(Node startToNode: startNodeToListR) {
			String startToNodeType = startToNode.getNode_type();
			if(startToNodeType.equals("BehInt") || startToNodeType.equals("ConnInt") || startToNodeType.equals("ExpInt")) {
				for(int i = 0; i < intNodeList.size(); i++) {
					Node node = intNodeList.get(i);
					if(startToNode.getNode_no() == node.getNode_no() && startToNodeType.equals(node.getNode_type())) {
						Node tmpNode = new Node();
						tmpNode = copyIntNode(tmpNode, node);
						tmpNode.setNode_fromList(tmpStartNodeToListR);
						intNodeList.set(i, tmpNode);
						break;
					}
				}
			} else {
				for(int i = 0; i < ctrlNodes.size(); i++) {
					CtrlNode node = ctrlNodes.get(i);
					if(startToNode.getNode_no() == node.getNode_no() && startToNodeType.equals(node.getNode_type())) {
						CtrlNode tmpNode = new CtrlNode();
						tmpNode = copyCtrlNode(tmpNode, node);
						tmpNode.setNode_fromList(tmpStartNodeToListR);
						ctrlNodes.set(i, tmpNode);
						break;
					}
				}
			}
		}
		
		for(Node endFromNode: endNodeFromListL) {
			String endFromNodeType = endFromNode.getNode_type();
			if(endFromNodeType.equals("BehInt") || endFromNodeType.equals("ConnInt") || endFromNodeType.equals("ExpInt")) {
				for(Node node: intNodeList) {
					if(endFromNode.getNode_no() == node.getNode_no() && endFromNodeType.equals(node.getNode_type())) {
						node.setNode_toList(tmpEndNodeFromListL);
						break;
					}
				}
			} else {
				for(CtrlNode node: ctrlNodes) {
					if(endFromNode.getNode_no() == node.getNode_no() && endFromNodeType.equals(node.getNode_type())) {
						node.setNode_toList(tmpEndNodeFromListL);
						break;
					}
				}
			}
		}
		for(Node endFromNode: endNodeFromListR) {
			String endFromNodeType = endFromNode.getNode_type();
			if(endFromNodeType.equals("BehInt") || endFromNodeType.equals("ConnInt") || endFromNodeType.equals("ExpInt")) {
				for(Node node: intNodeList) {
					if(endFromNode.getNode_no() == node.getNode_no() && endFromNodeType.equals(node.getNode_type())) {
						node.setNode_toList(tmpEndNodeFromListR);
						break;
					}
				}
			} else {
				for(CtrlNode node: ctrlNodes) {
					if(endFromNode.getNode_no() == node.getNode_no() && endFromNodeType.equals(node.getNode_type())) {
						node.setNode_toList(tmpEndNodeFromListR);
						break;
					}
				}
			}
		}
		int mergeNo = 1;
		int branchNo = 1;
		List<CtrlNode> newCtrlNodes = new ArrayList<CtrlNode>();
		// 遍历所有现象
		for(int i = ctrlNodes.size() - 1; i >= 0; i--) {
			CtrlNode ctrlNode = ctrlNodes.get(i);
			List<Node> ctrlNodeFromList = ctrlNode.getNode_fromList();
			List<Node> ctrlNodeToList = ctrlNode.getNode_toList();
			// 若该现象的fromList的长度大于等于2，则新增一个合并节点
			// 将该现象的fromList设置为新增的合并节点
			// 将该现象的fromList中的节点的toList设置为新增的合并节点
			// 将合并节点的fromList设置为该现象的fromList
			// 将合并节点的toList设置为该现象
			if(ctrlNodeFromList != null && ctrlNodeFromList.size() >= 2) {
				CtrlNode mergeNode = newMergeNode(ctrlNode, mergeNo, ctrlNodeFromList, intNodeList);
				newCtrlNodes.add(mergeNode);
				List<Node> ctrlNodeNewFromList = new ArrayList<Node>();
				ctrlNodeNewFromList.add(mergeNode);
				CtrlNode mergeCtrlNode = new CtrlNode();
				mergeCtrlNode = copyCtrlNode(mergeCtrlNode, ctrlNode);
				mergeCtrlNode.setNode_fromList(ctrlNodeNewFromList);
				ctrlNodes.set(i, mergeCtrlNode);
				for(Node fromNode: ctrlNodeFromList) {
					String nodeType = fromNode.getNode_type();
					if(nodeType.equals("ExpInt")) {
						expCtrlNodes.add(mergeNode);
						for(int j = expCtrlNodes.size() - 1; j >= 0; j--) {
							CtrlNode expCtrlNode = expCtrlNodes.get(j);
							if(expCtrlNode.getNode_no() == ctrlNode.getNode_no() && expCtrlNode.getNode_type().equals(ctrlNode.getNode_type())) {
								expCtrlNodes.set(j, mergeCtrlNode);
								break;
							}
						}
						intNodeList = addNodeToList(fromNode, intNodeList, ctrlNodeNewFromList, ctrlNode);
					} else if(nodeType.equals("BehInt") || nodeType.equals("ConnInt")) {
						behCtrlNodes.add(mergeNode);
						for(int j = behCtrlNodes.size() - 1; j >= 0; j--) {
							CtrlNode behCtrlNode = behCtrlNodes.get(j);
							if(behCtrlNode.getNode_no() == ctrlNode.getNode_no() && behCtrlNode.getNode_type().equals(ctrlNode.getNode_type())) {
								behCtrlNodes.set(j, mergeCtrlNode);
								break;
							}
						}
						intNodeList = addNodeToList(fromNode, intNodeList, ctrlNodeNewFromList, ctrlNode);
					} else {
						ctrlNodes = addCtrlNodeToList(fromNode, ctrlNodes, ctrlNodeNewFromList, ctrlNode);
						behCtrlNodes = addCtrlNodeToList(fromNode, behCtrlNodes, ctrlNodeNewFromList, ctrlNode);
						expCtrlNodes = addCtrlNodeToList(fromNode, expCtrlNodes, ctrlNodeNewFromList, ctrlNode);
					}
				}
				mergeNo++;
			}
			// 若该现象的toList的长度大于等于2，则新增一个分支节点
			// 将该现象的toList设置为新增的分支节点
			// 将该现象的toList中的节点的fromList设置为新增的分支节点
			// 将分支节点的fromList设置为该现象
			// 将分支节点的toList设置为该现象的toList
			if(ctrlNodeToList != null && ctrlNodeToList.size() >= 2) {
				if(!ctrlNode.getNode_type().equals("Decision")) {
					CtrlNode branchNode = newBranchNode(ctrlNode, branchNo, ctrlNodeToList);
					newCtrlNodes.add(branchNode);
					List<Node> ctrlNodeNewToList = new ArrayList<Node>();
					ctrlNodeNewToList.add(branchNode);
					CtrlNode branchCtrlNode = new CtrlNode();
					branchCtrlNode = copyCtrlNode(branchCtrlNode, ctrlNode);
					branchCtrlNode.setNode_toList(ctrlNodeNewToList);
					ctrlNodes.set(i, branchCtrlNode);
					for(Node toNode: ctrlNodeToList) {
						String nodeType = toNode.getNode_type();
						if(nodeType.equals("ExpInt")) {
							expCtrlNodes.add(branchNode);
							for(int j = expCtrlNodes.size() - 1; j >= 0; j--) {
								CtrlNode expCtrlNode = expCtrlNodes.get(j);
								if(expCtrlNode.getNode_no() == ctrlNode.getNode_no() && expCtrlNode.getNode_type().equals(ctrlNode.getNode_type())) {
									expCtrlNodes.set(j, branchCtrlNode);
									break;
								}
							}
							intNodeList = addNodeFromList(toNode, intNodeList, ctrlNodeNewToList, ctrlNode);
						} else if(nodeType.equals("BehInt") || nodeType.equals("ConnInt")) {
							behCtrlNodes.add(branchNode);
							for(int j = behCtrlNodes.size() - 1; j >= 0; j--) {
								CtrlNode behCtrlNode = behCtrlNodes.get(j);
								if(behCtrlNode.getNode_no() == ctrlNode.getNode_no() && behCtrlNode.getNode_type().equals(ctrlNode.getNode_type())) {
									behCtrlNodes.set(j, branchCtrlNode);
								}
							}
							intNodeList = addNodeFromList(toNode, intNodeList, ctrlNodeNewToList, ctrlNode);
						} else {
							ctrlNodes = addCtrlNodeFromList(toNode, ctrlNodes, ctrlNodeNewToList, ctrlNode);
							behCtrlNodes = addCtrlNodeFromList(toNode, behCtrlNodes, ctrlNodeNewToList, ctrlNode);
							expCtrlNodes = addCtrlNodeFromList(toNode, expCtrlNodes, ctrlNodeNewToList, ctrlNode);
						}
					}
					branchNo++;
				}
			}
		}
		ctrlNodes.addAll(newCtrlNodes);
//		List<CtrlNode> newCtrlNodes2 = new ArrayList<CtrlNode>();
		for(int i = 0; i < intNodeList.size(); i++) {
			Node intNode = intNodeList.get(i);
			if(intNode == null) {
				continue;
			}
			List<Node> intNodeFromList = intNode.getNode_fromList();
			List<Node> intNodeToList = intNode.getNode_toList();
			if(intNodeFromList == null || intNodeToList == null) {
				continue;
			}
			if(intNodeFromList.size() >= 2) {
				CtrlNode mergeNode = newMergeNode(intNodeList.get(i), mergeNo, intNodeFromList, intNodeList);
				ctrlNodes.add(mergeNode);
				if(intNode.getNode_type().equals("ExpInt")) {
					expCtrlNodes.add(mergeNode);
				} else if(intNode.getNode_type().equals("BehInt") || intNode.getNode_type().equals("ConnInt")) {
					behCtrlNodes.add(mergeNode);
				}
				List<Node> intNodeNewFromList = new ArrayList<Node>();
				intNodeNewFromList.add(mergeNode);
				Node mergeIntNode = new Node();
				mergeIntNode = copyIntNode(mergeIntNode, intNode);
				mergeIntNode.setNode_fromList(intNodeNewFromList);
				intNodeList.set(i, mergeIntNode);
				for(Node node: intNodeFromList) {
					String nodeType = node.getNode_type();
					if(nodeType.equals("BehInt") || nodeType.equals("ConnInt") || nodeType.equals("ExpInt")) {
						intNodeList = addNodeToList(node, intNodeList, intNodeNewFromList, intNode);
					} else {
						ctrlNodes = addCtrlNodeToList(node, ctrlNodes, intNodeNewFromList, intNode);
						behCtrlNodes = addCtrlNodeToList(node, behCtrlNodes, intNodeNewFromList, intNode);
						expCtrlNodes = addCtrlNodeToList(node, expCtrlNodes, intNodeNewFromList, intNode);
					}
				}
				mergeNo++;
			} 
			if(intNodeToList != null && intNodeToList.size() >= 2) {
				CtrlNode branchNode = newBranchNode(intNodeList.get(i), branchNo, intNodeToList);
				ctrlNodes.add(branchNode);
				if(intNode.getNode_type().equals("ExpInt")) {
					expCtrlNodes.add(branchNode);
				} else if(intNode.getNode_type().equals("BehInt") || intNode.getNode_type().equals("ConnInt")) {
					behCtrlNodes.add(branchNode);
				}
				List<Node> intNodeNewToList = new ArrayList<Node>();
				intNodeNewToList.add(branchNode);
				Node branchIntNode = new Node();
				branchIntNode = copyIntNode(branchIntNode, intNodeList.get(i));
				branchIntNode.setNode_toList(intNodeNewToList);
				intNodeList.set(i, branchIntNode);
				for(Node node: intNodeToList) {
					String nodeType = node.getNode_type();
					if(nodeType.equals("BehInt") || nodeType.equals("ConnInt") || nodeType.equals("ExpInt")) {
						intNodeList = addNodeFromList(node, intNodeList, intNodeNewToList, intNode);
					} else {
						ctrlNodes = addCtrlNodeFromList(node, ctrlNodes, intNodeNewToList, intNode);
						behCtrlNodes = addCtrlNodeFromList(node, behCtrlNodes, intNodeNewToList, intNode);
						expCtrlNodes = addCtrlNodeFromList(node, expCtrlNodes, intNodeNewToList, intNode);
					}
				}
				branchNo++;
			}
		}
//		ctrlNodes.addAll(newCtrlNodes2);
		for(int i = behCtrlNodes.size() - 1; i >= 0; i--) {
			CtrlNode ctrlNode1 = behCtrlNodes.get(i);
			for(int j = i - 1; j >= 0; j--) {
				CtrlNode ctrlNode2 = behCtrlNodes.get(j);
				if(ctrlNode1.getNode_no() == ctrlNode2.getNode_no() && ctrlNode1.getNode_type().equals(ctrlNode2.getNode_type())) {
					behCtrlNodes.remove(ctrlNode1);
					break;
				}
			}
		}
		for(int i = expCtrlNodes.size() - 1; i >= 0; i--) {
			CtrlNode ctrlNode1 = expCtrlNodes.get(i);
			for(int j = i - 1; j >= 0; j--) {
				CtrlNode ctrlNode2 = expCtrlNodes.get(j);
				if(ctrlNode1.getNode_no() == ctrlNode2.getNode_no() && ctrlNode1.getNode_type().equals(ctrlNode2.getNode_type())) {
					expCtrlNodes.remove(ctrlNode1);
					break;
				}
			}
		}
		
		// 处理lineList
		// 首先把中间的线和与选择节点相连的线加入lineList中
		List<Line> conditionLines = new ArrayList<Line>();
		for(int i = lineList.size() - 1; i >= 0; i--) {
			Line line = lineList.get(i);
			if(line.getLine_type().equals("Synchrony") || line.getLine_type().equals("BehEnable") || line.getLine_type().equals("ExpEnable")) {
				lines.add(line);
				lineList.remove(i);
			}
			if(line.getCondition() != null) {
				Node toNode = line.getToNode();
				String toNodeType = toNode.getNode_type();
				// 新建一条线
				Line newLine = new Line();
				newLine = copyLine(newLine, line);
				Node lineToNode = new Node();
				// 如果与选择节点相连的节点是开始或结束节点，该线由选择节点指向后来新建的开始/结束节点
				String lineType = line.getLine_type();
				if(toNodeType.equals("Start")) {
					if(lineType.equals("BehOrder")) {
						lineToNode = copyLineNode(lineToNode, startNodeL);
					} else if(lineType.equals("ExpOrder")) {
						lineToNode = copyLineNode(lineToNode, startNodeR);
					}
				} else if(toNodeType.equals("End")) {
					if(lineType.equals("BehOrder")) {
						lineToNode = copyLineNode(lineToNode, endNodeL);
					} else if(lineType.equals("ExpOrder")) {
						lineToNode = copyLineNode(lineToNode, endNodeR);
					}
				} 
				else if(toNodeType.equals("Merge")) {
					// 如果与选择节点相连的节点是合并节点，先找到以该合并节点为fromNode的线，进而找到该线的toNode
					// 然后将选择节点指向该toNode的fromList中的节点
					lineToNode = searchMergeNode(toNode, lineList, intNodeList, ctrlNodes);
				}
				if(lineToNode.getNode_type() != null) {
					newLine.setToNode(lineToNode);
				}
				conditionLines.add(newLine);
				lineList.remove(i);
			}		
		}
		List<Line> behLineList = getLineListByCtrlNodes(behCtrlNodes, "BehOrder", conditionLines);
		List<Line> expLineList = getLineListByCtrlNodes(expCtrlNodes, "ExpOrder", conditionLines);
		for(Node intNode: intNodeList) {
			List<Node> toList = intNode.getNode_toList();
			if(toList == null) {
				continue;
			}
			for(Node node: toList) {
				if(node == null) {
					continue;
				}
				Line line = new Line();
				line.setFromNode(intNode);
				line.setToNode(node);
				if(intNode.getNode_type().equals("BehInt") || intNode.getNode_type().equals("ConnInt")) {
					line.setLine_no(behLineList.size() + 1);
					line.setLine_type("BehOrder");
					behLineList.add(line);
				} else if(intNode.getNode_type().equals("ExpInt")) {
					line.setLine_no(expLineList.size() + 1);
					line.setLine_type("ExpOrder");
					expLineList.add(line);
				}
			}
		}
		// 去重
		lines = removeRedundantLines(lines);
		behLineList = removeRedundantLines(behLineList);
		expLineList = removeRedundantLines(expLineList);
		// 排序
		behLineList = sortLineNo(behLineList, intNodeList, conditionLines, ctrlNodes);
		expLineList = sortLineNo(expLineList, intNodeList, conditionLines, ctrlNodes);
		List<Line> tmpLines = new ArrayList<Line>();
		tmpLines.addAll(behLineList);
		tmpLines.addAll(expLineList);
		lines.addAll(tmpLines);
		// TODO 重新排列节点的位置   done
		for(int i = 0; i < tmpLines.size(); i++) {
			Line line = tmpLines.get(i);
			Node fromNode = line.getFromNode();
			Node toNode = line.getToNode();
			String toNodeType = toNode.getNode_type();
			String fromNodeType = fromNode.getNode_type();
			Node tmpFromNode = null;
			if(fromNodeType.equals("BehInt") || fromNodeType.equals("ConnInt") || fromNodeType.equals("ExpInt")) {
				for(int j = 0; j < intNodeList.size(); j++) {
					Node intNode = intNodeList.get(j);
					if(fromNode.getNode_no() == intNode.getNode_no() && fromNode.getNode_type().equals(intNode.getNode_type())) {
						tmpFromNode = intNode;
						break;
					}
				}
			} else {
				for(int j = 0; j < ctrlNodes.size(); j++) {
					CtrlNode ctrlNode = ctrlNodes.get(j);
					if(fromNode.getNode_no() == ctrlNode.getNode_no() && fromNode.getNode_type().equals(ctrlNode.getNode_type())) {
						tmpFromNode = ctrlNode;
						break;
					}
				}
			}
			if(toNodeType.equals("BehInt") || toNodeType.equals("ConnInt") || toNodeType.equals("ExpInt")) {
				for(int j = 0; j < intNodeList.size(); j++) {
					Node intNode = intNodeList.get(j);
					if(toNode.getNode_no() == intNode.getNode_no() && toNode.getNode_type().equals(intNode.getNode_type())) {
						if(fromNode.getNode_type().equals("Merge")) {
							intNode.setNode_x(tmpFromNode.getNode_x() + 20);
							intNode.setNode_y(tmpFromNode.getNode_y() + 100);
							intNodeList.set(j, intNode);
							break;
						} else if(fromNode.getNode_type().equals("Branch")) {
							List<Node> branchNodes = tmpFromNode.getNode_toList();
							for(int m = 0; m < branchNodes.size(); m++) {
								Node branchNode = branchNodes.get(m);
								if(intNode.getNode_no() == branchNode.getNode_no() && intNode.getNode_type().equals(branchNode.getNode_type())) {
									if(m == 0) {
										intNode.setNode_x(tmpFromNode.getNode_x());
										intNode.setNode_y(tmpFromNode.getNode_y() + 80);
										intNodeList.set(j, intNode);
										break;
									} else {
										Node leftNode = branchNodes.get(m - 1);
										String leftNodeType = leftNode.getNode_type();
										if(leftNodeType.equals("BehInt") || leftNodeType.equals("ConnInt") || leftNodeType.equals("ExpInt")) {
											for(Node intnode: intNodeList) {
												if(leftNode.getNode_no() == intnode.getNode_no() && leftNode.getNode_type().equals(intnode.getNode_type())) {
													tmpFromNode = intnode;
													break;
												}
											}
										} else {
											for(Node ctrlnode: ctrlNodes) {
												if(leftNode.getNode_no() == ctrlnode.getNode_no() && leftNode.getNode_type().equals(ctrlnode.getNode_type())) {
													tmpFromNode = ctrlnode;
													break;
												}
											}
										}
										intNode.setNode_x(tmpFromNode.getNode_x() + 160);
										intNode.setNode_y(tmpFromNode.getNode_y() + 30);
										intNodeList.set(j, intNode);
										break;
									}
								}
							}
							break;
						} else if(fromNode.getNode_type().equals("Decision")) {
							intNode.setNode_x(tmpFromNode.getNode_x() + 20);
							intNode.setNode_y(tmpFromNode.getNode_y() + 120);
							intNodeList.set(j, intNode);
							break;
						}
						else {
							intNode.setNode_x(tmpFromNode.getNode_x());
							intNode.setNode_y(tmpFromNode.getNode_y() + 80);
							intNodeList.set(j, intNode);
							break;
						}
					}
				}
			} else {
				for(int j = 0; j < ctrlNodes.size(); j++) {
					CtrlNode ctrlNode = ctrlNodes.get(j);
					if(toNode.getNode_no() == ctrlNode.getNode_no() && toNode.getNode_type().equals(ctrlNode.getNode_type())) {
						if(toNodeType.equals("Start")) {
							break;
						} else if(toNodeType.equals("Branch")) {
							ctrlNode.setNode_x(tmpFromNode.getNode_x() - 75);
							ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
							ctrlNodes.set(j, ctrlNode);
							break;
						} else if(fromNode.getNode_type().equals("Branch")) {
							List<Node> branchNodes = tmpFromNode.getNode_toList();
							for(int m = 0; m < branchNodes.size(); m++) {
								Node branchNode = branchNodes.get(m);
								if(ctrlNode.getNode_no() == branchNode.getNode_no() && ctrlNode.getNode_type().equals(branchNode.getNode_type())) {
									if(m == 0) {
										ctrlNode.setNode_x(tmpFromNode.getNode_x());
										ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
										ctrlNodes.set(j, ctrlNode);
										break;
									} else {
										Node leftNode = branchNodes.get(m - 1);
										String leftNodeType = leftNode.getNode_type();
										if(leftNodeType.equals("BehInt") || leftNodeType.equals("ConnInt") || leftNodeType.equals("ExpInt")) {
											for(Node intnode: intNodeList) {
												if(leftNode.getNode_no() == intnode.getNode_no() && leftNode.getNode_type().equals(intnode.getNode_type())) {
													tmpFromNode = intnode;
													break;
												}
											}
										} else {
											for(Node ctrlnode: ctrlNodes) {
												if(leftNode.getNode_no() == ctrlnode.getNode_no() && leftNode.getNode_type().equals(ctrlnode.getNode_type())) {
													tmpFromNode = ctrlnode;
													break;
												}
											}
										}
										ctrlNode.setNode_x(tmpFromNode.getNode_x() + 160);
										ctrlNode.setNode_y(tmpFromNode.getNode_y() + 30);
										ctrlNodes.set(j, ctrlNode);
										break;
									}
								}
							}
							break;
						} else if(toNodeType.equals("Merge")) {
							String nodeType = fromNode.getNode_type();
							if(nodeType.equals("BehInt") || nodeType.equals("ConnInt") || nodeType.equals("ExpInt")) {
								ctrlNode.setNode_x(tmpFromNode.getNode_x() - 20);
								ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
								ctrlNodes.set(j, ctrlNode);
								break;
							} else {
								ctrlNode.setNode_x(tmpFromNode.getNode_x());
								ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
								ctrlNodes.set(j, ctrlNode);
								break;
							}
						} else if(toNodeType.equals("Decision")) {
							ctrlNode.setNode_x(tmpFromNode.getNode_x() - 20);
							ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
							break;
						} else if(toNodeType.equals("End")) {
							ctrlNode.setNode_x(tmpFromNode.getNode_x());
							ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
							break;
						}
						else {
							ctrlNode.setNode_x(tmpFromNode.getNode_x());
							ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
							break;
						}
					}
				}
			}
		}
        retMap.put(FSGProperty.ctrlNodeList, ctrlNodes);
        retMap.put(FSGProperty.intNodeList, intNodeList);
        retMap.put(FSGProperty.lineList, lines);

        return retMap;
    }

	
	private List<Line> removeRedundantLines(List<Line> lines) {
		for(int i = lines.size() - 1; i >= 0; i--) {
			Line line1 = lines.get(i);
			for(int j = i - 1; j >= 0; j--) {
				Line line2 = lines.get(j);
				if(line1.getLine_type().equals(line2.getLine_type())
						&& line1.getFromNode().getNode_no() == line2.getFromNode().getNode_no()
						&& line1.getFromNode().getNode_type().equals(line2.getFromNode().getNode_type())
						&& line1.getToNode().getNode_no() == line2.getToNode().getNode_no()
						&& line1.getToNode().getNode_type().equals(line2.getToNode().getNode_type())) {
					lines.remove(line1);
					break;
				}
			}
		}
		return lines;
	}
	
	private List<Line> sortLineNo(List<Line> lineList, List<Node> intNodeList, List<Line> conditionLines, List<CtrlNode> ctrlNodes) {
		int lineNo = 1;
		List<Line> lines = new ArrayList<Line>();
		for(int i = 0; i < lineList.size(); i++) {
			Line line = lineList.get(i);
			if(line.getFromNode().getNode_type().equals("Start")) {
				line.setLine_no(lineNo);
				lineList.remove(i);
				lines.add(line);
				lineNo++; 
				Node toNode = line.getToNode();
				lines = sortLineNoByBranch(toNode, lineList, lineNo, lines, intNodeList, conditionLines, ctrlNodes, lineList.size() + 1);
				break;
			}
		}
		return lines;
	}

	private List<Line> sortLineNoByBranch(Node toNode, List<Line> lineList, int lineNo, List<Line> lines, 
			List<Node> intNodeList, List<Line> conditionLines, List<CtrlNode> ctrlNodes, int lineLength) {
		while(toNode != null && !toNode.getNode_type().equals("End")) {
			if(toNode.getNode_type().equals("Merge")) {
				toNode = searchMergeNode(toNode, lineList, intNodeList, ctrlNodes);
			}
			if(toNode == null) {
				break;
			}
			if(toNode.getNode_type().equals("Branch")) {
				toNode = searchBranchNode(toNode, lines, intNodeList, ctrlNodes);
				for(int j = 0; j < toNode.getNode_toList().size(); j++) {
					for(Line newLine: lineList) {
						if(toNode.getNode_no() == newLine.getFromNode().getNode_no() && 
								toNode.getNode_type().equals(newLine.getFromNode().getNode_type())) {
							newLine.setLine_no(lineNo);
							lineList.remove(newLine);
							lines.add(newLine);
							lineNo++;
							lines = sortLineNoByBranch(newLine.getToNode(), lineList, lineNo, lines, intNodeList, conditionLines, ctrlNodes, lineLength);
							lineNo = lines.size() + 1;
							break;
						}
					} 
				}
				List<Node> tmpToNodes = toNode.getNode_toList();
				Node tmpToNode = null;
				for(Node node: tmpToNodes) {
					String nodeType = node.getNode_type();
					if(nodeType.equals("BehInt") || nodeType.equals("ConnInt") || nodeType.equals("ExpInt")) {
						tmpToNode = node;
						break;
					}
				}
				for(Node node: intNodeList) {
					if(node.getNode_no() == tmpToNode.getNode_no() && node.getNode_type().equals(tmpToNode.getNode_type())) {
						toNode = node.getNode_toList().get(0);
						break;
					}
				}
			} else if(toNode.getNode_type().equals("Decision")) {
				Line tmpLine = null;
				for(int j = conditionLines.size() - 1; j >= 0; j--) {
					Line conditionLine = conditionLines.get(j);
					Node fromNode = conditionLine.getFromNode();
					if(fromNode.getNode_no() == toNode.getNode_no() && fromNode.getNode_type().equals(toNode.getNode_type())) {
						conditionLine.setLine_no(lineNo);
						System.out.println("remove:" + conditionLines.get(j) + "  " + conditionLine);
						conditionLines.remove(j);
						lines.add(conditionLine);
						lineNo++;
						// 这里暂时写死了
						if(!conditionLine.getToNode().getNode_type().equals("Start")) {
							tmpLine = conditionLine;
						}
					}
				}
				if(tmpLine != null) {
					toNode = tmpLine.getToNode();
				}
				else {
					toNode = null;
				}
			} else {
				for(Line newLine: lineList) {
					if(toNode.getNode_no() == newLine.getFromNode().getNode_no() && 
							toNode.getNode_type().equals(newLine.getFromNode().getNode_type())) {
						newLine.setLine_no(lineNo);
						lineList.remove(newLine);
						lines.add(newLine);
						lineNo++;
						toNode = newLine.getToNode();
						break;
					}
				}
			}
			if(lines.size() >= lineLength) {
				break;
			}
		}
		return lines;
	}

	private Node searchMergeNode(Node toNode, List<Line> lineList, List<Node> intNodeList, List<CtrlNode> ctrlNodes) {
		Node lineToNode = null;
		Node tmpToNode = null;
		for(Line tmpLine: lineList) {
			Node tmpFromNode = tmpLine.getFromNode();
			if(toNode.getNode_no() == tmpFromNode.getNode_no() && toNode.getNode_type().equals(tmpFromNode.getNode_type())) {
				tmpToNode = tmpLine.getToNode();
				break;
			}
		}
		if(tmpToNode != null) {
			String tmpToNodeType = tmpToNode.getNode_type();
			if(tmpToNodeType.equals("BehInt") || tmpToNodeType.equals("ConnInt") || tmpToNodeType.equals("ExpInt")) {
				for(Node node: intNodeList) {
					if(node.getNode_no() == tmpToNode.getNode_no() && node.getNode_type().equals(tmpToNode.getNode_type())) {
						List<Node> tmpFromList = node.getNode_fromList();
						if(tmpFromList != null) {
							lineToNode = tmpFromList.get(0);
						}
						break;
					}
				}
			} else {
				for(CtrlNode node: ctrlNodes) {
					if(node.getNode_no() == tmpToNode.getNode_no() && node.getNode_type().equals(tmpToNode.getNode_type())) {
						List<Node> tmpFromList = node.getNode_fromList();
						if(tmpFromList != null) {
							lineToNode = tmpFromList.get(0);
						}
						break;
					}
				}
			}
		}
		return lineToNode;
	}
	
	private Node searchBranchNode(Node toNode, List<Line> lineList, List<Node> intNodeList, List<CtrlNode> ctrlNodes) {
		Node lineToNode = null;
		String toNodeType = toNode.getNode_type();
		if(toNodeType.equals("BehInt") || toNodeType.equals("ConnInt") || toNodeType.equals("ExpInt")) {
			for(Node node: intNodeList) {
				if(node.getNode_no() == toNode.getNode_no() && node.getNode_type().equals(toNode.getNode_type())) {
					lineToNode = node;
					break;
				}
			}
		} else {
			for(CtrlNode node: ctrlNodes) {
				if(node.getNode_no() == toNode.getNode_no() && node.getNode_type().equals(toNode.getNode_type())) {
					lineToNode = node;
					break;
				}
			}
		}
		return lineToNode;
	}

	private List<Line> getLineListByCtrlNodes(List<CtrlNode> ctrlNodes, String string, List<Line> lines) {
		List<Line> lineList = new ArrayList<Line>();
		for(CtrlNode ctrlNode: ctrlNodes) {
			String ctrlNodeType = ctrlNode.getNode_type();
			List<Node> toList = ctrlNode.getNode_toList();
			if(toList != null) {
				for(Node node: toList) {
					if(ctrlNodeType.equals("Decision")) {
						for(Line tmpLine: lines) {
							Node fromNode = tmpLine.getFromNode();
							Node toNode = tmpLine.getToNode();
							if(fromNode.getNode_no() == ctrlNode.getNode_no() 
									&& fromNode.getNode_type().equals(ctrlNode.getNode_type())
									&& toNode.getNode_type().equals(node.getNode_type())) {
								lineList.add(tmpLine);
								break;
							}
						}
					} else {
						Line line = new Line();
						line.setLine_no(lineList.size() + 1);
						line.setLine_type(string);
						line.setFromNode(ctrlNode);
						line.setToNode(node);
						lineList.add(line);
					}
					
				}
			}
		}
		return lineList;
	}

	private List<CtrlNode> addCtrlNodeFromList(Node node, List<CtrlNode> ctrlNodes, List<Node> ctrlNodeNewToList, Node fromNode) {
		for(int j = ctrlNodes.size() - 1; j >= 0; j--) {
			CtrlNode ctrlnode = ctrlNodes.get(j);
			if(ctrlnode.getNode_no() == node.getNode_no() && ctrlnode.getNode_type().equals(node.getNode_type())) {
				CtrlNode updateCtrlNode = new CtrlNode();
				updateCtrlNode = copyCtrlNode(updateCtrlNode, ctrlnode);
				List<Node> fromList = ctrlnode.getNode_fromList();
				for(Node tmpFromNode: fromList) {
					if(tmpFromNode.getNode_no() == fromNode.getNode_no() && tmpFromNode.getNode_type().equals(fromNode.getNode_type())) {
						fromList.remove(tmpFromNode);
						break;
					}
				}
				List<Node> tmpFromList = new ArrayList<Node>();
				if(fromList != null) {
					for(Node fromnode: fromList) {
						tmpFromList.add(fromnode);
					}
				}
				tmpFromList.addAll(ctrlNodeNewToList);
				updateCtrlNode.setNode_fromList(tmpFromList);
				ctrlNodes.set(j, updateCtrlNode);
			}
		}
		return ctrlNodes;
	}

	private List<Node> addNodeFromList(Node node, List<Node> intNodeList, List<Node> ctrlNodeNewToList, Node fromNode) {
		for(int j = intNodeList.size() - 1; j >= 0; j--) {
			Node intnode = intNodeList.get(j);
			if(intnode.getNode_no() == node.getNode_no() && intnode.getNode_type().equals(node.getNode_type())) {
				Node updateNode = new Node();
				updateNode = copyIntNode(updateNode, intnode);
				List<Node> fromList = intnode.getNode_fromList();
				for(Node tmpFromNode: fromList) {
					if(tmpFromNode.getNode_no() == fromNode.getNode_no() && tmpFromNode.getNode_type().equals(fromNode.getNode_type())) {
						fromList.remove(tmpFromNode);
						break;
					}
				}
				List<Node> tmpFromList = new ArrayList<Node>();
				if(fromList != null) {
					for(Node fromnode: fromList) {
						tmpFromList.add(fromnode);
					}
				}
				tmpFromList.addAll(ctrlNodeNewToList);
				updateNode.setNode_fromList(tmpFromList);
				intNodeList.set(j, updateNode);
			}
		}
		return intNodeList;
	}

	private List<CtrlNode> addCtrlNodeToList(Node node, List<CtrlNode> ctrlNodes, List<Node> ctrlNodeNewFromList, Node toNode) {
		for(int i = ctrlNodes.size() - 1; i >= 0; i--) {
			CtrlNode ctrlnode = ctrlNodes.get(i);
			if(ctrlnode.getNode_no() == node.getNode_no() && ctrlnode.getNode_type().equals(node.getNode_type())) {
				CtrlNode updateCtrlNode = new CtrlNode();
				updateCtrlNode = copyCtrlNode(updateCtrlNode, ctrlnode);
				List<Node> toList = ctrlnode.getNode_toList();
				for(Node tmpToNode: toList) {
					if(tmpToNode.getNode_no() == toNode.getNode_no() && tmpToNode.getNode_type().equals(toNode.getNode_type())) {
						toList.remove(tmpToNode);
						break;
					}
				}
				List<Node> tmpToList = new ArrayList<Node>();
				if(toList != null) {
					for(Node tonode: toList) {
						tmpToList.add(tonode);
					}
				}
				tmpToList.addAll(ctrlNodeNewFromList);
				updateCtrlNode.setNode_toList(tmpToList);
//				updateCtrlNode.setNode_toList(ctrlNodeNewFromList);
				ctrlNodes.set(i, updateCtrlNode);
			}
		}
		return ctrlNodes;
	}

	private List<Node> addNodeToList(Node node, List<Node> intNodeList, List<Node> ctrlNodeNewFromList, Node toNode) {
		for(int i = intNodeList.size() - 1; i >= 0; i--) {
			Node intnode = intNodeList.get(i);
			if(intnode.getNode_no() == node.getNode_no() && intnode.getNode_type().equals(node.getNode_type())) {
				Node updateNode = new Node();
				updateNode = copyIntNode(updateNode, intnode);
				List<Node> toList = intnode.getNode_toList();
				for(Node tmpToNode: toList) {
					if(tmpToNode.getNode_no() == toNode.getNode_no() && tmpToNode.getNode_type().equals(toNode.getNode_type())) {
						toList.remove(tmpToNode);
						break;
					}
				}
				List<Node> tmpToList = new ArrayList<Node>();
				if(toList != null) {
					for(Node tonode: toList) {
						tmpToList.add(tonode);
					}
				}
				tmpToList.addAll(ctrlNodeNewFromList);
				updateNode.setNode_toList(tmpToList);
//				updateNode.setNode_toList(ctrlNodeNewFromList);
				intNodeList.set(i, updateNode);
			}
		}
		return intNodeList;
	}

	private List<CtrlNode> getCtrlNodeListBySG(List<ScenarioGraph> SGList){
		List<CtrlNode> ctrlNodeList = new ArrayList<CtrlNode>();
		for(ScenarioGraph SG: SGList) {
			List<CtrlNode> SGCtrlNodeList = SG.getCtrlNodeList();
			for(int i = 0; i < SGCtrlNodeList.size(); i++) {
				CtrlNode ctrlNode = SGCtrlNodeList.get(i);
				CtrlNode SGCtrlNode = new CtrlNode();
				SGCtrlNode.setNode_no(ctrlNode.getNode_no());
				SGCtrlNode.setNode_text(ctrlNode.getNode_text());
				SGCtrlNode.setNode_type(ctrlNode.getNode_type());
				SGCtrlNode.setNode_x(ctrlNode.getNode_x());
				SGCtrlNode.setNode_y(ctrlNode.getNode_y());
				SGCtrlNode.setDelay_type(ctrlNode.getDelay_type());
				SGCtrlNode.setNode_consition1(ctrlNode.getNode_consition1());
				SGCtrlNode.setNode_consition2(ctrlNode.getNode_consition2());
				SGCtrlNode.setNode_fromList(ctrlNode.getNode_fromList());
				SGCtrlNode.setNode_toList(ctrlNode.getNode_toList());
				ctrlNodeList.add(SGCtrlNode);
			}
		}
		return ctrlNodeList;
	}
	
	private List<Node> getIntNodeListBySG(List<ScenarioGraph> SGList){
		List<Node> intNodeList = new ArrayList<Node>();
		for (ScenarioGraph SG: SGList) { 
			List<Node> SGIntNodeList = SG.getIntNodeList();
			for(int i = 0; i < SGIntNodeList.size(); i++) {
				Node intNode = SGIntNodeList.get(i);
				Node SGIntNode = new Node();
				SGIntNode.setNode_no(intNode.getNode_no());
				SGIntNode.setNode_type(intNode.getNode_type());
				SGIntNode.setNode_x(intNode.getNode_x());
				SGIntNode.setNode_y(intNode.getNode_y());
				SGIntNode.setNode_fromList(intNode.getNode_fromList());
				SGIntNode.setNode_toList(intNode.getNode_toList());
				SGIntNode.setPre_condition(intNode.getPre_condition());
				SGIntNode.setPost_condition(intNode.getPost_condition());
				intNodeList.add(SGIntNode);
			}
		}
		return intNodeList;
	}
	
	private List<Line> getLineListBySG(List<ScenarioGraph> SGList){
		List<Line> lineList = new ArrayList<Line>();
		for (ScenarioGraph SG: SGList) { 
			List<Line> SGLineList = SG.getLineList();
			for(int i = 0; i < SGLineList.size(); i++) {
				Line line = SGLineList.get(i);
				Line SGLine = new Line();
				SGLine.setLine_no(line.getLine_no());
				SGLine.setLine_type(line.getLine_type());
				SGLine.setFromNode(line.getFromNode());
				SGLine.setToNode(line.getToNode());
				SGLine.setTurnings(line.getTurnings());
				SGLine.setCondition(line.getCondition());
				lineList.add(SGLine);
			}
		}
		return lineList;
	}
	
	private List<Node> addNodeList(List<Node> NodeList, Node lineNode) {
		if(lineNode.getPre_condition() == null && lineNode.getPost_condition() == null) {
			NodeList.add(lineNode);
		}
		return NodeList;
	}
	
	private List<Node> getStartNodeList(List<Node> startNodeList, List<CtrlNode> ctrlNodeList, List<Node> intNodeList, List<Node> sameNodes){
		for(int i = startNodeList.size() - 1; i >= 0; i--) {
			Node node = startNodeList.get(i);
			for(int j = i - 1; j >= 0; j--) {
				Node node2 = startNodeList.get(j);
				if(node.getNode_no() == node2.getNode_no() && node.getNode_type().equals(node2.getNode_type())) {
					startNodeList.remove(node);
					break;
				}
			}
			for(CtrlNode ctrlNode: ctrlNodeList) {
				if(node.getNode_no() == ctrlNode.getNode_no() && node.getNode_type().equals(ctrlNode.getNode_type())) {
					startNodeList.remove(node);
					if(node.getNode_type().equals("Merge") || node.getNode_type().equals("Branch")) {
						for(Node ctrlToNode: ctrlNode.getNode_toList()) {
							int sameNo = 0;
							boolean flag = true;
							for(Node sameNode: sameNodes) {
								if(ctrlToNode.getNode_no() == sameNode.getNode_no() && ctrlToNode.getNode_type().equals(sameNode.getNode_type())) {
									flag = false;
								}
							}
							for(Node intNode: intNodeList) {
								if(ctrlToNode.getNode_no() == intNode.getNode_no() && ctrlToNode.getNode_type().equals(intNode.getNode_type())) {
									if(intNode.getPre_condition() == null) {
										sameNo++;
									}
								}
							}
							if(flag && sameNo == 1) {
								startNodeList.add(ctrlToNode);
							}
						}
					} else {
						startNodeList.add(ctrlNode);
					}
					break;
				}
			}
		}
		return startNodeList;
	}
	
	private List<Node> getEndNodeList(List<Node> endNodeList, List<CtrlNode> ctrlNodeList, List<Node> intNodeList, List<Node> sameNodes){
		for(int i = endNodeList.size() - 1; i >= 0; i--) {
			Node node = endNodeList.get(i);
			for(int j = i - 1; j >= 0; j--) {
				Node node2 = endNodeList.get(j);
				if(node.getNode_no() == node2.getNode_no() && node.getNode_type().equals(node2.getNode_type())) {
					endNodeList.remove(node);
					break;
				}
			}
			for(CtrlNode ctrlNode: ctrlNodeList) {
				if(node.getNode_no() == ctrlNode.getNode_no() && node.getNode_type().equals(ctrlNode.getNode_type())) {
					endNodeList.remove(node);
					if(node.getNode_type().equals("Merge") || node.getNode_type().equals("Branch")) {
						for(Node ctrlFromNode: ctrlNode.getNode_fromList()) {
							int sameNo = 0;
							boolean flag = true;
							for(Node sameNode: sameNodes) {
								if(ctrlFromNode.getNode_no() == sameNode.getNode_no() && ctrlFromNode.getNode_type().equals(sameNode.getNode_type())) {
									flag = false;
								}
							}
							for(Node intNode: intNodeList) {
								if(ctrlFromNode.getNode_no() == intNode.getNode_no() && ctrlFromNode.getNode_type().equals(intNode.getNode_type())) {
									if(intNode.getPost_condition() == null) {
										sameNo++;
									}
								}
							}
							if(flag && sameNo == 1) {
								endNodeList.add(ctrlFromNode);
							}
						}
					} else {
						endNodeList.add(ctrlNode);
					}
					break;
				}
			}
		}
		return endNodeList;
	}
	
	private CtrlNode addStartNode(List<Node> startNodeFromList, List<Node> startNodeToList, int node_no){
		CtrlNode startNode = new CtrlNode();
		if(startNodeToList.size() >= 1) {
			
			startNode.setNode_no(node_no);
			startNode.setNode_type("Start");
			startNode.setNode_fromList(startNodeFromList);
			startNode.setNode_toList(startNodeToList);
			if(node_no == 1) {
				startNode.setNode_x(200);
				startNode.setNode_y(50);
			} else {
				startNode.setNode_x(600);
				startNode.setNode_y(50);
			}
//			ctrlNodes.add(startNode);
		}
		return startNode;
	}
	
	private CtrlNode addEndNode(List<Node> endNodeFromList, List<Node> endNodeToList, int node_no){
		CtrlNode endNode = new CtrlNode();
		if(endNodeFromList.size() >= 1) {
			endNode.setNode_no(node_no);
			endNode.setNode_type("End");
			endNode.setNode_fromList(endNodeFromList);
			endNode.setNode_toList(endNodeToList);
		}
		return endNode;
	}
	
	private boolean compareList(List<Node> list1, List<Node> list2) {
		if(list1 == null && list2 == null) {
			return true;
		}
		if(list1 == null || list2 == null || list1.size() != list2.size())
			return false;
		for(int i = 0; i < list1.size(); i++){
			for(int j = 0; j < list2.size(); j++)
			if(list1.get(i).getNode_no() != list2.get(j).getNode_no()
					|| !list1.get(i).getNode_type().equals(list2.get(j).getNode_type()))
				return false;
		}
		return true;
	}
	
	private List<Node> addIntNodeFromList(List<Node> intNode1FromList, List<Node> intNode2FromList) {
		List<Node> intNodeFromList = new ArrayList<Node>();
		for(Node node: intNode1FromList) {
			if(!node.getNode_type().equals("Branch")) {
				intNodeFromList.add(node);
			} 
		}
		for(Node node: intNode2FromList) {
			if(!node.getNode_type().equals("Branch")) {
				intNodeFromList.add(node);
			} 
		}
		return intNodeFromList;
	}
	
	private List<Node> addIntNodeToList(List<Node> intNode1ToList, List<Node> intNode2ToList) {
		List<Node> intNodeToList = new ArrayList<Node>();
		for(Node node: intNode1ToList) {
			if(!node.getNode_type().equals("Merge")) {
				intNodeToList.add(node);
			} 
		}
		for(Node node: intNode2ToList) {
			if(!node.getNode_type().equals("Merge")) {
				intNodeToList.add(node);
			} 
		}
		return intNodeToList;
	}
	
	private CtrlNode newMergeNode(Node node, int mergeNo, List<Node> nodeFromList, List<Node> intNodeList) {
		CtrlNode mergeNode = new CtrlNode();
		List<Node> mergeNodeToList = new ArrayList<Node>();
		mergeNodeToList.add(node);
		mergeNode.setNode_no(mergeNo);
		mergeNode.setNode_type("Merge");
		mergeNode.setNode_fromList(nodeFromList);
		mergeNode.setNode_toList(mergeNodeToList);
		return mergeNode;
	}
	
	private CtrlNode newBranchNode(Node node, int branchNo, List<Node> nodeToList) {
		CtrlNode branchNode = new CtrlNode();
		List<Node> branchNodeFromList = new ArrayList<Node>();
		branchNodeFromList.add(node);
		branchNode.setNode_no(branchNo);
		branchNode.setNode_type("Branch");
		branchNode.setNode_fromList(branchNodeFromList);
		branchNode.setNode_toList(nodeToList);
		return branchNode;
	}
	
	private Node copyIntNode(Node updateNode, Node intnode) {
		updateNode.setNode_no(intnode.getNode_no());
		updateNode.setNode_type(intnode.getNode_type());
		updateNode.setNode_x(intnode.getNode_x());
		updateNode.setNode_y(intnode.getNode_y());
		updateNode.setNode_fromList(intnode.getNode_fromList());
		updateNode.setNode_toList(intnode.getNode_toList());
		updateNode.setPre_condition(intnode.getPre_condition());
		updateNode.setPost_condition(intnode.getPost_condition());
		return updateNode;
	}
	
	private CtrlNode copyCtrlNode(CtrlNode copyCtrlNode, CtrlNode ctrlNode) {
		copyCtrlNode.setNode_no(ctrlNode.getNode_no());
		copyCtrlNode.setNode_type(ctrlNode.getNode_type());
		copyCtrlNode.setNode_x(ctrlNode.getNode_x());
		copyCtrlNode.setNode_y(ctrlNode.getNode_y());
		copyCtrlNode.setNode_fromList(ctrlNode.getNode_fromList());
		copyCtrlNode.setNode_toList(ctrlNode.getNode_toList());
		copyCtrlNode.setDelay_type(ctrlNode.getDelay_type());
		copyCtrlNode.setNode_text(ctrlNode.getNode_text());
		copyCtrlNode.setNode_consition1(ctrlNode.getNode_consition1());
		copyCtrlNode.setNode_consition2(ctrlNode.getNode_consition2());
		return copyCtrlNode;
	}
	
	private Line copyLine(Line newLine, Line line) {
		newLine.setLine_no(line.getLine_no());
		newLine.setLine_type(line.getLine_type());
		newLine.setFromNode(line.getFromNode());
		newLine.setToNode(line.getToNode());
		newLine.setCondition(line.getCondition());
		newLine.setTurnings(line.getTurnings());
		return newLine;
	}
	
	private Node copyLineNode(Node lineToNode, CtrlNode node) {
		lineToNode.setNode_no(node.getNode_no());
		lineToNode.setNode_type(node.getNode_type());
		lineToNode.setNode_x(node.getNode_x());
		lineToNode.setNode_y(node.getNode_y());
		return lineToNode;
	}
}
