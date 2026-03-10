package com.example.demo.service;

import java.io.File;
import java.util.*;

import com.example.demo.bean.*;
import com.example.demo.bean.Error;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.ontology.OntClass;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
	private String reqModelPrefix = AddressService.reqModelDir;
	private String devModelPrefix = AddressService.devModelDir;
	// 定义使用到的类型常量
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
	private static final String Sensor = "Sensor Device";  // 感应器设备
	private static final String Actuator = "Actuator Device";  // 执行器设备
	private static final String ActiveDevice = "Active Device";  // 主动设备

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
		Set<Phenomenon> phenomenonSet = new HashSet<>(phenomenonList);
		phenomenonList = new ArrayList<>(phenomenonSet);
//		for  ( int  i  =   0 ; i  <  phenomenonList.size()  -   1 ; i ++ )  {
//			for  ( int  j  =  phenomenonList.size()  -   1 ; j  >  i; j -- )  {
//				if  (phenomenonList.get(j).getPhenomenon_no() == phenomenonList.get(i).getPhenomenon_no())  {
//					phenomenonList.remove(j);
//				}
//			}
//		}
//		for(int i = 0; i < phenomenonList.size(); i++) {
//			for (int j = 0; j < phenomenonList.size() -  i - 1; j++) {
//				Phenomenon phe1 = phenomenonList.get(j);
//				Phenomenon phe2 = phenomenonList.get(j+1);
//				if(phe1.getPhenomenon_no() > phe2.getPhenomenon_no()) {
//					Phenomenon phe;
//					phe = phe1;
//					phenomenonList.set(j, phe2);
//					phenomenonList.set(j+1, phe);
//				}
//			}
//		}
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
			for (int j = 0; j < phenomenonList.size() -  i - 1; j++) {
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
		errorList.add(check_Interface(CD));
		return errorList;
	}


	//问题图正确性检测
	public List<Error> checkCorrectProblem(Project project){
		List<Error> errorList = new ArrayList<Error>();
		ContextDiagram CD=project.getContextDiagram();
		errorList.add(check_Machine(CD));
		errorList.add(check_ProblemDomain(CD));
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
		//System.out.println(errorList);
		return errorList;
	}

	//良构检测
	public List<Error> checkWellFormed(Project project) {
		List<Error> errorList = new ArrayList<Error>();
		List<ScenarioGraph> SGList = project.getScenarioGraphList();

		for (int i = 0; i < SGList.size() ; i++) {    //依次对每个情景图进行检查
			ScenarioGraph SG = SGList.get(i);
			Error error = new Error();
			List<String> errMsgList = new ArrayList<String>();
			error.setTitle(SG.getTitle());
			errMsgList.addAll(checkSemantic(SG));    //语义检查,是否有交互关系
			errMsgList.addAll(checkState(SG,project));    //语义检查，是否既为静态又为动态
			error.setErrorList(errMsgList);
			errorList.add(error);
		}
		return errorList;
	}

	public Error checkIntegrity(Project project) {
		System.out.println("========== checkIntegrity START ==========");
		Error error = new Error();
		List<String> errorList = new ArrayList<>();
		error.setTitle(null);
		error.setType("完整性错误");

		List<String> expErrors = this.checkExpPheIntegrity(project);
		System.out.println("Expected Phenomenon Errors: " + (expErrors != null ? expErrors.size() : 0));
		if(expErrors != null) errorList.addAll(expErrors);

		List<String> behErrors = this.checkBehPheIntegrity(project);
		System.out.println("Behavior Phenomenon Errors: " + (behErrors != null ? behErrors.size() : 0));
		if(behErrors != null) errorList.addAll(behErrors);

		System.out.println("Total Errors: " + errorList.size());
		for(String err : errorList) {
			System.out.println("  - " + err);
		}
		System.out.println("========== checkIntegrity END ==========");
		error.setErrorList(errorList);
		return error;

//		List<IntInfo> intList = getInt(project);//获得情景图中所有int
//		List<Phenomenon> pheList = getPhenomenon(project);//获得问题图中所有现象
//
//		IntInfo intInfo;
//		List<IntInfo> intInfoList = new ArrayList<IntInfo>();    // 问题图中涉及到的所有int
//		//System.out.println("问题图中涉及到的所有int");
//		for (int i = 0; i < pheList.size(); i ++) {
//			Phenomenon phe = pheList.get(i);
//			intInfo = new IntInfo();
//			intInfo.setNo(phe.getPhenomenon_no());;
//			intInfo.setInit(phe.getPhenomenon_from());
//			intInfo.setRece(phe.getPhenomenon_to());
//			intInfo.setType(phe.getPhenomenon_type());
//			intInfo.setState(0);
//			int index = ifexist(intInfo,intList);
//			if(index != -1 && index < intList.size()){
//				intInfo.setState(1);//被描述了
//				intList.get(index).setState(2);//存在问题图中
//			}
//			intInfoList.add(intInfo);
//		}
//
//		for(int i = 0; i < intInfoList.size(); i ++ ) {
//			intInfo = intInfoList.get(i);
//			if(intInfo.getState() == 0) {
//				String errMsg = "完整性错误:int" + intInfo.getNo() + " 没有被描述！";
//				errorList.add(errMsg);
//			}
//		}
//		for(int i = 0; i < intList.size(); i ++){
//			if(intList.get(i).getState() == 1) {
//				String errMsg = "完整性错误:int" + intList.get(i).getNo() + " 不存在问题图中！";
//				errorList.add(errMsg);
//			}
//		}
//
//		error.setErrorList(errorList);
//		return error;
	}

	/**
	 * 检测期望交互完整性。检测问题图每个需求上的期望现象是否都在该需求对应的情景图中出现了
	 */
	private List<String> checkExpPheIntegrity(Project project) {
		List<String> errorList = new ArrayList<>();
		List<ScenarioGraph> scenarioGraphList = project.getScenarioGraphList();
		ProblemDiagram problemDiagram = project.getProblemDiagram();
		if(scenarioGraphList == null || problemDiagram == null) {
			System.out.println("checkExpPheIntegrity方法中问题图或情景图为空！！！");
			return errorList; // 返回空列表，不是 null
		}

		// 提取出每个需求的期望现象集合，存储在reqPheSet中。reqPheSet的key为需求全称，value为该需求引用/约束的期望现象
		Map<String, Set<Phenomenon>> reqPheSet = new HashMap<>();
		// 获取需求全称集合做初始化。注意：问题图中的引用/约束画的不规范，不能保证from指向的一定是需求，to指向的一定是领域
		for(Requirement requirement: problemDiagram.getRequirementList()) {
			reqPheSet.put(requirement.getRequirement_context(), new HashSet<>());
		}
		System.out.println("[DEBUG checkExpPheIntegrity] Requirements in problem diagram: " + reqPheSet.keySet());

		List<Constraint> constraintList = problemDiagram.getConstraintList();
		List<Reference> referenceList = problemDiagram.getReferenceList();
		if(constraintList != null) {
			System.out.println("[DEBUG checkExpPheIntegrity] Processing " + constraintList.size() + " constraints");
			for(Constraint constraint: constraintList) {
				System.out.println("  Constraint: " + constraint.getConstraint_from() + " -> " + constraint.getConstraint_to() +
					", phenomena: " + (constraint.getPhenomenonList() != null ? constraint.getPhenomenonList().size() : 0));
				if(constraint.getPhenomenonList() != null) {
					for(Phenomenon p : constraint.getPhenomenonList()) {
						System.out.println("    - Phenomenon: int" + p.getPhenomenon_no() + " (" + p.getPhenomenon_name() + ")");
					}
				}
				if(reqPheSet.containsKey(constraint.getConstraint_from())) {
					reqPheSet.get(constraint.getConstraint_from()).addAll(constraint.getPhenomenonList());
				}
				else if(reqPheSet.containsKey(constraint.getConstraint_to())) {
					reqPheSet.get(constraint.getConstraint_to()).addAll(constraint.getPhenomenonList());
				}
			}
		}
		if(referenceList != null) {
			System.out.println("[DEBUG checkExpPheIntegrity] Processing " + referenceList.size() + " references");
			for(Reference reference: referenceList) {
				System.out.println("  Reference: " + reference.getReference_from() + " -> " + reference.getReference_to() +
					", phenomena: " + (reference.getPhenomenonList() != null ? reference.getPhenomenonList().size() : 0));
				if(reference.getPhenomenonList() != null) {
					for(Phenomenon p : reference.getPhenomenonList()) {
						System.out.println("    - Phenomenon: int" + p.getPhenomenon_no() + " (" + p.getPhenomenon_name() + ")");
					}
				}
				if(reqPheSet.containsKey(reference.getReference_from())) {
					reqPheSet.get(reference.getReference_from()).addAll(reference.getPhenomenonList());
				}
				else if(reqPheSet.containsKey(reference.getReference_to())) {
					reqPheSet.get(reference.getReference_to()).addAll(reference.getPhenomenonList());
				}
			}
		}
		System.out.println("[DEBUG checkExpPheIntegrity] Expected phenomena by requirement:");

		// 提取每幅情景图中期望交互编号，保存到reqIntNumSet中。reqIntNumSet的key为需求全称，value为该需求对应情景图中期望现象编号集合
		// FIX: 应该收集 pre_condition.phenomenon_no，而不是 node_no
		Map<String, Set<Integer>> reqIntNumSet = new HashMap<>();
		System.out.println("[DEBUG checkExpPheIntegrity] Extracting ExpInt from scenario graphs:");
		for(ScenarioGraph scenarioGraph: scenarioGraphList) {
			Set<Integer> integerSet = new HashSet<>();
			List<Node> intNodeList = scenarioGraph.getIntNodeList();
			System.out.println("  SG: " + scenarioGraph.getTitle() + ", requirement: " + scenarioGraph.getRequirement());
			if(intNodeList != null) {
				System.out.println("    Total nodes in intNodeList: " + intNodeList.size());
				for(Node node: intNodeList) {
					System.out.println("      Node: no=" + node.getNode_no() + ", type=" + node.getNode_type());
					if(node.getNode_type().equals("ExpInt")) {
						// FIX: Add phenomenon_no from pre_condition, not node_no
						if(node.getPre_condition() != null) {
							int phenomenonNo = node.getPre_condition().getPhenomenon_no();
							integerSet.add(phenomenonNo);
							System.out.println("        -> Added ExpInt: phenomenon_no=" + phenomenonNo +
								" (node_no=" + node.getNode_no() +
								", name=" + node.getPre_condition().getPhenomenon_name() + ")");
						} else {
							System.out.println("        -> ExpInt has NO pre_condition!");
						}
					}
				}
			} else {
				System.out.println("    intNodeList is NULL!");
			}
			System.out.println("    Final phenomenon count for this SG: " + integerSet.size());
			if(scenarioGraph.getRequirement() != null) {
				reqIntNumSet.put(scenarioGraph.getRequirement(), integerSet);
			}
		}

		// 枚举reqPheSet每个需求的期望现象，判断该现象是否在对应的情景图中出现了
		for(Map.Entry<String, Set<Phenomenon>> entry: reqPheSet.entrySet()) {
			Set<Integer> intNumSet = reqIntNumSet.get(entry.getKey());
			if(intNumSet == null) {
				intNumSet = new HashSet<>(); // 如果该需求没有对应的情景图，用空集合
			}
			for(Phenomenon phenomenon: entry.getValue()) {
				if(!intNumSet.contains(phenomenon.getPhenomenon_no())) {
					String errMsg = "完整性错误:需求" + entry.getKey() + "的现象int" + phenomenon.getPhenomenon_no() + "没有在情景图中被描述！";
					errorList.add(errMsg);
				}
			}
		}
		return errorList;
	}

	/**
	 * 检测行为交互完整性。检测问题图每个行为交互现象是否都在情景图中出现了
	 */
	private List<String> checkBehPheIntegrity(Project project) {
		List<String> errorList = new ArrayList<>();
		List<ScenarioGraph> scenarioGraphList = project.getScenarioGraphList();
		ContextDiagram contextDiagram = project.getContextDiagram();
		if(scenarioGraphList == null || contextDiagram == null) {
			System.out.println("checkBehPheIntegrity方法中上下文图或情景图为空！！！");
			return errorList; // 返回空列表，不是 null
		}

		// 提取出情景图中所有行为交互与远程行为交互（ConnInt，情景图中绿色的交互节点）的编号，保存在behIntSet中
		Set<Integer> behIntSet = new HashSet<>();
		for(ScenarioGraph scenarioGraph: scenarioGraphList) {
			List<Node> behNodeList = scenarioGraph.getIntNodeList();
			if(behNodeList == null) continue;
			for(Node node: behNodeList) {
				behIntSet.add(node.getNode_no());
			}
		}

		// 枚举问题图中的每个行为交互现象，根据编号判断是否出现在behIntSet中
		for(Interface inter: contextDiagram.getInterfaceList()) {
			List<Phenomenon> phenomenonList = inter.getPhenomenonList();
			if(phenomenonList == null) continue;
			for(Phenomenon phenomenon: phenomenonList) {
				if(!behIntSet.contains(phenomenon.getPhenomenon_no())) {
					String errMsg = "完整性错误:问题图行为交互现象int" + phenomenon.getPhenomenon_no() + "没有在情景图中被描述！";
					errorList.add(errMsg);
				}
			}
		}
		return errorList;
	}

	private int ifexist(IntInfo intInfo,List<IntInfo> intList){
		for(int j = 0; j < intList.size(); j ++ ) {
			if(intInfo.getNo() == intList.get(j).getNo()) {
				return j;
			}
		}
		return -1;
	}

	private Error checkSynTax(ScenarioGraph SG) {
		Error error = new Error();
		List<String> errMsgList = new ArrayList<String>();
		error.setTitle(SG.getTitle());
		error.setType("语法错误");

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
			if (line.getLine_type().equals("BehOrder")) {    //行为序关系
				if (!fromNode.getNode_type().equals("ExpInt") && !toNode.getNode_type().equals("ExpInt")) {
					continue;
				}else {
					String errMsg = "语法错误:Int" + fromNode.getNode_no() +
							"和Int" + toNode.getNode_no() + "之间的关系错误!";
					errMsgList.add(errMsg);
				}
			}
			if (line.getLine_type().equals("BehEnable")) {    //行为使能
				if ((fromNode.getNode_type().equals("BehInt") || fromNode.getNode_type().equals("ConnInt")) && toNode.getNode_type().equals("ExpInt")
						&& fromNode.getNode_no() != toNode.getNode_no()) {
					continue;
				}else {
					String errMsg = "语法错误:Int" + fromNode.getNode_no() +
							"和Int" + toNode.getNode_no() + "之间的关系错误!";
					errMsgList.add(errMsg);
				}
			}
			if (line.getLine_type().equals("Synchrony")) {    //同步关系
				if ((fromNode.getNode_type().equals("ExpInt") && toNode.getNode_type().equals("BehInt"))
						|| (fromNode.getNode_type().equals("BehInt") && toNode.getNode_type().equals("ExpInt"))
						|| (fromNode.getNode_type().equals("ConnInt") && toNode.getNode_type().equals("ExpInt"))) {
					continue;
				}else {
					String errMsg = "语法错误:Int" + fromNode.getNode_no() +
							"和Int" + toNode.getNode_no() + "之间的关系错误!";
					errMsgList.add(errMsg);
				}
			}
			if (line.getLine_type().equals("ExpOrder")) {    //期望序关系
				if (!fromNode.getNode_type().equals("BehInt") && !toNode.getNode_type().equals("BehInt")) {
					continue;
				}else {
					String errMsg = "语法错误:Int" + fromNode.getNode_no() +
							"和Int" + toNode.getNode_no() + "之间的关系错误!";
					errMsgList.add(errMsg);
				}
			}
			if (line.getLine_type().equals("ExpEnable")) {    //期望使能
				if (fromNode.getNode_type().equals("ExpInt") && (toNode.getNode_type().equals("BehInt") || toNode.getNode_type().equals("ConnInt"))
						&& fromNode.getNode_no() != toNode.getNode_no() ) {
					continue;
				}else {
					String errMsg = "语法错误:Int" + fromNode.getNode_no() +
							"和Int" + toNode.getNode_no() + "之间的关系错误!";
					errMsgList.add(errMsg);
				}
			}
		}
		error.setErrorList(errMsgList);
		return error;
	}

	private List<IntInfo> getInt(Project project) {    //获取int
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

	public List<String> checkSemantic(ScenarioGraph SG) {    //语义检测
		List<String> errMsgList = new ArrayList<String>();

		List<Node> intNodeList = SG.getIntNodeList();
		List<Line> lineList = SG.getLineList();
		for (int i = 0; i < intNodeList.size() ; i++) {    //检测每一个int
			Node intNode = intNodeList.get(i);
			if(intNode.getNode_type().equals("ExpInt")) {    //遍历期望交互
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

	public List<String> checkState(ScenarioGraph SG, Project project) {    //静态、动态领域检测
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
			for (int j = 0; j < intNodeList.size() ; j++) {    //获取情景图中涉及到的int信息
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
					} else {
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
			for (int j = 0; j < pheList.size() -  i - 1; j++) {
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

	public Project getScenarioGraphs(Project project, String projectName,String reqVersion) {

		if(project.getScenarioGraphList() == null) {
			List<ScenarioGraph> scenarioGraphList = new ArrayList<ScenarioGraph>();
			scenarioGraphList.addAll(getNewSGList(project,projectName, reqVersion));
			project.setScenarioGraphList(scenarioGraphList);
		}else {
			project.getScenarioGraphList().clear();
			project.getScenarioGraphList().addAll(getNewSGList(project,projectName, reqVersion));
		}

		return project;
	}

	private String findPhenomenonReq(RequirementPhenomenon phenomenon, Project project) {
		// 找到现象phenomenon所属的需求，返回需求名称
		Map<String, ProblemDomain> domainMap = this.createDomainsMap(project.getContextDiagram().getProblemDomainList());
		for(Constraint constraint: project.getProblemDiagram().getConstraintList()) {
			for(RequirementPhenomenon phe: constraint.getPhenomenonList()) {
				if(phenomenon.equals(phe)) {
					if(domainMap.containsKey(constraint.getConstraint_from())) return constraint.getConstraint_to();
					else return constraint.getConstraint_from();
				}
			}
		}
		for(Reference reference: project.getProblemDiagram().getReferenceList()) {
			for(RequirementPhenomenon phe: reference.getPhenomenonList()) {
				if(phenomenon.equals(phe)) {
					if(domainMap.containsKey(reference.getReference_from())) return reference.getReference_to();
					else return reference.getReference_from();
				}
			}
		}

		return "";
	}

	public List<ScenarioGraph> getNewSGList(Project project, String projectName, String reqVersion) {
		FileService fileService = new FileService();
		List<ScenarioGraph> scenarioGraphs = new ArrayList<ScenarioGraph>();
		List<ScenarioGraph> scenarioGraphList = new ArrayList<>();
		Machine machine = project.getContextDiagram().getMachine();
		List<Requirement> requirementList = project.getProblemDiagram().getRequirementList();
//		EnvEntity envEntity = fileService.getEnvEntity(projectName);
//		List<MyOntClass> problemDomainClasses = new ArrayList<MyOntClass>();
//		if(envEntity != null) {
//			problemDomainClasses = envEntity.getProblemDomains();
//		}

		String address = AddressService.userAddress+"test/" + projectName + "/" ;
		if(reqVersion == null || reqVersion.equals("") || reqVersion.equals("undefined") || reqVersion.equals("null")){
			address += reqModelPrefix;
		} else if(reqVersion.startsWith("Req")){
			address += reqVersion + "/" + reqModelPrefix;
		}
		System.out.println(address);
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
			System.out.println(SGFiles.get(i));
			ScenarioGraph scenarioGraph = fileService.getScenarioGraph(address + "/" + SGFiles.get(i),address+"/");
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
			if(scenarioGraphList != null) {
				for(ScenarioGraph scenarioGraph: scenarioGraphList) {
					if(scenarioGraph.getRequirement() != null && scenarioGraph.getRequirement().equals(req)) {
						// 存在情景图满足需求req，对于这种情况，直接跳过，不做任何处理。因为若缺失期望交互，在检查情景图合法性时会提示出来，让用户手动添加
//						List<ProblemDomain> problemDomainList = project.getContextDiagram().getProblemDomainList();
//						List<Interface> interfaceList = project.getContextDiagram().getInterfaceList();
//						List<RequirementPhenomenon> requirementPhenomenonList = getRequirementPhenomenon(project);
//						List<Phenomenon> phenomenonList = new ArrayList<>();
//						List<RequirementPhenomenon> reqphenomenons = new ArrayList<>();
//						List<Phenomenon> phenomenons = new ArrayList<>();
//						for(int i = 0; i < interfaceList.size(); i ++ ) {
//							Interface inte = interfaceList.get(i);
//							phenomenonList.addAll(inte.getPhenomenonList());
//						}
//						List<Node> intNodeList = scenarioGraph.getIntNodeList();
//						List<Line> lineList = scenarioGraph.getLineList();
//						List<ProblemDomain> problemDomains = new ArrayList<>();
//
//						Node intNode;
//						int expNum = 0;
//						int intNum = 0;
//						if(intNodeList != null) {
//							for(Node node: intNodeList) {
//								if(node.getNode_type().equals(BehInt)) {
//									intNum += 1;
//								} else if(node.getNode_type().equals(ExpInt)) {
//									expNum += 1;
//								}
//							}
//						}
//						if(requirementPhenomenonList != null) {
//							for(RequirementPhenomenon reqPhe : requirementPhenomenonList) {
//								if(reqPhe.getPhenomenon_requirement() == requirement.getRequirement_no()) {
//									Boolean needAdd = true;
//									for(Node node: intNodeList) {
//										if(reqPhe.getPhenomenon_no() == node.getNode_no() && node.getNode_type().equals(ExpInt)) {
//											needAdd = false;
//										}
//									}
//									if(needAdd) {
//										expNum = expNum + 1;
//										intNode = new Node();
//										intNode.setNode_no(reqPhe.getPhenomenon_no());
//										intNode.setNode_type("ExpInt");
//										intNode.setNode_x(350);
//										intNode.setNode_y(expNum*80);
//										intNodeList.add(intNode);
//										reqphenomenons.add(reqPhe);
//										for(ProblemDomain problemDomain: problemDomainList) {
//											String pdName = problemDomain.getProblemdomain_shortname();
//											if(reqPhe.getPhenomenon_from().equals(pdName) ||
//													reqPhe.getPhenomenon_to().equals(pdName)) {
//												if(!problemDomains.contains(problemDomain)) {
//													problemDomains.add(problemDomain);
//													getProblemDomains(problemDomain,problemDomains,project);
//												}
//												break;
//											}
//										}
//									}
//								}
//							}
//						}

//						if(phenomenonList != null) {
//							for(Phenomenon phenomenon: phenomenonList) {
//								if(problemDomains == null) continue;
//								for(ProblemDomain problemDomain: problemDomains) {
//									if(problemDomain == null) continue;
//									String pdName = problemDomain.getProblemdomain_shortname();
//									if(phenomenon.getPhenomenon_from().equals(pdName) || phenomenon.getPhenomenon_to().equals(pdName)) {
//										Boolean needAdd = true;
//										if(intNodeList != null) {
//											for(Node node: intNodeList) {
//												if(phenomenon.getPhenomenon_no() == node.getNode_no() && node.getNode_type().equals(BehInt)) {
//													needAdd = false;
//												}
//											}
//										}
//										if(needAdd) {
//											intNum = intNum + 1;
//											intNode = new Node();
//											intNode.setNode_no(phenomenon.getPhenomenon_no());
//											intNode.setNode_x(100);
//											intNode.setNode_y(intNum*80);
//
//											if(!(phenomenon.getPhenomenon_from().equals(machine.getMachine_shortName())
//													|| phenomenon.getPhenomenon_to().equals(machine.getMachine_shortName()))) {
//												intNode.setNode_type("ConnInt");
//											}else {
//												intNode.setNode_type("BehInt");
//											}
//											intNodeList.add(intNode);
//											phenomenons.add(phenomenon);
//											break;
//										}
//									}
//								}
//							}
//						}
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
//				for(ProblemDomain problemDomain: problemDomains) {
//					if(isDevice(problemDomain) || problemDomain.getProblemdomain_type().equals(Clock)) {
//						String domianName = problemDomain.getProblemdomain_name();
//						System.out.println("*" + domianName);
//						for(MyOntClass problemDomainClass : problemDomainClasses) {
//							String className = problemDomainClass.getName();
//							if(className.equals(domianName) ||
//									Arrays.asList(domianName.split("&")).contains(className) ||
//									className.replace("_", " ").equals(domianName)) {
//								List<Transition> transitions = getTransition(problemDomainClass, envEntity);
//								if(transitions != null) {
//									SG = addTranstion(reqphenomenons, phenomenons, SG, transitions);
//								}
//								break;
//							}
//						}
//					}
//				}
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
//		String stateMachineName = problemDomain.getTA_name();
//		if(stateMachineName != null)
//		{
//			List<Individual> TimedAutomatons = problemDomain.getTimedAutomaton();
//			for(Individual TimedAutomaton: TimedAutomatons) {
//				String npuri = TimedAutomaton.getURI();
//				npuri = npuri.substring(0, npuri.indexOf("#")+1);
////				List<String> transitionNames = envEntity.getRestrictionValue(TimedAutomaton, "has_trans");
//				List<String> transitionNames = envEntity.getIndividualProperty(TimedAutomaton, "has_trans", npuri);
//				for(String transitionName: transitionNames) {
//					Transition transition = new Transition();
////					OntClass transitionClass = envEntity.getOntClass(envEntity.getFilepath(), transitionName);
////					envEntity.getRestrictionValue(transitionClass, "has_from");
////					String from = envEntity.getRestrictionValue(transitionClass, "has_state_from").get(0);
////					String to = envEntity.getRestrictionValue(transitionClass, "has_state_to").get(0);
////					List<String> triggers = envEntity.getRestrictionValue(transitionClass, "has_sync");
//
//					Individual transitionInd = envEntity.getIndvidual(envEntity.getFilepath(), npuri+transitionName);
//					List<String> ans = envEntity.getIndividualProperty(transitionInd, "has_state_from", npuri);
//					String from = null, to = null;
//					if(ans != null && !ans.isEmpty()) {
//						from = ans.get(0);
//					}
//					ans = envEntity.getIndividualProperty(transitionInd, "has_state_to", npuri);
//					if(ans != null && !ans.isEmpty()) {
//						to = ans.get(0);
//					}
//					List<String> triggers = envEntity.getIndividualProperty(transitionInd, "has_sync", npuri);
//					for(int i=0;i<triggers.size();i++) {
//						triggers.set(i, triggers.get(i).replace("?", ""));
//						triggers.set(i, triggers.get(i).replace("!", ""));
//					}
//
//					transition.setName(transitionName);
//					transition.setFrom(from);
//					transition.setTo(to);
//					transition.setSync(triggers);
//					transitions.add(transition);
//				}
//			}
//		}
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

	private Node getSameNode(Node node, List<Node> intNodeList, List<CtrlNode> ctrlNodeList) {
		// 从intNodeList和ctrlNodeList列表中找到和node节点id、类型相同的节点返回
		if(node == null) {
			return null;
		}
		for(Node intNode: intNodeList) {
			if(node.getNode_no() == intNode.getNode_no() && node.getNode_type().equals(intNode.getNode_type())) {
				return intNode;
			}
		}
		for(CtrlNode ctrlNode: ctrlNodeList) {
			if(node.getNode_no() == ctrlNode.getNode_no() && node.getNode_type().equals(ctrlNode.getNode_type())) {
				return ctrlNode;
			}
		}
		return null;
	}

	private Node searchForward(Node start, String tarType, HashSet<Node> visited, List<Node> intNodeList, List<CtrlNode> ctrlNodeList) {
		// 以start为起点，按照序关系方向搜索距离start最近的tarType类型的且不在visited集合中的节点（不包括start）。若没有符合要求的节点，返回null
		HashSet<Node> hashSet = new HashSet<Node>();  // 标记搜索过的节点，防止出现环路时死循环
		start = getSameNode(start, intNodeList, ctrlNodeList);
		hashSet.add(start);
		Queue<Node> queue = new LinkedList<Node>();
		if(start.getNode_toList() != null) {
			for(Node node: start.getNode_toList()) {
				Node transNode = getSameNode(node, intNodeList, ctrlNodeList);
				queue.add(transNode);
				hashSet.add(transNode);
			}
		}
		else {
			return null;
		}
		while (queue.size() > 0) {
			Node elem = queue.remove();
			elem = getSameNode(elem, intNodeList, ctrlNodeList);
			if(elem.getNode_type().equals(tarType) && !visited.contains(elem)) {
				return elem;
			}
			List<Node> toList = elem.getNode_toList();
			if(toList == null) {
				continue;
			}
			for(int i=0;i<toList.size();i++) {
				if(!hashSet.contains(toList.get(i)) && !visited.contains(toList.get(i))) {
					hashSet.add(toList.get(i));
					queue.add(toList.get(i));
				}
			}
		}
		return null;
	}

	private Pair<List<List<Node>>, Node> searchPalEnd(Node start, String target, List<Node> intNodeList, List<CtrlNode> ctrlNodeList) {
		// start为一段并行结构的开始，搜索得到该段并行结构的结束节点，返回并行结构中的每条分支路径和结束节点
		// 使用广度优先搜索
		List<List<Node>> branchPath = new ArrayList<List<Node>>();  // 并行结构的每条分支（每条分支都包括开始和结束的两个Branch节点）
		Node end = null;  // 并行结构的结束节点
		Queue<Pair<Node, Integer>> queue = new LinkedList<Pair<Node, Integer>>();
		start = getSameNode(start, intNodeList, ctrlNodeList);
		List<Node> toList = start.getNode_toList();
		HashSet<Node> hashSet = new HashSet<Node>();  // 标记搜索过的节点，防止出现环路时死循环
		hashSet.add(start);
		for(int i=0;i<toList.size();i++) {
			List<Node> path = new ArrayList<Node>();
			path.add(start);
			Pair<Node, Integer> elem = new ImmutablePair<>(toList.get(i), i);
			queue.add(elem);
			hashSet.add(toList.get(i));
			branchPath.add(path);
		}
		while (queue.size() > 0) {
			Pair<Node, Integer> elem = queue.remove();
			elem = new ImmutablePair<Node, Integer>(getSameNode(elem.getLeft(), intNodeList, ctrlNodeList), elem.getRight());
			branchPath.get(elem.getRight()).add(elem.getLeft());
			List<Node> toNodes = elem.getLeft().getNode_toList();
			if(toNodes == null) {
				continue;
			}
			for(int i=0;i<toNodes.size();i++) {
				if(!toNodes.get(i).getNode_type().equals(target) && !hashSet.contains(toNodes.get(i))) {
					// toNodes[i]不是并行结构的结束节点且没有被访问过（防止有循环），将toNodes[i]添加到队列中
					queue.add(new ImmutablePair<>(toNodes.get(i), elem.getRight()));
					hashSet.add(toNodes.get(i));
				}
				else if(toNodes.get(i).getNode_type().equals(target)) {
					end = toNodes.get(i);
					branchPath.get(elem.getRight()).add(end);
				}
			}
		}

		return new ImmutablePair<>(branchPath, end);
	}

	private String ideLoc(Node node, List<Node> intNodeList, List<CtrlNode> ctrlNodeList) {
		// 识别节点node属于行为交互序列还是期望交互序列，若属于行为交互序列，则返回"Beh"；若属于期望交互序列，则返回"Exp"
		// 若node为交互节点，则可直接根据节点类型判断属于哪类序列；若node为控制节点，则需要以node为中心进行扩展搜索，得到与其相关的交互节点，根据交互类型，判断node所属序列类型
		// 使用广度优先搜索以node为中心扩展搜索。这里如果用深度优先搜索的话，需要引入全局变量或引用传递参数来标记访问过的节点（防止环路造成无限递归），耦合程度比较高

		node = getSameNode(node, intNodeList, ctrlNodeList);
		HashSet<Node> hashSet = new HashSet<Node>();  // 标记搜索过的节点，防止出现环路时死循环
		hashSet.add(node);
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(node);
		while(queue.size() > 0) {
			Node elem = queue.remove();
			elem = getSameNode(elem, intNodeList, ctrlNodeList);
			if(elem.getNode_type().equals(BehInt)) {
				return "Beh";
			}
			else if(elem.getNode_type().equals(ExpInt)) {
				return "Exp";
			}
			else {
				List<Node> fromList = elem.getNode_fromList(), toList = elem.getNode_toList();
				if(fromList != null) {
					for(int i=0;i<fromList.size();i++) {
						if(!hashSet.contains(fromList.get(i))) {
							hashSet.add(fromList.get(i));
							queue.add(fromList.get(i));
						}
					}
				}
				if(toList != null) {
					for(int i=0;i<toList.size();i++) {
						if(!hashSet.contains(toList.get(i))) {
							hashSet.add(toList.get(i));
							queue.add(toList.get(i));
						}
					}
				}
			}
		}

		return "";
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

	private Boolean isDataStorage(ProblemDomain domain) {
		return domain != null && domain.getProblemdomain_type().equals(DataStorage);
	}

	private HashMap<String, ProblemDomain> createDomainsMap(List<ProblemDomain> problemDomainList) {
		// 建立领域简称映射表，存储在domainsMap中。Key为领域简称（String），Value为一个ProblemDomain对象
		HashMap<String, ProblemDomain> domainsMap = new HashMap<String, ProblemDomain>();
		if(problemDomainList == null) {
			return domainsMap;
		}
		for(int i=0;i<problemDomainList.size();i++) {
			domainsMap.put(problemDomainList.get(i).getProblemdomain_shortname(), problemDomainList.get(i));
		}

		return domainsMap;
	}

	private HashMap<Integer, Phenomenon> createPheMap(ProblemDiagram problemDiagram) {

		// 将问题图中的每条现象提出出来，并存储到phenomenonMap中。其中phenomenonMap的Key表示phenomenon_no，Value为一个Phenomenon对象
		HashMap<Integer, Phenomenon> phenomenonHashMap = new HashMap<Integer, Phenomenon>();

		List<Interface> interfaceList = problemDiagram.getContextDiagram().getInterfaceList();

		if(interfaceList != null) {
			for(int i=0;i<interfaceList.size();i++) {
				Interface inter = interfaceList.get(i);
				List<Phenomenon> phenomenonList = inter.getPhenomenonList();
				for(int j=0;j<phenomenonList.size();j++) {
					Phenomenon phenomenon = phenomenonList.get(j);
					phenomenonHashMap.put(phenomenon.getPhenomenon_no(), phenomenon);
				}
			}
		}

		List<Constraint> constraintList = problemDiagram.getConstraintList();
		if(constraintList != null) {
			for(Constraint constraint: constraintList) {
				List<RequirementPhenomenon> phenomenonList = constraint.getPhenomenonList();
				for(RequirementPhenomenon phenomenon: phenomenonList) {
					phenomenonHashMap.put(phenomenon.getPhenomenon_no(), phenomenon);
				}
			}
		}

		List<Reference> referenceList = problemDiagram.getReferenceList();
		if(referenceList != null) {
			for(Reference reference: referenceList) {
				List<RequirementPhenomenon> phenomenonList = reference.getPhenomenonList();
				for(RequirementPhenomenon phenomenon: phenomenonList) {
					phenomenonHashMap.put(phenomenon.getPhenomenon_no(), phenomenon);
				}
			}
		}

		return phenomenonHashMap;
	}

	private Project createFromTo(Project project) {
		// 为情景图中的节点建立node_fromList和node_toList，即为每个节点建立由序关系表达的前向节点和后向节点

		List<ScenarioGraph> scenarioGraphList = project.getScenarioGraphList();

		// 错误原因：scenarioGraph中Line的fromNode、toNode与intNodeList、ctrlNodeList中的节点不是同一个对象
		for(ScenarioGraph scenarioGraph: scenarioGraphList) {
			List<Line> lineList = scenarioGraph.getLineList();
			List<Node> intNodeList = scenarioGraph.getIntNodeList();
			List<CtrlNode> ctrlNodeList = scenarioGraph.getCtrlNodeList();
			for(Line line: lineList) {
				if(line.getLine_type().equals(BehOrder) || line.getLine_type().equals(ExpOrder)) {
					Node from = this.getSameNode(line.getFromNode(), intNodeList, ctrlNodeList), to = this.getSameNode(line.getToNode(), intNodeList, ctrlNodeList);
					if(from != null && to != null) {
						if(from.getNode_toList() == null) {
							List<Node> tmp = new ArrayList<>();
							tmp.add(to);
							from.setNode_toList(tmp);
						}
						else {
							Boolean exist = false;
							for(Node node: from.getNode_toList()) {
								if(this.isSameNode(node, to)) {
									exist = true;
								}
							}
							if(!exist) {
								from.getNode_toList().add(to);
							}
						}
						if(to.getNode_fromList() == null) {
							List<Node> tmp = new ArrayList<>();
							tmp.add(from);
							to.setNode_fromList(tmp);
						}
						else {
							Boolean exist = false;
							for(Node node: to.getNode_fromList()) {
								if(this.isSameNode(node, from)) {
									exist = true;
								}
							}
							if(!exist) {
								to.getNode_fromList().add(from);
							}
						}
					}
				}
			}
		}

		return project;
	}

	private Boolean isSameNode(Node a, Node b) {
		return (a == null && b == null) || (a.getNode_no() == b.getNode_no() && a.getNode_type().equals(b.getNode_type()));
	}

	private Boolean isValid(StrategyAdvice ans) {
		// 检查策略建议ans是否已经在inValidSet集合中标记过了，如果已经标记过，则说明该策略建议已经被处理过了，不应再作为新建议返回给用户
		String key = ans.getTitle();
		if(ans.getContent() == null) {
			return true;
		}
		for(String c: ans.getContent()) {
			key += c;
		}
		return !this.inValidSet.contains(key);
	}

	public List<StrategyAdvice> ckeckStrategy(Project project) {

		// 先为每个行为交互节点和期望交互节点建立前向和后向关系
		project = this.createFromTo(project);

		List<StrategyAdvice> advice = new ArrayList<StrategyAdvice>();

		// 检查策略1
		StrategyAdvice ans = ckeckStrategyOne(project);
		if(!ans.getContent().isEmpty() && !ans.getContent().get(0).equals("不满足应用条件") && isValid(ans)) {
			// 可以应用策略
			advice.add(ans);
		}

		System.out.println("策略1:" + ans.getContent());

		// 检查策略2
		ans = ckeckStrategyTwo(project);
		if(!ans.getContent().isEmpty() && !ans.getContent().get(0).equals("不满足应用条件") && isValid(ans)) {
			// 可以应用策略
			if(isValid(ans)) {
				advice.add(ans);
			}
		}

		System.out.println("策略2:" + ans.getContent());

		// 检查策略3
		ans = ckeckStrategyThree(project);
		if(!ans.getContent().isEmpty() && !ans.getContent().get(0).equals("不满足应用条件") && isValid(ans)) {
			// 可以应用策略
			if(isValid(ans)) {
				advice.add(ans);
			}
		}

		System.out.println("策略3:" + ans.getContent());

		// 检查策略4
		ans = ckeckStrategyFour(project);
		if(!ans.getContent().isEmpty() && !ans.getContent().get(0).equals("不满足应用条件") && isValid(ans)) {
			// 可以应用策略
			if(isValid(ans)) {
				advice.add(ans);
			}
		}

		System.out.println("策略4:" + ans.getContent());

		// 检查策略5
		ans = ckeckStrategyFive(project);
		if(!ans.getContent().isEmpty() && !ans.getContent().get(0).equals("不满足应用条件") && isValid(ans)) {
			// 可以应用策略
			if(isValid(ans)) {
				advice.add(ans);
			}
		}

		System.out.println("策略5:" + ans.getContent());

		return advice;
	}

	private StrategyAdvice ckeckStrategyOne(Project project) {
		// 检查项目project能否应用策略1：独立设备操作分离模式。若能应用，返回能应用的理由；若不能应用，返回不能应用的提示信息
		StrategyAdvice ans = new StrategyAdvice("策略1：独立设备操作分离模式");

		// 策略1：独立设备操作分离模式（如果一个系统需求涉及对不同设备的操作，并且这些操作不存在约束关系，可以将该需求按设备进行拆分，形成一组独立的子需求，使得每个子需求仅涉及单一设备）
		// 思路：检查每一幅情景图，若检测出情景图中存在并行结构，则去检查并行结构每条分支中的交互是不是涉及的设备各不相同，若不相同则应用策略将每条分支提取作为单独的情景图
		List<ScenarioGraph> scenarioGraphList = project.getScenarioGraphList();

		// 将问题图中的每条现象提出出来，并存储到phenomenonList中，方便后续使用
		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
		List<Interface> interfaceList = project.getProblemDiagram().getContextDiagram().getInterfaceList();
		for(int i=0;i<interfaceList.size();i++) {
			Interface inter = interfaceList.get(i);
			phenomenonList.addAll(inter.getPhenomenonList());
		}

		// 建立领域简称映射表，存储在domainsMap中。Key为领域简称（String），Value为一个ProblemDomain对象
		HashMap<String, ProblemDomain> domainsMap = createDomainsMap(project.getProblemDiagram().getContextDiagram().getProblemDomainList());

		HashMap<Integer, Phenomenon> pheMap = createPheMap(project.getProblemDiagram());

		// 检查每一幅情景图
		for(int i=0;i<scenarioGraphList.size();i++) {
			ScenarioGraph scenarioGraph = scenarioGraphList.get(i);
			List<Node> intNodeList = scenarioGraph.getIntNodeList();
			List<CtrlNode> ctrlNodeList = scenarioGraph.getCtrlNodeList();
			List<List<Node>> branchSat = new ArrayList<List<Node>>();  // 存储满足策略1检查条件的并行结构分支
			if(ctrlNodeList == null) {
				continue;
			}
			for(int j=0;j<ctrlNodeList.size();j++) {
				if(ctrlNodeList.get(j).getNode_type().equals(Start) && ideLoc(ctrlNodeList.get(j), intNodeList, ctrlNodeList).equals("Beh")) {
					// ctrlNodeList[i]为行为交互序列的开始节点
					HashSet<Node> visited = new HashSet<Node>();
					Node branchStart = searchForward(ctrlNodeList.get(j), Branch, visited, intNodeList, ctrlNodeList);

					if(branchStart != null) {
						Pair<List<List<Node>>, Node> branchPath = searchPalEnd(branchStart, Branch, intNodeList, ctrlNodeList);
						List<List<Node>> branches = branchPath.getLeft();
						Node end = branchPath.getRight();
						if(end == null) {
							break;
						}
						List<HashSet<ProblemDomain>> relDev = new ArrayList<HashSet<ProblemDomain>>(Collections.nCopies(branches.size(), new HashSet<ProblemDomain>()));  // 存储每条分支中行为交互涉及的设备
						for(int k=0;k<branches.size();k++) {
							List<Node> branch = branches.get(k);
							for(int m=0;m<branch.size();m++) {
								Node node = branch.get(m);
								if(node.getNode_type().equals(BehInt)) {
									for(int n=0;n<phenomenonList.size();n++) {
										Phenomenon phenomenon = phenomenonList.get(n);
										if(node.getNode_no() == phenomenon.getPhenomenon_no()) {
											// 若行为交互节点node对应的现象phenomenon涉及的发送方或接收方为设备（Sensor或Actuator或Active Device）类型，则将涉及的设备保存到relDev中
											if(domainsMap.containsKey(phenomenon.getPhenomenon_from()) && isDevice(domainsMap.get(phenomenon.getPhenomenon_from()))) {
												// phenomenon发送方为问题领域，接收方为Machine，并且发送方为设备类型
												relDev.get(k).add(domainsMap.get(phenomenon.getPhenomenon_from()));
											}
											else if(domainsMap.containsKey(phenomenon.getPhenomenon_to()) && isDevice(domainsMap.get(phenomenon.getPhenomenon_from()))) {
												// phenomenon发送方为Machine，接收方为问题领域，并且接收方为设备类型
												relDev.get(k).add(domainsMap.get(phenomenon.getPhenomenon_to()));
											}
										}
									}
								}
							}
						}
						// 检查每条分支中行为交互涉及的设备是否有重叠，获取不存在重叠的最大子集
						for(int k=0;k<relDev.size();k++) {
							Boolean flag = true;
							for(int m=0;m<relDev.size();m++) {
								if(m != k && !Collections.disjoint(relDev.get(k), relDev.get(m))) {
									flag = false;
									break;
								}
							}
							if(flag) {
								branchSat.add(branches.get(k));
							}
						}
					}
					break;
				}
			}
			// 根据得到的满足策略1检查条件的并行结构分支编写策略1满足理由
			if(branchSat.size() > 0) {
				String content = new String(scenarioGraph.getRequirement() + "中存在：");
				for(int j=0;j<branchSat.size();j++) {
					List<Node> path = branchSat.get(j);
					for(int k=0;k<path.size();k++) {
						if(path.get(k).getNode_type().equals(BehInt)) {
							Phenomenon phe = pheMap.get(path.get(k).getNode_no());
							if(domainsMap.containsKey(phe.getPhenomenon_from())) {
								content += "对" + domainsMap.get(phe.getPhenomenon_from()).getProblemdomain_name();
							}
							else if(domainsMap.containsKey(phe.getPhenomenon_to())) {
								content += "对" + domainsMap.get(phe.getPhenomenon_to()).getProblemdomain_name();
							}
							else {
								continue;
							}
							content += "设备的操作（" + phe.getPhenomenon_name() + "），";
						}
					}
					content = content.substring(0, content.length() - 1);
					content += "；";
				}
				content += "这些设备操作不存在约束关系，满足策略应用条件";
				ans.getContent().add(content);
			}
		}

		if(ans.getContent().isEmpty()) {
			ans.getContent().add("不满足应用条件");
		}

		return ans;
	}

	private StrategyAdvice ckeckStrategyTwo(Project project) {
		// 检查项目project能否应用策略2：设备操作融合和抽象模式。若能应用，返回修改建议和修改后的效果图；若不能应用，返回不能应用的提示信息
		StrategyAdvice ans = new StrategyAdvice("策略2：设备操作融合和抽象模式");

		// 策略2：设备操作融合和抽象模式（若多个需求要求软件控制器向同一设备发起多个不同的信号类型交互，应该将与设备交互的需求单独独立出来，
		// 使得原来的软件控制器仅仅需要负责产生与设备操作相关的指令，而将具体指令的执行放到设备交互需求里）

		// 思路：检查每一幅情景图，若检测出某情景图的行为交互序列中存在多个行为交互向同一设备发送信号，则把这些行为交互信息保存下来返回

		// 将问题图中的每条现象提出出来，并存储到phenomenonMap中，方便后续使用。其中phenomenonMap的Key表示phenomenon_no，Value为一个Phenomenon对象
		HashMap<Integer, Phenomenon> phenomenonHashMap = createPheMap(project.getProblemDiagram());

		// 建立领域简称映射表，存储在domainsMap中。Key为领域简称（String），Value为一个ProblemDomain对象
		HashMap<String, ProblemDomain> domainsMap = createDomainsMap(project.getProblemDiagram().getContextDiagram().getProblemDomainList());

		List<ScenarioGraph> scenarioGraphList = project.getScenarioGraphList();

		// 存储各个设备在不同需求中接收信号的现象集合，外层HashMap的Key为设备简称，内层HashMap的Key为需求名，内层HashMap的Value为现象集合
		HashMap<String, HashMap<String, HashSet<Phenomenon>>> pheClass = new HashMap<String, HashMap<String, HashSet<Phenomenon>>>();

		for(int i=0;i<scenarioGraphList.size();i++) {
			ScenarioGraph scenarioGraph = scenarioGraphList.get(i);
			List<Node> intNodeList = scenarioGraph.getIntNodeList();
			List<Phenomenon> phenomenonListSce = new ArrayList<Phenomenon>();  // 存储scenarioGraph中的向设备发送信号的行为交互对应的现象
			for(int j=0;j<intNodeList.size();j++) {
				Node node = intNodeList.get(j);
				if(node.getNode_type().equals(BehInt)) {
					Phenomenon phenomenon = phenomenonHashMap.get(node.getNode_no());
					if(phenomenon.getPhenomenon_type().equals(Signal) && domainsMap.containsKey(phenomenon.getPhenomenon_to()) && isDevice(domainsMap.get(phenomenon.getPhenomenon_to()))) {
						// phenomenon为Machine向设备发送的信号类型现象，满足条件，将其放入phenomenonListSce中
						phenomenonListSce.add(phenomenon);
					}
				}
			}

			// 检查phenomenonListSce中的现象，按照接收设备进行分类，每个接收设备收到的Signal现象不止一个的话，则满足策略2
			for(int j=0;j<phenomenonListSce.size();j++) {
				Phenomenon phenomenon = phenomenonListSce.get(j);
				String receiver = phenomenon.getPhenomenon_to();
				if(pheClass.containsKey(receiver)) {
					if(pheClass.get(receiver).containsKey(scenarioGraph.getRequirement())) {
						pheClass.get(receiver).get(scenarioGraph.getRequirement()).add(phenomenon);
					}
					else {
						HashSet<Phenomenon> tmp = new HashSet<Phenomenon>();
						tmp.add(phenomenon);
						pheClass.get(receiver).put(scenarioGraph.getRequirement(), tmp);
					}
				}
				else {
					pheClass.put(receiver, new HashMap<String, HashSet<Phenomenon>>());
					HashSet<Phenomenon> tmp = new HashSet<Phenomenon>();
					tmp.add(phenomenon);
					pheClass.get(receiver).put(scenarioGraph.getRequirement(), tmp);
				}
			}
		}

		Boolean flag = false;
		for(String dev: pheClass.keySet()) {
			if(pheClass.get(dev).size() > 1) {
				String content = new String("");
				flag = true;
				for(String req: pheClass.get(dev).keySet()) {
					content += req + "中存在：";
					for(Phenomenon phe: pheClass.get(dev).get(req)) {
						content += phe.getPhenomenon_name() + "，";
					}
					content += "向设备" + domainsMap.get(dev).getProblemdomain_name() + "发送信号；";
				}
				content += "符合多个需求要求软件控制器向同一设备发起多种不同操作的情况，满足策略应用条件";
				ans.getContent().add(content);
			}
		}

		if(!flag) {
			ans.getContent().add("不满足应用条件");
		}

		return ans;
	}

	private StrategyAdvice ckeckStrategyThree(Project project) {
		StrategyAdvice ans = new StrategyAdvice("策略3：数据生产消费分离模式");

		// 策略3：数据生产消费分离模式（若某需求从设备获取数据并用于控制设备，应该增加一个数据存储领域保存采集到的数据）

		// 思路：检查每一幅情景图，若检测出某情景图的期望交互序列中存在设备向软件控制器返回Value，在该序列后面的期望交互中存在该设备或其他设备状态变迁的现象，则认为满足策略3

		// 建立领域简称映射表，存储在domainsMap中。Key为领域简称（String），Value为一个ProblemDomain对象
		HashMap<String, ProblemDomain> domainsMap = createDomainsMap(project.getProblemDiagram().getContextDiagram().getProblemDomainList());

		// 将问题图中的每条现象提出出来，并存储到phenomenonMap中，方便后续使用。其中phenomenonMap的Key表示phenomenon_no，Value为一个Phenomenon对象
		HashMap<Integer, Phenomenon> phenomenonHashMap = createPheMap(project.getProblemDiagram());

		// 存储情景图scenarioGraph中符合策略3要求的交互
		HashMap<String, List<Phenomenon>> satPheMap = new HashMap<String, List<Phenomenon>>();

		for(ScenarioGraph scenarioGraph: project.getScenarioGraphList()) {
			List<Node> intNodeList = scenarioGraph.getIntNodeList();
			List<CtrlNode> ctrlNodeList = scenarioGraph.getCtrlNodeList();
			if(ctrlNodeList == null) {
				continue;
			}
			for(CtrlNode ctrlNode: ctrlNodeList) {
				if(ctrlNode.getNode_type().equals(Start) && ideLoc(ctrlNode, intNodeList, ctrlNodeList).equals("Exp")) {
					HashSet<Node> visited = new HashSet<Node>();
					Node node = searchForward(ctrlNode, ExpInt, visited, intNodeList, ctrlNodeList);
					while(node != null) {
						Phenomenon phe = phenomenonHashMap.get(node.getNode_no());
						ProblemDomain sender = domainsMap.get(phe.getPhenomenon_from());
						String receiver = phe.getPhenomenon_to();
						if(isDevice(sender) &&
								(phe.getPhenomenon_type().equals(Value) && (receiver!=null && !receiver.equals(""))
										|| phe.getPhenomenon_type().equals(State) && (receiver==null || receiver.equals("")))) {

							if(satPheMap.containsKey(scenarioGraph.getRequirement())) {
								satPheMap.get(scenarioGraph.getRequirement()).add(phe);
							}
							else {
								List<Phenomenon> tmp = new ArrayList<Phenomenon>();
								tmp.add(phe);
								satPheMap.put(scenarioGraph.getRequirement(), tmp);
							}
						}
						visited.add(node);
						node = searchForward(node, ExpInt, visited, intNodeList, ctrlNodeList);
					}
					break;
				}
			}
		}

		for(String req: satPheMap.keySet()) {
			List<Phenomenon> phenomenonList = satPheMap.get(req);
			Boolean foundValue = false, flag = false;
			for(Phenomenon phe: phenomenonList) {
				if(phe.getPhenomenon_type().equals(Value)) {
					foundValue = true;
				}
				else if(phe.getPhenomenon_type().equals(State) && foundValue) {
					flag = true;
				}
			}
			if(!flag) {
				continue;
			}
			String content = req + "中存在：";
			for(Phenomenon phe: phenomenonList) {
				if(phe.getPhenomenon_type().equals(Value)) {
					content += "从设备" + domainsMap.get(phe.getPhenomenon_from()).getProblemdomain_name() + "获取数据" + phe.getPhenomenon_name() + "，";
				}
				else if(phe.getPhenomenon_type().equals(State)) {
					content += "驱使设备" + domainsMap.get(phe.getPhenomenon_from()).getProblemdomain_name() + "发生状态变迁" + phe.getPhenomenon_name() + "，";
				}
			}
			content += "符合在数据的获取与使用之间存在着直接的数据依赖的情况，满足策略应用条件";
			ans.getContent().add(content);
		}

		if(ans.getContent().isEmpty()) {
			ans.getContent().add("不满足应用条件");
		}

		return ans;
	}

	private StrategyAdvice ckeckStrategyFour(Project project) {
		// 策略4：复杂计算级联分解模式（若某需求从数据存储读取数据进行计算，并将计算结果保存至数据存储中，可以将原计算需求分步拆分，并增加数据存储保存中间结果）
		StrategyAdvice ans = new StrategyAdvice("策略4：复杂计算级联分解模式");

		// 思路：检查每一幅情景图，若检测出某情景图的期望交互序列中存在数据存储向软件控制器返回value，在该序列后面的期望交互中存在数据存储领域保存value的现象，则认为满足策略4

		// 建立领域简称映射表，存储在domainsMap中。Key为领域简称（String），Value为一个ProblemDomain对象
		HashMap<String, ProblemDomain> domainsMap = createDomainsMap(project.getProblemDiagram().getContextDiagram().getProblemDomainList());

		// 将问题图中的每条现象提出出来，并存储到phenomenonMap中，方便后续使用。其中phenomenonMap的Key表示phenomenon_no，Value为一个Phenomenon对象
		HashMap<Integer, Phenomenon> phenomenonHashMap = createPheMap(project.getProblemDiagram());

		// 存储情景图scenarioGraph中符合策略4要求的交互
		HashMap<String, List<Phenomenon>> satPheMap = new HashMap<String, List<Phenomenon>>();

		for(ScenarioGraph scenarioGraph: project.getScenarioGraphList()) {
			List<Node> intNodeList = scenarioGraph.getIntNodeList();
			List<CtrlNode> ctrlNodeList = scenarioGraph.getCtrlNodeList();
			if(ctrlNodeList == null) {
				continue;
			}
			for(CtrlNode ctrlNode: ctrlNodeList) {
				if(ctrlNode.getNode_type().equals(Start) && ideLoc(ctrlNode, intNodeList, ctrlNodeList).equals("Exp")) {
					HashSet<Node> visited = new HashSet<Node>();
					Node node = searchForward(ctrlNode, ExpInt, visited, intNodeList, ctrlNodeList);
					while(node != null) {
						Phenomenon phe = phenomenonHashMap.get(node.getNode_no());
						ProblemDomain sender = domainsMap.get(phe.getPhenomenon_from());
						if(isDataStorage(sender) && phe.getPhenomenon_type().equals(Value)) {
							if(satPheMap.containsKey(scenarioGraph.getRequirement())) {
								satPheMap.get(scenarioGraph.getRequirement()).add(phe);
							}
							else {
								List<Phenomenon> tmp = new ArrayList<Phenomenon>();
								tmp.add(phe);
								satPheMap.put(scenarioGraph.getRequirement(), tmp);
							}
						}
						visited.add(node);
						node = searchForward(node, ExpInt, visited, intNodeList, ctrlNodeList);
					}
					break;
				}
			}
		}

		for(String req: satPheMap.keySet()) {
			List<Phenomenon> phenomenonList = satPheMap.get(req);
			Boolean foundValue = false, flag = false;
			for(Phenomenon phe: phenomenonList) {
				String receiver = phe.getPhenomenon_to();
				if(phe.getPhenomenon_type().equals(Value) && receiver!=null && !receiver.equals("")) {
					foundValue = true;
				}
				else if(phe.getPhenomenon_type().equals(Value) && foundValue && (receiver==null || receiver.equals(""))) {
					flag = true;
				}
			}
			if(!flag) {
				continue;
			}
			String content = req + "中存在交互：";
			for(Phenomenon phe: phenomenonList) {
				if(phe.getPhenomenon_to() == null || phe.getPhenomenon_to().equals("")) {
					content += "向" + domainsMap.get(phe.getPhenomenon_from()).getProblemdomain_name() + "保存数据";
				}
				else {
					content += "从" + domainsMap.get(phe.getPhenomenon_from()).getProblemdomain_name() + "载入数据";
				}
				content += phe.getPhenomenon_name() + "，";
			}
			content += "可能存在复杂数据处理需要按计算步骤进行分解的情况，满足策略应用条件";
			ans.getContent().add(content);
		}

		if(ans.getContent().isEmpty()) {
			ans.getContent().add("不满足应用条件");
		}

		return ans;
	}

	private StrategyAdvice ckeckStrategyFive(Project project) {
		// 策略5：控制-计算分离模式（若某需求从设备采集数据进行计算并以此控制设备时候，如果数据的计算特别复杂，可以增加计算需求，将控制和计算分离）

		// 思路：检查每一幅情景图，若检测出某情景图的期望交互序列中存在设备向软件控制器返回Value，在该序列后面的期望交互中存在该设备或其他设备状态类型的现象，则认为满足策略5

		StrategyAdvice ans = new StrategyAdvice("策略5：控制-计算分离模式");

		// 建立领域简称映射表，存储在domainsMap中。Key为领域简称（String），Value为一个ProblemDomain对象
		HashMap<String, ProblemDomain> domainsMap = createDomainsMap(project.getProblemDiagram().getContextDiagram().getProblemDomainList());

		// 将问题图中的每条现象提出出来，并存储到phenomenonMap中，方便后续使用。其中phenomenonMap的Key表示phenomenon_no，Value为一个Phenomenon对象
		HashMap<Integer, Phenomenon> phenomenonHashMap = createPheMap(project.getProblemDiagram());

		// 存储情景图scenarioGraph中符合策略3要求的交互
		HashMap<String, List<Phenomenon>> satPheMap = new HashMap<String, List<Phenomenon>>();

		for(ScenarioGraph scenarioGraph: project.getScenarioGraphList()) {
			List<Node> intNodeList = scenarioGraph.getIntNodeList();
			List<CtrlNode> ctrlNodeList = scenarioGraph.getCtrlNodeList();
			if(ctrlNodeList == null) {
				continue;
			}
			for(CtrlNode ctrlNode: ctrlNodeList) {
				if(ctrlNode.getNode_type().equals(Start) && ideLoc(ctrlNode, intNodeList, ctrlNodeList).equals("Exp")) {
					HashSet<Node> visited = new HashSet<Node>();
					Node node = searchForward(ctrlNode, ExpInt, visited, intNodeList, ctrlNodeList);
					while(node != null) {
						Phenomenon phe = phenomenonHashMap.get(node.getNode_no());
						ProblemDomain sender = domainsMap.get(phe.getPhenomenon_from());
						String receiver = phe.getPhenomenon_to();
						if(isDevice(sender) &&
								(phe.getPhenomenon_type().equals(Value) && (receiver!=null && !receiver.equals(""))
										|| phe.getPhenomenon_type().equals(State) && (receiver==null || receiver.equals("")))) {

							if(satPheMap.containsKey(scenarioGraph.getRequirement())) {
								satPheMap.get(scenarioGraph.getRequirement()).add(phe);
							}
							else {
								List<Phenomenon> tmp = new ArrayList<Phenomenon>();
								tmp.add(phe);
								satPheMap.put(scenarioGraph.getRequirement(), tmp);
							}
						}
						visited.add(node);
						node = searchForward(node, ExpInt, visited, intNodeList, ctrlNodeList);
					}
					break;
				}
			}
		}

		for(String req: satPheMap.keySet()) {
			List<Phenomenon> phenomenonList = satPheMap.get(req);
			Boolean foundValue = false, flag = false;
			for(Phenomenon phe: phenomenonList) {
				if(phe.getPhenomenon_type().equals(Value)) {
					foundValue = true;
				}
				else if(phe.getPhenomenon_type().equals(State) && foundValue) {
					flag = true;
				}
			}
			if(!flag) {
				continue;
			}
			String content = req + "中存在：";
			for(Phenomenon phe: phenomenonList) {
				if(phe.getPhenomenon_type().equals(Value)) {
					content += "从设备" + domainsMap.get(phe.getPhenomenon_from()).getProblemdomain_name() + "获取数据" + phe.getPhenomenon_name() + "进行计算，";
				}
				else if(phe.getPhenomenon_type().equals(State)) {
					content += "控制设备" + domainsMap.get(phe.getPhenomenon_from()).getProblemdomain_name() + "发生状态变迁" + phe.getPhenomenon_name() + "，";
				}
			}
			content += "符合控制需求和计算需求并存的情况，满足策略应用条件";
			ans.getContent().add(content);
		}

		if(ans.getContent().isEmpty()) {
			ans.getContent().add("不满足应用条件");
		}

		return ans;
	}

	public Boolean ignoreStrategyAdvice(StrategyAdvice strategyAdvice) {
		// 用户忽视strategyAdvice，即将该建议加入到inValidSet中
		String key = strategyAdvice.getTitle();
		if(strategyAdvice.getContent() == null) {
			return false;
		}
		for(String c: strategyAdvice.getContent()) {
			key += c;
		}
		this.inValidSet.add(key);
		return true;
	}

	private String pheToSpecification(Phenomenon phenomenon, HashMap<String, ProblemDomain> domainsMap) {
		// 将一条现象翻译成软件需求规约的一条语句
		String sender = phenomenon.getPhenomenon_from(), receiver = phenomenon.getPhenomenon_to(), ans = "";
		if(sender != null && receiver != null) {
			if(domainsMap.containsKey(sender) && !domainsMap.containsKey(receiver)) {
				// receiver是machine
				ans = receiver + " receives " + phenomenon.getPhenomenon_name() + " from " + sender + "[" + phenomenon.getPhenomenon_type() + "];";
			}
			else if(!domainsMap.containsKey(sender) && domainsMap.containsKey(receiver)) {
				// sender是machine
				ans = sender + " sends " + phenomenon.getPhenomenon_name() + " to " + receiver + "[" + phenomenon.getPhenomenon_type() + "];";
			}
			else {
				ans = "pheToSpecification:" + sender + "," + receiver + "错误！";
			}
		}
		return ans;
	}

	// start为一段分支结构的开始，搜索得到该段分支结构的结束节点，返回分支结构中的每条分支路径和结束节点
	private Pair<List<List<Node>>, Node> searchBranchEnd(Node start, List<Node> intNodeList, List<CtrlNode> ctrlNodeList) {
		// 使用广度优先搜索
		List<List<Node>> branchPath = new ArrayList<List<Node>>();  // 分支结构的每条分支（每条分支都包括分支结构开始的Decision节点和结束的节点）
		Node end = null;  // 分支结构的结束节点（两条分支第一次相遇的节点）
		Queue<Pair<Node, Integer>> queue = new LinkedList<Pair<Node, Integer>>();
		start = getSameNode(start, intNodeList, ctrlNodeList);
		List<Node> toList = start.getNode_toList();
		HashMap<Integer, HashSet<Node>> hashSet = new HashMap<>();  // 标记搜索过的节点，防止出现环路时死循环
		for(int i=0;i<toList.size();i++) {
			List<Node> path = new ArrayList<Node>();
			Node toNode = this.getSameNode(toList.get(i), intNodeList, ctrlNodeList);
			path.add(start);
			Pair<Node, Integer> elem = new ImmutablePair<>(toNode, i);
			queue.add(elem);
			HashSet<Node> tmp = new HashSet<>();
			tmp.add(toNode);
			hashSet.put(i, tmp);
			branchPath.add(path);
		}
		while (queue.size() > 0) {
			Pair<Node, Integer> elem = queue.remove();
			elem = new ImmutablePair<Node, Integer>(getSameNode(elem.getLeft(), intNodeList, ctrlNodeList), elem.getRight());
			branchPath.get(elem.getRight()).add(elem.getLeft());
			List<Node> toNodes = elem.getLeft().getNode_toList();
			if(toNodes == null) {
				continue;
			}
			for(int i=0;i<toNodes.size();i++) {
				Node toNode = this.getSameNode(toNodes.get(i), intNodeList, ctrlNodeList);
				if(!hashSet.get(elem.getRight()).contains(toNode)) {
					// toNodes[i]没有被访问过（防止有循环），将toNodes[i]添加到队列中
					queue.add(new ImmutablePair<>(toNode, elem.getRight()));
					hashSet.get(elem.getRight()).add(toNode);
				}
			}
		}

		List<Node> firstBranch = branchPath.get(0), secondBranch = branchPath.get(1);
//		System.out.println("firstBranch:");
//		for(Node node:firstBranch) {
//			System.out.println(node.getNode_no()+ " " + node.getNode_type());
//		}
//		System.out.println("secondBranch:");
//		for(Node node:secondBranch) {
//			System.out.println(node.getNode_no()+ " " + node.getNode_type());
//		}
		int firstEndIndex = -1, secondEndIndex = -1;
		for(int i=firstBranch.size()-1;i>=0;i--) {
			for(int j=secondBranch.size()-1;j>=0;j--) {
				if(this.isSameNode(firstBranch.get(i), secondBranch.get(j))) {
					firstEndIndex = i;
					secondEndIndex = j;
					end = firstBranch.get(i);
					break;
				}
			}
			if(firstEndIndex != -1 && secondEndIndex != -1) {
				break;
			}
		}

		for(int i=firstBranch.size()-1;i>firstEndIndex;i--) {
			firstBranch.remove(i);
		}
		for(int i=secondBranch.size()-1;i>secondEndIndex;i--) {
			secondBranch.remove(i);
		}

		return new ImmutablePair<>(branchPath, end);
	}

	// 判断以start为起点的循环中，哪条路径是循环、哪条路径是循环结束后。分别返回循环路径的第一个节点、循环结束后路径的第一个节点；如果不是循环结构，则返回null
	private Pair<Node, Node> judgeLoopPath(Node start, List<Node> intNodeList, List<CtrlNode> ctrlNodeList) {
		start = this.getSameNode(start, intNodeList, ctrlNodeList);
		Node node0 = start.getNode_toList().get(0), node1 = start.getNode_toList().get(1);
		node0 = this.getSameNode(node0, intNodeList, ctrlNodeList);
		node1 = this.getSameNode(node1, intNodeList, ctrlNodeList);

		HashSet<Node> visited = new HashSet<>();
		Queue<Node> queue = new LinkedList<>();
		queue.add(node0);
		visited.add(node0);

		while (!queue.isEmpty()) {
			Node elem = queue.remove();
			elem = this.getSameNode(elem, intNodeList, ctrlNodeList);
			List<Node> toNodeList = elem.getNode_toList();
			if(toNodeList != null) {
				for(Node toNode: toNodeList) {
					toNode = this.getSameNode(toNode, intNodeList, ctrlNodeList);
					if(this.isSameNode(toNode, start)) {
						return new ImmutablePair<>(node0, node1);
					}
					if(!visited.contains(toNode)) {
						visited.add(toNode);
						queue.add(toNode);
					}
				}
			}
		}

		visited.clear();
		queue.clear();
		queue.add(node1);
		visited.add(node1);

		while (!queue.isEmpty()) {
			Node elem = queue.remove();
			elem = this.getSameNode(elem, intNodeList, ctrlNodeList);
			List<Node> toNodeList = elem.getNode_toList();
			if(toNodeList != null) {
				for(Node toNode: toNodeList) {
					toNode = this.getSameNode(toNode, intNodeList, ctrlNodeList);
					if(this.isSameNode(toNode, start)) {
						return new ImmutablePair<>(node1, node0);
					}
					if(!visited.contains(toNode)) {
						visited.add(toNode);
						queue.add(toNode);
					}
				}
			}
		}

		return null;
	}

	private String structToSpecification(Node start, Node end, List<Node> intNodeList, List<CtrlNode> ctrlNodeList, List<Line> lineList, HashMap<Integer, Phenomenon> phenomenonHashMap, HashMap<String, ProblemDomain> domainsMap) {
		start = this.getSameNode(start, intNodeList, ctrlNodeList);
		end = this.getSameNode(end, intNodeList, ctrlNodeList);
		if(this.isSameNode(start, end) && (end.getNode_type().equals(End) || end.getNode_type().equals(Branch) || end.getNode_type().equals(Merge) || end.getNode_type().equals(Decision))) {
			// 当前需要转化的结构的开始节点和结束节点指向同一个End类型节点，无需翻译
			return "";
		}
		else if(this.isSameNode(start, end) && start.getNode_type().equals(BehInt)) {
			// 当前需要转化的结构是一个交互节点
			return this.pheToSpecification(phenomenonHashMap.get(start.getNode_no()), domainsMap);
		}
		else if(this.isSameNode(start, end) && start.getNode_type().equals(Delay)) {
			// 当前需要转化的结构是一个延时节点
			CtrlNode delay = (CtrlNode) this.getSameNode(start, intNodeList, ctrlNodeList);
			return "Delay " + delay.getDelay_type() + " " + delay.getNode_text() + ";";
		}
		else {
			Node subStart = start;
			String ans = "";
			while (subStart != null && !subStart.getNode_type().equals(End)) {
				while (subStart != null && !(subStart.getNode_type().equals(BehInt) ||
						subStart.getNode_type().equals(Delay) ||
						subStart.getNode_type().equals(Decision) ||
						subStart.getNode_type().equals(Merge) ||
						subStart.getNode_type().equals(Branch))) {
					subStart = this.getSameNode(subStart, intNodeList, ctrlNodeList);
					if(subStart.getNode_toList() == null || subStart.getNode_toList().isEmpty()) {
						return ans;
					}
					else {
						subStart = subStart.getNode_toList().get(0);
						subStart = this.getSameNode(subStart, intNodeList, ctrlNodeList);
					}
				}
				if(subStart == null || subStart.getNode_type().equals(End) || this.isSameNode(subStart, end)) {
					return ans;
				}
				else if(subStart.getNode_type().equals(BehInt) || subStart.getNode_type().equals(Delay)) {
					ans += structToSpecification(subStart, subStart, intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
					subStart = subStart.getNode_toList().get(0);
					subStart = this.getSameNode(subStart, intNodeList, ctrlNodeList);
				}
				else if(subStart.getNode_type().equals(Decision)) {
					// 以Decision为起点的结构可能是分支结构（if），也可能是有条件循环结构
					CtrlNode decision = (CtrlNode) this.getSameNode(subStart, intNodeList, ctrlNodeList);
					Pair<Node, Node> p = this.judgeLoopPath(this.getSameNode(subStart, intNodeList, ctrlNodeList), intNodeList, ctrlNodeList);
					if(p == null) {
						// 不是循环，是分支结构
						Pair<List<List<Node>>, Node> branchStruct = this.searchBranchEnd(subStart, intNodeList, ctrlNodeList);
						HashMap<String, List<Node>> conditionToPath = new HashMap<>();

						for(Line line: lineList) {
							if(this.isSameNode(line.getFromNode(), subStart) && line.getLine_type().equals(BehOrder)) {
								Node toNode = line.getToNode();
								List<List<Node>> branchPath = branchStruct.getLeft();
								for(List<Node> path: branchPath) {
									for(Node node: path) {
										if(this.isSameNode(toNode, node)) {
											conditionToPath.put(line.getCondition(), path);
											break;
										}
									}
								}
							}
						}

						Boolean ifUsed = false;
						for(String condition: conditionToPath.keySet()) {
							List<Node> path = conditionToPath.get(condition);
							if(!ifUsed) {
								ans += "if(" + decision.getNode_text() + "==" + condition + "){";
								ans += structToSpecification(path.get(1), branchStruct.getRight(), intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
								ans += "}";
								ifUsed = true;
							}
							else {
								ans += "else if(" + decision.getNode_text() + "==" + condition + "){";
								ans += structToSpecification(path.get(1), branchStruct.getRight(), intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
								ans += "}";
							}
							subStart = path.get(path.size()-1);
							subStart = this.getSameNode(subStart, intNodeList, ctrlNodeList);
						}
					}
					else {
						// 有条件循环结构
						String condition = "";
						for(Line line: lineList) {
							if(this.isSameNode(line.getFromNode(), subStart) && this.isSameNode(line.getToNode(), p.getLeft())) {
								condition = line.getCondition();
								break;
							}
						}
						ans += "while(" + decision.getNode_text() + "==" + condition + "){";
						ans += structToSpecification(p.getLeft(), subStart, intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
						ans += "}";
						subStart = p.getRight();
						subStart = this.getSameNode(subStart, intNodeList, ctrlNodeList);
					}
				}
				else if(subStart.getNode_type().equals(Merge)) {
					// 以subStart为开始的结构为死循环结构，死循环结构以Merge节点作为开始
					CtrlNode merge = (CtrlNode) this.getSameNode(subStart, intNodeList, ctrlNodeList);
					if(merge.getNode_toList() != null) {
						if(merge.getNode_toList().size() == 1) {
							ans += "while(" + merge.getNode_text() + "){";
							ans += structToSpecification(merge.getNode_toList().get(0), subStart, intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
							ans += "}";
							return ans;
						}
						else if(merge.getNode_toList().size() == 2) {
							Pair<Node, Node> p = this.judgeLoopPath(merge, intNodeList, ctrlNodeList);
							ans += "while(" + merge.getNode_text() + "){";
							ans += structToSpecification(p.getLeft(), subStart, intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
							ans += "}";
							subStart = p.getRight();
							subStart = this.getSameNode(subStart, intNodeList, ctrlNodeList);
						}
					}
				}
				else if(subStart.getNode_type().equals(Branch)) {
					// 以subStart为开始的结构有可能为并行结构，也有可能为合并结构
					Pair<List<List<Node>>, Node> branchPath = searchPalEnd(subStart, Branch, intNodeList, ctrlNodeList);
					if(branchPath.getRight() != null) {
						List<List<Node>> branches = branchPath.getLeft();
						for(int i=0;i<branches.size();i++) {
							List<Node> branch = branches.get(i);
							if(i < branches.size() - 1) {
								if(branch.size() > 1) {
									ans += "{";
									ans += structToSpecification(branch.get(1), branchPath.getRight(), intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
									ans += "}||";
								}
							}
							else {
								if(branch.size() > 1) {
									ans += "{";
									ans += structToSpecification(branch.get(1), branchPath.getRight(), intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
									ans += "}";
								}
							}
						}
						subStart = this.getSameNode(branchPath.getRight(), intNodeList, ctrlNodeList);
						subStart = subStart.getNode_toList().get(0);
						subStart = this.getSameNode(subStart, intNodeList, ctrlNodeList);
					}
					else {
						branchPath = searchPalEnd(subStart, Merge, intNodeList, ctrlNodeList);
						if(branchPath.getRight() != null) {
							List<List<Node>> branches = branchPath.getLeft();
							for(int i=0;i<branches.size();i++) {
								List<Node> branch = branches.get(i);
								if(i < branches.size() - 1) {
									if(branch.size() > 1) {
										ans += "{";
										ans += structToSpecification(branch.get(1), branchPath.getRight(), intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
										ans += "}|";
									}
								}
								else {
									if(branch.size() > 1) {
										ans += "{";
										ans += structToSpecification(branch.get(1), branchPath.getRight(), intNodeList, ctrlNodeList, lineList, phenomenonHashMap, domainsMap);
										ans += "}";
									}
								}
							}
							subStart = this.getSameNode(branchPath.getRight(), intNodeList, ctrlNodeList);
							subStart = subStart.getNode_toList().get(0);
							subStart = this.getSameNode(subStart, intNodeList, ctrlNodeList);
						}
					}
				}
			}
			return ans;
		}
	}

	// 将intNodeList中的行为交互涉及的领域提取出来，并按照领域类型进行分类
	private HashMap<String, HashSet<String>> genDomainDeclare(List<Node> intNodeList, HashMap<Integer, Phenomenon> phenomenonHashMap, HashMap<String, ProblemDomain> domainsMap) {
		HashMap<String, HashSet<String>> domainDeclare = new HashMap<>();
		for(Node intNode: intNodeList) {
			if(intNode.getNode_type().equals(BehInt)) {
				Phenomenon phenomenon = phenomenonHashMap.get(intNode.getNode_no());
				String sender = phenomenon.getPhenomenon_from(), receiver = phenomenon.getPhenomenon_to();
				if(domainsMap.containsKey(sender)) {
					ProblemDomain domain = domainsMap.get(sender);
					String type = domain.getProblemdomain_type();
					if(domainDeclare.containsKey(type)) {
						domainDeclare.get(type).add(domain.getProblemdomain_shortname());
					}
					else {
						HashSet<String> tmp = new HashSet<>();
						tmp.add(domain.getProblemdomain_shortname());
						domainDeclare.put(type, tmp);
					}
				}
				else if(domainsMap.containsKey(receiver)) {
					ProblemDomain domain = domainsMap.get(receiver);
					String type = domain.getProblemdomain_type();
					if(domainDeclare.containsKey(type)) {
						domainDeclare.get(type).add(domain.getProblemdomain_shortname());
					}
					else {
						HashSet<String> tmp = new HashSet<>();
						tmp.add(domain.getProblemdomain_shortname());
						domainDeclare.put(type, tmp);
					}
				}
			}
		}
		return domainDeclare;
	}
	public Boolean saveSpecification(Project project,String userName) {
		String address;
		if(userName.equals("")) {
			address = AddressService.rootAddress ;
		}else {
			address = AddressService.userAddress + userName + "/" ;
		}
		// 将project中的每一幅情景图转化为软件需求规约并保存成一个txt文件
		address = address + project.getTitle() + "/Specification.txt";
		String ans = "";
		project = this.createFromTo(project);
		HashMap<String, ProblemDomain> domainsMap = this.createDomainsMap(project.getProblemDiagram().getContextDiagram().getProblemDomainList());
		HashMap<Integer, Phenomenon> pheMap = this.createPheMap(project.getProblemDiagram());
		List<ScenarioGraph> scenarioGraphList = project.getScenarioGraphList();
		for(ScenarioGraph scenarioGraph: scenarioGraphList) {
			List<CtrlNode> ctrlNodeList = scenarioGraph.getCtrlNodeList();
			List<Node> intNodeList = scenarioGraph.getIntNodeList();
			for(CtrlNode ctrlNode: ctrlNodeList) {
				if(ctrlNode.getNode_type().equals(Start) && ideLoc(ctrlNode, intNodeList, ctrlNodeList).equals("Beh")) {
					HashSet<Node> visited = new HashSet<Node>();
					Node end = searchForward(ctrlNode, this.End, visited, intNodeList, ctrlNodeList);
					if (end != null) {
						ans += scenarioGraph.getRequirement() + "{";
						HashMap<String, HashSet<String>> domainDeclare = this.genDomainDeclare(intNodeList, pheMap, domainsMap);
						for(String type: domainDeclare.keySet()) {
							ans += type + " ";
							HashSet<String> domains = domainDeclare.get(type);
							int count = 0;
							for(String domain: domains) {
								if(count < domains.size() - 1) {
									ans += domain + ",";
								}
								else {
									ans += domain + ";";
								}
								count += 1;
							}
						}
						ans += this.structToSpecification(ctrlNode, end, intNodeList, ctrlNodeList, scenarioGraph.getLineList(), pheMap, domainsMap);
						ans += "}\n";
					}
				}
			}
		}
		System.out.println("ans:" + ans);
		FileService fileService = new FileService();
		return fileService.writeTxt(address, ans);
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
