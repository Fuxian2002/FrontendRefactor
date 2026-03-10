package com.example.demo.LSP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.websocket.Session;

import com.example.demo.bean.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.LSP.bean.DiagramContentChangeEvent;
import com.example.demo.service.ASTService;
import com.example.demo.service.EMFService;
import com.example.demo.service.FileOperation;
import com.github.gumtreediff.tree.EMFTreeContext;
import com.github.gumtreediff.tree.ITree;

import lombok.extern.log4j.Log4j2;

//@Log4j2(topic = "log")
public class LSPDiagramObserver extends LSPObserver {

	private ArrayList<DiagramContentChangeEvent> contentChanges = new ArrayList<DiagramContentChangeEvent>();
	private ArrayList<DiagramContentChangeEvent> ccFromEditor = new ArrayList<DiagramContentChangeEvent>();

	private final static Logger log = LogManager.getLogger("log");
	private final static Logger serverurilogger = LogManager.getLogger("serveruri");
	// ==========================初始化====================
	// public LSPDiagramObserver(Session session, String uri, Project project) {
	// super(session, uri, "diagram");
	// toEMFFile(project);
	// iniContext();
	// notifyClient("registered", "registered");
	// // recordNodeNumber();
	// startTime = Calendar.getInstance().getTime().toString();
	// }

	public LSPDiagramObserver(Session session, String uri, Project project,
			String username) {
		super(session, uri, "diagram", username);
		System.out.println("LSPDiagramObserver");
		System.out.println("project: int: "+project.getContextDiagram().getInterfaceList().size()
				+" ref: "+project.getProblemDiagram().getReferenceList().size()
				+" con: "+project.getProblemDiagram().getConstraintList().size());
		toEMFFile(project);
		iniContext();
		notifyClient("registered", "registered");
		startTime = Calendar.getInstance().getTime().toString();
	}

	public LSPDiagramObserver(Session session, String uri,
			String projectAddress, String version, String username) {
		super(session, uri, "diagram", projectAddress, version, username);
		startTime = Calendar.getInstance().getTime().toString();
	}

	private boolean toEMFFile(Project project) {
		this.project = project;
		EMFService.ProjectToEMF(editorPath, project);
		EMFService.deleteIndent(editorPath);
		System.out.printf("toEMFFile---editorPath:" + editorPath + "  newestPath:"+newestPath);
		FileOperation.copyFile(editorPath, newestPath);
		return true;
	}

	synchronized private void recordNodeNumber() {
		System.out.println("-----recordNodeNumber-");
		int nodeNumber = 0;
		if (project == null)
			return;
		if (project.getIntentDiagram().getSystem() != null)
			nodeNumber += 1;
		if (project.getIntentDiagram().getExternalEntityList() != null)
			nodeNumber += project.getIntentDiagram().getExternalEntityList().size();
		if (project.getIntentDiagram().getInterfaceList() != null)
			nodeNumber += project.getIntentDiagram().getInterfaceList().size();
		if (project.getContextDiagram().getMachine() != null)
			nodeNumber += 1;
		if (project.getContextDiagram().getProblemDomainList() != null)
			nodeNumber += project.getContextDiagram().getProblemDomainList().size();
		if (project.getProblemDiagram().getRequirementList() != null)
			nodeNumber += project.getProblemDiagram().getRequirementList().size();
		int linkNumber = 0;
		if (project.getIntentDiagram().getInterfaceList() != null)
			linkNumber += project.getIntentDiagram().getInterfaceList().size();
		if (project.getIntentDiagram().getConstraintList() != null)
			linkNumber += project.getIntentDiagram().getConstraintList().size();
		if (project.getIntentDiagram().getReferenceList() != null)
			linkNumber += project.getIntentDiagram().getReferenceList().size();
		if (project.getContextDiagram().getInterfaceList() != null)
			linkNumber += project.getContextDiagram().getInterfaceList().size();
		if (project.getProblemDiagram().getConstraintList() != null)
			linkNumber += project.getProblemDiagram().getConstraintList().size();
		if (project.getProblemDiagram().getReferenceList() != null)
			linkNumber += project.getProblemDiagram().getReferenceList().size();
		this.nodeNumber=nodeNumber;
		this.linkNumber = linkNumber;
		if(subject!=null) {
			subject.nodeNumber = nodeNumber;
			subject.linkNumber = linkNumber;
			if (ASTnodeNumber != 0)
				subject.ASTnodeNumber = ASTnodeNumber;
		}		
		System.out.println("nodeNumber=" + nodeNumber + ", linkNumber = " + linkNumber);
	}
	
	public void setTreeAddGetLastestData(EMFTreeContext newest) {
		super.setTreeAddGetLastestData(newest);

		System.out.println("------setTreeAddGetLastestData");
		// EMF AST -> EMF file
		ASTService.generateEMFXmlFile(newest.getRoot(), newestPath);

		// emf file -> project
		project = EMFService.emfFile2Project(newestPath);
		notifyClient("registered", "registered");
	}

	public void setTree(EMFTreeContext newest) {
		super.setTree(newest);
		diff1("setTree_newest", newestContext, editorContext);
		System.out.println("-------setTree");
		// EMF AST -> EMF file
		ASTService.generateEMFXmlFile(newest.getRoot(), newestPath);

		// emf file -> project
		project = EMFService.emfFile2Project(newestPath);
		notifyClient("registered", "registered");
	}

