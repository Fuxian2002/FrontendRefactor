package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.springframework.stereotype.Service;

import com.example.demo.bean.Constraint;
import com.example.demo.bean.ContextDiagram;
import com.example.demo.bean.Interface;
import com.example.demo.bean.Machine;
import com.example.demo.bean.Phenomenon;
import com.example.demo.bean.ProblemDiagram;
import com.example.demo.bean.ProblemDomain;
import com.example.demo.bean.Project;
import com.example.demo.bean.Reference;
import com.example.demo.bean.Requirement;
import com.example.demo.bean.RequirementPhenomenon;

import pf.PfStandaloneSetup;;

@Service
public class ProblemEditor {
	private static String pfRootAddress = AddressService.pfRootAddress;

	public static File performSave(String filename) {
		File file = new File(pfRootAddress + filename);
		// http://www.eclipse.org/forums/index.php?t=msg&goto=520616&
		PfStandaloneSetup.doSetup();
		XtextResourceSet resourceSet = new XtextResourceSet();

		// http://www.eclipse.org/forums/index.php?t=msg&goto=480679&

		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		URI uri = URI.createURI(file.getPath()); // your input textual file

		// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(".pf", new
		// XMIResourceFactoryImpl());

		Resource xtextResource = resourceSet.getResource(uri, true);

		File xmlFile = new File(pfRootAddress + filename.replace(".pf", ".xml"));
		System.out.println(xmlFile.getPath());
		URI xmiURI = URI.createURI(xmlFile.getPath());

		Resource xmiResource = new XMIResourceFactoryImpl().createResource(xmiURI);	// 创建特定的资源

		xmiResource.getContents().add(xtextResource.getContents().get(0));

		try {
			xmiResource.save(null);
			System.out.println("saved");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("failed to write " + uri);
		}
		return xmlFile;
	}

