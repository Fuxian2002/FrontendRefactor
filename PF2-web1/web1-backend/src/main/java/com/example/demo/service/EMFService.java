package com.example.demo.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.demo.bean.*;
import org.apache.jena.base.Sys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.example.demo.util.SpringContextUtil;
import lombok.extern.log4j.Log4j2;
import pf.PfStandaloneSetup;

//@Log4j2(topic = "log")
public class EMFService {

	private static String getRootAddress() {
		return SpringContextUtil.getBean(AddressService.class).getRootAddress();
	}
	private final static Logger log = LogManager.getLogger("log");

	// =========================project===================================
	/**
	 * @Title: emfFile2Project
	 * @Description: TODO
	 * @param @param emfFilename
	 * @param @return 设定文件
	 * @return Project 返回类型
	 * @throws
	 */
	public static Project emfFile2Project(String emfFilename) {
		System.out.println("emfFile2Project-------"+emfFilename);
		File xmlFile = new File(emfFilename);
		Project project = new Project();
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(xmlFile);
			Element pfElement = document.getRootElement();
			List<?> nodeElementList = pfElement.elements("nodes");
			List<?> linkElementList = pfElement.elements("links");
			List<?> phenomenonElementList = pfElement.elements("phenomenons");

			Machine machine = new Machine();
			ESystem system = new ESystem();
			List<ExternalEntity> externalEntityList = new ArrayList<ExternalEntity>();
			List<Intent> intentionList = new ArrayList<Intent>();
			List<Interface> interfaceList_i = new ArrayList<Interface>();
			List<Reference> referenceList_i = new ArrayList<Reference>();
			List<Constraint> constraintList_i = new ArrayList<Constraint>();
			List<ProblemDomain> problemDomainList = new ArrayList<ProblemDomain>();
			List<Interface> interfaceList = new ArrayList<Interface>();
			List<Requirement> requirementList = new ArrayList<Requirement>();
			List<Reference> referenceList = new ArrayList<Reference>();
			List<Constraint> constraintList = new ArrayList<Constraint>();
			List<String> node = new ArrayList<String>();
//			HashMap<String,String> nodetype = new HashMap<String,String>();

			int intentnum = 0;
			int entitynum = 0;
			int reqnum = 0;
			int domainnum = 0;
			for (Object object : nodeElementList) {
				Element nodeElement = (Element) object;
				String name = nodeElement.attributeValue("name").replaceAll("#",
						"");
				String description = nodeElement.attributeValue("description");
				String type = nodeElement.attributeValue("type");
				int height = 50;
				int width = 100;

				if (type.equals("requirement")) {
					reqnum = reqnum + 1;
					Requirement requirement = new Requirement();
					requirement.setRequirement_no(reqnum);
					requirement.setRequirement_shortname(name);
					node.add(name);
//					nodetype.put(name,"requirement");
					if (description == null) {
						requirement.setRequirement_context(name);
					} else {
						requirement.setRequirement_context(description);
					}
					requirement.setRequirement_h(height);
					requirement.setRequirement_w(width);
					requirement.setRequirement_x(900);
					requirement.setRequirement_y(50 + reqnum * 100);
					requirementList.add(requirement);
				} else if (type.equals("intention")) {
					intentnum = intentnum + 1;
					Intent intent = new Intent();
					intent.setIntent_no(intentnum);
					intent.setIntent_shortname(name);
					node.add(name);
//					nodetype.put(name,"intention");
					if (description == null) {
						intent.setIntent_context(name);
					} else {
						intent.setIntent_context(description);
					}
					intent.setIntent_h(height);
					intent.setIntent_w(width);
					intent.setIntent_x(900);
					intent.setIntent_y(50 + intentnum * 100);
					intentionList.add(intent);
				} else if (type.equals("system")) {
					node.add(name);
//					nodetype.put(name,"system");
					if (description == null) {
						system.setSystem_name(name);
					} else {
						system.setSystem_name(description);
					}
					system.setSystem_shortName(name);
					system.setSystem_h(height);
					system.setSystem_w(width);
					system.setSystem_x(200);
					system.setSystem_y(150);
				} else if (type.equals("machine")) {
					node.add(name);
//					nodetype.put(name,"machine");
					if (description == null) {
						machine.setMachine_name(name);
					} else {
						machine.setMachine_name(description);
					}
					machine.setMachine_shortName(name);
					machine.setMachine_h(height);
					machine.setMachine_w(width);
					machine.setMachine_x(200);
					machine.setMachine_y(150);
				}  else if (type.equals("extentity")) {
					node.add(name);
//					nodetype.put(name,"extentity");
					entitynum = entitynum + 1;
					ExternalEntity externalEntity = new ExternalEntity();
					if (description == null) {
						externalEntity.setExternalentity_name(name);
					} else {
						externalEntity.setExternalentity_name(description);
					}
					externalEntity.setExternalentity_shortname(name);
					externalEntity.setExternalentity_no(entitynum);
					externalEntity.setExternalentity_h(height);
					externalEntity.setExternalentity_w(width);
					externalEntity.setExternalentity_x(500);
					externalEntity.setExternalentity_y(entitynum * 100);
					externalEntityList.add(externalEntity);
				} else if (type.equals("prodomain")) {
					node.add(name);
//					nodetype.put(name,"prodomain");
					String prodomaintype = nodeElement.attributeValue("prodomaintype");
					domainnum = domainnum + 1;
					ProblemDomain problemDomain = new ProblemDomain();
					if (description == null) {
						problemDomain.setProblemdomain_name(name);
					} else {
						problemDomain.setProblemdomain_name(description);
					}
					problemDomain.setProblemdomain_shortname(name);
					problemDomain.setProblemdomain_no(domainnum);
					problemDomain.setProblemdomain_h(height);
					problemDomain.setProblemdomain_w(width);
					problemDomain.setProblemdomain_x(500);
					problemDomain.setProblemdomain_y(domainnum * 100);
					if (prodomaintype.equals("clock")) {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("Clock");
					} else if (prodomaintype.equals("datastorage")) {
						problemDomain.setProblemdomain_property("DesignDomain");
						problemDomain.setProblemdomain_type("Data Storage");
					} else if (prodomaintype.equals("sensor")) {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("Sensor");
					} else if (prodomaintype.equals("actuator")) {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("Actuator");
					} else if (prodomaintype.equals("active")) {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("Active Device");
					} else if (prodomaintype.equals("passive")) {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("Passive Device");
					} else if (prodomaintype.equals("device")) {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("Device");
					} else {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("");
					}

					problemDomainList.add(problemDomain);
				}
			}
			ProblemEditor.deleteOverlapEE(externalEntityList);
			ProblemEditor.deleteOverlapIntent(intentionList);
			ProblemEditor.deleteOverlapPd(problemDomainList);
			ProblemEditor.deleteOverlapReq(requirementList);

			int interfaceNum = 0;
			int referenceNum = 0;
			int constraintNum = 0;
			int pheNum = 0;
			List<Phenomenon> pheList = new ArrayList<Phenomenon>(); // 全部现象
			HashMap<String,Integer> phenoList = new HashMap<String,Integer>();
			for (Object object : phenomenonElementList){
				pheNum = pheNum + 1;
				Element nodeElement = (Element) object;
				Phenomenon phe = new Phenomenon();
				String type = nodeElement.attributeValue("type");
				String name = nodeElement.attributeValue("name").replaceAll("#", "");
				String description = nodeElement.attributeValue("description");
				phe.setPhenomenon_no(pheNum);
				phe.setPhenomenon_type(type);
				phe.setPhenomenon_shortname(name);
				phe.setPhenomenon_name(description);
				phe.setPhenomenon_from(nodeElement.attributeValue("from"));
				phe.setPhenomenon_to(nodeElement.attributeValue("to"));
				pheList.add(phe);
				phenoList.put(phe.getPhenomenon_shortname(),phe.getPhenomenon_no());
			}

			for (Object object : linkElementList) {
				Element linkElement = (Element) object;
				String type = linkElement.attributeValue("type");
				String name = linkElement.attributeValue("description");
				System.out.println("linkname: "+ name +" type:" +type);
				if (linkElement.attributeValue("from") == null)
					continue;
				String from = linkElement.attributeValue("from").replaceAll("#", "");
				if (linkElement.attributeValue("to") == null)
					continue;
				String to = linkElement.attributeValue("to").replaceAll("#", "");
				boolean isintent = false;
//				String fromtype = nodetype.get(from);
//				String totype = nodetype.get(to);
				System.out.println("from:"+from+" to:"+to);
//				System.out.println("from:"+fromtype+" to:"+totype);
//				if(fromtype.contentEquals("system") || totype.contentEquals("system")
//						|| fromtype.contentEquals("intention") || totype.contentEquals("intention")){
//					isintent = true;
//				}
				if(system.getShortname()!=null
						&& (from.contentEquals(system.getShortname()) || to.contentEquals(system.getShortname()))){
					System.out.println("isintent = true;");
					isintent = true;
				}else {
					for(Intent it : intentionList){
						if(from.contentEquals(it.getShortname()) || to.contentEquals(it.getShortname())){
							System.out.println("isintent = true;");
							isintent = true;
							break;
						}
					}
				}
				String phenomenas = linkElement.attributeValue("phenomena");
				System.out.println("phenomenas: "+phenomenas);
				List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
				String description = name + ":";
				String pheStr = "";
				if(phenomenas != null){
					String[] phenomenaList = phenomenas.split("; ");
					if(phenomenaList != null && phenomenaList.length > 0 ) {
						for (String obj : phenomenaList) {
//						int pheNo = Integer.parseInt(obj.split("\\.")[1]);
							if(phenoList.get(obj) == null){
								continue;
							}
							int pheNo = phenoList.get(obj) - 1;
							Phenomenon phe = pheList.get(pheNo);
							Phenomenon phenomenon = new Phenomenon();
							phenomenon.setPhenomenon_name(phe.getPhenomenon_name());
							phenomenon.setPhenomenon_shortname(phe.getPhenomenon_shortname());
							phenomenon.setPhenomenon_type(phe.getPhenomenon_type());
							phenomenon.setPhenomenon_no(phe.getPhenomenon_no());
							phenomenon.setPhenomenon_from(phe.getPhenomenon_from());
							phenomenon.setPhenomenon_to(phe.getPhenomenon_to());
							phenomenonList.add(phenomenon);
							if (pheStr.equals("")) {
								pheStr = pheStr + phenomenon.getPhenomenon_name();
							} else {
								pheStr = pheStr + "; " + phenomenon.getPhenomenon_name();
							}
						}
					}
				}
				if (!pheStr.equals("")) {
					pheStr = from + "!" + "{" + pheStr + "}";
				}
				description = description + pheStr;
				if (type.equals("--")) {
					interfaceNum = interfaceNum + 1;
					Interface inte = new Interface();
					inte.setInterface_no(interfaceNum);
					inte.setInterface_name(name);
					inte.setInterface_from(from);
					inte.setInterface_to(to);
					inte.setInterface_description(description);
					inte.setIsintent(isintent);
					inte.setPhenomenonList(phenomenonList);
					System.out.println(from + "--" + to);
					if(isintent){
						interfaceList_i.add(inte);
					}else {
						interfaceList.add(inte);
					}
				} else if (type.equals("~~")) {
					referenceNum = referenceNum + 1;
					// if from(to) is req/intent,change from(to) to req/intent'name
					if(isintent) {
						for (Intent req : intentionList) {
							if (from.contentEquals(req.getShortname())) {
								from = req.getName();
								break;
							} else if (to.contentEquals(req.getShortname())) {
								to = req.getName();
								break;
							}
						}
					}else {
						for (Requirement req : requirementList) {
							if (from.contentEquals(req.getShortname())) {
								from = req.getName();
								break;
							} else if (to.contentEquals(req.getShortname())) {
								to = req.getName();
								break;
							}
						}
					}
					System.out.println(from + "~~" + to);
					Reference reference = new Reference();
					reference.setReference_no(referenceNum);
					reference.setReference_name(name);
					reference.setReference_from(from);
					reference.setReference_to(to);
					List<RequirementPhenomenon> reqPheList = new ArrayList<RequirementPhenomenon>();
					for (Phenomenon phe : phenomenonList) {
						RequirementPhenomenon reqPhe = new RequirementPhenomenon();
						reqPhe.setPhenomenon_no(phe.getPhenomenon_no());
						reqPhe.setPhenomenon_name(phe.getPhenomenon_name());
						reqPhe.setPhenomenon_shortname(phe.getPhenomenon_shortname());
						reqPhe.setPhenomenon_type(phe.getPhenomenon_type());
						reqPhe.setPhenomenon_from(phe.getPhenomenon_from());
						reqPhe.setPhenomenon_to(phe.getPhenomenon_to());
						reqPhe.setPhenomenon_constraint("false");
						for (Requirement req : requirementList) {
							if (req.getRequirement_context().equals(from) || req.getRequirement_context().equals(to)) {
								reqPhe.setPhenomenon_requirement(req.getRequirement_no());
							}
						}
						reqPheList.add(reqPhe);
					}
					reference.setReference_description(description);
					reference.setPhenomenonList(reqPheList);
					if(isintent) {
						referenceList_i.add(reference);
					}else {
						referenceList.add(reference);
					}
				} else if (type.equals("<~") || type.equals("~>")) {
					constraintNum = constraintNum + 1;
					// if from(to) is req/intent,change from(to) to req/intent'name
					if(isintent) {
						for (Intent req : intentionList) {
							if (from.contentEquals(req.getShortname())) {
								from = req.getName();
								break;
							} else if (to.contentEquals(req.getShortname())) {
								to = req.getName();
								break;
							}
						}
					}else {
						for (Requirement req : requirementList) {
							if (from.contentEquals(req.getShortname())) {
								from = req.getName();
								break;
							} else if (to.contentEquals(req.getShortname())) {
								to = req.getName();
								break;
							}
						}
					}
					System.out.println(from + "~>" + to);
					Constraint constraint = new Constraint();
					constraint.setConstraint_no(constraintNum);
					constraint.setConstraint_name(name);
					constraint.setConstraint_from(from);
					constraint.setConstraint_to(to);
					List<RequirementPhenomenon> reqPheList = new ArrayList<RequirementPhenomenon>();
					for (Phenomenon phe : phenomenonList) {
						RequirementPhenomenon reqPhe = new RequirementPhenomenon();
						reqPhe.setPhenomenon_no(phe.getPhenomenon_no());
						reqPhe.setPhenomenon_name(phe.getPhenomenon_name());
						reqPhe.setPhenomenon_shortname(phe.getPhenomenon_shortname());
						reqPhe.setPhenomenon_type(phe.getPhenomenon_type());
						reqPhe.setPhenomenon_from(phe.getPhenomenon_from());
						reqPhe.setPhenomenon_to(phe.getPhenomenon_to());
						reqPhe.setPhenomenon_constraint("true");
						for (Requirement req : requirementList) {
//							System.out.println(req.getRequirement_context());
//							System.out.println(from);
//							System.out.println(to);
							if (req.getRequirement_context().equals(from) || req.getRequirement_context().equals(to)) {
								reqPhe.setPhenomenon_requirement(req.getRequirement_no());
							}
						}
						reqPheList.add(reqPhe);
					}
					constraint.setConstraint_description(description);
					constraint.setPhenomenonList(reqPheList);
					if(isintent) {
						constraintList_i.add(constraint);
					}else {
						constraintList.add(constraint);
					}
				}
			}

			ProblemEditor.deleteOverlapInt(interfaceList_i);
			ProblemEditor.deleteOverlapRef(referenceList_i);
			ProblemEditor.deleteOverlapCon(constraintList_i);
			ProblemEditor.deleteOverlapInt(interfaceList);
			ProblemEditor.deleteOverlapRef(referenceList);
			ProblemEditor.deleteOverlapCon(constraintList);
//			ProblemEditor.changeRefPhe(interfaceList, referenceList);
//			ProblemEditor.changeConPhe(interfaceList, constraintList);

			String title = pfElement.attributeValue("name");
			IntentDiagram intentDiagram = new IntentDiagram();
			String intentDiagramName = "IntentDiagram";
			intentDiagram.setTitle(intentDiagramName);
			if (system.getName() != null)
				intentDiagram.setSystem(system);
			else
				intentDiagram.setSystem(null);
			intentDiagram.setExternalEntityList(externalEntityList);
			intentDiagram.setIntentList(intentionList);
			intentDiagram.setInterfaceList(interfaceList_i);
			intentDiagram.setReferenceList(referenceList_i);
			intentDiagram.setConstraintList(constraintList_i);
			ContextDiagram contextDiagram = new ContextDiagram();
			String contextDiagramName = "ContextDiagram";
			contextDiagram.setTitle(contextDiagramName);
			if (machine.getName() != null)
				contextDiagram.setMachine(machine);
			else
				contextDiagram.setMachine(null);
			contextDiagram.setProblemDomainList(problemDomainList);
			contextDiagram.setInterfaceList(interfaceList);

			ProblemDiagram problemDiagram = new ProblemDiagram();
			String problemDiagramName = "ProblemDiagram";
			problemDiagram.setTitle(problemDiagramName);
			problemDiagram.setRequirementList(requirementList);
			problemDiagram.setReferenceList(referenceList);
			problemDiagram.setConstraintList(constraintList);
			problemDiagram.setContextDiagram(contextDiagram);

			project.setTitle(title);
			project.setIntentDiagram(intentDiagram);
			project.setContextDiagram(contextDiagram);
			project.setProblemDiagram(problemDiagram);

			System.out.println("interfaceList_i: "+interfaceList_i.size()
					+" "+project.getIntentDiagram().getInterfaceList().size());
			System.out.println("referenceList_i: "+referenceList_i.size()
					+" "+project.getIntentDiagram().getReferenceList().size());
			System.out.println("constraintList_i: "+constraintList_i.size()
					+" "+project.getIntentDiagram().getConstraintList().size());
			System.out.println("interfaceList: "+interfaceList.size()
					+" "+project.getContextDiagram().getInterfaceList().size());
			System.out.println("referenceList: "+referenceList.size()
					+" "+project.getProblemDiagram().getReferenceList().size());
			System.out.println("constraintList: "+constraintList.size()
					+" "+project.getProblemDiagram().getConstraintList().size());

		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("1.移动文件：从路径 " + emfFilename + " 移动到路径 "
					+ getRootAddress() + xmlFile.getName());
			try {
				FileOperation.fileMove(emfFilename,
						getRootAddress() + xmlFile.getName());// 调用方法实现文件的移动
			} catch (Exception ee) {
				System.out.println("移动文件出现问题" + ee.getMessage());
			}
			return null;
		}
		return Project.getCorrectProject(project);
	}

	/**
	 * @Title: old_ProjectToEMF
	 * @Description: links' from and to are //@node.i
	 * @param @param rootAddress
	 * @param @param projectAddress
	 * @param @param project
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
//	public static boolean ProjectToOldEMF(String rootAddress,
//			String projectAddress, Project project) {
//		Document document = DocumentHelper.createDocument();
//		new SAXReader();
//		Element pfElement = document.addElement("pf:ProblemDiagram");
//		pfElement.addAttribute("xmi:version", "2.0");
//		pfElement.addAttribute("xmln:pf",
//				"http://www.Pf.pf");
//		pfElement.addAttribute("name", projectAddress);
//
//		List<PdNode> nodeList = new ArrayList<PdNode>();
//		Machine machine = project.getContextDiagram().getMachine();
//		if (machine != null) {
//			nodeList.add(machine);
//			Element nodesElement = pfElement.addElement("nodes");
//			nodesElement.addAttribute("name", machine.getShortname());
//			nodesElement.addAttribute("type", "M");
//			nodesElement.addAttribute("description", machine.getName());
//		}
//		for (ProblemDomain pd : project.getContextDiagram()
//				.getProblemDomainList()) {
//			nodeList.add(pd);
//			Element nodesElement = pfElement.addElement("nodes");
//			nodesElement.addAttribute("name", pd.getShortname());
//			String type = "";
//			switch (pd.getType()) {
//			case "Causal":type = "C";
//			case "Lexical":type = "X";
//			case "Biddable":type = "B";
//			case "Clock":	type = "CL";
//			case "Data Storage":	type = "DS";
//			case "Sensor":	type = "SE";
//			case "Actuator":	type = "AC";
//			case "Active Device":	type = "AD";
//			case "Passive Device":	type = "PD";
//			case "Device":	type = "DE";
//			}
//			nodesElement.addAttribute("type", type);
//			nodesElement.addAttribute("description", pd.getName());
//		}
//		for (Requirement req : project.getProblemDiagram()
//				.getRequirementList()) {
//			nodeList.add(req);
//			Element nodesElement = pfElement.addElement("nodes");
//			nodesElement.addAttribute("name", req.getShortname());
//			nodesElement.addAttribute("type", "R");
//			nodesElement.addAttribute("description", req.getName());
//		}
//		for (Interface interfacee : project.getContextDiagram()
//				.getInterfaceList()) {
//			Element linksElement = pfElement.addElement("links");
//			for (int i = 0; i < nodeList.size(); i++) {
//				if (interfacee.getInterface_from()
//						.contentEquals(nodeList.get(i).getShortname())) {
//					linksElement.addAttribute("from", "//@nodes." + i);
//				}
//			}
//			linksElement.addAttribute("type", "--");
//			for (int i = 0; i < nodeList.size(); i++) {
//				if (interfacee.getInterface_to()
//						.contentEquals(nodeList.get(i).getShortname())) {
//					linksElement.addAttribute("to", "//@nodes." + i);
//				}
//			}
//			linksElement.addAttribute("description", interfacee.getInterface_name());
//
//			for (Phenomenon phe : interfacee.getPhenomenonList()) {
//				Element phenomenaElement = linksElement.addElement("phenomena");
//				phenomenaElement.addAttribute("type", phe.getPhenomenon_type());
//				String name = phe.getPhenomenon_name();
//				if (name.contains(","))
//					phenomenaElement.addAttribute("description", phe.getPhenomenon_name());
//				else
//					phenomenaElement.addAttribute("name", phe.getPhenomenon_name());
//			}
//		}
//		for (Reference ref : project.getProblemDiagram().getReferenceList()) {
//			Element linksElement = pfElement.addElement("links");
//			for (int i = 0; i < nodeList.size(); i++) {
//				if (ref.getReference_from().contentEquals(nodeList.get(i).getName())
//						|| ref.getReference_from().contentEquals(
//								nodeList.get(i).getShortname())) {
//					linksElement.addAttribute("from", "//@nodes." + i);
//				}
//			}
//			linksElement.addAttribute("type", "~~");
//			for (int i = 0; i < nodeList.size(); i++) {
//				if (ref.getReference_to().contentEquals(nodeList.get(i).getName())
//						|| ref.getReference_to().contentEquals(
//								nodeList.get(i).getShortname())) {
//					linksElement.addAttribute("to", "//@nodes." + i);
//				}
//			}
//			linksElement.addAttribute("description", ref.getReference_name());
//			for (Phenomenon phe : ref.getPhenomenonList()) {
//				Element phenomenaElement = linksElement.addElement("phenomena");
//				phenomenaElement.addAttribute("type", phe.getPhenomenon_type());
//				String name = phe.getPhenomenon_name();
//				if (name.contains(","))
//					phenomenaElement.addAttribute("description", phe.getPhenomenon_name());
//				else
//					phenomenaElement.addAttribute("name", phe.getPhenomenon_name());
//
//			}
//		}
//		for (Constraint con : project.getProblemDiagram().getConstraintList()) {
//			Element linksElement = pfElement.addElement("links");
//			for (int i = 0; i < nodeList.size(); i++) {
//				if (con.getConstraint_from().contentEquals(nodeList.get(i).getName())
//						|| con.getConstraint_from().contentEquals(
//								nodeList.get(i).getShortname())) {
//					linksElement.addAttribute("from", "//@nodes." + i);
//				}
//			}
//			linksElement.addAttribute("type", "~>");
//			for (int i = 0; i < nodeList.size(); i++) {
//				if (con.getConstraint_to().contentEquals(nodeList.get(i).getName())
//						|| con.getConstraint_to().contentEquals(
//								nodeList.get(i).getShortname())) {
//					linksElement.addAttribute("to", "//@nodes." + i);
//				}
//			}
//			linksElement.addAttribute("description", con.getConstraint_name());
//			for (Phenomenon phe : con.getPhenomenonList()) {
//				Element phenomenaElement = linksElement.addElement("phenomena");
//				phenomenaElement.addAttribute("type", phe.getPhenomenon_type());
//				String name = phe.getPhenomenon_name();
//				if (name.contains(","))
//					phenomenaElement.addAttribute("description", phe.getPhenomenon_name());
//				else
//					phenomenaElement.addAttribute("name", phe.getPhenomenon_name());
//
//			}
//		}
//
//		StringWriter strWtr = new StringWriter();
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		format.setEncoding("ASCII");
//		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
//		try {
//			xmlWriter.write(document);
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
//
//		File xmlFile = new File(rootAddress + projectAddress + ".xml");
//		if (xmlFile.exists() == true) {
//			xmlFile.delete();
//		}
//		try {
//			xmlFile.createNewFile();
//			XMLWriter out = new XMLWriter(new FileWriter(xmlFile));
//			// XMLWriter out = new XMLWriter(new FileWriter(file), format);
//			out.write(document);
//			out.flush();
//			out.close();
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
//
//		return true;
//	}

	/**
	 * @Title: project2pf
	 * @Description: project to pf text 调用该方法前确保project不包含#
	 * @param @param projectAddress
	 * @param @param project
	 * @param @param branch
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static String getCorrectName(String name) {
		if(name == null){
			return null;
		}
		boolean hasNonAlpha = name.matches("^([A-Za-z]|_)([A-Za-z0-9]*_*)*$");
		if (!hasNonAlpha) {
			name = "#" + name + "#";
		}
		return name;
	}

	/**
	 * @Title: ProjectToEMF
	 * @Description: links' from and to are shortname
	 * @param @param fileAddress : emf xml路径
	 * @param @param projectAddress
	 * @param @param project
	 * @param @return 设定文件
	 * @return File 返回类型
	 * @throws
	 */
	public static File ProjectToEMF(String emfFileAddress, Project project) {
		System.out.println("ProjectToEMF-------"+emfFileAddress);
		Document document = DocumentHelper.createDocument();
		new SAXReader();
		Element pfElement = document.addElement("pf:ProblemDiagram");
		pfElement.addAttribute("xmi:version", "2.0");
		pfElement.addAttribute("xmlns:xmi", "http://www.omg.org/XMI");
		pfElement.addAttribute("xmlns:pf", "http://www.Pf.pf");
		pfElement.addAttribute("name", getCorrectName(project.getTitle()));

		List<PdNode> nodeList = new ArrayList<PdNode>();
		ESystem system = project.getIntentDiagram().getSystem();
		if (system != null && system.getShortname() != null) {
			nodeList.add(system);
			Element nodesElement = pfElement.addElement("nodes");
			nodesElement.addAttribute("type", "system");
			nodesElement.addAttribute("name",
					getCorrectName(system.getShortname()));
			nodesElement.addAttribute("description", system.getName());
		}
		Machine machine = project.getContextDiagram().getMachine();
		if (machine != null && machine.getShortname() != null) {
			nodeList.add(machine);
			Element nodesElement = pfElement.addElement("nodes");
			nodesElement.addAttribute("type", "machine");
			nodesElement.addAttribute("name",
					getCorrectName(machine.getShortname()));
			nodesElement.addAttribute("description", machine.getName());
		}
		if(project.getIntentDiagram().getExternalEntityList()!=null) {
			for (ExternalEntity entity : project.getIntentDiagram().getExternalEntityList()) {
				nodeList.add(entity);
				Element nodesElement = pfElement.addElement("nodes");
				nodesElement.addAttribute("type", "extentity");
				nodesElement.addAttribute("name",
						getCorrectName(entity.getShortname()));
				nodesElement.addAttribute("description", entity.getName());
			}
		}
		if(project.getContextDiagram().getProblemDomainList()!=null) {
			for (ProblemDomain pd : project.getContextDiagram().getProblemDomainList()) {
				if (pd == null) continue;
				nodeList.add(pd);
				Element nodesElement = pfElement.addElement("nodes");
				nodesElement.addAttribute("type", "prodomain");
				String type = pd.getType();//prodomaintype
				if (type == null)
					type = "?";
				switch (type) {
					case "Clock":
						type = "clock";
						break;
					case "Data Storage":
						type = "datastorage";
						break;
					case "Sensor":
						type = "sensor";
						break;
					case "Actuator":
						type = "actuator";
						break;
					case "Active Device":
						type = "active";
						break;
					case "Passive Device":
						type = "passive";
						break;
					case "Device":
						type = "device";
						break;
				}
				nodesElement.addAttribute("prodomaintype", type);
				nodesElement.addAttribute("name", getCorrectName(pd.getShortname()));
				nodesElement.addAttribute("description", pd.getName());
			}
		}
		if(project.getIntentDiagram().getIntentList()!=null) {
			for (Intent intent : project.getIntentDiagram().getIntentList()) {
				nodeList.add(intent);
				Element nodesElement = pfElement.addElement("nodes");
				nodesElement.addAttribute("type", "intention");
				nodesElement.addAttribute("name",
						getCorrectName(intent.getShortname()));
				nodesElement.addAttribute("description", intent.getName());
			}
		}
		if(project.getProblemDiagram().getRequirementList()!=null) {
			for (Requirement req : project.getProblemDiagram().getRequirementList()) {
				nodeList.add(req);
				Element nodesElement = pfElement.addElement("nodes");
				nodesElement.addAttribute("type", "requirement");
				nodesElement.addAttribute("name",
						getCorrectName(req.getShortname()));
				nodesElement.addAttribute("description", req.getName());
			}
		}
		HashMap<Integer,Phenomenon> allphe = new HashMap<Integer,Phenomenon>();
		ArrayList<String> existlink = new ArrayList<String>();
		if(project.getIntentDiagram().getInterfaceList()!=null) {
			for (Interface interfacee : project.getIntentDiagram().getInterfaceList()) {
				if (existlink.contains(interfacee.getInterface_name())) {
					continue;
				}
				existlink.add(interfacee.getInterface_name());
				Element linksElement = pfElement.addElement("links");
				linksElement.addAttribute("from",
						getCorrectName(interfacee.getInterface_from()));
				linksElement.addAttribute("type", "--");
				linksElement.addAttribute("to",
						getCorrectName(interfacee.getInterface_to()));
				linksElement.addAttribute("description", interfacee.getInterface_name());
				String phenomena = "";
				System.out.println(interfacee.getInterface_description());
				for (Phenomenon phe : interfacee.getPhenomenonList()) {
					Phenomenon phe1 = new Phenomenon();
					phe1.cope(phe);
					System.out.println(phe.phe2String());
					allphe.put(phe1.getPhenomenon_no(), phe1);
					if (phenomena.equals("")) {
						phenomena += getCorrectName(phe.getPhenomenon_shortname());
					} else {
						phenomena += "; " + getCorrectName(phe.getPhenomenon_shortname());
					}
				}
				System.out.println(phenomena);
				linksElement.addAttribute("phenomena", phenomena);
			}
		}
		if(project.getIntentDiagram().getReferenceList()!=null) {
			for (Reference ref : project.getIntentDiagram().getReferenceList()) {
			if(existlink.contains(ref.getReference_name())){
				continue;
			}
			existlink.add(ref.getReference_name());
			Element linksElement = pfElement.addElement("links");
			for (int i = 0; i < nodeList.size(); i++) {
				if (ref.getReference_from().contentEquals(nodeList.get(i).getName())
						|| ref.getReference_from().contentEquals(nodeList.get(i).getShortname())) {
					linksElement.addAttribute("from",
							getCorrectName(nodeList.get(i).getShortname()));
					break;
				}
			}
			linksElement.addAttribute("type", "~~");
			for (int i = 0; i < nodeList.size(); i++) {
				if (ref.getReference_to().contentEquals(nodeList.get(i).getName())
						|| ref.getReference_to().contentEquals(nodeList.get(i).getShortname())) {
					linksElement.addAttribute("to",
							getCorrectName(nodeList.get(i).getShortname()));
					break;
				}
			}
			System.out.println(ref.getReference_description());
			linksElement.addAttribute("description", ref.getReference_name());
			String phenomena = "";
			for (Phenomenon phe : ref.getPhenomenonList()) {
				Phenomenon phe1 = new Phenomenon();
				phe1.cope(phe);
				System.out.println(phe1.phe2String());
				allphe.put(phe1.getPhenomenon_no(),phe1);
				if(phenomena.equals("")){
					phenomena += getCorrectName(phe.getPhenomenon_shortname());
				}else{
					phenomena += "; " + getCorrectName(phe.getPhenomenon_shortname());
				}
			}
			System.out.println(phenomena);
			linksElement.addAttribute("phenomena",phenomena);
		}
		}
		if(project.getIntentDiagram().getConstraintList()!=null) {
			for (Constraint con : project.getIntentDiagram().getConstraintList()) {
			if(existlink.contains(con.getConstraint_name())){
				continue;
			}
			existlink.add(con.getConstraint_name());
			Element linksElement = pfElement.addElement("links");
			for (int i = 0; i < nodeList.size(); i++) {
				if (con.getConstraint_from().contentEquals(nodeList.get(i).getName())
						|| con.getConstraint_from().contentEquals(nodeList.get(i).getShortname())) {
					linksElement.addAttribute("from",
							getCorrectName(nodeList.get(i).getShortname()));
					break;
				}
			}
			linksElement.addAttribute("type", "~>");
			for (int i = 0; i < nodeList.size(); i++) {
				if (con.getConstraint_to().contentEquals(nodeList.get(i).getName())
						|| con.getConstraint_to().contentEquals(nodeList.get(i).getShortname())) {
					linksElement.addAttribute("to",
							getCorrectName(nodeList.get(i).getShortname()));
					break;
				}
			}
			System.out.println(con.getConstraint_description());
			linksElement.addAttribute("description", con.getConstraint_name());
			String phenomena = "";
			for (Phenomenon phe : con.getPhenomenonList()) {
				Phenomenon phe1 = new Phenomenon();
				phe1.cope(phe);
				System.out.println(phe1.phe2String());
				allphe.put(phe1.getPhenomenon_no(),phe1);
				if(phenomena.equals("")){
					phenomena += getCorrectName(phe.getPhenomenon_shortname());
				}else{
					phenomena += "; " + getCorrectName(phe.getPhenomenon_shortname());
				}
			}
			System.out.println(phenomena);
			linksElement.addAttribute("phenomena",phenomena);
		}
		}
		if(project.getContextDiagram().getInterfaceList()!=null) {
			for (Interface interfacee : project.getContextDiagram().getInterfaceList()) {
			if(existlink.contains(interfacee.getInterface_name())){
				continue;
			}
			Element linksElement = pfElement.addElement("links");
			linksElement.addAttribute("from",
					getCorrectName(interfacee.getInterface_from()));
			linksElement.addAttribute("type", "--");
			linksElement.addAttribute("to",
					getCorrectName(interfacee.getInterface_to()));
			linksElement.addAttribute("description", interfacee.getInterface_name());
			String phenomena = "";
			System.out.println(interfacee.getInterface_description());
			for (Phenomenon phe : interfacee.getPhenomenonList()) {
				Phenomenon phe1 = new Phenomenon();
				phe1.cope(phe);
				System.out.println(phe1.phe2String());
				allphe.put(phe1.getPhenomenon_no(),phe1);
				if(phenomena.equals("")){
					phenomena += getCorrectName(phe.getPhenomenon_shortname());
				}else{
					phenomena += "; " + getCorrectName(phe.getPhenomenon_shortname());
				}
			}
			System.out.println(phenomena);
			linksElement.addAttribute("phenomena",phenomena);
		}
		}
		if(project.getProblemDiagram().getReferenceList()!=null) {
			for (Reference ref : project.getProblemDiagram().getReferenceList()) {
			if(existlink.contains(ref.getReference_name())){
				continue;
			}
			existlink.add(ref.getReference_name());
			Element linksElement = pfElement.addElement("links");
			for (int i = 0; i < nodeList.size(); i++) {
				if (ref.getReference_from().contentEquals(nodeList.get(i).getName())
						|| ref.getReference_from().contentEquals(nodeList.get(i).getShortname())) {
					linksElement.addAttribute("from",
							getCorrectName(nodeList.get(i).getShortname()));
				}
			}
			linksElement.addAttribute("type", "~~");
			for (int i = 0; i < nodeList.size(); i++) {
				if (ref.getReference_to().contentEquals(nodeList.get(i).getName())
						|| ref.getReference_to().contentEquals(nodeList.get(i).getShortname())) {
					linksElement.addAttribute("to",
							getCorrectName(nodeList.get(i).getShortname()));
				}
			}
			System.out.println(ref.getReference_description());
			linksElement.addAttribute("description", ref.getReference_name());
			String phenomena = "";
			for (Phenomenon phe : ref.getPhenomenonList()) {
				Phenomenon phe1 = new Phenomenon();
				phe1.cope(phe);
				System.out.println(phe1.phe2String());
				allphe.put(phe1.getPhenomenon_no(),phe1);
				if(phenomena.equals("")){
					phenomena += getCorrectName(phe.getPhenomenon_shortname());
				}else{
					phenomena += "; " + getCorrectName(phe.getPhenomenon_shortname());
				}
			}
			System.out.println(phenomena);
			linksElement.addAttribute("phenomena",phenomena);
		}
		}
		if(project.getProblemDiagram().getConstraintList()!=null) {
			for (Constraint con : project.getProblemDiagram().getConstraintList()) {
				if (existlink.contains(con.getConstraint_name())) {
					continue;
				}
				existlink.add(con.getConstraint_name());
				Element linksElement = pfElement.addElement("links");
				for (int i = 0; i < nodeList.size(); i++) {
					if (con.getConstraint_from().contentEquals(nodeList.get(i).getName())
							|| con.getConstraint_from().contentEquals(nodeList.get(i).getShortname())) {
						linksElement.addAttribute("from",
								getCorrectName(nodeList.get(i).getShortname()));
					}
				}
				linksElement.addAttribute("type", "~>");
				for (int i = 0; i < nodeList.size(); i++) {
					if (con.getConstraint_to().contentEquals(nodeList.get(i).getName())
							|| con.getConstraint_to().contentEquals(nodeList.get(i).getShortname())) {
						linksElement.addAttribute("to",
								getCorrectName(nodeList.get(i).getShortname()));
					}
				}
				linksElement.addAttribute("description", con.getConstraint_name());
				System.out.println(con.getConstraint_description());
				String phenomena = "";
				for (Phenomenon phe : con.getPhenomenonList()) {
					Phenomenon phe1 = new Phenomenon();
					phe1.cope(phe);
					System.out.println(phe1.phe2String());
					allphe.put(phe1.getPhenomenon_no(), phe1);
					if (phenomena.equals("")) {
						phenomena += getCorrectName(phe.getPhenomenon_shortname());
					} else {
						phenomena += "; " + getCorrectName(phe.getPhenomenon_shortname());
					}
				}
				System.out.println(phenomena);
				linksElement.addAttribute("phenomena", phenomena);
			}
		}
		System.out.println("allphe");
		for(Phenomenon phe : allphe.values()){
			System.out.println(phe.phe2String());
			Element phenomenonsElement = pfElement.addElement("phenomenons");
			phenomenonsElement.addAttribute("type", phe.getPhenomenon_type());
			phenomenonsElement.addAttribute("name", getCorrectName(phe.getPhenomenon_shortname()));
			if(phe.getPhenomenon_from() == null
					|| phe.getPhenomenon_from().equals("")){
				phenomenonsElement.addAttribute("nofrom","*");
			}else{
				phenomenonsElement.addAttribute("from",
						getCorrectName(phe.getPhenomenon_from()));
			}
			if(phe.getPhenomenon_to() == null
					|| phe.getPhenomenon_to().equals("")){
				phenomenonsElement.addAttribute("noto","*");
			}else{
				phenomenonsElement.addAttribute("to",
						getCorrectName(phe.getPhenomenon_to()));
			}
			phenomenonsElement.addAttribute("description", phe.getPhenomenon_name());
		}
		System.out.println("end----allphe");
		StringWriter strWtr = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("ASCII");
		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {

			e.printStackTrace();
		}

		File xmlFile = new File(emfFileAddress);
		if (xmlFile.exists() == true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile), format);
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xmlFile;
	}

	// ==============================pf===========================
	/**
	 * @Title: EMF file -> Pf
	 * @Description: EMF -> PF
	 * @param @param rootAddress EMF目录名，若不在则创建
	 * @param @param projectAddress 无后缀的EMF文件名
	 * @return String pf文本
	 * @throws
	 */
	public static String EMFToPf(String fileAddress) {
		EMF emf = getEMF(fileAddress);
		if (emf == null)
			return null;
		return emf.toString();
	}

	/**
	 * @Title: saveEditorData
	 * @Description: save pf and transform to emf file
	 * 返回格式化的文件(没有缩进，没有空行)
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean pf2emfFile(String pf, String rootAddress,
			String editorPath) {
		// save pf to tempPath

		String tempPath = rootAddress + "temp.pf";
		System.out.println("pf2emfFile："+pf+"  tempPath: "+tempPath);
		FileOperation.writeToFile(pf, rootAddress, tempPath);
		if (ASTService.isSyntaxError(tempPath))
			return false;

		EMFService.pfFile2EmfFile(tempPath, editorPath);
		return true;
	}

	/**
	 * @Title: pfFile2newEmfFile
	 * @Description: TODO
	 * @param @param pfFilename: the absolute path of pf file
	 * @param @param emfFilename: the absolute path of emf file
	 * @param @return 设定文件
	 * @return File 返回EMF文件，文件中link的 from,to 为简称
	 * @throws
	 */
	public static void pfFile2EmfFile(String pfFilename, String emfFilename) {
		pfFile2OldEmfFile(pfFilename, emfFilename);
		changeFromAndToInFile(emfFilename);
		deleteIndent(emfFilename);
	}

	/**
	 * @Title: pfFile2emfFile
	 * @Description: TODO
	 * @param @param pfFilename: the absolute path of pf file
	 * @param @param emfFilename: the absolute path of emf file
	 * @param @return 设定文件
	 * @return File 返回EMF文件，文件中link的 from,to 为//@nodes.?
	 * @throws
	 */
	public static File pfFile2OldEmfFile(String pfFilename, String emfFilename) {
		System.out.println("pfFile2OldEmfFile---"+pfFilename+"\n--------"+emfFilename);
		File file = new File(pfFilename);
		PfStandaloneSetup.doSetup();

		XtextResourceSet resourceSet = new XtextResourceSet();
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL,
				Boolean.TRUE);
		URI uri = URI.createURI(file.getPath());
		Resource xtextResource = resourceSet.getResource(uri, true);

		File xmlFile = new File(emfFilename);
		URI xmiURI = URI.createURI(xmlFile.getPath());
		Resource xmiResource = new XMIResourceFactoryImpl()
				.createResource(xmiURI); // 创建特定的资源
		xmiResource.getContents().add(xtextResource.getContents().get(0));

		try {
			xmiResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("failed to write " + uri);
		}

		return xmlFile;
	}

	/**
	 * @Title: changeFromAndToInFile
	 * @Description: 修改EMF文件中link的 from,to 为简称
	 * @param @param fileAddress 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void changeFromAndToInFile(String fileAddress) {
		// 从EMF文件中读取EMF
		EMF emf = getEMF(fileAddress);
		changeFromTo(emf);
		saveEMF(fileAddress, emf);
	}

	public static void deleteIndent(String fileAddress) {
		System.out.println("deleteIndent-------");
		String str = FileOperation.readFromFile(fileAddress);
		str = str.replace("\n\n", "\n");
		while (str.contains("\n ")) {
			str = str.replace("\n  ", "\n");
			str = str.replace("\n ", "\n");
		}
		FileOperation.writeToFile(str, fileAddress);
	}

	/**
	 * @Title: getEMF from EMF file
	 * @Description: 从EMF文件中读取EMF
	 * @param @param rootAddress
	 * @param @param projectAddress
	 * @param @return 设定文件
	 * @return EMF 返回类型
	 * @throws
	 */
	public static EMF getEMF(String fileAddress) {
		System.out.println("getEMF----"+fileAddress);
		EMF emf = new EMF();
		SAXReader saxReader = new SAXReader();
		try {
			File xmlFile = new File(fileAddress);
			Document document = saxReader.read(xmlFile);
			Element pfElement = document.getRootElement();
			String title = pfElement.attributeValue("name");
			List<PfNode> nodes = getNodes(pfElement);
			List<PfPhenomenon> phenomenons = getPhenomenons(pfElement);
			List<PfLink> links = getLinks(pfElement,phenomenons);
			emf.setTitle(title);
			emf.setNodes(nodes);
			emf.setPhenomenons(phenomenons);
			emf.setLinks(links);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
		return emf;
	}

	private static List<PfNode> getNodes(Element pfElement) {
		// TODO Auto-generated method stub
		List<?> pfNodeElementList = pfElement.elements("nodes");
		List<PfNode> pfNodeList = new ArrayList<PfNode>();
		for (Object object : pfNodeElementList) {
			Element pfNodeElement = (Element) object;
			PfNode pfNode = getNode(pfNodeElement);
			pfNodeList.add(pfNode);
		}
		return pfNodeList;
	}

	private static PfNode getNode(Element pfNodeElement) {
		// TODO Auto-generated method stub
		String shortname = pfNodeElement.attributeValue("name");
		String type = pfNodeElement.attributeValue("type");
		String prodomaintype = pfNodeElement.attributeValue("prodomaintype");
		String name = pfNodeElement.attributeValue("description");
		PfNode pfNode = new PfNode(shortname, type, name, prodomaintype);
		return pfNode;
	}

	private static List<PfLink> getLinks(Element pfElement, List<PfPhenomenon> phenomenons ) {
		// TODO Auto-generated method stub
		List<?> pfLinkElementList = pfElement.elements("links");
		List<PfLink> pfLinkList = new ArrayList<PfLink>();
		for (Object object : pfLinkElementList) {
			Element pfLinkElement = (Element) object;
			PfLink pfLink = getLink(pfLinkElement, phenomenons);
			pfLinkList.add(pfLink);
		}
		return pfLinkList;
	}

	private static PfLink getLink(Element pfLinkElement, List<PfPhenomenon> phenomenons ) {
		// TODO Auto-generated method stub
		String from = pfLinkElement.attributeValue("from");
		String type = pfLinkElement.attributeValue("type");
		String to = pfLinkElement.attributeValue("to");
		List<String> phes = getPhenomena(pfLinkElement,phenomenons);
		String name = pfLinkElement.attributeValue("description");
		System.out.println("getLink: "+from+" "+type+" "+to+" "+name);
		PfLink pfLink = new PfLink(from, type, to, phes, name);
		return pfLink;
	}

	private static List<String> getPhenomena(Element pfLinkElement, List<PfPhenomenon> phenomenons ) {
		// TODO Auto-generated method stub
		String phenomenastring = pfLinkElement.attributeValue("phenomena");
		System.out.println("getPhenomena: "+phenomenastring);
		ArrayList<String> pheList = new ArrayList<String>();
		if(phenomenastring != null
				&& !phenomenastring.contentEquals("") && !phenomenastring.contentEquals(" ")) {
//			String[] phenomenaList = phenomenastring.split("\\s+");
			String[] phenomenaList = phenomenastring.split("; ");
			for (String obj : phenomenaList) {
				System.out.println(obj);
				pheList.add(obj);
//				int pheNo = Integer.parseInt(obj.split("\\.")[1]);
//				PfPhenomenon phe = phenomenons.get(pheNo);
//				pheList.add(phe.getName());
			}
		}
		return pheList;
	}
	private static List<PfPhenomenon> getPhenomenons(Element pfElement) {
		// TODO Auto-generated method stub
		List<?> pfPhenomenonElementList = pfElement.elements("phenomenons");
		List<PfPhenomenon> pfPhenomenonList = new ArrayList<PfPhenomenon>();
		for (Object object : pfPhenomenonElementList) {
			Element pfPhenomenonElement = (Element) object;
			PfPhenomenon pfPhenomenon = getPhenomenon(pfPhenomenonElement);
			pfPhenomenonList.add(pfPhenomenon);
		}
		return pfPhenomenonList;
	}
	private static PfPhenomenon getPhenomenon(Element pfPhenomenonElement) {
		// TODO Auto-generated method stub
		String type = pfPhenomenonElement.attributeValue("type");
		String name = pfPhenomenonElement.attributeValue("name");
		String description = pfPhenomenonElement.attributeValue("description");
		String from = pfPhenomenonElement.attributeValue("from");
		String to = pfPhenomenonElement.attributeValue("to");
		PfPhenomenon pfPhenomenon = new PfPhenomenon(type, name, description, from, to);
		System.out.println("getPhenomenon----: "+pfPhenomenon.toString());
		return pfPhenomenon;
	}

	/**
	 * @Title: changeFromTo
	 * @Description: TODO
	 * @param @param emf
	 * @param @return 设定文件
	 * @return EMF 返回类型
	 * @throws
	 */
	public static EMF changeFromTo(EMF emf) {
		System.out.print("changeFromTo------");
		List<PfNode> pfNodeList = emf.getNodes();
		List<PfLink> pfLinkList = emf.getLinks();
		List<PfPhenomenon> pfPhenomenonList = emf.getPhenomenons();
		for (PfLink pfLink : pfLinkList) {
			String from = pfLink.getFrom();
			String to = pfLink.getTo();
			System.out.println("changeFromTo:"+pfLink.toString());
			int i_from = Integer.parseInt(from.substring(9));
			int i_to = Integer.parseInt(to.substring(9));
			pfLink.setFrom(pfNodeList.get(i_from).getShortname());
			pfLink.setTo(pfNodeList.get(i_to).getShortname());
		}
		for (PfPhenomenon pfPhenomenon : pfPhenomenonList) {
			System.out.println("changeFromTo:"+pfPhenomenon.toString());
			String from = pfPhenomenon.getFrom();
			String to = pfPhenomenon.getTo();
			if(from != null){
				int i_from = Integer.parseInt(from.substring(9));
				pfPhenomenon.setFrom(pfNodeList.get(i_from).getShortname());
			}
			if(to != null){
				int i_to = Integer.parseInt(to.substring(9));
				pfPhenomenon.setTo(pfNodeList.get(i_to).getShortname());
			}
		}
		return emf;
	}

	/**
	 * @Title: saveEMF
	 * @Description: save emf as XML file, delete first line
	 * @param @param fileAddress
	 * @param @param projectAddress
	 * @param @param project
	 * @param @return 设定文件
	 * @return File 返回类型
	 * @throws
	 */
	public static File saveEMF(String fileAddress, EMF emf) {
		System.out.println("saveEMF----"+fileAddress);
		Document document = DocumentHelper.createDocument();
		new SAXReader();
		Element pfElement = document.addElement("pf:ProblemDiagram");
		pfElement.addAttribute("xmi:version", "2.0");
		pfElement.addAttribute("xmlns:xmi", "http://www.omg.org/XMI");
		pfElement.addAttribute("xmlns:pf",
				"http://www.Pf.pf");
		pfElement.addAttribute("name", emf.getTitle());

		for (PfNode node : emf.getNodes()) {
			Element nodesElement = pfElement.addElement("nodes");
			nodesElement.addAttribute("name", node.getShortname());
			nodesElement.addAttribute("type", node.getType());
			if(node.getType().equals("prodomain")){
				nodesElement.addAttribute("prodomaintype",node.getProdomaintype());
			}
			nodesElement.addAttribute("description", node.getName());
		}

		for (PfPhenomenon phenomenon : emf.getPhenomenons()) {
			Element phenomenonsElement = pfElement.addElement("phenomenons");
			phenomenonsElement.addAttribute("type", phenomenon.getType());
			phenomenonsElement.addAttribute("shortname", phenomenon.getName());
			phenomenonsElement.addAttribute("name", phenomenon.getDescription());
			phenomenonsElement.addAttribute("from", phenomenon.getFrom());
			phenomenonsElement.addAttribute("to", phenomenon.getTo());
			phenomenonsElement.addAttribute("description", phenomenon.getDescription());
		}

		for (PfLink link : emf.getLinks()) {
			Element linksElement = pfElement.addElement("links");
			linksElement.addAttribute("from", link.getFrom());
			if (link.getType() != null)
				linksElement.addAttribute("type", link.getType());
			linksElement.addAttribute("to", link.getTo());
			linksElement.addAttribute("description", link.getName());
			String pheList = "";
			for (String phe : link.getPheList()) {
				if(pheList.equals("")){
					pheList += phe;
				}else{
					pheList += "; " + phe;
				}
			}
			linksElement.addAttribute("phenomena",pheList);
		}

		StringWriter strWtr = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("ASCII");
		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File xmlFile = new File(fileAddress);
		if (xmlFile.exists() == true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile), format);
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return xmlFile;
	}

}