	public void notifyClient(String mes, String method) {
		long ST3=System.currentTimeMillis();
		newestContext.setSt3(ST3);
		JSONObject message = new JSONObject();
		message.put("jsonrpc", "2.0");
		message.put("id", id++);
		message.put("message", mes);// ?
		message.put("method", "Diagram/" + method);

		JSONObject params = new JSONObject();

		JSONObject diagram = new JSONObject();
		diagram.put("uri", uri);
		diagram.put("version", newestContext.getVersion());
		if (lastContext != null)
			diagram.put("lastVersion", lastContext.getVersion());
		diagram.put("T0", newestContext.getT0());
		diagram.put("T1", newestContext.getT1());
		diagram.put("T2", newestContext.getT2());
		diagram.put("T3", newestContext.getT3());
		diagram.put("ST0", newestContext.getSt0());
		diagram.put("ST1", newestContext.getSt1());
		diagram.put("ST2", newestContext.getSt2());
		diagram.put("ST3", newestContext.getSt3());
		diagram.put("Flag1", newestContext.getFlag1());
		diagram.put("Flag2", newestContext.getFlag2());
		diagram.put("Flag3", newestContext.getFlag3());		

		recordNodeNumber();
		diagram.put("nodes", nodeNumber);
		diagram.put("edges", linkNumber);
		diagram.put("ASTNodes", ASTnodeNumber);
		
		params.put("diagram", diagram);

		if (method.contentEquals("registered")) {
			JSONArray contentChanges = new JSONArray();
			JSONObject contentChange = new JSONObject();
			contentChange.put("shapeType", "project");
			contentChange.put("newProject", project);
			contentChanges.add(contentChange);
			params.put("contentChanges", contentChanges);
		}
		if (method.contentEquals("didChange")) {
			if (this.contentChanges.size() == 0)
				return;
			params.put("contentChanges", this.contentChanges);
		}
		message.put("params", params);
		recordServerSynTime(diagram);
		synchronized (this) {
			try {
				session.getBasicRemote().sendText(JSON.toJSONString(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void recordServerSynTime(JSONObject diagram) {
		String uri = diagram.getString("uri");
		String lastVersion = diagram.getString("lastVersion");
		long ST0 = diagram.getLongValue("ST0");
		long ST1 = diagram.getLongValue("ST1");
		long ST2 = diagram.getLongValue("ST2");
		long ST3 = diagram.getLongValue("ST3");
//		long T4 = params.getLongValue("T4");
		int Flag1 = diagram.getIntValue("Flag1");
		int Flag2 = diagram.getIntValue("Flag2");
		int Flag3 = diagram.getIntValue("Flag3");
		String str = "";
		str += "uri:" + uri;
		str += ",\tlastVersion:" + lastVersion;
//		str += ",\tfromuuid:" + fromuuid;
//		str += ",\tfromEditorType:" + fromEditorType;
//		str += ",\ttouuid:" + touuid;
//		str += ",\ttoEditorType:" + toEditorType;
		str += ",\tnodes:" + nodeNumber;
		str += ",\tedges:" + linkNumber;
		str += ",\tbytes:" + 0;
		str += ",\tASTNodes:" + ASTnodeNumber;
		str += ",\tST0:" + ST0;
		str += ",\tST1:" + ST1;
		str += ",\tST2:" + ST2;
		str += ",\tST3:" + ST3;
//		str += ",\tT4:" + T4;
		str += ",\tFlag1:" + Flag1;
		str += ",\tFlag2:" + Flag2;
		str += ",\tFlag3:" + Flag3;
		long synTime = ST3 - ST0;
		str += ",\tsynTime:" + synTime;
		if (ST3 == 0 || ST0 == 0) {
//			System.err.println(str);
			return;
		}
		serverurilogger.info(str);
	}
	

	// ========================change=====================
	// 更新本地project并发送给客户端
	public boolean change(String message) {
		long ST0=System.currentTimeMillis();
		recordLSPMessage(message);
		EMFTreeContext temp = syn_change(message,ST0);
//		System.out.println("editor1 change:"+temp.time());
		if (temp == null)
			return false;
		else {
			try {
				subject.setValue(temp,editorType+" "+session.getId());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	private synchronized EMFTreeContext syn_change(String message,long ST0) {
		System.out.println("-----------syn_change");
		long timeStart = new Date().getTime();
		// 根据message 修改 project
		Project pro = dealWithMessage(message);
		// System.out.println(message);

		// 将修改后的 project 存为EMF-XMI文件(edi.xml)
		EMFService.ProjectToEMF(editorPath, pro);

		// 根据editorContext 修改 newestContext
		change(pro.getTime(),ST0);

		// 修改后端project
		Project newProject = EMFService.emfFile2Project(newestPath);
		contentChanges = getContentChanges(newProject);

		// 发送给远端
		if (newestContext.getFlag1() == 3) {
			for (DiagramContentChangeEvent cc : contentChanges) {
				for (DiagramContentChangeEvent oldcc : ccFromEditor) {
					if (cc.getChangeType().contentEquals("add")
							&& oldcc.getChangeType().contentEquals("add")
							&& cc.getShapeType().contentEquals(oldcc.getShapeType())
							&& cc.getNewShape().getName().contentEquals(oldcc.getNewShape().getName())) {
						if (cc.getShapeType().contentEquals("mac")
								|| cc.getShapeType().contentEquals("pro")
								|| cc.getShapeType().contentEquals("req")
								|| cc.getShapeType().contentEquals("sys")
								|| cc.getShapeType().contentEquals("ext")
								|| cc.getShapeType().contentEquals("tas")) {
							((PdNode) cc.getNewShape())
									.setX(((PdNode) oldcc.getNewShape()).getX());
							((PdNode) cc.getNewShape())
									.setY(((PdNode) oldcc.getNewShape()).getY());
						}
					}
				}
			}
			notifyClient(message, "didChange");
		} else {
			// System.out.println(message);
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		long timeEnd = new Date().getTime();
		newestContext.setT1(timeEnd - timeStart);
		return newestContext.deriveTree();
	}

	// --------change project according to change message---------
	public Project dealWithMessage(String message) {
		System.out.println("dealWithMessage");
		JSONObject json = JSONObject.parseObject(message);
		JSONObject params = (JSONObject) json.get("params");
		JSONObject diagram = (JSONObject) params.get("diagram");
		lastVersion = diagram.getString("lastVersion");
		long time = diagram.getLongValue("changeTime");
		JSONArray contentChanges = params.getJSONArray("contentChanges");
		Project pro = Project.copyProject(project);
		System.out.println("project:intention int: "+project.getIntentDiagram().getInterfaceList().size()
				+" ref: "+project.getIntentDiagram().getReferenceList().size()
				+" con: "+project.getIntentDiagram().getConstraintList().size());
		System.out.println("project: int: "+project.getContextDiagram().getInterfaceList().size()
				+" ref: "+project.getProblemDiagram().getReferenceList().size()
				+" con: "+project.getProblemDiagram().getConstraintList().size());
		pro.setTime(time);
		ccFromEditor.clear();
		for (Object contentChangeEvent : contentChanges) {
			JSONObject jcontentChange = (JSONObject) contentChangeEvent;
			String shapeType = jcontentChange.getString("shapeType");
			String changeType = jcontentChange.getString("changeType");
			switch (changeType) {
			case "add":
				ccFromEditor.add(add(pro, shapeType, jcontentChange));
				break;
			case "delete":
				ccFromEditor.add(delete(pro, shapeType, jcontentChange));
				break;
			case "change":
				ccFromEditor.add(change(pro, shapeType, jcontentChange));
				break;
			}
		}
		return pro;
	}

	public DiagramContentChangeEvent add(Project project, String shape,
			JSONObject jcontentChange) {
		System.out.println("add:"+shape);
		String snewShape = jcontentChange.getString("newShape");
		Shape newShape = null;
		switch (shape) {
			case "sys":
				newShape = JSON.parseObject(snewShape, ESystem.class);
				if (project == null)
					System.out.println("project==null");
				else if (project.getIntentDiagram() == null)
					System.out.println(" project.getIntentDiagram()==null");
				else {
					project.getIntentDiagram().setSystem((ESystem) newShape);
				}
				break;
			case "ext":
				newShape = JSON.parseObject(snewShape, ExternalEntity.class);
				project.getIntentDiagram().getExternalEntityList()
						.add((ExternalEntity) newShape);
				break;
			case "tas":
				newShape = JSON.parseObject(snewShape, Intent.class);
				project.getIntentDiagram().getIntentList()
						.add((Intent) newShape);
				break;
			case "mac":
				newShape = JSON.parseObject(snewShape, Machine.class);
				if (project == null)
					System.out.println("project==null");
				else if (project.getContextDiagram() == null)
					System.out.println(" project.getContextDiagram()==null");
				else {
					project.getContextDiagram().setMachine((Machine) newShape);
				}
				break;
			case "pro":
				newShape = JSON.parseObject(snewShape, ProblemDomain.class);
				project.getContextDiagram().getProblemDomainList()
						.add((ProblemDomain) newShape);
				break;
			case "req":
				newShape = JSON.parseObject(snewShape, Requirement.class);
				project.getProblemDiagram().getRequirementList()
						.add((Requirement) newShape);
				break;
			case "int":
				newShape = JSON.parseObject(snewShape, Interface.class);
				Interface newint = (Interface) newShape;
				if(newint.isIsintent()){
					project.getIntentDiagram().getInterfaceList()
							.add((Interface) newShape);
				}else {
					project.getContextDiagram().getInterfaceList()
							.add((Interface) newShape);
				}
				break;
			case "ref":
				newShape = JSON.parseObject(snewShape, Reference.class);
				Reference newref = (Reference) newShape;
				if(newref.isIsintent()){
					project.getIntentDiagram().getReferenceList()
							.add((Reference) newShape);
				}else{
					project.getProblemDiagram().getReferenceList()
							.add((Reference) newShape);
				}
				break;
			case "con":
				newShape = JSON.parseObject(snewShape, Constraint.class);
				Constraint newcon = (Constraint) newShape;
				if(newcon.isIsintent()){
					project.getIntentDiagram().getConstraintList()
							.add((Constraint) newShape);
				}else{
					project.getProblemDiagram().getConstraintList()
							.add((Constraint) newShape);
				}

		}
		return new DiagramContentChangeEvent(shape, "add", null, newShape);
	}

	public DiagramContentChangeEvent delete(Project project, String shape,
			JSONObject jcontentChange) {
		String soldShape = jcontentChange.getString("oldShape");
		Shape oldShape = null;
		switch (shape) {
			case "sys":
				oldShape = JSON.parseObject(soldShape, ESystem.class);
				deleteRelatedLines2(project, oldShape);
				project.getIntentDiagram().setSystem(null);
				break;
			case "ext":
				oldShape = JSON.parseObject(soldShape, ExternalEntity.class);
				deleteRelatedLines2(project, oldShape);
				delete((ExternalEntity) oldShape,
						project.getIntentDiagram().getExternalEntityList());
				break;
			case "tas":
				oldShape = JSON.parseObject(soldShape, Intent.class);
				deleteRelatedLines2(project, oldShape);
				delete((Intent) oldShape,
						project.getIntentDiagram().getIntentList());
				break;
			case "mac":
				oldShape = JSON.parseObject(soldShape, Machine.class);
				deleteRelatedLines(project, oldShape);
				project.getContextDiagram().setMachine(null);
				break;
			case "pro":
				oldShape = JSON.parseObject(soldShape, ProblemDomain.class);
				deleteRelatedLines(project, oldShape);
				delete((ProblemDomain) oldShape,
						project.getContextDiagram().getProblemDomainList());
				break;
			case "req":
				oldShape = JSON.parseObject(soldShape, Requirement.class);
				deleteRelatedLines(project, oldShape);
				delete((Requirement) oldShape,
						project.getProblemDiagram().getRequirementList());
				break;
			case "int":
				oldShape = JSON.parseObject(soldShape, Interface.class);
				Interface oldint = (Interface) oldShape;
				if(oldint.isIsintent()) {
					delete((Interface) oldShape,
							project.getIntentDiagram().getInterfaceList());
				}else{
					delete((Interface) oldShape,
							project.getContextDiagram().getInterfaceList());
				}
				break;
			case "ref":
				oldShape = JSON.parseObject(soldShape, Reference.class);
				Reference oldref = (Reference) oldShape;
				if(oldref.isIsintent()) {
					delete((Reference) oldShape,
							project.getIntentDiagram().getReferenceList());
				}else{
					delete((Reference) oldShape,
							project.getProblemDiagram().getReferenceList());
				}
				break;
			case "con":
				oldShape = JSON.parseObject(soldShape, Constraint.class);
				Constraint oldcon = (Constraint) oldShape;
				if(oldcon.isIsintent()) {
					delete((Constraint) oldShape,
							project.getIntentDiagram().getConstraintList());
				}else{
					delete((Constraint) oldShape,
							project.getProblemDiagram().getConstraintList());
				}
				break;
		}
		return new DiagramContentChangeEvent(shape, "delete", oldShape, null);
	}

	private boolean delete(Intent shape, List<Intent> list) {
		if (shape == null)
			return false;
		for (Shape item : list) {
			if (item.getName().contentEquals(shape.getName())) {
				list.remove(item);
				return true;
			}
		}
		return false;
	}

	private boolean delete(ExternalEntity shape, List<ExternalEntity> list) {
		if (shape == null)
			return false;
		for (Shape item : list) {
			if (item.getName().contentEquals(shape.getName())) {
				list.remove(item);
				return true;
			}
		}
		return false;
	}

	public boolean delete(ProblemDomain shape, List<ProblemDomain> list) {
		if (shape == null)
			return false;
		for (Shape item : list) {
			if (item.getName().contentEquals(shape.getName())) {
				list.remove(item);
				return true;
			}
		}
		return false;
	}

	public boolean delete(Requirement shape, List<Requirement> list) {
		for (Shape item : list) {
			if (item.getName().contentEquals(shape.getName())) {
				list.remove(item);
				return true;
			}
		}
		return false;
	}

	public boolean delete(Reference shape, List<Reference> list) {
		for (Shape item : list) {
			if (item.getName().contentEquals(shape.getName())) {
				list.remove(item);
				return true;
			}
		}
		return false;
	}

	public boolean delete(Interface shape, List<Interface> list) {
		for (Shape item : list) {
			if (item.getName().contentEquals(shape.getName())) {
				list.remove(item);
				return true;
			}
		}
		return false;
	}

	public boolean delete(Constraint shape, List<Constraint> list) {
		for (Shape item : list) {
			if (item.getName().contentEquals(shape.getName())) {
				list.remove(item);
				return true;
			}
		}
		return false;
	}
	//contextdiagram+problemdiagram
	private void deleteRelatedLines(Project project, Shape delete) {
		System.out.println("--------deleteRelatedLines-----");
		List<Interface> interfaceList = project.getContextDiagram()
				.getInterfaceList();
		List<Interface> interfaceList2 = project.getIntentDiagram().
				getInterfaceList();
		List<Reference> referenceList = project.getProblemDiagram()
				.getReferenceList();
		List<Constraint> constraintList = project.getProblemDiagram()
				.getConstraintList();
		if (delete instanceof Machine) {
			Machine machine = (Machine) delete;
			List<Interface> deleteInterfaceList = new LinkedList<>();
			for (Interface interfacee : interfaceList) {
				if (interfacee.getInterface_to().equals(machine.getShortname())
						|| interfacee.getInterface_from()
								.equals(machine.getShortname())) {
					interfacee.getPhenomenonList().clear();
					deleteInterfaceList.add(interfacee);
				}
			}
			interfaceList.removeAll(deleteInterfaceList);
		}
		if (delete instanceof ProblemDomain) {
			ProblemDomain problemDomain = (ProblemDomain) delete;
			List<Interface> deleteInterfaceList = new LinkedList<>();
			for (Interface interfacee : interfaceList) {
				if (interfacee.getInterface_to().equals(problemDomain.getShortname())
						|| interfacee.getInterface_from()
								.equals(problemDomain.getShortname())) {
					interfacee.getPhenomenonList().clear();
					deleteInterfaceList.add(interfacee);
				}
			}
			interfaceList.removeAll(deleteInterfaceList);

			List<Interface> deleteInterfaceList2 = new LinkedList<>();
			for (Interface interfacee : interfaceList2) {
				if (interfacee.getInterface_to().equals(problemDomain.getShortname())
						|| interfacee.getInterface_from()
						.equals(problemDomain.getShortname())) {
					interfacee.getPhenomenonList().clear();
					deleteInterfaceList2.add(interfacee);
				}
			}
			interfaceList2.removeAll(deleteInterfaceList2);
			List<Reference> deleteReferebceList = new LinkedList<>();
			for (Reference reference : referenceList) {
				if (reference.getReference_to().equals(problemDomain.getShortname())
						|| reference.getReference_from()
								.equals(problemDomain.getShortname())) {
					reference.getPhenomenonList().clear();
					deleteReferebceList.add(reference);

				}
			}
			referenceList.removeAll(deleteReferebceList);

			List<Constraint> deleteConstraintList = new LinkedList<>();
			for (Constraint constraint : constraintList) {
				if (constraint.getConstraint_to().equals(problemDomain.getShortname())
						|| constraint.getConstraint_from()
								.equals(problemDomain.getShortname())) {
					constraint.getPhenomenonList().clear();
					deleteConstraintList.add(constraint);

				}
			}
			constraintList.removeAll(deleteConstraintList);
		}
		else if (delete instanceof Requirement) {
			Requirement requirement = (Requirement) delete;

			List<Reference> deleteReferebceList = new LinkedList<>();
			for (Reference reference : referenceList) {
				if (reference.getReference_to().equals(requirement.getName())) {
					reference.getPhenomenonList().clear();
					deleteReferebceList.add(reference);
				} else if (reference.getReference_from().equals(requirement.getName())) {
					reference.getPhenomenonList().clear();
					deleteReferebceList.add(reference);
				}
			}
			referenceList.removeAll(deleteReferebceList);

			List<Constraint> deleteConstraintList = new LinkedList<>();
			for (Constraint constraint : constraintList) {
				if (constraint.getConstraint_to().equals(requirement.getName())) {
					constraint.getPhenomenonList().clear();
					deleteConstraintList.add(constraint);

				} else if (constraint.getConstraint_from().equals(requirement.getName())) {
					constraint.getPhenomenonList().clear();
					deleteConstraintList.add(constraint);

				}
			}
			constraintList.removeAll(deleteConstraintList);
		}
	}
	//intentDiagram
	private void deleteRelatedLines2(Project project, Shape delete) {
		System.out.println("--------deleteRelatedLines2-----");
		List<Interface> interfaceList = project.getIntentDiagram().
				getInterfaceList();
		List<Interface> interfaceList2 = project.getContextDiagram().
				getInterfaceList();
		List<Reference> referenceList = project.getIntentDiagram()
				.getReferenceList();
		List<Constraint> constraintList = project.getIntentDiagram()
				.getConstraintList();
		if (delete instanceof ESystem) {
			ESystem system = (ESystem) delete;
			List<Interface> deleteInterfaceList = new LinkedList<>();
			for (Interface interfacee : interfaceList) {
				if (interfacee.getInterface_to().equals(system.getShortname())
						|| interfacee.getInterface_from()
						.equals(system.getShortname())) {
					interfacee.getPhenomenonList().clear();
					deleteInterfaceList.add(interfacee);
				}
			}
			interfaceList.removeAll(deleteInterfaceList);
		}
		if (delete instanceof ExternalEntity) {
			ExternalEntity externalEntity = (ExternalEntity) delete;
			List<Interface> deleteInterfaceList = new LinkedList<>();
			for (Interface interfacee : interfaceList) {
				if (interfacee.getInterface_to().equals(externalEntity.getShortname())
						|| interfacee.getInterface_from()
						.equals(externalEntity.getShortname())) {
					interfacee.getPhenomenonList().clear();
					deleteInterfaceList.add(interfacee);
				}
			}

			interfaceList.removeAll(deleteInterfaceList);
			List<Interface> deleteInterfaceList2 = new LinkedList<>();
			for (Interface interfacee : interfaceList2) {
				if (interfacee.getInterface_to().equals(externalEntity.getShortname())
						|| interfacee.getInterface_from()
						.equals(externalEntity.getShortname())) {
					interfacee.getPhenomenonList().clear();
					deleteInterfaceList2.add(interfacee);
				}
			}
			interfaceList2.removeAll(deleteInterfaceList2);

			List<Reference> deleteReferebceList = new LinkedList<>();
			for (Reference reference : referenceList) {
				if (reference.getReference_to().equals(externalEntity.getShortname())
						|| reference.getReference_from()
						.equals(externalEntity.getShortname())) {
					reference.getPhenomenonList().clear();
					deleteReferebceList.add(reference);

				}
			}
			referenceList.removeAll(deleteReferebceList);

			List<Constraint> deleteConstraintList = new LinkedList<>();
			for (Constraint constraint : constraintList) {
				if (constraint.getConstraint_to().equals(externalEntity.getShortname())
						|| constraint.getConstraint_from()
						.equals(externalEntity.getShortname())) {
					constraint.getPhenomenonList().clear();
					deleteConstraintList.add(constraint);

				}
			}
			constraintList.removeAll(deleteConstraintList);
		}
		else if (delete instanceof Intent) {
			Intent intent = (Intent) delete;

			List<Reference> deleteReferebceList = new LinkedList<>();
			for (Reference reference : referenceList) {
				if (reference.getReference_to().equals(intent.getName())) {
					reference.getPhenomenonList().clear();
					deleteReferebceList.add(reference);
				} else if (reference.getReference_from().equals(intent.getName())) {
					reference.getPhenomenonList().clear();
					deleteReferebceList.add(reference);
				}
			}
			referenceList.removeAll(deleteReferebceList);

			List<Constraint> deleteConstraintList = new LinkedList<>();
			for (Constraint constraint : constraintList) {
				if (constraint.getConstraint_to().equals(intent.getName())) {
					constraint.getPhenomenonList().clear();
					deleteConstraintList.add(constraint);

				} else if (constraint.getConstraint_from().equals(intent.getName())) {
					constraint.getPhenomenonList().clear();
					deleteConstraintList.add(constraint);

				}
			}
			constraintList.removeAll(deleteConstraintList);
		}
	}

	public DiagramContentChangeEvent change(Project project, String shape,
			JSONObject jcontentChange) {
		String soldShape = jcontentChange.getString("oldShape");
		String snewShape = jcontentChange.getString("newShape");
		Shape oldShape = null;
		Shape newShape = null;
		switch (shape) {
			case "sys":
				newShape = JSON.parseObject(snewShape, ESystem.class);
				oldShape = JSON.parseObject(soldShape, ESystem.class);
				change(project, (ESystem) oldShape, (ESystem) newShape);
				break;
			case "ext":
				oldShape = JSON.parseObject(soldShape, ExternalEntity.class);
				newShape = JSON.parseObject(snewShape, ExternalEntity.class);
				change(project, (ExternalEntity) oldShape, (ExternalEntity) newShape);
				break;
			case "tas":
				oldShape = JSON.parseObject(soldShape, Intent.class);
				newShape = JSON.parseObject(snewShape, Intent.class);
				change(project, (Intent) oldShape, (Intent) newShape);
				break;
			case "mac":
				newShape = JSON.parseObject(snewShape, Machine.class);
				oldShape = JSON.parseObject(soldShape, Machine.class);
				change(project, (Machine) oldShape, (Machine) newShape);
				break;
			case "pro":
				oldShape = JSON.parseObject(soldShape, ProblemDomain.class);
				newShape = JSON.parseObject(snewShape, ProblemDomain.class);
				change(project, (ProblemDomain) oldShape, (ProblemDomain) newShape);
				break;
			case "req":
				oldShape = JSON.parseObject(soldShape, Requirement.class);
				newShape = JSON.parseObject(snewShape, Requirement.class);
				change(project, (Requirement) oldShape, (Requirement) newShape);
				break;
			case "int":
				oldShape = JSON.parseObject(soldShape, Interface.class);
				newShape = JSON.parseObject(snewShape, Interface.class);
				change(project, (Interface) oldShape, (Interface) newShape);
				break;
			case "ref":
				oldShape = JSON.parseObject(soldShape, Reference.class);
				newShape = JSON.parseObject(snewShape, Reference.class);
				change(project, (Reference) oldShape, (Reference) newShape);
				break;

			case "con":
				oldShape = JSON.parseObject(soldShape, Constraint.class);
				newShape = JSON.parseObject(snewShape, Constraint.class);
				change(project, (Constraint) oldShape, (Constraint) newShape);
				break;
		}
		return new DiagramContentChangeEvent(shape, "change", oldShape,
				newShape);
	}

	private boolean change(Project project, Intent old, Intent new1) {
		int i = 0;
		for (Intent item : project.getIntentDiagram().getIntentList()) {
			if (item.getName().contentEquals(old.getName())) {
				project.getIntentDiagram().getIntentList().set(i, new1);
				if (!new1.getName().contentEquals(old.getName())) {
					changeRelatedLink2(project, old.getName(), new1.getName());
				}
				return true;
			}
			i++;
		}
		return false;
	}

	public boolean change(Project project, ExternalEntity old,
						  ExternalEntity new1) {
		int i = 0;
		for (ExternalEntity item : project.getIntentDiagram().getExternalEntityList()) {
			if (item.getName().contentEquals(old.getName())) {
				project.getIntentDiagram().getExternalEntityList().set(i, new1);
				if (!new1.getShortname().contentEquals(old.getShortname())) {
					changeRelatedLink(project, old.getShortname(), new1.getShortname());
					changeRelatedLink2(project, old.getShortname(), new1.getShortname());
				}
				return true;
			}
			i++;
		}
		return false;
	}

	private boolean change(Project project, ESystem old, ESystem new1) {
		project.getIntentDiagram().setSystem((ESystem) new1);
		if (!new1.getShortname().contentEquals(old.getShortname())) {
			changeRelatedLink2(project, old.getShortname(), new1.getShortname());
		}
		return true;
	}

	public boolean change(Project project, Machine old, Machine new1) {
		project.getContextDiagram().setMachine((Machine) new1);
		project.getProblemDiagram().getContextDiagram()
				.setMachine((Machine) new1);
		if (!new1.getShortname().contentEquals(old.getShortname())) {
			changeRelatedLink(project, old.getShortname(), new1.getShortname());
		}
		return true;
	}

	public boolean change(Project project, ProblemDomain old, ProblemDomain new1) {
		int i = 0;
		for (ProblemDomain item : project.getContextDiagram()
				.getProblemDomainList()) {
			if (item.getName().contentEquals(old.getName())) {
				project.getContextDiagram().getProblemDomainList().set(i, new1);
				if (!new1.getShortname().contentEquals(old.getShortname())) {
					changeRelatedLink(project, old.getShortname(), new1.getShortname());
					changeRelatedLink2(project, old.getShortname(), new1.getShortname());
				}
				return true;
			}
			i++;
		}
		return false;
	}

	public boolean change(Project project, Requirement old, Requirement new1) {
		int i = 0;
		for (Requirement item : project.getProblemDiagram()
				.getRequirementList()) {
			if (item.getName().contentEquals(old.getName())) {
				project.getProblemDiagram().getRequirementList().set(i, new1);
				if (!new1.getName().contentEquals(old.getName())) {
					changeRelatedLink(project, old.getName(), new1.getName());
				}
				return true;
			}
			i++;
		}
		return false;
	}

	public boolean change(Project project, Reference old, Reference new1) {
		int i = 0;
		if(old.isIsintent()){
			for (Reference item : project.getIntentDiagram().getReferenceList()) {
				if (item.getReference_from().contentEquals(old.getReference_from())
						&& item.getReference_to().contentEquals(old.getReference_to())) {
					project.getIntentDiagram().getReferenceList().set(i, new1);
					return true;
				}
				i++;
			}
		}else{
			for (Reference item : project.getProblemDiagram().getReferenceList()) {
				if (item.getReference_from().contentEquals(old.getReference_from())
						&& item.getReference_to().contentEquals(old.getReference_to())) {
					project.getProblemDiagram().getReferenceList().set(i, new1);
					return true;
				}
				i++;
			}
		}

		return false;
	}

	public boolean change(Project project, Interface old, Interface new1) {
		int i = 0;
		if(old.isIsintent()) {
			for (Interface item : project.getIntentDiagram().getInterfaceList()) {
				if (item.getInterface_from().contentEquals(old.getInterface_from())
						&& item.getInterface_to().contentEquals(old.getInterface_to())) {
					project.getIntentDiagram().getInterfaceList().set(i, new1);
					return true;
				}
				i++;
			}
		}else {
			for (Interface item : project.getContextDiagram().getInterfaceList()) {
				if (item.getInterface_from().contentEquals(old.getInterface_from())
						&& item.getInterface_to().contentEquals(old.getInterface_to())) {
					project.getContextDiagram().getInterfaceList().set(i, new1);
					return true;
				}
				i++;
			}
		}
		return false;
	}

	public boolean change(Project project, Constraint old, Constraint new1) {
		int i = 0;
		if(old.isIsintent()) {
			for (Constraint item : project.getIntentDiagram().getConstraintList()) {
				if (item.getConstraint_from().contentEquals(old.getConstraint_from())
						&& item.getConstraint_to().contentEquals(old.getConstraint_to())) {
					project.getIntentDiagram().getConstraintList().set(i, new1);
					return true;
				}
				i++;
			}
		}else {
			for (Constraint item : project.getProblemDiagram().getConstraintList()) {
				if (item.getConstraint_from().contentEquals(old.getConstraint_from())
						&& item.getConstraint_to().contentEquals(old.getConstraint_to())) {
					project.getProblemDiagram().getConstraintList().set(i, new1);
					return true;
				}
				i++;
			}
		}
		return false;
	}
	//contextdiagram+problemdiagram
	public void changeRelatedLink(Project project, String oldShortName,
			String newShortName) {
		List<Interface> interfaceList = project.getContextDiagram()
				.getInterfaceList();
		List<Reference> referenceList = project.getProblemDiagram()
				.getReferenceList();
		List<Constraint> constraintList = project.getProblemDiagram()
				.getConstraintList();
		for (Interface interfacee : interfaceList) {
			if (interfacee.getInterface_from().contentEquals(oldShortName))
				interfacee.setInterface_from(newShortName);
			else if (interfacee.getInterface_to().contentEquals(oldShortName))
				interfacee.setInterface_to(newShortName);
			interfacee.refreshPhenomenonList(oldShortName, newShortName);
		}
		for (Reference reference : referenceList) {
			if (reference.getReference_from().contentEquals(oldShortName))
				reference.setReference_from(newShortName);
			else if (reference.getReference_to().contentEquals(oldShortName))
				reference.setReference_to(newShortName);
			reference.refreshPhenomenonList(oldShortName, newShortName);
		}
		for (Constraint constraint : constraintList) {
			if (constraint.getConstraint_from().contentEquals(oldShortName))
				constraint.setConstraint_from(newShortName);
			else if (constraint.getConstraint_to().contentEquals(oldShortName))
				constraint.setConstraint_to(newShortName);
			constraint.refreshPhenomenonList(oldShortName, newShortName);
		}

	}
	//intentdiagram
	public void changeRelatedLink2(Project project, String oldShortName,
								  String newShortName) {
		List<Interface> interfaceList = project.getIntentDiagram()
				.getInterfaceList();
		List<Reference> referenceList = project.getIntentDiagram()
				.getReferenceList();
		List<Constraint> constraintList = project.getIntentDiagram()
				.getConstraintList();
		for (Interface interfacee : interfaceList) {
			if (interfacee.getInterface_from().contentEquals(oldShortName))
				interfacee.setInterface_from(newShortName);
			else if (interfacee.getInterface_to().contentEquals(oldShortName))
				interfacee.setInterface_to(newShortName);
			interfacee.refreshPhenomenonList(oldShortName, newShortName);
		}
		for (Reference reference : referenceList) {
			if (reference.getReference_from().contentEquals(oldShortName))
				reference.setReference_from(newShortName);
			else if (reference.getReference_to().contentEquals(oldShortName))
				reference.setReference_to(newShortName);
			reference.refreshPhenomenonList(oldShortName, newShortName);
		}
		for (Constraint constraint : constraintList) {
			if (constraint.getConstraint_from().contentEquals(oldShortName))
				constraint.setConstraint_from(newShortName);
			else if (constraint.getConstraint_to().contentEquals(oldShortName))
				constraint.setConstraint_to(newShortName);
			constraint.refreshPhenomenonList(oldShortName, newShortName);
		}

	}
	// ================================merge======================
	public synchronized void update(EMFTreeContext remote) {
		System.out.println("------update");
		long st2 = new Date().getTime();
		String message = "";
		if (remote.getVersion().contentEquals(newestContext.getVersion())) {
			message = "diagram " + session.getId()+": from="+remote.getFrom()+","
					+ remote.getFlag1()+remote.getFlag2()+" 1,自己的修改，重置lastVersion,并发送修改时间戳";
			System.out.println(message);
			lastContext = remote.deriveTree();
			long time2 = new Date().getTime();
			newestContext.setT2(remote.getT2());//在trunk端的处理时间（虽然内容没变，但时间需要记录）
			newestContext.setT3(time2 - st2);
			newestContext.setSt1(remote.getSt1());
			newestContext.setSt2(st2);
			newestContext.setFlag3(1);
			notifyClient("changeLastVersion", "changeLastVersion");
			return;
		}

		if (newestContext == null || remote.getLastVersion().contentEquals(newestContext.getVersion())) {
			message = "diagram " + session.getId() +": from="+remote.getFrom()+","
					+ remote.getFlag1()+remote.getFlag2()+ "1,自从trunk上次传来版本后没有修改,直接替换";
			System.out.println(message);
			
			// no change since trunk last notify Version
			lastContext = remote.deriveTree();
			newestContext = remote.deriveTree();
			newestContext.setFlag3(1);
			// EMF AST -> EMF file
			ASTService.generateEMFXmlFile(newestContext.getRoot(), newestPath);
			// FileOperation.copyFile(newestPath, rootAddress +
			// System.currentTimeMillis() +
			// "_two_newest.xml");
		} else if (!remote.getLastVersion().contentEquals(newestContext.getVersion())) {
			message = "diagram " + session.getId() +": from="+remote.getFrom()+","
					+ remote.getFlag1()+remote.getFlag2() + "3, 3-way merge"
					+", lastVersion=" + lastContext.getVersion().substring(36, 40)
					+ ",newestVersion="+newestContext.getVersion().substring(36, 40)
					+ ",remoteVersion"+remote.getVersion().substring(36, 40)
					+ ",remote.lastVersion="+remote.getLastVersion().substring(36, 40)
					+ ",newest.lastVersion="+newestContext.getLastVersion().substring(36, 40);
			System.out.println(message);

			String versions = getEditorType()+" "+session.getId() + "_three way merge_"
					+ lastContext.getVersion().substring(36, 40) + "_"
					+ newestContext.getVersion().substring(36, 40) + "_"
					+ remote.getVersion().substring(36, 40);
			
			// if edi.lastVersion != version,then 3-way-merge
			newestContext = three_way_merge(lastContext, newestContext, remote);
			
			lastContext = remote.deriveTree();
			System.out.println("diagram " + session.getId() +": "+versions + "-->" + newestContext.getVersion().substring(36, 40));
		}
		// emf file 2 project
		Project newProject = EMFService.emfFile2Project(newestPath);
		if (newProject == null) {
			System.out.println("EMFService.emfFile2Project(" + newestPath + ") error!");
			return;
		}

		contentChanges = getContentChanges(newProject);
		long time2 = new Date().getTime();
		newestContext.setT3(time2 - st2);
		newestContext.setSt2(st2);
//		System.out.println("editor2 change:"+newestContext.time());
		notifyClient(message, "didChange");
	}

	public ArrayList<DiagramContentChangeEvent> getContentChanges(
			Project newProject) {
		ArrayList<DiagramContentChangeEvent> contentChanges = new ArrayList<DiagramContentChangeEvent>();
		project.changeSystemWithNewProject(newProject, contentChanges);
		project.changeExternalEntityWithNewProject(newProject, contentChanges);
		project.changeIntentWithNewProject(newProject, contentChanges);
		project.changeInterfaceWithNewProject2(newProject, contentChanges);
		project.changeConstraintWithNewProject2(newProject, contentChanges);
		project.changeReferenceWithNewProject2(newProject, contentChanges);
		project.changeMachineWithNewProject(newProject, contentChanges);
		project.changeProblemDomainWithNewProject(newProject, contentChanges);
		project.changeRequirementWithNewProject(newProject, contentChanges);
		project.changeInterfaceWithNewProject(newProject, contentChanges);
		project.changeConstraintWithNewProject(newProject, contentChanges);
		project.changeReferenceWithNewProject(newProject, contentChanges);
		return contentChanges;
	}

	@Override
	public void recordMoveTimes(int moveTimes) {
		this.moveTimes = moveTimes;
	}

	@Override
	public boolean getDataAndInitContext() {
		String branch = projectAddress;
		project = fileService.getCorrectProject(getUserAdd(username)+branch+"/", version, branch);
		System.out.println("getDataAndInitContext:"+project.getTitle());
		toEMFFile(project);
		iniContext();
		notifyClient("registered", "registered");
		recordNodeNumber();
		return true;
	}
	public String getUserAdd(String username) {
		String userAdd;
		if (username == null || username == "")
			userAdd = rootAddress;
		else
			userAdd = userAddress + username + "/";
		return userAdd;
	}
}