	public static Project transformXML(File xmlFile) {
		Project project = new Project();
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(xmlFile);
			Element pfElement = document.getRootElement();
			List<?> nodeElementList = pfElement.elements("nodes");
			List<?> linkElementList = pfElement.elements("links");

			Machine machine = new Machine();
			List<ProblemDomain> problemDomainList = new ArrayList<ProblemDomain>();
			List<Interface> interfaceList = new ArrayList<Interface>();
			List<Requirement> requirementList = new ArrayList<Requirement>();
			List<Reference> referenceList = new ArrayList<Reference>();
			List<Constraint> constraintList = new ArrayList<Constraint>();
			List<String> node = new ArrayList<String>();

			int reqnum = 0;
			int domainnum = 0;
			for (Object object : nodeElementList) {
				Element nodeElement = (Element) object;
				String name = nodeElement.attributeValue("name");
				String description = nodeElement.attributeValue("description");
				String type = nodeElement.attributeValue("type");
				int height = 50;
				int width = 100;

				if (type == null) {
					reqnum = reqnum + 1;
					Requirement requirement = new Requirement();
					requirement.setRequirement_no(reqnum);
					if (description == null) {
						requirement.setRequirement_context(name);
						node.add(name);
					} else {
						requirement.setRequirement_context(description);
						node.add(description);
					}
					requirement.setRequirement_h(height);
					requirement.setRequirement_w(width);
					requirement.setRequirement_x(900);
					requirement.setRequirement_y(50 + reqnum * 100);
					requirementList.add(requirement);
				} else if (type.equals("R")) {
					reqnum = reqnum + 1;
					Requirement requirement = new Requirement();
					requirement.setRequirement_no(reqnum);
					if (description == null) {
						requirement.setRequirement_context(name);
						node.add(name);
					} else {
						requirement.setRequirement_context(description);
						node.add(description);
					}
					requirement.setRequirement_h(height);
					requirement.setRequirement_w(width);
					requirement.setRequirement_x(900);
					requirement.setRequirement_y(50 + reqnum * 100);
					requirementList.add(requirement);
					// node.add(description);
				} else if (type.equals("M")) {
					node.add(name);
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
				} else {
					node.add(name);
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
					if (type.equals("B")) {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("Biddable");
					} else if (type.equals("C")) {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("Causal");
					} else if (type.equals("X")) {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_type("Lexical");
					} else if (type.equals("D")) {
						problemDomain.setProblemdomain_property("DesignDomain");
					} else {
						problemDomain.setProblemdomain_property("GivenDomain");
						problemDomain.setProblemdomain_property("");
					}
					problemDomainList.add(problemDomain);
				}
			}

			int interfaceNum = 0;
			int referenceNum = 0;
			int constraintNum = 0;
			int pheNum = 0;
			List<Phenomenon> pheList = new ArrayList<Phenomenon>();	// 全部现象
			for (Object object : linkElementList) {
				Element nodeElement = (Element) object;
				String type = nodeElement.attributeValue("type");
				String name = nodeElement.attributeValue("description");
				int fromNo = Integer.parseInt(nodeElement.attributeValue("from").split("\\.")[1]);
				int toNo = Integer.parseInt(nodeElement.attributeValue("to").split("\\.")[1]);
				String from = node.get(fromNo);
				String to = node.get(toNo);

				List<?> pheElementList = nodeElement.elements("phenomena");
				List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();

				for (Object obj : pheElementList) {
					Element pheElement = (Element) obj;
					String pheName = pheElement.attributeValue("name");
					String pheType = pheElement.attributeValue("type");

					boolean exit = false;
					for (Phenomenon phe : pheList) {
						if (phe.getPhenomenon_name().equals(pheName) && phe.getPhenomenon_type().equals(pheType)) {
							exit = true;
							break;
						}
					}
					if (!exit) {
						pheNum = pheNum + 1;
					}
					Phenomenon phenomenon = new Phenomenon();
					phenomenon.setPhenomenon_name(pheName);
					phenomenon.setPhenomenon_type(pheType);
					phenomenon.setPhenomenon_no(pheNum);
					phenomenon.setPhenomenon_from(from);
					phenomenon.setPhenomenon_to(to);
					phenomenonList.add(phenomenon);
					pheList.add(phenomenon);
				}
				String description = name + ":";
				String pheStr = "";
				for (Phenomenon phenomenon : phenomenonList) {
					if (pheStr.equals("")) {
						pheStr = pheStr + phenomenon.getPhenomenon_name();
					} else {
						pheStr = pheStr + ", " + phenomenon.getPhenomenon_name();
					}
				}
				if (!pheStr.equals("")) {
					pheStr = from + "!" + "{" + pheStr + "}";
				}
				description = description + pheStr;
				if (type == null) {
					interfaceNum = interfaceNum + 1;
					Interface inte = new Interface();
					inte.setInterface_no(interfaceNum);
					inte.setInterface_name(name);
					inte.setInterface_from(from);
					inte.setInterface_to(to);
					inte.setPhenomenonList(phenomenonList);
					inte.setInterface_description(description);
					interfaceList.add(inte);
				} else if (type.equals("~~")) {
					referenceNum = referenceNum + 1;
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
					reference.setPhenomenonList(reqPheList);
					reference.setReference_description(description);
					referenceList.add(reference);
				} else if (type.equals("<~") || type.equals("~>")) {
					constraintNum = constraintNum + 1;
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
						reqPhe.setPhenomenon_type(phe.getPhenomenon_type());
						reqPhe.setPhenomenon_from(phe.getPhenomenon_from());
						reqPhe.setPhenomenon_to(phe.getPhenomenon_to());
						reqPhe.setPhenomenon_constraint("true");
						for (Requirement req : requirementList) {
							System.out.println(req.getRequirement_context());
							System.out.println(from);
							System.out.println(to);
							if (req.getRequirement_context().equals(from) || req.getRequirement_context().equals(to)) {
								reqPhe.setPhenomenon_requirement(req.getRequirement_no());
							}
						}
						reqPheList.add(reqPhe);
					}
					constraint.setPhenomenonList(reqPheList);
					constraint.setConstraint_description(description);
					constraintList.add(constraint);
				}
			}
			deleteOverlapInt(interfaceList);
			deleteOverlapRef(referenceList);
			deleteOverlapCon(constraintList);

			String title = pfElement.attributeValue("name");
			ContextDiagram contextDiagram = new ContextDiagram();
			String contextDiagramName = "ContextDiagram";
			contextDiagram.setTitle(contextDiagramName);
			contextDiagram.setMachine(machine);
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
			System.out.println("-------------------------");
			System.out.println(intei.getInterface_name());
			System.out.println(intei.getInterface_from());
			for (int j = interfaces.size() - 1; j > i; j--) {
				Interface intej = interfaces.get(j);
				System.out.println("to:" + intej.getInterface_to());
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
		for (int i = 0; i < constraints.size(); i++) {
			Constraint constrainti = constraints.get(i);
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

}
