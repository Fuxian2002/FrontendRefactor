package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.demo.bean.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pf.PfStandaloneSetup;

@Service
public class ProblemEditor {
	@Autowired
	AddressService addressService;
	
	public File performSave(String filename) {
		File file = new File(addressService.getPfRootAddress() + filename);
		// http://www.eclipse.org/forums/index.php?t=msg&goto=520616&
		PfStandaloneSetup.doSetup();
		XtextResourceSet resourceSet = new XtextResourceSet();

		// http://www.eclipse.org/forums/index.php?t=msg&goto=480679&

		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		URI uri = URI.createURI("file:///" +file.getPath()); // your input textual file

		// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(".pf", new
		// XMIResourceFactoryImpl());

		Resource xtextResource = resourceSet.getResource(uri, true);

		File xmlFile = new File(addressService.getPfRootAddress() + filename.replace(".pf", ".xml"));
		System.out.println(xmlFile.getPath());
		URI xmiURI = URI.createURI("file:///" +xmlFile.getPath());

		Resource xmiResource = new XMIResourceFactoryImpl().createResource(xmiURI);	// 创建特定的资源

		xmiResource.getContents().add(xtextResource.getContents().get(0));

		try {
			xmiResource.save(null);
			System.out.println("performSaved");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("failed to write " + uri);
		}
		return xmlFile;
	}

