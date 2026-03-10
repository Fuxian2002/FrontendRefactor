package com.example.demo.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import com.example.demo.bean.*;
import com.example.demo.bean.Error;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ProjectService {
	@Autowired
	AddressService addressService;
	
	private static final String BehOrder = "BehOrder";  // 行为序关系
	private static final String BehEnable = "BehEnable";  // 行为使能关系
	private static final String Synchrony = "Synchrony";  // 同步关系
	private static final String ExpOrder = "ExpOrder";  // 期望序关系
	private static final String ExpEnable = "ExpEnable";  // 需求期望关系
	private static final String BehInt = "BehInt";  // 行为交互
	private static final String ConnInt = "ConnInt";  // 远程领域交互
	private static final String ExpInt = "ExpInt";  // 期望交互
	private static final String Start = "Start";  // 开始节点
	private static final String End = "End";  // 终止节点
	private static final String Decision = "Decision";  // 决定节点
	private static final String Merge = "Merge";  // 合并节点
	private static final String Delay = "Delay";  // 延时节点
	private static final String Branch = "Branch";  // 分支节点
	private static final String Instruction = "instruction";  // 指令类型现象
	private static final String Signal = "signal";  // 信号类型现象
	private static final String State = "state";  // 状态类型现象
	private static final String Value = "value";  // 数值类型现象
	private static final String Clock = "Clock";  // 时钟领域
	private static final String DataStorage = "Data Storage";  // 数据存储领域
	private static final String Sensor = "Sensor";  // 感应器设备
	private static final String Actuator = "Actuator";  // 执行器设备
	private static final String ActiveDevice = "Active Device";  // 主动设备
	private static final String PassiveDevice = "Passive Device";  // 被动设备
	private static final String DataProcessDevice = "DataProcessDevice";
	private static final String LaunchDevice = "LaunchDevice";
	private static final String ManageControlDevice = "ManageControlDevice";
	private static final String ReceiveDevice = "ReceiveDevice";

	private Set<String> inValidSet = Collections.synchronizedSet(new HashSet<>());  // 用于去重策略的线程安全集合

	//获取现象列表
	public List<Phenomenon> getPhenomenon(Project project) {
		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
		List<Interface> interfaceList = project.getProblemDiagram().getContextDiagram().getInterfaceList();
		List<Reference> referenceList = project.getProblemDiagram().getReferenceList();
		List<Constraint> constraintList = project.getProblemDiagram().getConstraintList();
		for(int i = 0; i < interfaceList.size(); i ++ ) {
			Interface inte = interfaceList.get(i);
			phenomenonList.addAll(inte.getPhenomenonList());
		}
		for(int i = 0; i < referenceList.size(); i ++ ) {
			Reference reference = referenceList.get(i);
			phenomenonList.addAll(reference.getPhenomenonList());
		}
		for(int i = 0; i < constraintList.size(); i ++ ) {
			Constraint constraint = constraintList.get(i);
			phenomenonList.addAll(constraint.getPhenomenonList());
		}
		for  ( int  i  =   0 ; i  <  phenomenonList.size()  -   1 ; i ++ )  {
			for  ( int  j  =  phenomenonList.size()  -   1 ; j  >  i; j -- )  {
				if  (phenomenonList.get(j).getPhenomenon_no() == phenomenonList.get(i).getPhenomenon_no())  {
					phenomenonList.remove(j);
				}
			}
		}
		for(int i = 0; i < phenomenonList.size(); i++) {
			for (int j = 0; j < phenomenonList.size() -  i - 1; j++)
			{
				Phenomenon phe1 = phenomenonList.get(j);
				Phenomenon phe2 = phenomenonList.get(j+1);
				if(phe1.getPhenomenon_no() > phe2.getPhenomenon_no()) {
					Phenomenon phe;
					phe = phe1;
					phenomenonList.set(j, phe2);
					phenomenonList.set(j+1, phe);
				}
			}
		}
		return phenomenonList;
	}

	//获取现象列表
	public List<Phenomenon> getPhenomenon(SubProblemDiagram spd) {
		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
		List<Interface> interfaceList = spd.getInterfaceList();
		List<Reference> referenceList = spd.getReferenceList();
		List<Constraint> constraintList = spd.getConstraintList();
		for(int i = 0; i < interfaceList.size(); i ++ ) {
			Interface inte = interfaceList.get(i);
			phenomenonList.addAll(inte.getPhenomenonList());
		}
		for(int i = 0; i < referenceList.size(); i ++ ) {
			Reference reference = referenceList.get(i);
			phenomenonList.addAll(reference.getPhenomenonList());
		}
		for(int i = 0; i < constraintList.size(); i ++ ) {
			Constraint constraint = constraintList.get(i);
			phenomenonList.addAll(constraint.getPhenomenonList());
		}
		for  ( int  i  =   0 ; i  <  phenomenonList.size()  -   1 ; i ++ )  {
			for  ( int  j  =  phenomenonList.size()  -   1 ; j  >  i; j -- )  {
				if  (phenomenonList.get(j).getPhenomenon_no() == phenomenonList.get(i).getPhenomenon_no())  {
					phenomenonList.remove(j);
				}
			}
		}
		for(int i = 0; i < phenomenonList.size(); i++) {
			for (int j = 0; j < phenomenonList.size() -  i - 1; j++)
			{
				Phenomenon phe1 = phenomenonList.get(j);
				Phenomenon phe2 = phenomenonList.get(j+1);
				if(phe1.getPhenomenon_no() > phe2.getPhenomenon_no()) {
					Phenomenon phe;
					phe = phe1;
					phenomenonList.set(j, phe2);
					phenomenonList.set(j+1, phe);
				}
			}
		}
		return phenomenonList;
	}

	//获取约束引用列表
	public List<RequirementPhenomenon> getRequirementPhenomenon(Project project) {
		List<RequirementPhenomenon> phenomenonList = new ArrayList<RequirementPhenomenon>();
		List<Reference> referenceList = project.getProblemDiagram().getReferenceList();
		List<Constraint> constraintList = project.getProblemDiagram().getConstraintList();
		for(int i = 0; i < referenceList.size(); i ++ ) {
			Reference reference = referenceList.get(i);
			phenomenonList.addAll(reference.getPhenomenonList());
		}
		for(int i = 0; i < constraintList.size(); i ++ ) {
			Constraint constraint = constraintList.get(i);
			phenomenonList.addAll(constraint.getPhenomenonList());
		}
		// 去除重复的引用和约束
		for (int  i  =  0; i < phenomenonList.size() - 1; i ++ )  {
			for (int j = phenomenonList.size() - 1 ;j > i; j -- )  {
				if(phenomenonList.get(j).getPhenomenon_no() == phenomenonList.get(i).getPhenomenon_no()
						&& phenomenonList.get(j).getPhenomenon_requirement() == phenomenonList.get(i).getPhenomenon_requirement()){
					phenomenonList.remove(j);
				}
			}
		}
		// 排序
		for(int i = 0; i < phenomenonList.size(); i++) {
			for (int j = 0; j < phenomenonList.size() -  i - 1; j++)
			{
				RequirementPhenomenon phe1 = phenomenonList.get(j);
				RequirementPhenomenon phe2 = phenomenonList.get(j+1);
				if(phe1.getPhenomenon_requirement() > phe2.getPhenomenon_requirement()) {
					RequirementPhenomenon phe = phe1;
					phenomenonList.set(j, phe2);
					phenomenonList.set(j+1, phe);
				}
			}
		}
		return phenomenonList;
	}


	//上下文图正确性检测

	public List<Error> checkCorrectContext(Project project){
		List<Error> errorList = new ArrayList<Error>();
		ContextDiagram CD=project.getContextDiagram();
		errorList.add(check_Machine(CD));
		errorList.add(check_ProblemDomain(CD));
		errorList.add(check_Interface1(CD));
		return errorList;
	}

	//问题图正确性检测
	public List<Error> checkCorrectProblem(Project project){
		List<Error> errorList = new ArrayList<Error>();
		ContextDiagram CD=project.getContextDiagram();
		errorList.add(check_Interface(CD));
		ProblemDiagram PD=project.getProblemDiagram();
		errorList.add(check_Requirement(PD));
		errorList.add(check_Reference(PD));
		errorList.add(check_Constraint(PD));
		return errorList;
	}

	//Machine检测
	private Error check_Machine(ContextDiagram CD) {
		Error error = new Error();
		List<String> errorList = new ArrayList<String>();
		error.setTitle(null);
		Machine CDM=CD.getMachine();
		if(CDM==null) {
			String errMsg = "Does not exist machine.";
			errorList.add(errMsg);
			error.setErrorList(errorList);
			return error;
		}
		String M_name=CDM.getMachine_name();
		if(M_name.contains("?")) {
			String errMsg = "machine's name is illegal.";
			errorList.add(errMsg);
			error.setErrorList(errorList);
			return error;
		}
		error.setErrorList(errorList);
		return error;
	}

	//ProblemDomain检测
	private Error check_ProblemDomain(ContextDiagram CD) {
		Error error = new Error();
		List<String> errorList = new ArrayList<String>();
		error.setTitle(null);
		int PDnum=0;
		List <ProblemDomain> CDPD=CD.getProblemDomainList();
		for(int i=0;i<CDPD.size();i++) {
			ProblemDomain PD_check=CDPD.get(i);
			String PD_name=PD_check.getProblemdomain_name();
			if(PD_name.contains("?")) {
				String errMsg = "ProblemDomain's name is illegal.";
				errorList.add(errMsg);
				error.setErrorList(errorList);
				return error;
			}
			PDnum++;
		}
		if(PDnum==0) {
			String errMsg = "Does not have ProblemDomain.";
			errorList.add(errMsg);
			error.setErrorList(errorList);
			return error;
		}
		error.setErrorList(errorList);
		return error;
	}

	//Interface检测
	private Error check_Interface1(ContextDiagram CD) {
		Error error = new Error();
		List<String> errorList = new ArrayList<String>();
		error.setTitle(null);
		int Inum=0;
		List <ProblemDomain> CDPD=CD.getProblemDomainList();
		List<Interface> CDI=CD.getInterfaceList();
		for(int i=0;i<CDPD.size();i++) {
			int hasconnect=0;
			ProblemDomain PD_check=CDPD.get(i);
			String PD_name=PD_check.getProblemdomain_shortname();
			for(int j=0;j<CDI.size();j++) {
				Interface I_check=CDI.get(j);
//				String I_name=I_check.getInterface_description();
//				if(I_check.getPhenomenonList().size()==0) {
//					String errMsg = "Exist undefined interface.";
//					errorList.add(errMsg);
//					error.setErrorList(errorList);
//					return error;
//				}
//				if(I_name==null) {
//					String errMsg = "Exist undefined interface.";
//					errorList.add(errMsg);
//					error.setErrorList(errorList);
//					return error;
//				}
//				if(I_name.contains("?")) {
//					String errMsg = "Exist undefined interface.";
//					errorList.add(errMsg);
//					error.setErrorList(errorList);
//					return error;
//				}
				Inum++;
				String I_from=I_check.getInterface_from();
				String I_to=I_check.getInterface_to();
				if(I_from==null||I_to==null) {
					String errMsg = "Interface does not have from or to shape.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				if(I_from.equals(PD_name)) {
					hasconnect=1;
				}
				else if(I_to.equals(PD_name)) {
					hasconnect=1;
				}
			}
			if(hasconnect==0) {
				String errMsg = "Exist Unconnected ProblemDomain.";
				errorList.add(errMsg);
				error.setErrorList(errorList);
				return error;
			}
		}
		if(Inum==0) {
			String errMsg = "Does not have Interface.";
			errorList.add(errMsg);
			error.setErrorList(errorList);
			return error;
		}
		error.setErrorList(errorList);
		return error;
	}
	private Error check_Interface(ContextDiagram CD) {
		Error error = new Error();
		List<String> errorList = new ArrayList<String>();
		error.setTitle(null);
		int Inum=0;
		List <ProblemDomain> CDPD=CD.getProblemDomainList();
		List<Interface> CDI=CD.getInterfaceList();
		for(int i=0;i<CDPD.size();i++) {
			int hasconnect=0;
			ProblemDomain PD_check=CDPD.get(i);
			String PD_name=PD_check.getProblemdomain_shortname();
			for(int j=0;j<CDI.size();j++) {
				Interface I_check=CDI.get(j);
				String I_name=I_check.getInterface_description();
				if(I_check.getPhenomenonList().size()==0) {
					String errMsg = "Exist undefined interface.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				if(I_name==null) {
					String errMsg = "Exist undefined interface.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				if(I_name.contains("?")) {
					String errMsg = "Exist undefined interface.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				Inum++;
				String I_from=I_check.getInterface_from();
				String I_to=I_check.getInterface_to();
				if(I_from==null||I_to==null) {
					String errMsg = "Interface does not have from or to shape.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				if(I_from.equals(PD_name)) {
					hasconnect=1;
				}
				else if(I_to.equals(PD_name)) {
					hasconnect=1;
				}
			}
			if(hasconnect==0) {
				String errMsg = "Exist Unconnected ProblemDomain.";
				errorList.add(errMsg);
				error.setErrorList(errorList);
				return error;
			}
		}
		if(Inum==0) {
			String errMsg = "Does not have Interface.";
			errorList.add(errMsg);
			error.setErrorList(errorList);
			return error;
		}
		error.setErrorList(errorList);
		return error;
	}
	//Requirement检测
	private Error check_Requirement(ProblemDiagram PD) {
		Error error = new Error();
		List<String> errorList = new ArrayList<String>();
		error.setTitle(null);
		int Rnum=0;
		List <Requirement> PDR=PD.getRequirementList();
		for(int i=0;i<PDR.size();i++) {
			Requirement R_check=PDR.get(i);
			String R_name=R_check.getRequirement_context();
			if(R_name.contains("?")) {
				String errMsg = "Requirement's name is illegal.";
				errorList.add(errMsg);
				error.setErrorList(errorList);
				return error;
			}
			Rnum++;
		}
		if(Rnum==0) {
			String errMsg = "Does not have Requirement.";
			errorList.add(errMsg);
			error.setErrorList(errorList);
			return error;
		}
		error.setErrorList(errorList);
		return error;
	}

	//Reference检测
	private Error check_Reference(ProblemDiagram PD) {
		Error error = new Error();
		List<String> errorList = new ArrayList<String>();
		int RCnum=0;
		List <Requirement> PDPD=PD.getRequirementList();
		List<Reference> PDR=PD.getReferenceList();
		List <Constraint> PDC=PD.getConstraintList();
		for(int i=0;i<PDPD.size();i++) {
			int hasconnect=0;
			Requirement R_check=PDPD.get(i);
			String R_name=R_check.getRequirement_context();
			for(int j=0;j<PDR.size();j++) {
				Reference Rr_check=PDR.get(j);
				String Rr_name=Rr_check.getReference_description();
				if(Rr_check.getPhenomenonList().size()==0) {
					String errMsg = "Exist undefine reference.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				if(Rr_name==null) {
					String errMsg = "Exist undefine reference.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				if(Rr_name.contains("?")) {
					String errMsg = "Reference's name is illegal.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				RCnum++;
				String Rr_from=Rr_check.getReference_from();
				String Rr_to=Rr_check.getReference_to();
				if(Rr_from==null||Rr_to==null) {
					String errMsg = "Reference does not have from or to shape.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				if(Rr_from.equals(R_name)) {
					hasconnect=1;
				}
				else if(Rr_to.contentEquals(R_name)) {
					hasconnect=1;
				}
			}
			for(int j=0;j<PDC.size();j++) {
				Constraint C_check=PDC.get(j);
				String C_name=C_check.getConstraint_description();
				if(C_name.contains("?")) {
					String errMsg = "Constraint's name is illegal.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				RCnum++;
				String C_from=C_check.getConstraint_from();
				String C_to=C_check.getConstraint_to();
				if(C_from==null||C_to==null) {
					String errMsg = "Constraint does not have from or to shape.";
					errorList.add(errMsg);
					error.setErrorList(errorList);
					return error;
				}
				if(C_from.equals(R_name)) {
					hasconnect=1;
				}
				else if(C_to.equals(R_name)) {
					hasconnect=1;
				}
			}
			if(hasconnect==0) {
				String errMsg = "Exist Unconnected Reqirement.";
				errorList.add(errMsg);
				error.setErrorList(errorList);
				return error;
			}
		}
		if(RCnum==0) {
			String errMsg = "Does not have Reference or Constraint.";
			errorList.add(errMsg);
			error.setErrorList(errorList);
			return error;
		}
		error.setErrorList(errorList);
		return error;
	}

	//Constraint检测
	private Error check_Constraint(ProblemDiagram PD) {
		Error error = new Error();
		List<String> errorList = new ArrayList<String>();
//		int RCnum=0;
		int Rnum=0;
		List <Constraint> PDC=PD.getConstraintList();
		for(int i=0;i<PDC.size();i++) {
			Constraint C_check=PDC.get(i);
			String C_name=C_check.getConstraint_description();
			if(C_check.getPhenomenonList().size()==0) {
				String errMsg = "Exist undefine Constraint.";
				errorList.add(errMsg);
				error.setErrorList(errorList);
				return error;
			}
			if(C_name==null) {
				String errMsg = "Exist undefine Constraint.";
				errorList.add(errMsg);
				error.setErrorList(errorList);
				return error;
			}
			if(C_name.contains("?")) {
				String errMsg = "Constraint's name is illegal.";
				errorList.add(errMsg);
				error.setErrorList(errorList);
				return error;
			}
			Rnum++;
		}
		if(Rnum==0) {
			String errMsg = "Does not have Constraint";
			errorList.add(errMsg);
			error.setErrorList(errorList);
			return error;
		}
		error.setErrorList(errorList);
		return error;
	}

	//问题图检测
	public boolean checkPD(ProblemDiagram problemDiagram)
	{
		ContextDiagram contextDiagram = problemDiagram.getContextDiagram();
		Machine machine = contextDiagram.getMachine();
		List<ProblemDomain> problemDomainList = contextDiagram.getProblemDomainList();
		List<Interface> interfaceList = contextDiagram.getInterfaceList();
		List<Constraint> constraintList = problemDiagram.getConstraintList();
		List<Reference> referenceList = problemDiagram.getReferenceList();
		List<Requirement> requirementList = problemDiagram.getRequirementList();

		if(machine == null)
		{
			return false;
		}
		if(problemDomainList == null) {
			return false;
		}
		if(interfaceList == null) {
			return false;
		}
		if(constraintList == null) {
			return false;
		}
		if(referenceList == null) {
			return false;
		}
		if(requirementList == null) {
			return false;
		}
		return true;
	}

	//情景图正确性检测
	public List<Error> checkCorrectness(Project project) {
		List<Error> errorList = new ArrayList<Error>();
		errorList.add(checkIntegrity(project));
		List<ScenarioGraph> SGList = project.getScenarioGraphList();
		for(int i = 0; i < SGList.size(); i ++ ) {
			ScenarioGraph SG = SGList.get(i);
			errorList.add(checkSynTax(SG));
		}
		System.out.println(errorList);
		return errorList;
	}

	//良构检测
	public List<Error> checkWellFormed(Project project) {
		List<Error> errorList = new ArrayList<Error>();
		List<ScenarioGraph> SGList = project.getScenarioGraphList();

		for (int i = 0; i < SGList.size() ; i++) {	//依次对每个情景图进行检查
			ScenarioGraph SG = SGList.get(i);
			Error error = new Error();
			List<String> errMsgList = new ArrayList<String>();
			error.setTitle(SG.getTitle());
			errMsgList.addAll(checkSemantic(SG));	//语义检查,是否有交互关系
			errMsgList.addAll(checkState(SG,project));	//语义检查，是否既为静态又为动态
			error.setErrorList(errMsgList);
			errorList.add(error);
		}
		return errorList;
	}

	public Error checkIntegrity(Project project) {		//检查情景图的完整性（是否包含全部的int）
		Error error = new Error();
		List<String> errorList = new ArrayList<String>();
		error.setTitle(null);
		error.setType("IntegrityError");

		List<IntInfo> intList = getInt(project);
		List<Phenomenon> pheList = getPhenomenon(project);

		IntInfo intInfo;
		List<IntInfo> intInfoList = new ArrayList<IntInfo>();	//问题图中涉及到的所有int
		for (int i = 0; i < pheList.size(); i ++) {
			Phenomenon phe = pheList.get(i);
			intInfo = new IntInfo();
			intInfo.setNo(phe.getPhenomenon_no());;
			intInfo.setInit(phe.getPhenomenon_from());
			intInfo.setRece(phe.getPhenomenon_to());
			intInfo.setType(phe.getPhenomenon_type());
			intInfo.setState(0);
			intInfoList.add(intInfo);
		}
		for(int i = 0; i < intInfoList.size(); i ++ ) {
			intInfo = intInfoList.get(i);
			for(int j = 0; j < intList.size(); j ++ ) {
				if(intInfo.getNo() == intList.get(j).getNo()) {
					intInfoList.get(j).setState(1);
					break;
				}
			}
		}
		for(int i = 0; i < intInfoList.size(); i ++ ) {
			intInfo = intInfoList.get(i);
			if(intInfo.getState() == 0) {
				String errMsg = "IntegrityError:int" + intInfo.getNo() + " is not described！";
				errorList.add(errMsg);
			}
		}
		error.setErrorList(errorList);
		return error;
	}

	private Error checkSynTax(ScenarioGraph SG) {
		Error error = new Error();
		List<String> errMsgList = new ArrayList<String>();
		error.setTitle(SG.getTitle());
		error.setType("SynTaxError");

		List<Line> lineList = SG.getLineList();
		//检查交互序关系
		if(lineList == null) {
			error.setErrorList(errMsgList);
			return error;
		}
		for (int i = 0; i < lineList.size(); i++ ) {
			Line line = lineList.get(i);
			Node fromNode = line.getFromNode();
			Node toNode = line.getToNode();
			if (line.getLine_type().equals("BehOrder")) {	//行为序关系
				if (!fromNode.getNode_type().equals("ExpInt") && !toNode.getNode_type().equals("ExpInt")) {
					continue;
				}else {
					String errMsg = "SynTaxError:Interaction" + fromNode.getNode_no() +
							" and Interaction" + toNode.getNode_no() + " have a wrong relationship!";
					errMsgList.add(errMsg);
				}
			}
			if (line.getLine_type().equals("BehEnable")) {	//行为使能
				if (fromNode.getNode_type().equals("BehInt") && toNode.getNode_type().equals("ExpInt")
						&& fromNode.getNode_no() != toNode.getNode_no()) {
					continue;
				}else {
					String errMsg = "SynTaxError:Interaction" + fromNode.getNode_no() +
							" and Interaction" + toNode.getNode_no() + " have a wrong relationship!";
					errMsgList.add(errMsg);
				}
			}
			if (line.getLine_type().equals("Synchrony")) {	//同步关系
				if((fromNode.getNode_type().equals("ExpInt") && toNode.getNode_type().equals("BehInt"))
						|| (fromNode.getNode_type().equals("BehInt") && toNode.getNode_type().equals("ExpInt"))
						|| (fromNode.getNode_type().equals("ConnInt") && toNode.getNode_type().equals("ExpInt"))) {
					continue;
				}else {
					String errMsg = "SynTaxError:Interaction" + fromNode.getNode_no() +
							" and Interaction" + toNode.getNode_no() + " have a wrong relationship!";
					errMsgList.add(errMsg);
				}
			}
			if (line.getLine_type().equals("ExpOrder")) {	//期望序关系
				if (!fromNode.getNode_type().equals("BehInt") && !toNode.getNode_type().equals("BehInt")) {
					continue;
				}else {
					String errMsg = "SynTaxError:Interaction" + fromNode.getNode_no() +
							" and Interaction" + toNode.getNode_no() + " have a wrong relationship!";
					errMsgList.add(errMsg);
				}
			}
			if (line.getLine_type().equals("ExpEnable")) {	//期望使能
				if (fromNode.getNode_type().equals("ExpInt") && toNode.getNode_type().equals("BehInt")
						&& fromNode.getNode_no() != toNode.getNode_no() ) {
					continue;
				}else {
					String errMsg = "SynTaxError:Interaction" + fromNode.getNode_no() +
							" and Interaction" + toNode.getNode_no() + " have a wrong relationship!";
					errMsgList.add(errMsg);
				}
			}
		}
		error.setErrorList(errMsgList);
		return error;
	}

	private List<IntInfo> getInt(Project project) {	//获取int
		List<IntInfo> intList = new ArrayList<IntInfo>();
		List<ScenarioGraph> SGList = project.getScenarioGraphList();
		for(int i = 0; i < SGList.size(); i ++ ) {
			ScenarioGraph SG = SGList.get(i);
			List<Node> intNodeList= SG.getIntNodeList();
			for(int j = 0; j < intNodeList.size(); j ++ ) {
				Node intNode = intNodeList.get(j);
				IntInfo intInfo = new IntInfo();
				intInfo.setNo(intNode.getNode_no());
				intList.add(intInfo);
			}
		}
		for (int  i  =  0; i < intList.size() - 1; i ++ )  {
			for (int j = intList.size() - 1 ;j > i; j -- )  {
				if(intList.get(j).getNo() == intList.get(i).getNo()){
					intList.remove(j);
				}
			}
		}
		return intList;
	}

	public List<String> checkSemantic(ScenarioGraph SG) {	//语义检测
		List<String> errMsgList = new ArrayList<String>();

		List<Node> intNodeList = SG.getIntNodeList();
		List<Line> lineList = SG.getLineList();
		for (int i = 0; i < intNodeList.size() ; i++) {	//检测每一个int
			Node intNode = intNodeList.get(i);
			if(intNode.getNode_type().equals("ExpInt")) {	//遍历期望交互
				boolean connection = false;
				if(lineList != null) {
					for (int j = 0; j < lineList.size(); j++) {
						Line line = lineList.get(j);
						Node fromNode = line.getFromNode();
						Node toNode = line.getToNode();
						String type = line.getLine_type();
						//行为使能
						if (type.equals("BehEnable") && toNode.getNode_no() == intNode.getNode_no()) {
							connection = true;
							break;
						}
						//同步关系
						if (type.equals("Synchrony") && (fromNode.getNode_no() == intNode.getNode_no()
								|| toNode.getNode_no() == intNode.getNode_no())) {
							connection = true;
							break;
						}
						//期望使能
						if (type.equals("ExpEnable") && fromNode.getNode_no() == intNode.getNode_no()) {
							connection = true;
							break;
						}
					}
				}
				if (!connection) {
					String errMsg = "语义错误:交互" + intNode.getNode_no() + "(r) 没有被确认!";
					errMsgList.add(errMsg);
				}
			}

		}
		return errMsgList;
	}

	public List<String> checkState(ScenarioGraph SG, Project project) {	//静态、动态领域检测
		List<String> errMsgList = new ArrayList<String>();

		List<Phenomenon> pheList = getPhenomenon(project);
		IntInfo intInfo = null;
		List<IntInfo> intInfoList = new ArrayList<IntInfo>();
		List<Node> intNodeList = SG.getIntNodeList();
		for (int i = 0; i < pheList.size(); i ++) {
			Phenomenon phe = pheList.get(i);
			intInfo = new IntInfo();
			intInfo.setNo(phe.getPhenomenon_no());
			intInfo.setInit(phe.getPhenomenon_from());
			intInfo.setRece(phe.getPhenomenon_to());
			intInfo.setType(phe.getPhenomenon_type());
			for (int j = 0; j < intNodeList.size() ; j++) {	//获取情景图中涉及到的int信息
				Node intNode = intNodeList.get(j);
				if(intNode.getNode_no() == intInfo.getNo()){
					intInfoList.add(intInfo);
					break;
				}
			}
		}
		ProblemDomain problemDomain = null;
		List<ProblemDomain> problemDomainList = project.getProblemDiagram().getContextDiagram().getProblemDomainList();
		String state = null;
		for(int i = 0;i < problemDomainList.size(); i ++) {
			problemDomain = problemDomainList.get(i);
			for(int j = 0; j < intInfoList.size(); j ++) {
				if(i >= intInfoList.size()) continue;
				intInfo = intInfoList.get(i);
				if(problemDomain.getProblemdomain_name().equals(intInfo.getInit())
						|| problemDomain.getProblemdomain_name().equals(intInfo.getRece())) {
					if(intInfo.getType().equals("value")) {
						state = "static";
					}
					else {
						state = "dynamic";
					}
					if(problemDomain.getState() == null) {
						problemDomain.setState(state);
					}else if(!problemDomain.getState().equals(state)) {
						String errMsg = "状态错误:" + problemDomain.getProblemdomain_name() + "既是动态领域又是静态领域!";
						errMsgList.add(errMsg);
						break;
					}
				}
			}
		}
		return errMsgList;
	}

	public Project getSubProblemDiagram(Project project) {
		List<ScenarioGraph> SGList = project.getScenarioGraphList();
		if(project.getSubProblemDiagramList() == null) {
			List<SubProblemDiagram> subProblemDiagramList = new ArrayList<SubProblemDiagram>();
			for (int i = 0; i < SGList.size(); i++) {	//由每一个子情景图投影子问题图
				ScenarioGraph SG = SGList.get(i);
				SubProblemDiagram spd = getSPD(SG,project);
				System.out.println("spd:"+spd);
				if (spd != null) {
					String title = spd.getRequirement().getRequirement_context();
					title = title.replace(" ", "_");
					title = title.replace("\n", "_");
					spd.setTitle("SPD" + (i + 1) + "-" + title);
					subProblemDiagramList.add(spd);
					System.out.println("subProblemDiagramList:"+subProblemDiagramList);
					project.setSubProblemDiagramList(subProblemDiagramList);
				}
			}
//		}else if(project.getSubProblemDiagramList().size() < project.getProblemDiagram().getRequirementList().size()) {
		}else {
			List<SubProblemDiagram> subProblemDiagramList = new ArrayList<SubProblemDiagram>();
			reqLoop:
			for(int i = 0; i < project.getProblemDiagram().getRequirementList().size(); i ++ ) {
				Requirement requirement = project.getProblemDiagram().getRequirementList().get(i);
				for(SubProblemDiagram spd: project.getSubProblemDiagramList()) {
					if(requirement.equals(spd.getRequirement())) {
						continue reqLoop;
					}
				}

				for (ScenarioGraph SG: SGList) {	//由每一个子情景图投影子问题图
					if(SG.getRequirement().equals(requirement.getRequirement_context())) {
						SubProblemDiagram spd = getSPD(SG,project);
						if (spd != null) {
							String title = spd.getRequirement().getRequirement_context();
							title = title.replace(" ", "_");
							title = title.replace("\n", "_");
							spd.setTitle("SPD" + (i + 1) + "-" + title);
							subProblemDiagramList.add(spd);
							System.out.println("subProblemDiagramList:"+subProblemDiagramList);
							project.setSubProblemDiagramList(subProblemDiagramList);
						}
					}
				}
			}
		}
//		System.out.println(project.getSubProblemDiagramList().size());
		return project;
	}

	private SubProblemDiagram getSPD(ScenarioGraph SG,Project project) {
		ProblemDiagram problemDiagram = project.getProblemDiagram();	//获取全部问题领域
		SubProblemDiagram spd = new SubProblemDiagram();	//新建子问题图实体

		String reqName = SG.getRequirement();	//情景图描述的需求名称
		Machine machine = problemDiagram.getContextDiagram().getMachine();	//机器
		Requirement requirement = new Requirement();	//新建需求实体
		List<Requirement> requirements = problemDiagram.getRequirementList();	//获取全部需求
		for(Requirement req : requirements) {	//找到情景图描述的需求实体
			if(req.getRequirement_context().equals(reqName)) {
				requirement = req;
			}
		}
		List<ProblemDomain> problemDomainList = getProblemDomainBySG(SG,project);	//获取子问题图中的问题领域
		List<Interface> interfaceList = getInterfaceListBySG(SG,project,requirement,problemDomainList);	//获取子问题图中的交互
		List<Constraint> constraintList = getConstraintListBySG(SG,project,problemDomainList);	//获取子问题图中的约束
		List<Reference> referenceList = getReferenceListBySG(SG,project,problemDomainList);	//获取子问题图中的引用

		spd.setMachine(machine);
		spd.setRequirement(requirement);
		spd.setProblemDomainList(problemDomainList);
		spd.setInterfaceList(interfaceList);
		spd.setConstraintList(constraintList);
		spd.setReferenceList(referenceList);

		return spd;
	}

	private List<ProblemDomain> getProblemDomainBySG(ScenarioGraph SG,Project project) {
		List<ProblemDomain> problemDomainList = project.getContextDiagram().getProblemDomainList(); //获取全部的问题领域
		List<Phenomenon> phenomenonList = getPhenomenon(project);	//获取全部的phe

		List<ProblemDomain> problemDomains = new ArrayList<ProblemDomain>();
		List<Node> intNodeList = SG.getIntNodeList();	//情景图中的交互节点
		for(ProblemDomain problemDomain : problemDomainList) {	//遍历问题领域
			String pdName = problemDomain.getProblemdomain_shortname();
			intLoop:
			for(Node intNode : intNodeList) {	//遍历交互节点
				int intNo = intNode.getNode_no();
				for(Phenomenon phenomenon: phenomenonList) {
					if(phenomenon.getPhenomenon_no() == intNo) {	//找到交互节点对应的phe
						if(phenomenon.getPhenomenon_from().equals(pdName)) {
							problemDomains.add(problemDomain);
							break intLoop;
						}else if(phenomenon.getPhenomenon_to().equals(pdName)) {
							problemDomains.add(problemDomain);
							break intLoop;
						}
					}
				}
			}
		}
		return problemDomains;
	}

	private List<Interface> getInterfaceListBySG(ScenarioGraph SG,Project project, Requirement requirement, List<ProblemDomain> problemDomainList) {
		List<Interface> interfaceList = new ArrayList<Interface>();	//获取问题图中的所有交互
		List<Node> intNodeList = SG.getIntNodeList();	//获取对应情景图中的int
		List<Interface> interfaces = new ArrayList<Interface>();

		for(Interface inte : project.getContextDiagram().getInterfaceList()) {
			interfaceList.add((Interface) inte.clone());
		}

		Machine machine = project.getContextDiagram().getMachine();
		String mcName = machine.getMachine_shortName();
		reqLoop:
		for(Interface inte : interfaceList) {	//对交互进行遍历
			for(ProblemDomain problemDomain1 : problemDomainList) {
				String pdName1 = problemDomain1.getProblemdomain_shortname();
				for(ProblemDomain problemDomain2 : problemDomainList) {
					String pdName2 = problemDomain2.getProblemdomain_shortname();
					if((inte.getInterface_from().equals(pdName1) && inte.getInterface_to().equals(pdName2))
							|| (inte.getInterface_from().equals(pdName2) && inte.getInterface_to().equals(pdName1))) {
						interfaces.add((Interface) inte.clone());
						continue reqLoop;
					}
				}
			}
			for(ProblemDomain problemDomain : problemDomainList) {
				String pdName = problemDomain.getProblemdomain_shortname();
				if((inte.getInterface_from().equals(pdName) && inte.getInterface_to().equals(mcName))
						|| (inte.getInterface_from().equals(mcName) && inte.getInterface_to().equals(pdName))) {
					interfaces.add((Interface) inte.clone());
					continue reqLoop;
				}
			}
		}

		for(int i = 0; i < interfaces.size(); i++) {
			for(int j = interfaces.size() - 1; j > i; j--) {
				if(interfaces.get(j).equals(interfaces.get(i))) {
					interfaces.remove(j);
				}
			}
		}

		for(Interface inte : interfaces) {
			inte.setInterface_description(null);
			List<Phenomenon> delete = new ArrayList<Phenomenon>();
			List<Phenomenon> phenomenons = new ArrayList<Phenomenon>();
			for(Phenomenon phenomenon: inte.getPhenomenonList()) {
				delete.add((Phenomenon) phenomenon.clone());
				phenomenons.add((Phenomenon) phenomenon.clone());
			}
			for(Node intNode : intNodeList) {
				if((intNode.getNode_type().equals("BehInt"))
						|| (intNode.getNode_type().equals("ConnInt"))) {
					int intNo = intNode.getNode_no();
					for(int i = delete.size() - 1; i >= 0 ; i -- ) {
						if(delete.get(i).getPhenomenon_no() == intNo) {
							delete.remove(i);
						}
					}
				}
			}

			phenomenons.removeAll(delete);
			Iterator<Phenomenon> it = phenomenons.iterator();  //创建迭代器
			while(it.hasNext()){  //循环遍历迭代器
				System.out.println(it.next());  //输出集合中元素
			}
			inte.setPhenomenonList(phenomenons);
		}

		for(Interface inte : interfaces) {
			String description = inte.getInterface_name() + ":";
			String from = "";
			String to = "";
			for(Phenomenon phenomenon: inte.getPhenomenonList()) {
				if(phenomenon.getPhenomenon_from().equals(inte.getInterface_from())) {
					if(from.equals("")) {
						from = from + phenomenon.getPhenomenon_name();
					}
					else {
						from = from + ", " + phenomenon.getPhenomenon_name();
					}
				}else {
					if(to.equals("")) {
						to = to + phenomenon.getPhenomenon_name();
					}
					else {
						to = to + ", " + phenomenon.getPhenomenon_name();
					}
				}
			}
			if(!from.equals("")) {
				from = inte.getInterface_from() + "!" + "{" +  from + "}";
			}
			if(!to.equals("")) {
				to = inte.getInterface_to() + "!" + "{" +  to + "}";
			}
			if(from.equals("")) {
				description = description + to;
			}else if(to.equals("")) {
				description = description + from;
			}
			else {
				description = description + from + ", " + to;
			}
			inte.setInterface_description(description);
		}
		return interfaces;
	}

	private List<Constraint> getConstraintListBySG(ScenarioGraph SG,Project project,List<ProblemDomain> problemDomainList) {
		List<Constraint> constraintList = project.getProblemDiagram().getConstraintList();
		List<RequirementPhenomenon> phenomenonList = getRequirementPhenomenon(project);
		List<Node> intNodeList = SG.getIntNodeList();

		List<Constraint> constraints = new ArrayList<Constraint>();

		String reqName = SG.getRequirement();	//情景图描述的需求名称
		for(Constraint constraint : constraintList) {
			if(constraint.getConstraint_from().equals(reqName)
					|| constraint.getConstraint_to().equals(reqName)) {
				constraint.setConstraint_description(null);
				constraints.add(constraint);
			}
		}
		for(Constraint constraint : constraints) {
			List<RequirementPhenomenon> delete = new ArrayList<RequirementPhenomenon>(constraint.getPhenomenonList());
			for(Node intNode : intNodeList) {
				int intNo = intNode.getNode_no();
				for(RequirementPhenomenon phenomenon: phenomenonList) {
					if(phenomenon.getPhenomenon_no() == intNo) {
						if(delete.contains(phenomenon)) {
							delete.remove(phenomenon);
						}
					}
				}
			}
			constraint.getPhenomenonList().removeAll(delete);
		}
		for(Constraint constraint : constraints) {
			String description = constraint.getConstraint_name() + ":";
			String from = "";
			String to = "";
			for(RequirementPhenomenon phenomenon: constraint.getPhenomenonList()) {
				if(phenomenon.getPhenomenon_from().equals(constraint.getConstraint_from())) {
					if(from.equals("")) {
						from = from + phenomenon.getPhenomenon_name();
					}
					else {
						from = from + ", " + phenomenon.getPhenomenon_name();
					}
				}else {
					if(to.equals("")) {
						to = to + phenomenon.getPhenomenon_name();
					}
					else {
						to = to + ", " + phenomenon.getPhenomenon_name();
					}
				}
			}
			if(!from.equals("")) {
				from = constraint.getConstraint_from() + "!" + "{" +  from + "}";
			}
			if(!to.equals("")) {
				to = constraint.getConstraint_to() + "!" + "{" +  to + "}";
			}
			if(from.equals("")) {
				description = description + to;
			}else if(to.equals("")) {
				description = description + from;
			}
			else {
				description = description + from + ", " + to;
			}
			constraint.setConstraint_description(description);
		}
		return constraints;
	}

	private List<Reference> getReferenceListBySG(ScenarioGraph SG,Project project,List<ProblemDomain> problemDomainList) {
		List<Reference> referenceList = project.getProblemDiagram().getReferenceList();
		List<RequirementPhenomenon> phenomenonList = getRequirementPhenomenon(project);
		List<Node> intNodeList = SG.getIntNodeList();

		List<Reference> references = new ArrayList<Reference>();

		String reqName = SG.getRequirement();	//情景图描述的需求名称
		for(Reference reference : referenceList) {
			if(reference.getReference_from().equals(reqName)
					|| reference.getReference_to().equals(reqName)) {
				reference.setReference_description(null);
				references.add(reference);
			}
		}
		for(Reference reference : references) {
			List<RequirementPhenomenon> delete = new ArrayList<RequirementPhenomenon>(reference.getPhenomenonList());
			for(Node intNode : intNodeList) {
				int intNo = intNode.getNode_no();
				for(RequirementPhenomenon phenomenon: phenomenonList) {
					if(phenomenon.getPhenomenon_no() == intNo) {
						if(delete.contains(phenomenon)) {
							delete.remove(phenomenon);
						}
					}
				}
			}
			reference.getPhenomenonList().removeAll(delete);
		}
		for(Reference reference : references) {
			String description = reference.getReference_name() + ":";
			String from = "";
			String to = "";
			for(RequirementPhenomenon phenomenon: reference.getPhenomenonList()) {
				if(phenomenon.getPhenomenon_from().equals(reference.getReference_from())) {
					if(from.equals("")) {
						from = from + phenomenon.getPhenomenon_name();
					}
					else {
						from = from + ", " + phenomenon.getPhenomenon_name();
					}
				}else {
					if(to.equals("")) {
						to = to + phenomenon.getPhenomenon_name();
					}
					else {
						to = to + ", " + phenomenon.getPhenomenon_name();
					}
				}
			}
			if(!from.equals("")) {
				from = reference.getReference_from() + "!" + "{" +  from + "}";
			}
			if(!to.equals("")) {
				to = reference.getReference_to() + "!" + "{" +  to + "}";
			}
			if(from.equals("")) {
				description = description + to;
			}else if(to.equals("")) {
				description = description + from;
			}
			else {
				description = description + from + ", " + to;
			}
			reference.setReference_description(description);
		}
		return references;
	}

	@SuppressWarnings("unchecked")
	public Project getCombinedScenarioGraph(Project project) {
		ScenarioGraph fsg = new ScenarioGraph();
		List<ScenarioGraph> SGList = project.getScenarioGraphList();
		List<CtrlNode> ctrlNodeList = new ArrayList<CtrlNode>();
		List<Node> intNodeList = new ArrayList<Node>();
		List<Line> lineList = new ArrayList<Line>();
		if(SGList.size() == 1) {
			ScenarioGraph scenarioGraph = SGList.get(0);
			ctrlNodeList = scenarioGraph.getCtrlNodeList();
			intNodeList = scenarioGraph.getIntNodeList();
			lineList = scenarioGraph.getLineList();
		} else {
			FullScenarioGraphService fsgService = new FullScenarioGraphServiceImpl();
			EnumMap<FullScenarioGraphService.FSGProperty,Object> nodeListInfo = fsgService.getNodeList(project);
			ctrlNodeList = (List<CtrlNode>) nodeListInfo.get(FullScenarioGraphService.FSGProperty.ctrlNodeList);
			intNodeList = (List<Node>) nodeListInfo.get(FullScenarioGraphService.FSGProperty.intNodeList);
			lineList = (List<Line>) nodeListInfo.get(FullScenarioGraphService.FSGProperty.lineList);
		}
		fsg.setTitle("ScenarioGraph");
		fsg.setCtrlNodeList(ctrlNodeList);
		fsg.setIntNodeList(intNodeList);
		fsg.setLineList(lineList);

		project.setFullScenarioGraph(fsg);
		return project;
	}

	public List<ScenarioGraph> getNewSGList(Project project) {
		List<ScenarioGraph> scenarioGraphs = new ArrayList<ScenarioGraph>();
		List<ScenarioGraph> scenarioGraphList = project.getScenarioGraphList();
		List<Requirement> requirementList = project.getProblemDiagram().getRequirementList();
		boolean exist = false;
		for(Requirement requirement : requirementList) {
			String req = requirement.getRequirement_context();
			if(scenarioGraphList != null) {
				for(ScenarioGraph scenarioGraph: scenarioGraphList) {
					if(scenarioGraph.getRequirement().equals(req)) {
						exist = true;
					}
				}
			}
			if(exist == false) {
				List<ProblemDomain> problemDomainList = project.getContextDiagram().getProblemDomainList();
				List<Interface> interfaceList = project.getContextDiagram().getInterfaceList();
				List<RequirementPhenomenon> requirementPhenomenonList = getRequirementPhenomenon(project);
				List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
				for(int i = 0; i < interfaceList.size(); i ++ ) {
					Interface inte = interfaceList.get(i);
					phenomenonList.addAll(inte.getPhenomenonList());
				}
				ScenarioGraph SG = new ScenarioGraph();
				String title = "SG" + requirement.getRequirement_no() + "-" + req;
				title = title.replace(" ", "_");
				List<Node> intNodeList = new ArrayList<Node>();
				List<ProblemDomain> problemDomains = new ArrayList<ProblemDomain>();

				Node intNode;
				int expNum = 0;
				int behNum = 0;
				for(RequirementPhenomenon reqPhe : requirementPhenomenonList) {
					if(reqPhe.getPhenomenon_requirement() == requirement.getRequirement_no()) {
						expNum = expNum + 1;
						intNode = new Node();
						intNode.setNode_no(reqPhe.getPhenomenon_no());
						intNode.setNode_type("ExpInt");
						intNode.setNode_x(200);
						intNode.setNode_y(expNum*50);
						intNodeList.add(intNode);

						for(ProblemDomain problemDomain: problemDomainList) {
							String pdName = problemDomain.getProblemdomain_shortname();
							if(reqPhe.getPhenomenon_from().equals(pdName)
									|| reqPhe.getPhenomenon_to().equals(pdName)) {
								problemDomains.add(problemDomain);
							}

						}
					}
				}
				for(Phenomenon phenomenon: phenomenonList) {
					for(ProblemDomain problemDomain: problemDomains) {
						String pdName = problemDomain.getProblemdomain_shortname();
						if(phenomenon.getPhenomenon_from().equals(pdName)
								|| phenomenon.getPhenomenon_to().equals(pdName)) {
							behNum = behNum + 1;
							intNode = new Node();
							intNode.setNode_no(phenomenon.getPhenomenon_no());
							intNode.setNode_type("BehInt");
							intNode.setNode_x(100);
							intNode.setNode_y(behNum*50);
							intNodeList.add(intNode);
							break;
						}
					}
				}

				SG.setTitle(title);
				SG.setRequirement(req);
				SG.setIntNodeList(intNodeList);
				scenarioGraphs.add(SG);
			}
		}
		return scenarioGraphs;
	}

	public List<Phenomenon> getBehIntList(Project project, String sgName) {
		List<ScenarioGraph> sgList = project.getScenarioGraphList();
		List<Requirement> requirementList = project.getProblemDiagram().getRequirementList();

		List<Phenomenon> pheList = null;
		ScenarioGraph sg = null;
		for(ScenarioGraph scenarioGraph: sgList) {
			if(scenarioGraph.getTitle().equals(sgName)) {
				for(Requirement requirement: requirementList) {
					if(requirement.getRequirement_context().equals(scenarioGraph.getRequirement())) {
						pheList = getBehIntList(project,requirement);
						sg = scenarioGraph;
					}
				}
			}
		}
		List<Node> intNodeList = sg.getIntNodeList();
		for(int i = pheList.size() - 1; i >= 0; i -- ) {
			Phenomenon phe = pheList.get(i);
			for(Node intNode: intNodeList) {
				if(intNode.getNode_type().equals("BehInt")) {
					if(phe.getPhenomenon_no() == intNode.getNode_no()) {
						pheList.remove(phe);
					}
				}
				// 这里是不是应该把ConInt节点remove掉？
			}
		}

		//排序
		for(int i = 0; i < pheList.size(); i++) {
			for (int j = 0; j < pheList.size() -  i - 1; j++)
			{
				Phenomenon phe1 = pheList.get(j);
				Phenomenon phe2 = pheList.get(j+1);
				if(phe1.getPhenomenon_no() > phe2.getPhenomenon_no()) {
					Phenomenon phe;
					phe = phe1;
					pheList.set(j, phe2);
					pheList.set(j+1, phe);
				}
			}
		}
		return pheList;
	}

	private List<Phenomenon> getBehIntList(Project project, Requirement requirement) {
		List<ProblemDomain> problemDomainList = project.getContextDiagram().getProblemDomainList();
		List<Interface> interfaceList = project.getContextDiagram().getInterfaceList();
		List<RequirementPhenomenon> requirementPhenomenonList = getRequirementPhenomenon(project);  //获取跟需求相关的现象列表
		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
		List<Phenomenon> phenomenons = new ArrayList<Phenomenon>();
		for(int i = 0; i < interfaceList.size(); i ++ ) {
			Interface inte = interfaceList.get(i);
			phenomenonList.addAll(inte.getPhenomenonList());
		}

		//获取跟需求相关的现象列表的问题域
		List<ProblemDomain> problemDomains = new ArrayList<ProblemDomain>();
		for(RequirementPhenomenon reqPhe : requirementPhenomenonList) {
			if(reqPhe.getPhenomenon_requirement() == requirement.getRequirement_no()) {
				for(ProblemDomain problemDomain: problemDomainList) {
					String pdName = problemDomain.getProblemdomain_shortname();
					if(reqPhe.getPhenomenon_from().equals(pdName)
							|| reqPhe.getPhenomenon_to().equals(pdName)) {
						problemDomains.add(problemDomain);
					}
				}
			}
		}
		for(Phenomenon phenomenon: phenomenonList) {
			for(ProblemDomain problemDomain: problemDomains) {
				String pdName = problemDomain.getProblemdomain_shortname();
				if(phenomenon.getPhenomenon_from().equals(pdName)
						|| phenomenon.getPhenomenon_to().equals(pdName)) {
					phenomenons.add(phenomenon);
					break;
				}
			}
		}
		return phenomenons;
	}

	public List<Phenomenon> getConnIntList(Project project, String sgName, String type) {
		List<ScenarioGraph> sgList = project.getScenarioGraphList();
		List<Requirement> requirementList = project.getProblemDiagram().getRequirementList();
		List<Phenomenon> pheList = null;
		ScenarioGraph sg = null;
		for(ScenarioGraph scenarioGraph: sgList) {
			if(scenarioGraph.getTitle().equals(sgName)) {
				for(Requirement requirement: requirementList) {
					if(requirement.getRequirement_context().equals(scenarioGraph.getRequirement())) {
						pheList = getConnIntList(project, requirement, type);
						sg = scenarioGraph;
						break;
					}
				}
				break;
			}
		}
		List<Node> intNodeList = sg.getIntNodeList();
		for(int i = pheList.size() - 1; i >= 0; i -- ) {
			Phenomenon phe = pheList.get(i);
			for(Node intNode: intNodeList) {
				if(intNode.getNode_type().equals(type)) {
					if(phe.getPhenomenon_no() == intNode.getNode_no()) {
						pheList.remove(phe);
					}
				}
			}
		}

		//排序
		for(int i = 0; i < pheList.size(); i++) {
			for (int j = 0; j < pheList.size() -  i - 1; j++)
			{
				Phenomenon phe1 = pheList.get(j);
				Phenomenon phe2 = pheList.get(j+1);
				if(phe1.getPhenomenon_no() > phe2.getPhenomenon_no()) {
					Phenomenon phe;
					phe = phe1;
					pheList.set(j, phe2);
					pheList.set(j+1, phe);
				}
			}
		}
		return pheList;
	}

	private List<Phenomenon> getConnIntList(Project project, Requirement requirement, String type) {
		List<ProblemDomain> problemDomainList = project.getContextDiagram().getProblemDomainList();
		List<Interface> interfaceList = project.getContextDiagram().getInterfaceList();
		List<RequirementPhenomenon> requirementPhenomenonList = getRequirementPhenomenon(project);
		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
		Machine machine = project.getContextDiagram().getMachine();

		List<Phenomenon> phenomenons = new ArrayList<Phenomenon>();
		for(int i = 0; i < interfaceList.size(); i ++ ) {
			Interface inte = interfaceList.get(i);
			phenomenonList.addAll(inte.getPhenomenonList());
		}
		List<ProblemDomain> problemDomains = new ArrayList<ProblemDomain>();
		for(RequirementPhenomenon reqPhe : requirementPhenomenonList) {
			if(reqPhe.getPhenomenon_requirement() == requirement.getRequirement_no()) {
				for(ProblemDomain problemDomain: problemDomainList) {
					String pdName = problemDomain.getProblemdomain_shortname();
					if(reqPhe.getPhenomenon_from().equals(pdName)
							|| reqPhe.getPhenomenon_to().equals(pdName)) {
						problemDomains.add(problemDomain);
					}
				}
			}
		}
		for(Phenomenon phenomenon: phenomenonList) {
			for(ProblemDomain problemDomain: problemDomains) {
				String pdName = problemDomain.getProblemdomain_shortname();
				if(phenomenon.getPhenomenon_from().equals(pdName)
						|| phenomenon.getPhenomenon_to().equals(pdName)) {
					if(!(phenomenon.getPhenomenon_from().equals(machine.getMachine_shortName())
							|| phenomenon.getPhenomenon_to().equals(machine.getMachine_shortName()))) {
						phenomenons.add(phenomenon);
					}
					break;
				}
			}
		}
		return phenomenons;
	}

	public List<Phenomenon> getExpIntList(Project project, String sgName) {
		List<ScenarioGraph> sgList = project.getScenarioGraphList();
		List<Requirement> requirementList = project.getProblemDiagram().getRequirementList();
		List<Phenomenon> pheList = null;
		ScenarioGraph sg = null;
		for(ScenarioGraph scenarioGraph: sgList) {
			if(scenarioGraph.getTitle().equals(sgName)) {
				sg = scenarioGraph;
				for(Requirement requirement: requirementList) {
					if(requirement.getRequirement_context().equals(scenarioGraph.getRequirement())) {
						pheList = getExpIntList(project,requirement);
					}
				}
			}
		}
		List<Node> intNodeList = sg.getIntNodeList();
		for(int i = pheList.size() - 1; i >= 0; i -- ) {
			Phenomenon phe = pheList.get(i);
			for(Node intNode: intNodeList) {
				if(intNode.getNode_type().equals("ExpInt")) {
					if(phe.getPhenomenon_no() == intNode.getNode_no()) {
						pheList.remove(phe);
					}
				}
			}
		}
		//排序
		for(int i = 0; i < pheList.size(); i++) {
			for (int j = 0; j < pheList.size() -  i - 1; j++)
			{
				Phenomenon phe1 = pheList.get(j);
				Phenomenon phe2 = pheList.get(j+1);
				if(phe1.getPhenomenon_no() > phe2.getPhenomenon_no()) {
					Phenomenon phe;
					phe = phe1;
					pheList.set(j, phe2);
					pheList.set(j+1, phe);
				}
			}
		}
		return pheList;
	}

	private List<Phenomenon> getExpIntList(Project project, Requirement requirement) {
		List<RequirementPhenomenon> requirementPhenomenonList = getRequirementPhenomenon(project);
		List<Phenomenon> phenomenons = new ArrayList<Phenomenon>();
		for(RequirementPhenomenon reqPhe : requirementPhenomenonList) {
			if(reqPhe.getPhenomenon_requirement() == requirement.getRequirement_no()) {
				phenomenons.add(reqPhe);
			}
		}
		return phenomenons;
	}

	public List<Phenomenon> getIntList(Project project, String sgName) {
		List<ScenarioGraph> sgList = project.getScenarioGraphList();
		List<Requirement> requirementList = project.getProblemDiagram().getRequirementList();
		List<Phenomenon> pheList = null;
		for(ScenarioGraph scenarioGraph: sgList) {
			if(!scenarioGraph.getTitle().equals(sgName)) {
				for(Requirement requirement: requirementList) {
					if(requirement.getRequirement_context().equals(scenarioGraph.getRequirement())) {
						pheList = getBehIntList(project,requirement);
					}
				}
			}
		}

		//排序
		for(int i = 0; i < pheList.size(); i++) {
			for (int j = 0; j < pheList.size() -  i - 1; j++)
			{
				Phenomenon phe1 = pheList.get(j);
				Phenomenon phe2 = pheList.get(j+1);
				if(phe1.getPhenomenon_no() > phe2.getPhenomenon_no()) {
					Phenomenon phe;
					phe = phe1;
					pheList.set(j, phe2);
					pheList.set(j+1, phe);
				}
			}
		}
		return pheList;
	}

	public List<Phenomenon> getFullExpIntList(Project project, String sgName) {
		List<ScenarioGraph> sgList = project.getScenarioGraphList();
		List<Requirement> requirementList = project.getProblemDiagram().getRequirementList();
		List<Phenomenon> pheList = null;
		for(ScenarioGraph scenarioGraph: sgList) {
			if(!scenarioGraph.getTitle().equals(sgName)) {
				for(Requirement requirement: requirementList) {
					if(requirement.getRequirement_context().equals(scenarioGraph.getRequirement())) {
						pheList = getExpIntList(project,requirement);
					}
				}
			}
		}

		//排序
		if(pheList != null) {
			for(int i = 0; i < pheList.size(); i++) {
				for (int j = 0; j < pheList.size() -  i - 1; j++)
				{
					Phenomenon phe1 = pheList.get(j);
					Phenomenon phe2 = pheList.get(j+1);
					if(phe1.getPhenomenon_no() > phe2.getPhenomenon_no()) {
						Phenomenon phe;
						phe = phe1;
						pheList.set(j, phe2);
						pheList.set(j+1, phe);
					}
				}
			}
		}
		return pheList;
	}

	public Project getScenarioGraphs(Project project, String userName, String projectName) {

		if(project.getScenarioGraphList() == null) {
			List<ScenarioGraph> scenarioGraphList = new ArrayList<ScenarioGraph>();
			scenarioGraphList.addAll(getNewSGList(project,userName, projectName));
			project.setScenarioGraphList(scenarioGraphList);
		}else {
			project.getScenarioGraphList().clear();
			project.getScenarioGraphList().addAll(getNewSGList(project,userName, projectName));
		}

		return project;
	}

	public List<ScenarioGraph> getNewSGList(Project project, String userName, String projectName) {
		String address;
		if(userName.equals("")) {
			address = addressService.getRootAddress() + projectName;
		}else {
			address = addressService.getUserAddress() + userName + "/" + projectName;
		}
		FileService fileService = new FileService();
		List<ScenarioGraph> scenarioGraphs = new ArrayList<ScenarioGraph>();
		List<ScenarioGraph> scenarioGraphList = new ArrayList<>();
		Machine machine = project.getContextDiagram().getMachine();
		List<Requirement> requirementList = project.getProblemDiagram().getRequirementList();
		EnvEntity envEntity = fileService.getEnvEntity(address+"/");
		List<MyOntClass> problemDomainClasses = new ArrayList<MyOntClass>();
		if(envEntity != null) {
			problemDomainClasses = envEntity.getProblemDomains();
		}

		// 获取当前项目目录下的所有文件名
		File directory = new File(address);
		File result[] = directory.listFiles();
		List<String> SGFiles = new ArrayList<>();
		for(int i = 0; i<result.length; i++){
			File fs = result[i];
			if (fs.isFile() && fs.getName().contains("SG")) {
				SGFiles.add(fs.getName());
			}
		}

		// 删除多余的情景图
		for(int i=0;i<SGFiles.size();i++) {
			boolean found = false;
			String SGFile = SGFiles.get(i).replace(".xml", "");
			SGFile = SGFile.substring(SGFile.indexOf('-') + 1);
			for(Requirement requirement: requirementList) {
				String req = requirement.getRequirement_context().replace(" ", "_");
				if(SGFile.equals(req)) {
					found = true;
					break;
				}
			}
			if(!found) {
				File deleteFile = new File(address + "/" + SGFiles.get(i));
				deleteFile.delete();
				SGFiles.remove(i);
				i--;
			}
		}

		for(int i=0;i<SGFiles.size();i++) {
			ScenarioGraph scenarioGraph = fileService.getScenarioGraph(address + "/" + SGFiles.get(i));
			for(Requirement requirement: requirementList) {
				String req = requirement.getRequirement_context().replace(" ", "_");
				if(SGFiles.get(i).contains(req+".xml")) {
					scenarioGraph.setRequirement(requirement.getRequirement_context());
					break;
				}
			}
			scenarioGraphList.add(scenarioGraph);
		}

		for(Requirement requirement : requirementList) {
			boolean exist = false;
			String req = requirement.getRequirement_context();
			System.out.println("req:" + req);
			if(scenarioGraphList != null) {
				for(ScenarioGraph scenarioGraph: scenarioGraphList) {
					if(scenarioGraph.getRequirement()!=null && scenarioGraph.getRequirement().equals(req)) {

						List<ProblemDomain> problemDomainList = project.getContextDiagram().getProblemDomainList();
						List<Interface> interfaceList = project.getContextDiagram().getInterfaceList();
						List<RequirementPhenomenon> requirementPhenomenonList = getRequirementPhenomenon(project);
						List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
						List<RequirementPhenomenon> reqphenomenons = new ArrayList<RequirementPhenomenon>();
						List<Phenomenon> phenomenons = new ArrayList<Phenomenon>();
						for(int i = 0; i < interfaceList.size(); i ++ ) {
							Interface inte = interfaceList.get(i);
							phenomenonList.addAll(inte.getPhenomenonList());
						}
						List<Node> intNodeList = scenarioGraph.getIntNodeList();
						List<Line> lineList = scenarioGraph.getLineList();
						List<ProblemDomain> problemDomains = new ArrayList<ProblemDomain>();

						Node intNode;
						int expNum = 0;
						int intNum = 0;
						for(Node node: intNodeList) {
							if(node.getNode_type().equals(BehInt)) {
								intNum += 1;
							}
							else if(node.getNode_type().equals(ExpInt)) {
								expNum += 1;
							}
						}
						for(RequirementPhenomenon reqPhe : requirementPhenomenonList) {
							if(reqPhe.getPhenomenon_requirement() == requirement.getRequirement_no()) {
								Boolean needAdd = true;
								for(Node node: intNodeList) {
									if(reqPhe.getPhenomenon_no() == node.getNode_no() && node.getNode_type().equals(ExpInt)) {
										needAdd = false;
									}
								}
								if(needAdd) {
									expNum = expNum + 1;
									intNode = new Node();
									intNode.setNode_no(reqPhe.getPhenomenon_no());
									intNode.setNode_type("ExpInt");
									intNode.setNode_x(350);
									intNode.setNode_y(expNum*80);
									intNodeList.add(intNode);
									reqphenomenons.add(reqPhe);
									for(ProblemDomain problemDomain: problemDomainList) {
										String pdName = problemDomain.getProblemdomain_shortname();
										if(reqPhe.getPhenomenon_from().equals(pdName) ||
												reqPhe.getPhenomenon_to().equals(pdName)) {
											if(!problemDomains.contains(problemDomain)) {
												problemDomains.add(problemDomain);
												getProblemDomains(problemDomain,problemDomains,project);
											}
											break;
										}
									}
								}
							}
						}

						for(Phenomenon phenomenon: phenomenonList) {
							for(ProblemDomain problemDomain: problemDomains) {
								String pdName = problemDomain.getProblemdomain_shortname();
								if(phenomenon.getPhenomenon_from().equals(pdName) || phenomenon.getPhenomenon_to().equals(pdName)) {
									Boolean needAdd = true;
									for(Node node: intNodeList) {
										if(phenomenon.getPhenomenon_no() == node.getNode_no() && node.getNode_type().equals(BehInt)) {
											needAdd = false;
										}
									}
									if(needAdd) {
										intNum = intNum + 1;
										intNode = new Node();
										intNode.setNode_no(phenomenon.getPhenomenon_no());
										intNode.setNode_x(100);
										intNode.setNode_y(intNum*80);

										if(!(phenomenon.getPhenomenon_from().equals(machine.getMachine_shortName())
												|| phenomenon.getPhenomenon_to().equals(machine.getMachine_shortName()))) {
											intNode.setNode_type("ConnInt");
										}else {
											intNode.setNode_type("BehInt");
										}
										intNodeList.add(intNode);
										phenomenons.add(phenomenon);
										break;
									}
								}
							}
						}
						exist = true;
						break;
					}
				}
			}
			if(!exist) {
				List<ProblemDomain> problemDomainList = project.getContextDiagram().getProblemDomainList();
				List<Interface> interfaceList = project.getContextDiagram().getInterfaceList();
				List<RequirementPhenomenon> requirementPhenomenonList = getRequirementPhenomenon(project);
				List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
				List<RequirementPhenomenon> reqphenomenons = new ArrayList<RequirementPhenomenon>();
				List<Phenomenon> phenomenons = new ArrayList<Phenomenon>();
				for(int i = 0; i < interfaceList.size(); i ++ ) {
					Interface inte = interfaceList.get(i);
					phenomenonList.addAll(inte.getPhenomenonList());
				}
				ScenarioGraph SG = new ScenarioGraph();
				String title = "SG" + requirement.getRequirement_no() + "-" + req;
				title = title.replace(" ", "_");
				title = title.replace("\n", "_");
				List<Node> intNodeList = new ArrayList<Node>();
				List<Line> lineList = new ArrayList<Line>();
				List<ProblemDomain> problemDomains = new ArrayList<ProblemDomain>();

				Node intNode;
				int expNum = 0;
				int intNum = 0;
				for(RequirementPhenomenon reqPhe : requirementPhenomenonList) {
					if(reqPhe.getPhenomenon_requirement() == requirement.getRequirement_no()) {
						expNum = expNum + 1;
						intNode = new Node();
						intNode.setNode_no(reqPhe.getPhenomenon_no());
//					System.out.println("reqPhe.getPhenomenon_no():" + reqPhe.getPhenomenon_no() + "  reqPhe.getPhenomenon_requirement():" + reqPhe.getPhenomenon_requirement());
						intNode.setNode_type("ExpInt");
						intNode.setNode_x(350);
						intNode.setNode_y(expNum*80);
						intNodeList.add(intNode);
						reqphenomenons.add(reqPhe);
						for(ProblemDomain problemDomain: problemDomainList) {
							String pdName = problemDomain.getProblemdomain_shortname();
							if(reqPhe.getPhenomenon_from().equals(pdName) ||
									reqPhe.getPhenomenon_to().equals(pdName)) {
								if(!problemDomains.contains(problemDomain)) {
									problemDomains.add(problemDomain);
									getProblemDomains(problemDomain,problemDomains,project);
								}
								break;
							}
						}
					}
				}

				for(Phenomenon phenomenon: phenomenonList) {
					for(ProblemDomain problemDomain: problemDomains) {
						String pdName = problemDomain.getProblemdomain_shortname();
						if(phenomenon.getPhenomenon_from().equals(pdName) || phenomenon.getPhenomenon_to().equals(pdName)) {
							intNum = intNum + 1;
							intNode = new Node();
							intNode.setNode_no(phenomenon.getPhenomenon_no());
							intNode.setNode_x(100);
							intNode.setNode_y(intNum*80);

							if(!(phenomenon.getPhenomenon_from().equals(machine.getMachine_shortName())
									|| phenomenon.getPhenomenon_to().equals(machine.getMachine_shortName()))) {
								intNode.setNode_type("ConnInt");
							}else {
								intNode.setNode_type("BehInt");
							}
							intNodeList.add(intNode);
							phenomenons.add(phenomenon);
							break;
						}
					}
				}

				for(Node node : intNodeList) {
					if(node.getNode_type().equals("BehInt") || node.getNode_type().equals("ConnInt")) {
						for(Node tmpNode : intNodeList) {
							if(tmpNode.getNode_type().equals("ExpInt") && tmpNode.getNode_no() == node.getNode_no()) {
								Line line = new Line();
								line.setLine_no(lineList.size() + 1);
								line.setLine_type("Synchrony");
								line.setFromNode(node);
								line.setToNode(tmpNode);
								lineList.add(line);
								break;
							}
						}
					}
				}
				SG.setTitle(title);
				SG.setRequirement(req);
				SG.setIntNodeList(intNodeList);
				SG.setLineList(lineList);

//				for(ProblemDomain problemDomain: problemDomains) {
//					String domainType = problemDomain.getProblemdomain_type();
//					if(domainType.equals(Sensor) || domainType.equals(Actuator) || domainType.equals(Clock) || domainType.equals("Causal")) {
//						String domainName = problemDomain.getProblemdomain_name();
//						for(MyOntClass problemDomainClass : problemDomainClasses) {
//							String className = problemDomainClass.getName();
//							if(className.equals(domainName) ||
//									Arrays.asList(domainName.split("&")).contains(className) ||
//									className.replace("_", " ").equals(domainName)) {
//								List<Transition> transitions = getTransition(problemDomainClass, envEntity);
//								if(transitions != null) {
//									SG = addTranstion(reqphenomenons, phenomenons, SG, transitions);
//								}
//
//								List<IOAutomata> IOAutomatas = getIOAutomatas(problemDomainClass, envEntity);
//								if(IOAutomatas != null) {
//									SG = addIOTranstion(reqphenomenons, phenomenons, SG, IOAutomatas, envEntity);
//								}
//								break;
//							}
//						}
//					}
//				}
				for(ProblemDomain problemDomain: problemDomains) {
					if(isDevice(problemDomain) || problemDomain.getProblemdomain_type().equals(Clock)) {
						String domianName = problemDomain.getProblemdomain_name();
						System.out.println("*" + domianName);
						for(MyOntClass problemDomainClass : problemDomainClasses) {
							String className = problemDomainClass.getName();
							if(className.equals(domianName) ||
									Arrays.asList(domianName.split("&")).contains(className) ||
									className.replace("_", " ").equals(domianName)) {
								List<Transition> transitions = getTransition(problemDomainClass, envEntity);
								if(transitions != null) {
									SG = addTranstion(reqphenomenons, phenomenons, SG, transitions);
								}
								break;
							}
						}
					}
				}
				scenarioGraphs.add(SG);
			}
		}
		scenarioGraphs.addAll(scenarioGraphList);
		return scenarioGraphs;
	}

	private List<ProblemDomain> getProblemDomains(ProblemDomain problemDomain, List<ProblemDomain> problemDomains, Project project) {
		Machine machine = project.getContextDiagram().getMachine();
		List<Interface> interfaceList = project.getContextDiagram().getInterfaceList();
		List<ProblemDomain> domainList = project.getContextDiagram().getProblemDomainList();
		List<ProblemDomain> domains = new ArrayList<ProblemDomain>();
		String pdName = problemDomain.getProblemdomain_shortname();
		for(Interface inte : interfaceList) {
			if(inte.getInterface_from().equals(pdName)) {
				if(!inte.getInterface_to().equals(machine.getMachine_shortName())) {
					for(ProblemDomain pd: domainList) {
						if(pd.getProblemdomain_shortname().equals(inte.getInterface_to())) {
							if(!problemDomains.contains(pd)) {
								problemDomains.add(pd);
								getProblemDomains(pd, problemDomains, project);
							}
							break;
						}
					}
				}
			}else if(inte.getInterface_to().equals(pdName)) {
				if(!inte.getInterface_from().equals(machine.getMachine_shortName())) {
					for(ProblemDomain pd: domainList) {
						if(pd.getProblemdomain_shortname().equals(inte.getInterface_from())) {
							if(!problemDomains.contains(pd)) {
								problemDomains.add(pd);
								getProblemDomains(pd, problemDomains, project);
							}
							break;
						}
					}
				}
			}
		}
		return domains;
	}

//	public List<Transition> getTransition(MyOntClass problemDomain, EnvEntity envEntity){
//		List<Transition> transitions = new ArrayList<Transition>();
// 		String stateMachineName = problemDomain.getSM_name();
//		if(stateMachineName != null)
//	 	{
//			 List<OntClass> stateMachines = problemDomain.getStateMachine();
//			 if(stateMachines == null) {
//				 return transitions;
//			 }
//			 for(OntClass stateMachine: stateMachines) {
//				 List<String> transitionNames = envEntity.getRestrictionValue(stateMachine, "has_trans");
//				 for(String transitionName: transitionNames) {
//		   			Transition transition = new Transition();
//		   			OntClass transitionClass = envEntity.getOntClass(envEntity.getFilepath(), transitionName);
//		   			envEntity.getRestrictionValue(transitionClass, "source_from");
//		   			String from = envEntity.getRestrictionValue(transitionClass, "source_from").get(0);
//		   			String to = envEntity.getRestrictionValue(transitionClass, "sink_to").get(0);
//		   			List<String> triggers = envEntity.getRestrictionValue(transitionClass, "be_triggered_by");
//		   			transition.setFrom(from);
//		   			transition.setTo(to);
//		   			transition.setTrigger(triggers);
//		   			transitions.add(transition);
//		   			System.out.println("trigger:");
//		   			for(String trigger: triggers) {
//		   				System.out.println(trigger);
//		   			}
//		   		}
//	   		}
//	   	}
//		return transitions;
//	}

	public List<Transition> getTransition(MyOntClass problemDomain, EnvEntity envEntity){
		List<Transition> transitions = new ArrayList<Transition>();
		String stateMachineName = problemDomain.getTA_name();
		if(stateMachineName != null)
		{
			List<OntClass> TimedAutomatons = problemDomain.getTimedAutomaton();
			for(OntClass TimedAutomaton: TimedAutomatons) {
				String npuri = TimedAutomaton.getURI();
				npuri = npuri.substring(0, npuri.indexOf("#")+1);
				List<String> transitionNames = envEntity.getRestrictionValue(TimedAutomaton, "has_trans");
//				List<String> transitionNames = envEntity.getIndividualProperty(TimedAutomaton, "has_trans", npuri);
				for(String transitionName: transitionNames) {
					Transition transition = new Transition();
//					OntClass transitionClass = envEntity.getOntClass(envEntity.getFilepath(), transitionName);
//					envEntity.getRestrictionValue(transitionClass, "has_from");
//					String from = envEntity.getRestrictionValue(transitionClass, "has_state_from").get(0);
//					String to = envEntity.getRestrictionValue(transitionClass, "has_state_to").get(0);
//					List<String> triggers = envEntity.getRestrictionValue(transitionClass, "has_sync");

//					Individual transitionInd = envEntity.getIndvidual(envEntity.getFilepath(), npuri+transitionName);
//					List<String> ans = envEntity.getIndividualProperty(transitionInd, "has_state_from", npuri);
					OntClass transitionClass = envEntity.getOntClass(envEntity.getFilepath(), transitionName);
					List<String> ans = envEntity.getRestrictionValue(transitionClass, "has_state_from");
					String from = null, to = null;
					if(ans != null && !ans.isEmpty()) {
						from = ans.get(0);
					}
//					ans = envEntity.getIndividualProperty(transitionInd, "has_state_to", npuri);
					ans = envEntity.getRestrictionValue(transitionClass, "has_state_to");
					if(ans != null && !ans.isEmpty()) {
						to = ans.get(0);
					}
//					List<String> triggers = envEntity.getIndividualProperty(transitionInd, "has_sync", npuri);
					List<String> triggers = envEntity.getRestrictionValue(transitionClass, "has_sync");
					for(int i=0;i<triggers.size();i++) {
						triggers.set(i, triggers.get(i).replace("?", ""));
						triggers.set(i, triggers.get(i).replace("!", ""));
					}

					transition.setName(transitionName);
					transition.setFrom(from);
					transition.setTo(to);
					transition.setSync(triggers);
					transitions.add(transition);
				}
			}
		}
		return transitions;
	}

	//	public ScenarioGraph addTranstion(List<RequirementPhenomenon> reqphenomenons, List<Phenomenon> phenomenons, ScenarioGraph sg, List<Transition> transitions) {
//		List<Node> nodes = sg.getIntNodeList();
//		List<Line> lines = sg.getLineList();
//
//		for(Transition transition : transitions) {
//			String to = transition.getTo();
//			List<String> triggers = transition.getTrigger();
//			Phenomenon toPhenomenon = new Phenomenon();
//			List<Phenomenon> triggerPhenomenons = new ArrayList<Phenomenon>();
//			Node toNode = new Node();
//			List<Node> triggerNodes = new ArrayList<Node>();
//			for(String trigger : triggers) {
//				for(Phenomenon phenomenon : phenomenons) {
//					if(phenomenon.getPhenomenon_name().equals(trigger)) {
//						triggerPhenomenons.add(phenomenon);
//						break;
//					}
//				}
//			}
//
//			for(Phenomenon phenomenon : triggerPhenomenons) {
//				for(Node node : nodes) {
//					if(node.getNode_type().equals("BehInt") || node.getNode_type().equals("ConnInt")) {
//						if(node.getNode_no() == phenomenon.getPhenomenon_no()) {
//							triggerNodes.add(node);
//							break;
//						}
//					}
//				}
//			}
//
//			for(Node triggerNode : triggerNodes) {
//				for(Phenomenon phenomenon : reqphenomenons) {
//					if(phenomenon.getPhenomenon_name().equals(to)) {
//						toPhenomenon = phenomenon;
//						for(Node node : nodes) {
//							if(node.getNode_type().equals("ExpInt")) {
//								if(node.getNode_no() == toPhenomenon.getPhenomenon_no()) {
//									toNode = node;
//									Line line = new Line();
//									line.setLine_no(lines.size() + 1);
//									line.setLine_type("BehEnable");
//									line.setFromNode(triggerNode);
//									line.setToNode(toNode);
//									lines.add(line);
//									break;
//								}
//							}
//						}
//						break;
//					}
//				}
//
//				for(Phenomenon phenomenon : phenomenons) {
//					if(phenomenon.getPhenomenon_name().equals(to)) {
//						toPhenomenon = phenomenon;
//						for(Node node : nodes) {
//							if(node.getNode_type().equals("ConnInt")) {
//								if(node.getNode_no() == toPhenomenon.getPhenomenon_no()) {
//									toNode = node;
//									Line line = new Line();
//									line.setLine_no(lines.size() + 1);
//									line.setLine_type("BehOrder");
//									line.setFromNode(triggerNode);
//									line.setToNode(toNode);
//									lines.add(line);
//									break;
//								}
//							}
//						}
//						break;
//					}
//				}
//			}
//		}
//		return sg;
//	}
	public ScenarioGraph addTranstion(List<RequirementPhenomenon> reqphenomenons, List<Phenomenon> phenomenons, ScenarioGraph sg, List<Transition> transitions) {
		List<Node> nodes = sg.getIntNodeList();
		List<Line> lines = sg.getLineList();

		for(Transition transition : transitions) {
			String to = transition.getTo();
			List<String> triggers = transition.getSync();
			Phenomenon toPhenomenon = new Phenomenon();
			List<Phenomenon> triggerPhenomenons = new ArrayList<Phenomenon>();
			Node toNode = new Node();
			List<Node> triggerNodes = new ArrayList<Node>();
			for(String trigger : triggers) {
				for(Phenomenon phenomenon : phenomenons) {
					if(phenomenon.getPhenomenon_name().equals(trigger)) {
						triggerPhenomenons.add(phenomenon);
						break;
					}
				}
			}

			for(Phenomenon phenomenon : triggerPhenomenons) {
				for(Node node : nodes) {
					if(node.getNode_type().equals("BehInt") || node.getNode_type().equals("ConnInt")) {
						if(node.getNode_no() == phenomenon.getPhenomenon_no()) {
							triggerNodes.add(node);
							break;
						}
					}
				}
			}

			for(Node triggerNode : triggerNodes) {
				for(Phenomenon phenomenon : reqphenomenons) {
					if(phenomenon.getPhenomenon_name().equals(to)) {
						toPhenomenon = phenomenon;
						for(Node node : nodes) {
							if(node.getNode_type().equals("ExpInt")) {
								if(node.getNode_no() == toPhenomenon.getPhenomenon_no()) {
									toNode = node;
									Line line = new Line();
									line.setLine_no(lines.size() + 1);
									line.setLine_type("BehEnable");
									line.setFromNode(triggerNode);
									line.setToNode(toNode);
									lines.add(line);
									break;
								}
							}
						}
						break;
					}
				}

				for(Phenomenon phenomenon : phenomenons) {
					if(phenomenon.getPhenomenon_name().equals(to)) {
						toPhenomenon = phenomenon;
						for(Node node : nodes) {
							if(node.getNode_type().equals("ConnInt")) {
								if(node.getNode_no() == toPhenomenon.getPhenomenon_no()) {
									toNode = node;
									Line line = new Line();
									line.setLine_no(lines.size() + 1);
									line.setLine_type("BehOrder");
									line.setFromNode(triggerNode);
									line.setToNode(toNode);
									lines.add(line);
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
		return sg;
	}

//	private List<IOAutomata> getIOAutomatas(MyOntClass problemDomain, EnvEntity envEntity) {
//		List<IOAutomata> IOAutomatas = new ArrayList<IOAutomata>();
//		List<OntClass> IOAutomataClasses = problemDomain.getIOAutomata();
//		if(IOAutomataClasses != null)
//	 	{
//	   		for(OntClass IOAutomata: IOAutomataClasses) {
//	   			IOAutomata ioAutomata = new IOAutomata();
//	   			List<String> inputs = envEntity.getRestrictionValue(IOAutomata, "has_in");
//		   		List<String> outputs = envEntity.getRestrictionValue(IOAutomata, "has_out");
//		   		List<String> states = envEntity.getRestrictionValue(IOAutomata, "has_state");
//
//		   		List<Transition> transitions = getIOTransitions(IOAutomata, envEntity);
//		   		ioAutomata.setName(IOAutomata.getLocalName());
//		   		ioAutomata.setInputs(inputs);
//		   		ioAutomata.setOutputs(outputs);
//		   		ioAutomata.setStates(states);
//		   		ioAutomata.setTransitions(transitions);
//
//		   		IOAutomatas.add(ioAutomata);
//	   		}
//	   	}
//		return IOAutomatas;
//	}

	private ScenarioGraph addIOTranstion(List<RequirementPhenomenon> reqphenomenons, List<Phenomenon> phenomenons,
										 ScenarioGraph sg, List<IOAutomata> IOAutomatas, EnvEntity envEntity) {
		List<Node> nodes = sg.getIntNodeList();
		List<Line> lines = sg.getLineList();
		for(IOAutomata ioAutomata : IOAutomatas) {
			List<Transition> transitions = ioAutomata.getTransitions();
			List<String> inputs = ioAutomata.getInputs();
			List<String> outputs = ioAutomata.getOutputs();
			List<String> triggers = new ArrayList<String>();
			Node fromNode = null;

			System.out.println("******" + ioAutomata.getName() + "******");
			for(Transition transition: transitions) {
				System.out.println("trans: " + transition.getName());
				System.out.println("from: " + transition.getFrom());
				System.out.println("to: " + transition.getTo());
				for(String trigger : transition.getSync()) {
					System.out.println("trigger: " + trigger);
				}
			}

			for(String input : inputs) {
				List<Node> toNodes = new ArrayList<Node>();
				for(Phenomenon phenomenon : phenomenons) {
					if(phenomenon.getPhenomenon_name().equals(input)) {
						for(Node node : nodes) {
							if(node.getNode_type().equals("BehInt") || node.getNode_type().equals("ConnInt")) {
								if(node.getNode_no() == phenomenon.getPhenomenon_no()) {
									fromNode = node;
									System.out.println(fromNode.getNode_type() + fromNode.getNode_no());
									break;
								}
							}
						}
						break;
					}
				}

				for(Transition transition: transitions) {
					if(transition.getSync().contains(input)) {
						String toState = transition.getTo();
						for(Transition othertrans: transitions) {
							if(othertrans.getFrom().equals(toState)) {
								triggers = othertrans.getSync();
								break;
							}
						}
					}
				}

				for(String trigger: triggers) {
					System.out.println("output: " + trigger);
					if(outputs.contains(trigger)) {
						for(Phenomenon phenomenon : phenomenons) {
							if(phenomenon.getPhenomenon_name().equals(trigger)) {
								for(Node node : nodes) {
									if(node.getNode_type().equals("BehInt") || node.getNode_type().equals("ConnInt")) {
										if(node.getNode_no() == phenomenon.getPhenomenon_no()) {
											toNodes.add(node);
											break;
										}
									}
								}
								break;
							}
						}

					}
				}

				for(Node toNode : toNodes) {
					Line line = new Line();
					line.setLine_no(lines.size() + 1);
					line.setLine_type("BehOrder");
					line.setFromNode(fromNode);
					line.setToNode(toNode);
					System.out.println(line.getFromNode().getNode_type() + line.getFromNode().getNode_no() + " -> " + line.getToNode().getNode_type() + line.getToNode().getNode_no());
					lines.add(line);
				}
			}
		}
		return sg;
	}

	private List<Transition> getIOTransitions(OntClass IOAutomata, EnvEntity envEntity) {
		List<Transition> transitions = new ArrayList<Transition>();
		List<String> transitionNames = envEntity.getRestrictionValue(IOAutomata, "has_trans");
		for(String transitionName: transitionNames) {
			Transition transition = new Transition();
			OntClass transitionClass = envEntity.getOntClass(envEntity.getFilepath(), transitionName);
			String from = envEntity.getRestrictionValue(transitionClass, "has_state_from").get(0);
			String to = envEntity.getRestrictionValue(transitionClass, "has_state_to").get(0);
			List<String> triggers = envEntity.getRestrictionValue(transitionClass, "has_sync");
			transition.setName(transitionName);
			transition.setFrom(from);
			transition.setTo(to);
			transition.setSync(triggers);
			transitions.add(transition);

		}
		return transitions;
	}

	public Project getBreakdownScenarioGraph(Project project) {
		ScenarioGraph fullScenarioGraph = project.getFullScenarioGraph();
		List<CtrlNode> ctrlNodeList = getCtrlNodeListByFSG(fullScenarioGraph);
		List<Node> intNodeList = getIntNodeListByFSG(fullScenarioGraph);
		List<Line> lineList = getLineListByFSG(fullScenarioGraph);
		List<Line> behLineList = new ArrayList<Line>();
		List<Line> expLineList = new ArrayList<Line>();
		List<Line> middleLineList = new ArrayList<Line>();
		for(Line line: lineList) {
			if(line.getLine_type().equals("BehOrder")) {
				behLineList.add(line);
			} else if(line.getLine_type().equals("ExpOrder")) {
				expLineList.add(line);
			} else {
				middleLineList.add(line);
			}
		}

		List<List<String>> behLinePath = new ArrayList<List<String>>();
		List<List<String>> expLinePath = new ArrayList<List<String>>();
		// 获取所有路径
		behLinePath = getPath(behLineList, behLinePath);
		expLinePath = getPath(expLineList, expLinePath);
		// 根据路径分别构造左边的活动图和右边的活动图
		List<ScenarioGraph> behADList = new ArrayList<ScenarioGraph>();
		List<ScenarioGraph> expADList = new ArrayList<ScenarioGraph>();
		behADList = generateADByPath(behLinePath, intNodeList, ctrlNodeList, behLineList, behADList, "BehOrder");
		expADList = generateADByPath(expLinePath, intNodeList, ctrlNodeList, expLineList, expADList, "ExpOrder");
		behADList = dealWithADList(behADList, "BehOrder");
		expADList = dealWithADList(expADList, "ExpOrder");
		// 把左右两边的活动图以及中间的线结合成完整的活动图
		List<ScenarioGraph> newADList = new ArrayList<ScenarioGraph>();
		newADList = generateADList(behADList, expADList, middleLineList, newADList);
		project.setNewScenarioGraphList(newADList);
		return project;
	}
	private Boolean isDevice(ProblemDomain domain) {
		// 判断domain是否属于设备
		if(domain == null) {
			return false;
		}
		String type = domain.getProblemdomain_type();

		return type.equals(Sensor) || type.equals(Actuator)
				|| type.equals(ActiveDevice) || type.equals(DataProcessDevice)
				|| type.equals(LaunchDevice) || type.equals(ManageControlDevice)
				|| type.equals(ReceiveDevice);
	}


	private List<ScenarioGraph> generateADList(List<ScenarioGraph> behADList, List<ScenarioGraph> expADList,
											   List<Line> middleLineList, List<ScenarioGraph> newADList) {
		for(int i = 0; i < behADList.size(); i++) {
			ScenarioGraph behAD = behADList.get(i);
			ScenarioGraph expAD = expADList.get(i);
			List<CtrlNode> ctrlNodeList = new ArrayList<CtrlNode>();
			List<Node> intNodeList = new ArrayList<Node>();
			List<Line> lineList = new ArrayList<Line>();
			intNodeList = getFullIntNodeList(behAD.getIntNodeList(), expAD.getIntNodeList());
			ctrlNodeList = getFullCtrlNodeList(behAD.getCtrlNodeList(), expAD.getCtrlNodeList());
			lineList = getFullLineList(behAD.getLineList(), expAD.getLineList());
			for(Line line: middleLineList) {
				Node fromNode = line.getFromNode();
				Node toNode = line.getToNode();
				for(int m = 0; m < intNodeList.size(); m++) {
					for(int n = m + 1; n < intNodeList.size(); n++) {
						Node tmpNode1 = intNodeList.get(m);
						Node tmpNode2 = intNodeList.get(n);
						if(fromNode.getNode_no() == tmpNode1.getNode_no() && fromNode.getNode_type().equals(tmpNode1.getNode_type())
								&& toNode.getNode_no() == tmpNode2.getNode_no() && toNode.getNode_type().equals(tmpNode2.getNode_type())) {
							lineList.add(line);
						}
					}
				}
			}
			ScenarioGraph scenarioGraph = new ScenarioGraph();
			String title = "NSG" + (i + 1) + "-" + "ScenarioGraph";
			scenarioGraph.setTitle(title);
			scenarioGraph.setRequirement(null);
			scenarioGraph.setIntNodeList(intNodeList);
			scenarioGraph.setCtrlNodeList(ctrlNodeList);
			scenarioGraph.setLineList(lineList);
			newADList.add(scenarioGraph);
		}
		return newADList;
	}

	private List<Node> getFullIntNodeList(List<Node> intNodeList1, List<Node> intNodeList2) {
		List<Node> list = new ArrayList<Node>();
		list.addAll(intNodeList1);
		list.addAll(intNodeList2);
		return list;
	}

	private List<CtrlNode> getFullCtrlNodeList(List<CtrlNode> ctrlNodeList1, List<CtrlNode> ctrlNodeList2) {
		List<CtrlNode> list = new ArrayList<CtrlNode>();
		list.addAll(ctrlNodeList1);
		list.addAll(ctrlNodeList2);
		return list;
	}

	private List<Line> getFullLineList(List<Line> lineList1, List<Line> lineList2) {
		List<Line> list = new ArrayList<Line>();
		list.addAll(lineList1);
		list.addAll(lineList2);
		return list;
	}

	private List<ScenarioGraph> dealWithADList(List<ScenarioGraph> ADList, String lineType) {
		for(ScenarioGraph scenarioGraph: ADList) {
			List<CtrlNode> ctrlNodeList = scenarioGraph.getCtrlNodeList();
			List<Node> intNodeList = scenarioGraph.getIntNodeList();
			List<Line> lineList = scenarioGraph.getLineList();
			List<Line> lines = new ArrayList<Line>();
			for(Line line: lineList) {
				Node fromNode = line.getFromNode();
				Node toNode = line.getToNode();
				// 如果某条线的toNode是Branch节点或Merge节点
				// 则新建一条线，该线的toNode设置为与Branch节点或Merge节点相连的下一节点
				if(toNode != null && (toNode.getNode_type().equals("Branch") || toNode.getNode_type().equals("Merge"))) {
					for(Line tmpLine: lineList) {
						Node tmpNode = tmpLine.getFromNode();
						if(tmpNode.getNode_no() == toNode.getNode_no() && tmpNode.getNode_type().equals(toNode.getNode_type())) {
							Line newLine = new Line();
							newLine = copyLine(newLine, line);
							newLine.setToNode(tmpLine.getToNode());
							lines.add(newLine);
							ctrlNodeList.remove(tmpNode);
							ctrlNodeList.remove(toNode);
							break;
						}
					}
				} else if(fromNode != null && (fromNode.getNode_type().equals("Branch") || fromNode.getNode_type().equals("Merge"))) {
					continue;
				} else {
					lines.add(line);
				}
			}
			// 删除重复的节点
			intNodeList = removeRedundantNodes(intNodeList);
			ctrlNodeList = removeRedundantCtrlNodes(ctrlNodeList);
			// TODO 重新设置节点的fromList和toList，并重新排列位置   done
			Node startNode = lines.get(0).getFromNode();
			for(CtrlNode ctrlNode: ctrlNodeList) {
				if(ctrlNode == null) {
					continue;
				}
				if(startNode.getNode_no() == ctrlNode.getNode_no() && startNode.getNode_type().equals(ctrlNode.getNode_type())) {
					if(lineType.equals("BehOrder")) {
						ctrlNode.setNode_x(150);
						ctrlNode.setNode_y(50);
					} else {
						ctrlNode.setNode_x(400);
						ctrlNode.setNode_y(50);
					}
				}
			}
			for(int i = 0; i < lines.size(); i++) {
				Line line = lines.get(i);
				Node fromNode = line.getFromNode();
				Node toNode = line.getToNode();
				if(fromNode == null || toNode == null) {
					continue;
				}
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
					for(int j = 0; j < ctrlNodeList.size(); j++) {
						CtrlNode ctrlNode = ctrlNodeList.get(j);
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
							if(fromNodeType.equals("Start")) {
								intNode.setNode_x(tmpFromNode.getNode_x() + 10);
								intNode.setNode_y(tmpFromNode.getNode_y() + 90);
								intNodeList.set(j, intNode);
								break;
							} else if(fromNodeType.equals("Delay")) {
								intNode.setNode_x(tmpFromNode.getNode_x() + 18);
								intNode.setNode_y(tmpFromNode.getNode_y() + 120);
								intNodeList.set(j, intNode);
								break;
							} else {
								intNode.setNode_x(tmpFromNode.getNode_x());
								intNode.setNode_y(tmpFromNode.getNode_y() + 80);
								intNodeList.set(j, intNode);
								break;
							}
						}
					}
				} else {
					for(int j = 0; j < ctrlNodeList.size(); j++) {
						CtrlNode ctrlNode = ctrlNodeList.get(j);
						if(toNode.getNode_no() == ctrlNode.getNode_no() && toNode.getNode_type().equals(ctrlNode.getNode_type())) {
							if(fromNodeType.equals("Start") && toNodeType.equals("Delay")) {
								ctrlNode.setNode_x(tmpFromNode.getNode_x() - 10);
								ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
								ctrlNodeList.set(j, ctrlNode);
								break;
							} else if(fromNodeType.equals("Start")) {
								ctrlNode.setNode_x(tmpFromNode.getNode_x() + 10);
								ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
								ctrlNodeList.set(j, ctrlNode);
								break;
							} else if(toNodeType.equals("Delay")) {
								ctrlNode.setNode_x(tmpFromNode.getNode_x() - 20);
								ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
								ctrlNodeList.set(j, ctrlNode);
								break;
							} else {
								ctrlNode.setNode_x(tmpFromNode.getNode_x());
								ctrlNode.setNode_y(tmpFromNode.getNode_y() + 80);
								ctrlNodeList.set(j, ctrlNode);
								break;
							}
						}
					}
				}
			}
			scenarioGraph.setLineList(lines);
		}
		return ADList;
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

	private List<Node> removeRedundantNodes(List<Node> nodeList) {
		for(int i = nodeList.size() - 1; i >= 0; i--) {
			Node node1 = nodeList.get(i);
			for(int j = i - 1; j >= 0; j--) {
				Node node2 = nodeList.get(j);
				if(node1.getNode_no() == node2.getNode_no() && node1.getNode_type().equals(node2.getNode_type())) {
					nodeList.remove(node1);
					break;
				}
			}
		}
		return nodeList;
	}

	private List<CtrlNode> removeRedundantCtrlNodes(List<CtrlNode> nodeList) {
		for(int i = nodeList.size() - 1; i >= 0; i--) {
			CtrlNode node1 = nodeList.get(i);
			for(int j = i - 1; j >= 0; j--) {
				CtrlNode node2 = nodeList.get(j);
				if(node1 != null && node2 != null && node1.getNode_no() == node2.getNode_no() && node1.getNode_type().equals(node2.getNode_type())) {
					nodeList.remove(node1);
					break;
				}
			}
		}
		return nodeList;
	}

	private List<ScenarioGraph> generateADByPath(List<List<String>> linePath, List<Node> intNodeList,
												 List<CtrlNode> ctrlNodeList, List<Line> lineList, List<ScenarioGraph> ADList, String lineType) {
		for(List<String> nowPath: linePath) {
			int lineNo = 1;
			List<CtrlNode> ctrlNodes = new ArrayList<CtrlNode>();
			List<Node> intNodes = new ArrayList<Node>();
			List<Line> lines = new ArrayList<Line>();
			for(int i = 0; i < nowPath.size() - 1; i++) {
				String from = nowPath.get(i);
				String to = nowPath.get(i + 1);
				String fromType = from.substring(0, from.length() - 1);
				String toType = to.substring(0, to.length() - 1);
				Node fromNode = new Node();
				CtrlNode fromCtrlNode = new CtrlNode();
				Node toNode = new Node();
				CtrlNode toCtrlNode = new CtrlNode();
				if(fromType.equals("BehInt") || fromType.equals("ConnInt") || fromType.equals("ExpInt")) {
					fromNode = findIntNode(from, intNodeList);
					intNodes.add(fromNode);
				} else {
					fromCtrlNode = findCtrlNode(from, ctrlNodeList);
					ctrlNodes.add(fromCtrlNode);
				}
				if(toType.equals("BehInt") || toType.equals("ConnInt") || toType.equals("ExpInt")) {
					toNode = findIntNode(to, intNodeList);
					intNodes.add(toNode);
				} else {
					toCtrlNode = findCtrlNode(to, ctrlNodeList);
					ctrlNodes.add(toCtrlNode);
				}
				Line line = new Line();
				line.setLine_no(lineNo);
				line.setLine_type(lineType);
				line.setTurnings(null);
				if(fromType.equals("Decision")) {
					for(Line tmpLine: lineList) {
						Node tmpNode = tmpLine.getFromNode();
						if(from.equals(tmpNode.getNode_type() + tmpNode.getNode_no())) {
							line.setCondition(tmpLine.getCondition());
						}
					}
				} else {
					line.setCondition(null);
				}
				if(fromType.equals("BehInt") || fromType.equals("ConnInt") || fromType.equals("ExpInt")) {
					line.setFromNode(fromNode);
				} else {
					line.setFromNode(fromCtrlNode);
				}
				if(toType.equals("BehInt") || toType.equals("ConnInt") || toType.equals("ExpInt")) {
					line.setToNode(toNode);
				} else {
					line.setToNode(toCtrlNode);
				}
				lines.add(line);
				lineNo++;
			}
			ScenarioGraph scenarioGraph = new ScenarioGraph();
			scenarioGraph.setTitle("ActivityDiagram");
			scenarioGraph.setRequirement(null);
			scenarioGraph.setIntNodeList(intNodes);
			scenarioGraph.setCtrlNodeList(ctrlNodes);
			scenarioGraph.setLineList(lines);
			ADList.add(scenarioGraph);
		}
		return ADList;
	}

	private Node findIntNode(String from, List<Node> intNodeList) {
		for(Node intNode: intNodeList) {
			if(from.equals(intNode.getNode_type() + intNode.getNode_no())) {
				return intNode;
			}
		}
		return null;
	}

	private CtrlNode findCtrlNode(String from, List<CtrlNode> ctrlNodeList) {
		for(CtrlNode ctrlNode: ctrlNodeList) {
			if(from.equals(ctrlNode.getNode_type() + ctrlNode.getNode_no())) {
				return ctrlNode;
			}
		}
		return null;
	}

	private List<List<String>> getPath(List<Line> lineList, List<List<String>> linePath) {
		List<DirectedLine> directedLineList = new ArrayList<>();
		String startName = null;
		String endName = null;
		for(Line line: lineList) {
			Node fromNode = line.getFromNode();
			Node toNode = line.getToNode();
			String fromName = fromNode.getNode_type() + fromNode.getNode_no();
			String toName = toNode.getNode_type() + toNode.getNode_no();
			directedLineList.add(new DirectedLine(fromName, toName));
			if(fromNode.getNode_type().equals("Start")) {
				startName = fromName;
			}
			if(toNode.getNode_type().equals("End")) {
				endName = toName;
			}
		}
		// 当前路径
		List<String> nowPath = new ArrayList<>();
		// 所有路径
		List<List<String>> allPath = new ArrayList<List<String>>();
		linePath = findAllPath(directedLineList, startName, endName, nowPath, allPath);
		return linePath;
	}


	private List<List<String>> findAllPath(List<DirectedLine> directedLineList, String startName, String endName,
										   List<String> nowPath, List<List<String>> allPath) {
		List<String> newPath = new ArrayList<>();
		if(nowPath.contains(startName)){
			System.out.println("这是一个环：:" + nowPath);
			nowPath.remove(nowPath.size() - 1);
			return null;
		}

		for(int i = 0; i < directedLineList.size(); i++){
			DirectedLine line = directedLineList.get(i);
			if(line.getSource().equals(startName)){
				nowPath.add(line.getSource());
				if(line.getTarget().equals(endName)){
					nowPath.add(line.getTarget());
					System.out.println("这是一条路径：:" + nowPath);
					newPath = copyPath(newPath, nowPath);
					allPath.add(newPath);
					// 因为添加了终点路径,所以要返回两次
					nowPath.remove(nowPath.size() - 1);
					nowPath.remove(nowPath.size() - 1);
					// 已经找到路径,返回上层找其他路径
					continue;
				}
				findAllPath(directedLineList, line.getTarget(), endName, nowPath, allPath);
			}
		}
		// 如果找不到下个节点,返回上层
		if(nowPath.size() > 0){
			nowPath.remove(nowPath.size() - 1);
		}
		return allPath;
	}

	private List<String> copyPath(List<String> newPath, List<String> nowPath) {
		for(int i = 0; i < nowPath.size(); i++) {
			String element = nowPath.get(i);
			newPath.add(element);
		}
		return newPath;
	}

	private List<CtrlNode> getCtrlNodeListByFSG(ScenarioGraph fullScenarioGraph){
		List<CtrlNode> ctrlNodeList = new ArrayList<CtrlNode>();
		List<CtrlNode> SGCtrlNodeList = fullScenarioGraph.getCtrlNodeList();
		if(SGCtrlNodeList == null) {
			return ctrlNodeList;
		}
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
		return ctrlNodeList;
	}

	private List<Node> getIntNodeListByFSG(ScenarioGraph fullScenarioGraph){
		List<Node> intNodeList = new ArrayList<Node>();
		List<Node> SGIntNodeList = fullScenarioGraph.getIntNodeList();
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
		return intNodeList;
	}

	private List<Line> getLineListByFSG(ScenarioGraph fullScenarioGraph){
		List<Line> lineList = new ArrayList<Line>();
		List<Line> SGLineList = fullScenarioGraph.getLineList();
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
		return lineList;
	}
}