	public Project transformXML(File xmlFile) {
		System.out.println("transformXML----------");
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
			List<Requirement> requirementList = new ArrayList<Requirement>();
			List<Interface> interfaceList = new ArrayList<Interface>();
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
				String name = nodeElement.attributeValue("name");
				String description = nodeElement.attributeValue("description");
				String type = nodeElement.attributeValue("type");
				String prodomaintype = nodeElement.attributeValue("prodomaintype");
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
				}
				else if (type.equals("intention")) {
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
				}
				else if (type.equals("system")) {
					node.add(name);
//					nodetype.put(name,"system");
					system.setSystem_shortName(name);
					if (description == null) {
						system.setSystem_name(name);
					} else {
						system.setSystem_name(description);
					}
					system.setSystem_h(height);
					system.setSystem_w(width);
					system.setSystem_x(200);
					system.setSystem_y(150);
				}
				else if (type.equals("machine")) {
					node.add(name);
//					nodetype.put(name,"machine");
					machine.setMachine_shortName(name);
					if (description == null) {
						machine.setMachine_name(name);
					} else {
						machine.setMachine_name(description);
					}
					machine.setMachine_h(height);
					machine.setMachine_w(width);
					machine.setMachine_x(200);
					machine.setMachine_y(150);
				}
				else if (type.equals("extentity")) {
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
				}
				else if (type.equals("prodomain")) {
					node.add(name);
//					nodetype.put(name,"prodomain");
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

			int interfaceNum = 0;
			int referenceNum = 0;
			int constraintNum = 0;
			int pheNum = 0;
			List<Phenomenon> pheList = new ArrayList<Phenomenon>();	// 全部现象
			for (Object object : phenomenonElementList){
				pheNum = pheNum + 1;
				Element nodeElement = (Element) object;
				Phenomenon phe = new Phenomenon();
				String type = nodeElement.attributeValue("type");
				String name = nodeElement.attributeValue("name");
				String description = nodeElement.attributeValue("description");
				String nofrom = nodeElement.attributeValue("nofrom");
				String noto = nodeElement.attributeValue("noto");
				phe.setPhenomenon_no(pheNum);
				phe.setPhenomenon_type(type);
				phe.setPhenomenon_shortname(name);
				phe.setPhenomenon_name(description);
				if(nofrom == null){
					int fromNo = Integer.parseInt(nodeElement.attributeValue("from").split("\\.")[1]);
					String from = node.get(fromNo);
					phe.setPhenomenon_from(from);
				}
				if(noto == null){
					int toNo = Integer.parseInt(nodeElement.attributeValue("to").split("\\.")[1]);
					String to = node.get(toNo);
					phe.setPhenomenon_to(to);
				}
				pheList.add(phe);
			}

			String title = pfElement.attributeValue("name");
			IntentDiagram intentDiagram = new IntentDiagram();
			String intentDiagramName = "IntentDiagram";
			intentDiagram.setTitle(intentDiagramName);
			intentDiagram.setSystem(system);
			intentDiagram.setExternalEntityList(externalEntityList);

			ContextDiagram contextDiagram = new ContextDiagram();
			String contextDiagramName = "ContextDiagram";
			contextDiagram.setTitle(contextDiagramName);
			contextDiagram.setMachine(machine);
			contextDiagram.setProblemDomainList(problemDomainList);

			ProblemDiagram problemDiagram = new ProblemDiagram();
			String problemDiagramName = "ProblemDiagram";
			problemDiagram.setTitle(problemDiagramName);
			problemDiagram.setRequirementList(requirementList);
			project.setTitle(title);
			project.setIntentDiagram(intentDiagram);
			project.setContextDiagram(contextDiagram);
			project.setProblemDiagram(problemDiagram);

			for (Object object : linkElementList) {
				Element nodeElement = (Element) object;
				String type = nodeElement.attributeValue("type");
				String name = nodeElement.attributeValue("description");
				if(nodeElement.attributeValue("from")!=null && nodeElement.attributeValue("to")!=null) {
					int fromNo = Integer.parseInt(nodeElement.attributeValue("from").split("\\.")[1]);
					int toNo = Integer.parseInt(nodeElement.attributeValue("to").split("\\.")[1]);
					String from = node.get(fromNo);
					String to = node.get(toNo);
					System.out.println("linkfrom: " + from + " to: " + to);
					boolean isintent = false;
//				if(nodetype.get(from).equals("system") || nodetype.get(to).equals("system")
//				|| nodetype.get(from).equals("intention") || nodetype.get(to).equals("intention")){
//					isintent = true;
//				}
					if (system.getShortname() != null
							&& (from.contentEquals(system.getShortname()) || to.contentEquals(system.getShortname()))) {
						System.out.println("isintent = true;");
						isintent = true;
					} else {
						for (Intent it : intentionList) {
							if (from.contentEquals(it.getShortname()) || to.contentEquals(it.getShortname())) {
								System.out.println("isintent = true;");
								isintent = true;
								break;
							}
						}
					}
					String phenomenas = nodeElement.attributeValue("phenomena");
					List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
					String description = name + ":";
					String pheStr = "";
					if (phenomenas != null) {
						String[] phenomenaList = phenomenas.split("\\s+");
						for (String obj : phenomenaList) {
							System.out.println(obj);
							int pheNo = Integer.parseInt(obj.split("\\.")[1]);
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
								pheStr = pheStr + phenomenon.getPhenomenon_shortname();
							} else {
								pheStr = pheStr + "; " + phenomenon.getPhenomenon_shortname();
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
						if (isintent) {
							interfaceList_i.add(inte);
						} else {
							interfaceList.add(inte);
						}
					} else if (type.equals("~~")) {
						referenceNum = referenceNum + 1;
						if (isintent) {
							for (Intent req : intentionList) {
								if (from.contentEquals(req.getShortname())) {
									from = req.getName();
									break;
								} else if (to.contentEquals(req.getShortname())) {
									to = req.getName();
									break;
								}
							}
						} else {
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
						if (isintent) {
							referenceList_i.add(reference);
						} else {
							referenceList.add(reference);
						}
					} else if (type.equals("<~") || type.equals("~>")) {
						constraintNum = constraintNum + 1;
						if (isintent) {
							for (Intent req : intentionList) {
								if (from.contentEquals(req.getShortname())) {
									from = req.getName();
									break;
								} else if (to.contentEquals(req.getShortname())) {
									to = req.getName();
									break;
								}
							}
						} else {
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
						if (isintent) {
							constraintList_i.add(constraint);
						} else {
							constraintList.add(constraint);
						}
					}
				}
			}
			deleteOverlapInt(interfaceList_i);
			deleteOverlapRef(referenceList_i);
			deleteOverlapCon(constraintList_i);
			deleteOverlapInt(interfaceList);
			deleteOverlapRef(referenceList);
			deleteOverlapCon(constraintList);

			intentDiagram.setIntentList(intentionList);
			intentDiagram.setInterfaceList(interfaceList_i);
			intentDiagram.setReferenceList(referenceList_i);
			intentDiagram.setConstraintList(constraintList_i);

			contextDiagram.setInterfaceList(interfaceList);

			problemDiagram.setReferenceList(referenceList);
			problemDiagram.setConstraintList(constraintList);
			problemDiagram.setContextDiagram(contextDiagram);

//			project.setTitle(title);
			project.setIntentDiagram(intentDiagram);
			project.setContextDiagram(contextDiagram);
			project.setProblemDiagram(problemDiagram);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return project;
	}

	public static void deleteOverlapInt(List<Interface> interfaces) {
		for (int i = 0; i < interfaces.size(); i++) {
			Interface intei = interfaces.get(i);
//			System.out.println("-----------deleteOverlapInt--------------");
//			System.out.println(intei.getInterface_name());
//			System.out.println(intei.getInterface_from());
//			System.out.println(intei.getInterface_to());
			for (int j = interfaces.size() - 1; j > i; j--) {
				Interface intej = interfaces.get(j);
//				System.out.println("to:" + intej.getInterface_to());
				if (intei.getInterface_from().equals(intej.getInterface_to())
						&& intei.getInterface_to().equals(intej.getInterface_from())) {
					String name = "";
					if (intei.getInterface_name().equals(intej.getInterface_name())) {
						name = intei.getInterface_name();
					} else {
						name = intei.getInterface_name() + "," + intej.getInterface_name();
					}
					intei.setInterface_name(name);
					intei.getPhenomenonList().addAll(intej.getPhenomenonList());
					String description = name + ":";
					if (intej.getInterface_description().split(":").length == 1) {
						if (intei.getInterface_description().split(":").length != 1) {
							description = description + intei.getInterface_description().split(":")[1];
						}
					} else {
						if (intei.getInterface_description().split(":").length == 1) {
							description = description + intej.getInterface_description().split(":")[1];
						} else {
							description = description + intei.getInterface_description().split(":")[1] + ","
									+ intej.getInterface_description().split(":")[1];
						}
					}
					intei.setInterface_description(description);
					interfaces.remove(intej);
				}
			}
		}
	}

	public static void deleteOverlapRef(List<Reference> references) {
		for (int i = 0; i < references.size(); i++) {
			Reference referencei = references.get(i);
			for (int j = references.size() - 1; j > i; j--) {
				Reference referencej = references.get(j);
				if (referencei.getReference_from().equals(referencej.getReference_to())
						&& referencei.getReference_to().equals(referencej.getReference_from())) {
					String name = "";
					if (referencei.getReference_name().equals(referencej.getReference_name())) {
						name = referencei.getReference_name();
					} else {
						name = referencei.getReference_name() + "," + referencej.getReference_name();
					}
					referencei.setReference_name(name);
					referencei.getPhenomenonList().addAll(referencej.getPhenomenonList());
					String description = name + ":";
					if (referencej.getReference_description().split(":").length == 1) {
						if (referencei.getReference_description().split(":").length != 1) {
							description = description + referencei.getReference_description().split(":")[1];
						}
					} else {
						if (referencei.getReference_description().split(":").length == 1) {
							description = description + referencej.getReference_description().split(":")[1];
						} else {
							description = description + referencej.getReference_description().split(":")[1] + ","
									+ referencej.getReference_description().split(":")[1];
						}
					}
					referencei.setReference_description(description);
					references.remove(referencej);
				}
			}
		}
	}

	public static void deleteOverlapCon(List<Constraint> constraints) {
//		System.out.println("-----------deleteOverlapCon--------------");
		for (int i = 0; i < constraints.size(); i++) {
			Constraint constrainti = constraints.get(i);
//			System.out.println(constrainti.getConstraint_name());
//			System.out.println(constrainti.getConstraint_from());
//			System.out.println(constrainti.getConstraint_to());
			for (int j = constraints.size() - 1; j > i; j--) {
				Constraint constraintj = constraints.get(j);
				if (constrainti.getConstraint_from().equals(constraintj.getConstraint_to())
						&& constrainti.getConstraint_to().equals(constraintj.getConstraint_from())) {
					String name = "";
					if (constrainti.getConstraint_name().equals(constraintj.getConstraint_name())) {
						name = constrainti.getConstraint_name();
					} else {
						name = constrainti.getConstraint_name() + "," + constraintj.getConstraint_name();
					}
					constrainti.setConstraint_name(name);
					constrainti.getPhenomenonList().addAll(constraintj.getPhenomenonList());
					String description = name + ":";
					if (constraintj.getConstraint_description().split(":").length == 1) {
						if (constrainti.getConstraint_description().split(":").length != 1) {
							description = description + constrainti.getConstraint_description().split(":")[1];
						}
					} else {
						if (constrainti.getConstraint_description().split(":").length == 1) {
							description = description + constraintj.getConstraint_description().split(":")[1];
						} else {
							description = description + constrainti.getConstraint_description().split(":")[1] + ","
									+ constraintj.getConstraint_description().split(":")[1];
						}
					}
					constrainti.setConstraint_description(description);
					constraints.remove(constraintj);
				}
			}
		}
	}
	public static void deleteOverlapEE(List<ExternalEntity> pds) {
		List<ExternalEntity> removepds = new ArrayList<ExternalEntity>();
		for (int i = 0; i < pds.size(); i++) {
			ExternalEntity intei = pds.get(i);
			for (int j = pds.size() - 1; j > i; j--) {
				ExternalEntity intej = pds.get(j);
				// System.out.println("to:" + intej.getInterface_to());
				if (intei.getShortname().equals(intej.getShortname())) {
					removepds.add(intei);
					break;
				}
			}
		}
		pds.removeAll(removepds);
		for (int i = 0; i < pds.size(); i++) {
			ExternalEntity intei = pds.get(i);
			for (int j = pds.size() - 1; j > i; j--) {
				ExternalEntity intej = pds.get(j);
				// System.out.println("to:" + intej.getInterface_to());
				if (intei.getName().equals(intej.getName())) {
					removepds.add(intei);
					break;
				}
			}
		}
		pds.removeAll(removepds);
	}

	public static void deleteOverlapIntent(List<Intent> reqs) {
		List<Intent> removepds = new ArrayList<Intent>();
		for (int i = 0; i < reqs.size(); i++) {
			Intent intei = reqs.get(i);
			for (int j = reqs.size() - 1; j > i; j--) {
				Intent intej = reqs.get(j);
				if (intei.getShortname().equals(intej.getShortname())) {
					removepds.add(intei);
					break;
				}
			}
		}
		reqs.removeAll(removepds);
		for (int i = 0; i < reqs.size(); i++) {
			Intent intei = reqs.get(i);
			for (int j = reqs.size() - 1; j > i; j--) {
				Intent intej = reqs.get(j);
				if (intei.getName().equals(intej.getName())) {
					removepds.add(intei);
					break;
				}
			}
		}
		reqs.removeAll(removepds);
	}
	public static void deleteOverlapPd(List<ProblemDomain> pds) {
		List<ProblemDomain> removepds = new ArrayList<ProblemDomain>();
		for (int i = 0; i < pds.size(); i++) {
			ProblemDomain intei = pds.get(i);
			for (int j = pds.size() - 1; j > i; j--) {
				ProblemDomain intej = pds.get(j);
				// System.out.println("to:" + intej.getInterface_to());
				if (intei.getShortname().equals(intej.getShortname())) {
					removepds.add(intei);
					break;
				}
			}
		}
		pds.removeAll(removepds);
		for (int i = 0; i < pds.size(); i++) {
			ProblemDomain intei = pds.get(i);
			for (int j = pds.size() - 1; j > i; j--) {
				ProblemDomain intej = pds.get(j);
				// System.out.println("to:" + intej.getInterface_to());
				if (intei.getName().equals(intej.getName())) {
					removepds.add(intei);
					break;
				}
			}
		}
		pds.removeAll(removepds);
	}

	public static void deleteOverlapReq(List<Requirement> reqs) {
		List<Requirement> removepds = new ArrayList<Requirement>();
		for (int i = 0; i < reqs.size(); i++) {
			Requirement intei = reqs.get(i);
			for (int j = reqs.size() - 1; j > i; j--) {
				Requirement intej = reqs.get(j);
				if (intei.getShortname().equals(intej.getShortname())) {
					removepds.add(intei);
					break;
				}
			}
		}
		reqs.removeAll(removepds);
		for (int i = 0; i < reqs.size(); i++) {
			Requirement intei = reqs.get(i);
			for (int j = reqs.size() - 1; j > i; j--) {
				Requirement intej = reqs.get(j);
				if (intei.getName().equals(intej.getName())) {
					removepds.add(intei);
					break;
				}
			}
		}
		reqs.removeAll(removepds);
	}
	public static void changeRefPhe(List<Interface> interfaces,
									List<Reference> references) {
		for (Reference ref : references) {
			for (RequirementPhenomenon reqPhe : ref.getPhenomenonList()) {
				changeRefPhe(interfaces, reqPhe);
			}
		}
	}

	public static void changeConPhe(List<Interface> interfaces,
									List<Constraint> cons) {
		for (Constraint ref : cons) {
			for (RequirementPhenomenon reqPhe : ref.getPhenomenonList()) {
				changeRefPhe(interfaces, reqPhe);
			}
		}
	}
	public static void changeRefPhe(List<Interface> interfaces,
									RequirementPhenomenon refPhe) {
		for (Interface inter : interfaces) {
			for (Phenomenon intPhe : inter.getPhenomenonList()) {
				if (intPhe.getPhenomenon_name().contentEquals(refPhe.getPhenomenon_name())) {
					refPhe.setPhenomenon_from(intPhe.getPhenomenon_from());
					refPhe.setPhenomenon_to(intPhe.getPhenomenon_to());
				}
			}
		}
	}
	public static String getCorrectName(String name) {
		boolean hasNonAlpha = name.matches("^([A-Za-z]|_)([A-Za-z0-9]*_*)*$");
		if (!hasNonAlpha) {
			name = "#" + name + "#";
		}
		return name;
	}
	public static String project2pf(Project project) {
		System.out.println("project2pf-------");
		StringBuffer buffer = new StringBuffer();

		buffer.append(
				"problem: " + getCorrectName(project.getTitle()) + "\n");

		// System
		ESystem system = project.getIntentDiagram().getSystem();
		if(system!=null && system.getShortname()!=null ) {
			buffer.append("system ");
			buffer.append(getCorrectName(system.getShortname()));
			if (system.getName() != "")
				buffer.append(" \"" + system.getName() + "\"");
			buffer.append("\n");
		}
		// Machine
		Machine machine = project.getContextDiagram().getMachine();
		if(machine!=null && machine.getMachine_shortName()!=null) {
			buffer.append("machine ");
			buffer.append(getCorrectName(machine.getShortname()));
			if (machine.getName() != "")
				buffer.append(" \"" + machine.getName() + "\"");
			buffer.append("\n");
		}

		// ProblemDomain
		List<ProblemDomain> pd = project.getContextDiagram()
				.getProblemDomainList();
		if(pd!=null) {
			int pdlen = project.getContextDiagram().getProblemDomainList().size();
			for (int i = 0; i < pdlen; i++) {
				buffer.append("prodomain");
				if (pd.get(i).getType() == null) {
					buffer.append(" ? ");
				} else if (pd.get(i).getType().contentEquals("Clock")) {
					buffer.append(" clock ");
				} else if (pd.get(i).getType().contentEquals("Data Storage")) {
					buffer.append(" datastorage ");
				} else if (pd.get(i).getType().contentEquals("Sensor")) {
					buffer.append(" sensor ");
				} else if (pd.get(i).getType().contentEquals("Actuator")) {
					buffer.append(" actuator ");
				} else if (pd.get(i).getType().contentEquals("Active Device")) {
					buffer.append(" active ");
				} else if (pd.get(i).getType().contentEquals("Passive Device")) {
					buffer.append(" passive ");
				} else {
					buffer.append(" " + pd.get(i).getType() + " ");
				}
				buffer.append(getCorrectName(pd.get(i).getShortname()));
				if (pd.get(i).getName() != "")
					buffer.append(" \"" + pd.get(i).getName() + "\"");
				buffer.append("\n");
			}
		}

		// intention
		List<Intent> intent = project.getIntentDiagram().getIntentList();
		if(intent!=null) {
			int intentlen = project.getIntentDiagram().getIntentList().size();
			for (int i = 0; i < intentlen; i++) {
				buffer.append("intention ");
				buffer.append(getCorrectName(intent.get(i).getShortname()));
				if (intent.get(i).getName() != "")
					buffer.append(" \"" + intent.get(i).getName() + "\"");
				buffer.append("\n");
			}
		}
		// externalentity
		List<ExternalEntity> extentity = project.getIntentDiagram().getExternalEntityList();
		if(extentity!=null) {
			int entlen = project.getIntentDiagram().getExternalEntityList().size();
			for (int i = 0; i < entlen; i++) {
				buffer.append("extentity ");
				buffer.append(getCorrectName(extentity.get(i).getShortname()));
				if (extentity.get(i).getName() != "")
					buffer.append(" \"" + extentity.get(i).getName() + "\"");
				buffer.append("\n");
			}
		}
		// requirement
		List<Requirement> req = project.getProblemDiagram()
				.getRequirementList();
		if(req!=null) {
			int reqlen = project.getProblemDiagram().getRequirementList().size();
			for (int i = 0; i < reqlen; i++) {
				buffer.append("requirement ");
				buffer.append(getCorrectName(req.get(i).getShortname()));
				if (req.get(i).getName() != "")
					buffer.append(" \"" + req.get(i).getName() + "\"");
				buffer.append("\n");
			}
		}
		// phenomenon
		HashMap<Integer,Phenomenon> allphe = new HashMap<Integer,Phenomenon>();
		// interface
		List<Interface> intf_i = project.getIntentDiagram().getInterfaceList();
		if(intf_i!=null) {
			int intlen_i = intf_i.size();
			for (int i = 0; i < intlen_i; i++) {
				String int_from = intf_i.get(i).getInterface_from();
				String int_to = intf_i.get(i).getInterface_to();

				List<Phenomenon> pheList = intf_i.get(i).getPhenomenonList();
				String pheList1 = "";
				for (Phenomenon phe : pheList) {
					Phenomenon phe1 = new Phenomenon();
					phe1.cope(phe);
					allphe.put(phe1.getPhenomenon_no(), phe1);
					if (pheList1.equals("")) {
						pheList1 += getCorrectName(phe.getPhenomenon_shortname());
					} else {
						pheList1 += "; " + getCorrectName(phe.getPhenomenon_shortname());
					}
				}
				buffer.append(int_from);
				buffer.append(" -- ");
				buffer.append(int_to);
				if (!pheList1.equals("")) {
					pheList1 = pheList1.substring(0, pheList1.length() - 1);
					buffer.append(" {");
					buffer.append(pheList1);
					buffer.append(" }");
				}
				buffer.append(" \"" + intf_i.get(i).getName() + "\"");
				buffer.append("\n");
			}
		}
		// reference
		List<Reference> ref_i = project.getIntentDiagram().getReferenceList();
		if(ref_i!=null) {
			int reflen_i = ref_i.size();
			for (int i = 0; i < reflen_i; i++) {
				String ref_from = getShortname2(project,
						ref_i.get(i).getReference_from());
				String ref_to = getShortname2(project,
						ref_i.get(i).getReference_to());
				List<RequirementPhenomenon> pheList = ref_i.get(i).getPhenomenonList();
				String pheList1 = "";
				for (RequirementPhenomenon phe : pheList) {
					Phenomenon phe1 = new Phenomenon();
					phe1.cope(phe);
					allphe.put(phe1.getPhenomenon_no(), phe1);
					if (pheList1.equals("")) {
						pheList1 += getCorrectName(phe.getPhenomenon_shortname());
					} else {
						pheList1 += "; " + getCorrectName(phe.getPhenomenon_shortname());
					}
				}
				buffer.append(getCorrectName(ref_from));
				buffer.append(" ~~ ");
				buffer.append(getCorrectName(ref_to));
				if (!pheList1.equals("")) {
					pheList1 = pheList1.substring(0, pheList1.length() - 1);
					buffer.append(" {");
					buffer.append(pheList1);
					buffer.append(" }");
				}
				buffer.append(" \"" + ref_i.get(i).getName() + "\"");
				buffer.append("\n");
			}
		}
		// constraint
		List<Constraint> con_i = project.getIntentDiagram().getConstraintList();
		if(con_i!=null) {
			int conlen_i = con_i.size();
			for (int i = 0; i < conlen_i; i++) {
				String con_from = getShortname2(project,
						con_i.get(i).getConstraint_from());
				String con_to = getShortname2(project,
						con_i.get(i).getConstraint_to());
				List<RequirementPhenomenon> pheList = con_i.get(i).getPhenomenonList();
				String pheList1 = "";
				for (RequirementPhenomenon phe : pheList) {
					Phenomenon phe1 = new Phenomenon();
					phe1.cope(phe);
					allphe.put(phe1.getPhenomenon_no(), phe1);
					if (pheList1.equals("")) {
						pheList1 += getCorrectName(phe.getPhenomenon_shortname());
					} else {
						pheList1 += "; " + getCorrectName(phe.getPhenomenon_shortname());
					}
				}
				buffer.append(getCorrectName(con_from));
				buffer.append(" ~> ");
				buffer.append(getCorrectName(con_to));
				if (!pheList1.equals("")) {
					pheList1 = pheList1.substring(0, pheList1.length() - 1);
					buffer.append(" {");
					buffer.append(pheList1);
					buffer.append(" }");
				}
				buffer.append(" \"" + con_i.get(i).getName() + "\"");
				buffer.append("\n");
			}
		}
		// interface
		List<Interface> intf = project.getContextDiagram().getInterfaceList();
		if(intf!=null) {
			int intlen = intf.size();
			for (int i = 0; i < intlen; i++) {
				String int_from = intf.get(i).getInterface_from();
				String int_to = intf.get(i).getInterface_to();

				List<Phenomenon> pheList = intf.get(i).getPhenomenonList();
				String pheList1 = "";
				for (Phenomenon phe : pheList) {
					Phenomenon phe1 = new Phenomenon();
					phe1.cope(phe);
					allphe.put(phe1.getPhenomenon_no(), phe1);
					if (pheList1.equals("")) {
						pheList1 += getCorrectName(phe.getPhenomenon_shortname());
					} else {
						pheList1 += "; " + getCorrectName(phe.getPhenomenon_shortname());
					}
				}

				buffer.append(int_from);
				buffer.append(" -- ");
				buffer.append(int_to);
				if (!pheList1.equals("")) {
					pheList1 = pheList1.substring(0, pheList1.length() - 1);
					buffer.append(" {");
					buffer.append(pheList1);
					buffer.append(" }");
				}
				buffer.append(" \"" + intf.get(i).getName() + "\"");
				buffer.append("\n");
			}
		}
		// reference
		List<Reference> ref = project.getProblemDiagram().getReferenceList();
		if(ref!=null) {
			int reflen = ref.size();
			for (int i = 0; i < reflen; i++) {
				String ref_from = getShortname(project,
						ref.get(i).getReference_from());
				String ref_to = getShortname(project,
						ref.get(i).getReference_to());
				List<RequirementPhenomenon> pheList = ref.get(i).getPhenomenonList();
				String pheList1 = "";
				for (RequirementPhenomenon phe : pheList) {
					Phenomenon phe1 = new Phenomenon();
					phe1.cope(phe);
					allphe.put(phe1.getPhenomenon_no(), phe1);
					if (pheList1.equals("")) {
						pheList1 += getCorrectName(phe.getPhenomenon_shortname());
					} else {
						pheList1 += "; " + getCorrectName(phe.getPhenomenon_shortname());
					}
				}
				buffer.append(getCorrectName(ref_from));
				buffer.append(" ~~ ");
				buffer.append(getCorrectName(ref_to));
				if (!pheList1.equals("")) {
					pheList1 = pheList1.substring(0, pheList1.length() - 1);
					buffer.append(" {");
					buffer.append(pheList1);
					buffer.append(" }");
				}
				buffer.append(" \"" + ref.get(i).getName() + "\"");
				buffer.append("\n");

			}
		}
		// constraint
		List<Constraint> con = project.getProblemDiagram().getConstraintList();
		if(con!=null) {
			int conlen = con.size();
			for (int i = 0; i < conlen; i++) {
				String con_from = getShortname(project,
						con.get(i).getConstraint_from());
				String con_to = getShortname(project,
						con.get(i).getConstraint_to());
				List<RequirementPhenomenon> pheList = con.get(i).getPhenomenonList();
				String pheList1 = "";
				for (RequirementPhenomenon phe : pheList) {
					Phenomenon phe1 = new Phenomenon();
					phe1.cope(phe);
					allphe.put(phe1.getPhenomenon_no(), phe1);
					if (pheList1.equals("")) {
						pheList1 += getCorrectName(phe.getPhenomenon_shortname());
					} else {
						pheList1 += "; " + getCorrectName(phe.getPhenomenon_shortname());
					}

				}
				buffer.append(getCorrectName(con_from));
				buffer.append(" ~> ");
				buffer.append(getCorrectName(con_to));
				if (!pheList1.equals("")) {
					pheList1 = pheList1.substring(0, pheList1.length() - 1);
					buffer.append(" {");
					buffer.append(pheList1);
					buffer.append(" }");
				}
				buffer.append("\"" + con.get(i).getName() + "\"");
				buffer.append("\n");
			}
		}
		for(Phenomenon phe : allphe.values()){
			buffer.append("phenomenon ");
			buffer.append(phe.getPhenomenon_type()+" ");
			buffer.append(getCorrectName(phe.getPhenomenon_shortname()) +" ");
			if(phe.getPhenomenon_from() == null || phe.getPhenomenon_from().equals("")){
				buffer.append("* ");
			}else {
				buffer.append(getCorrectName(phe.getPhenomenon_from()) +" ");
			}
			if(phe.getPhenomenon_to() == null || phe.getPhenomenon_to().equals("")){
				buffer.append("* ");
			}else {
				buffer.append(getCorrectName(phe.getPhenomenon_to()) +" ");
			}
			buffer.append("\"" + phe.getPhenomenon_name() + "\"");
			buffer.append("\n");
		}
		return buffer.toString();
	}
	public static String getShortname(Project project, String name) {
		List<Requirement> req = project.getProblemDiagram()
				.getRequirementList();
		int reqlen = req.size();
		for (int i = 0; i < reqlen; i++) {
			if (req.get(i).getName().contentEquals(name)) {
				return req.get(i).getShortname();
			}
		}
		// System.out.println("getShortname---------"+name+"-----");
		return name;
	}
	public static String getShortname2(Project project, String name) {
		List<Intent> req_i = project.getIntentDiagram().getIntentList();
		int reqlen_i = req_i.size();
		for (int i = 0; i < reqlen_i; i++) {
			if (req_i.get(i).getName().contentEquals(name)) {
				return req_i.get(i).getShortname();
			}
		}
		// System.out.println("getShortname---------"+name+"-----");
		return name;
	}
}
