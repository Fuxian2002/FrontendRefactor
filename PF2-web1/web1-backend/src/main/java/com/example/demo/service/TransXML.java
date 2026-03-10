package com.example.demo.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.example.demo.bean.Cell;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransXML {
	@Autowired
	AddressService addressService;
	
	//从xml文件中读取Project
	public  Project getProject(String projectAddress) {
		Project project = new Project();
		SAXReader saxReader = new SAXReader();
		try {
			File xmlFile = new File(addressService.getRootAddress() + projectAddress + "/Project.xml");
			Document document = saxReader.read(xmlFile);
			
			Element projectElement = document.getRootElement();
			Element titleElement = projectElement.element("title");
			Element fileListElement = projectElement.element("fileList");
			Element contextDiagramElement = fileListElement.element("ContextDiagram");
			Element problemDiagramElement = fileListElement.element("ProblemDiagram");
			
			String title = titleElement.getText();
			String contextDiagramName = contextDiagramElement.getText();
			String problemDiagramName = problemDiagramElement.getText();
			ContextDiagram contextDiagram = getContextDiagram(projectAddress,contextDiagramName);
			ProblemDiagram problemDiagram = getProblemDiagram(projectAddress,problemDiagramName);
								
			project.setTitle(title);
			project.setContextDiagram(contextDiagram);
			project.setProblemDiagram(problemDiagram);	
			
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return project;
	}
	
	private ContextDiagram getContextDiagram(String projectAddress,String contextDiagramName) {
		ContextDiagram contextDiagram = new ContextDiagram();
		SAXReader saxReader = new SAXReader();
		try {
			File contextDiagramFile = new File(addressService.getRootAddress() + projectAddress + "/" + contextDiagramName + ".xml");
			if(!contextDiagramFile.exists()) {
				return null;
			}
			Document document = saxReader.read(contextDiagramFile);
			
			Element contextDiagramElement = document.getRootElement();
			Element titleElement = contextDiagramElement.element("title");
			Element machineElement = contextDiagramElement.element("Machine");
			Element problemDomainListElement = contextDiagramElement.element("ProblemDomain");
			Element interfaceListElement = contextDiagramElement.element("Interface");
					
			String title = titleElement.getText();
			Machine machine = getMachine(machineElement);
			List<ProblemDomain> problemDomainList= getProblemDomainList(problemDomainListElement);
			List<Interface> interfaceList = getInterfaceList(interfaceListElement);
			
			contextDiagram.setTitle(title);
			contextDiagram.setMachine(machine);
			contextDiagram.setProblemDomainList(problemDomainList);
			contextDiagram.setInterfaceList(interfaceList);
		}catch (DocumentException e) {
			e.printStackTrace();
		}
		return contextDiagram;
	}

	private ProblemDiagram getProblemDiagram(String projectAddress,String problemDiagramName) {
		ProblemDiagram problemDiagram = new ProblemDiagram();
		SAXReader saxReader = new SAXReader();
		try {
			File problemDiagramFile = new File(addressService.getRootAddress() + projectAddress + "/" + problemDiagramName + ".xml");
			if(!problemDiagramFile.exists()) {
				return null;
			}
			Document document = saxReader.read(problemDiagramFile);
			
			Element problemDiagramElement = document.getRootElement();
			Element titleElement = problemDiagramElement.element("title");
			Element contextDiagramElement = problemDiagramElement.element("ContextDiagram");
			Element requirementListElement = problemDiagramElement.element("Requirement");
			Element constraintListElement = problemDiagramElement.element("Constraint");
			Element referenceListElement = problemDiagramElement.element("Reference");
			
			String title = titleElement.getText();
			String contextDiagramName = contextDiagramElement.getText();
			ContextDiagram contextDiagram = getContextDiagram(projectAddress,contextDiagramName);
			List<Requirement> requirementList = getRequirementList(requirementListElement);
			List<Constraint> constraintList = getConstraintList(constraintListElement);
			List<Reference> referenceList = getReferenceList(referenceListElement);
			
			problemDiagram.setTitle(title);
			problemDiagram.setContextDiagram(contextDiagram);
			problemDiagram.setRequirementList(requirementList);
			problemDiagram.setConstraintList(constraintList);
			problemDiagram.setReferenceList(referenceList);
		}catch (DocumentException e) {
			e.printStackTrace();
		}
		return problemDiagram;
	}
	
	private Machine getMachine(Element machineElement) {
		Machine machine = new Machine();
		
		String machine_name = machineElement.attributeValue("machine_name");
		String machine_shortName = machineElement.attributeValue("machine_shortname");
		String machine_locality = machineElement.attributeValue("machine_locality");
		String[] locality= machine_locality.split(",");
		int machine_x = Integer.parseInt(locality[0]);
		int machine_y = Integer.parseInt(locality[1]);
		int machine_h = Integer.parseInt(locality[2]);
		int machine_w = Integer.parseInt(locality[3]);
		
		machine.setMachine_name(machine_name);
		machine.setMachine_shortName(machine_shortName);
		machine.setMachine_h(machine_h);
		machine.setMachine_w(machine_w);
		machine.setMachine_x(machine_x);
		machine.setMachine_y(machine_y);
		
		return machine;
	}

	private List<ProblemDomain> getProblemDomainList(Element problemDomainListElement) {
		List<ProblemDomain> problemDomainList = new ArrayList<ProblemDomain> ();
		
		Element givenDomainListElement = problemDomainListElement.element("GivenDomain");
		Element designDomainListElement = problemDomainListElement.element("DesignDomain");
		List<?> givenDomainElementList = givenDomainListElement.elements("Element");
		List<?> designDomainElementList = designDomainListElement.elements("Element ");
		
		for(Object object : givenDomainElementList) {
			ProblemDomain problemDomain = new ProblemDomain();
			Element givenDomainElement = (Element) object;
			
			int problemdomain_no = Integer.parseInt(givenDomainElement.attributeValue("problemdomain_no"));
			String problemdomain_name = givenDomainElement.attributeValue("problemdomain_name");
			String problemdomain_shortname = givenDomainElement.attributeValue("problemdomain_shortname");
			String problemdomain_type = givenDomainElement.attributeValue("problemdomain_type");
			String problemdomain_property = "GivenDomain";
			String problemdomain_locality = givenDomainElement.attributeValue("problemdomain_locality");
			String[] locality= problemdomain_locality.split(",");
			int problemdomain_x = Integer.parseInt(locality[0]);
			int problemdomain_y = Integer.parseInt(locality[1]);
			int problemdomain_h = Integer.parseInt(locality[2]);
			int problemdomain_w = Integer.parseInt(locality[3]);
			
			problemDomain.setProblemdomain_no(problemdomain_no);
			problemDomain.setProblemdomain_name(problemdomain_name);
			problemDomain.setProblemdomain_shortname(problemdomain_shortname);
			problemDomain.setProblemdomain_type(problemdomain_type);
			problemDomain.setProblemdomain_property(problemdomain_property);
			problemDomain.setProblemdomain_x(problemdomain_x);
			problemDomain.setProblemdomain_y(problemdomain_y);
			problemDomain.setProblemdomain_h(problemdomain_h);
			problemDomain.setProblemdomain_w(problemdomain_w);
			
			problemDomainList.add(problemDomain);
		}
		for(Object object : designDomainElementList) {
			Element designDomainElement = (Element) object;
			
			int problemdomain_no = Integer.parseInt(designDomainElement.attributeValue("problemdomain_no"));
			String problemdomain_name = designDomainElement.attributeValue("problemdomain_name");
			String problemdomain_shortname = designDomainElement.attributeValue("problemdomain_shortname");
			String problemdomain_type = designDomainElement.attributeValue("problemdomain_type");
			String problemdomain_property = "DesignDomain";
			String problemdomain_locality = designDomainElement.attributeValue("problemdomain_locality");
			String[] locality= problemdomain_locality.split(",");
			int problemdomain_x = Integer.parseInt(locality[0]);
			int problemdomain_y = Integer.parseInt(locality[1]);
			int problemdomain_h = Integer.parseInt(locality[2]);
			int problemdomain_w = Integer.parseInt(locality[3]);
			
			ProblemDomain problemDomain = new ProblemDomain();
			problemDomain.setProblemdomain_no(problemdomain_no);
			problemDomain.setProblemdomain_name(problemdomain_name);
			problemDomain.setProblemdomain_shortname(problemdomain_shortname);
			problemDomain.setProblemdomain_type(problemdomain_type);
			problemDomain.setProblemdomain_property(problemdomain_property);
			problemDomain.setProblemdomain_x(problemdomain_x);
			problemDomain.setProblemdomain_y(problemdomain_y);
			problemDomain.setProblemdomain_h(problemdomain_h);
			problemDomain.setProblemdomain_w(problemdomain_w);
			
			problemDomainList.add(problemDomain);
		}
		
		return problemDomainList;
	}

	private List<Interface> getInterfaceList(Element interfaceListElement) {
		List<Interface> interfaceList = new ArrayList<Interface>();
		List<?> interfaceElementList = interfaceListElement.elements("Element");
		
		for(Object object : interfaceElementList) {
			Element interfaceElement = (Element) object;
			List<?> phenomenonElementList = interfaceElement.elements("Phenomenon");
			
			int interface_no = Integer.parseInt(interfaceElement.attributeValue("interface_no"));
			String interface_name = interfaceElement.attributeValue("interface_name");
			String interface_description = interfaceElement.attributeValue("interface_description");
			String interface_from = interfaceElement.attributeValue("interface_from");
			String interface_to = interfaceElement.attributeValue("interface_to");
			String interface_locality = interfaceElement.attributeValue("interface_locality");
			List<Phenomenon> phenomenonList = getPhenomenonList(phenomenonElementList);
			String[] locality= interface_locality.split(",");
			int interface_x1 = Integer.parseInt(locality[0]);
			int interface_x2 = Integer.parseInt(locality[1]);
			int interface_y1 = Integer.parseInt(locality[2]);
			int interface_y2 = Integer.parseInt(locality[3]);
			
			Interface inte = new Interface();
			inte.setInterface_no(interface_no);
			inte.setInterface_name(interface_name);
			inte.setInterface_description(interface_description);
			inte.setInterface_from(interface_from);
			inte.setInterface_to(interface_to);
			inte.setPhenomenonList(phenomenonList);
			inte.setInterface_x1(interface_x1);
			inte.setInterface_y1(interface_y1);
			inte.setInterface_x2(interface_x2);
			inte.setInterface_y2(interface_y2);
			
			interfaceList.add(inte);
		}
		
		return interfaceList;
	}
	
	private List<Phenomenon> getPhenomenonList(List<?> phenomenonElementList) {
		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon> ();
		for(Object object : phenomenonElementList) {
			Element phenomenonElement = (Element)object;
			
			int phenomenon_no = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_no"));
			String phenomenon_name = phenomenonElement.attributeValue("phenomenon_name");
			String phenomentn_type = phenomenonElement.attributeValue("phenomenon_type");
			String phenomenon_from = phenomenonElement.attributeValue("phenomenon_from");
			String phenomenon_to = phenomenonElement.attributeValue("phenomenon_to");
			
			Phenomenon phenomenon = new Phenomenon();
			phenomenon.setPhenomenon_no(phenomenon_no);
			phenomenon.setPhenomenon_name(phenomenon_name);
			phenomenon.setPhenomenon_type(phenomentn_type);
			phenomenon.setPhenomenon_from(phenomenon_from);
			phenomenon.setPhenomenon_to(phenomenon_to);
			
			phenomenonList.add(phenomenon);
		}
		return phenomenonList;
	}

	private List<Requirement> getRequirementList(Element requirementListElement) {
		List<?> requirementElementList = requirementListElement.elements("Element");
		List<Requirement> requirementList = new ArrayList<Requirement>();
		for(Object object : requirementElementList) {
			Element requirementElement = (Element)object;
			
			Requirement requirement = getRequirement(requirementElement);			
			
			requirementList.add(requirement);
		}
		return requirementList;
	}

	
	private Requirement getRequirement(Element requirementElement) {
		Requirement requirement = new Requirement();
		
		int requirement_no = Integer.parseInt(requirementElement.attributeValue("requirement_no"));
		String requirement_context = requirementElement.attributeValue("requirement_context");
		String requirement_locality = requirementElement.attributeValue("requirement_locality");
		String[] locality= requirement_locality.split(",");
		int requirement_x = Integer.parseInt(locality[0]);
		int requirement_y = Integer.parseInt(locality[1]);
		int requirement_h = Integer.parseInt(locality[2]);
		int requirement_w = Integer.parseInt(locality[3]);
		
		requirement.setRequirement_no(requirement_no);
		requirement.setRequirement_context(requirement_context);
		requirement.setRequirement_x(requirement_x);
		requirement.setRequirement_y(requirement_y);
		requirement.setRequirement_h(requirement_h);
		requirement.setRequirement_w(requirement_w);
		
		return requirement;
	}

	private List<Constraint> getConstraintList(Element constraintListElement) {
		List<Constraint> constraintList = new ArrayList<Constraint>();
		List<?> constraintElementList = constraintListElement.elements("Element");
		for(Object object : constraintElementList) {
			Element constraintElement = (Element)object;
			List<?> phenomenonElementList = constraintElement.elements("Phenomenon");
			
			int constraint_no = Integer.parseInt(constraintElement.attributeValue("constraint_no"));
			String constraint_name = constraintElement.attributeValue("constraint_name");
			String constraint_description = constraintElement.attributeValue("constraint_description");
			String constraint_from = constraintElement.attributeValue("constraint_from");
			String constraint_to = constraintElement.attributeValue("constraint_to");
			List<RequirementPhenomenon> phenomenonList = getRequirementPhenomenonList(phenomenonElementList);
			String constraint_locality = constraintElement.attributeValue("constraint_locality");
			String[] locality= constraint_locality.split(",");
			int constraint_x1 = Integer.parseInt(locality[0]);
			int constraint_x2 = Integer.parseInt(locality[1]);
			int constraint_y1 = Integer.parseInt(locality[2]);
			int constraint_y2 = Integer.parseInt(locality[3]);
			
			Constraint constraint = new Constraint();
			constraint.setConstraint_no(constraint_no);
			constraint.setConstraint_name(constraint_name);
			constraint.setConstraint_description(constraint_description);
			constraint.setConstraint_from(constraint_from);
			constraint.setConstraint_to(constraint_to);
			constraint.setPhenomenonList(phenomenonList);
			constraint.setConstraint_x1(constraint_x1);
			constraint.setConstraint_x2(constraint_x2);
			constraint.setConstraint_y1(constraint_y1);
			constraint.setConstraint_y2(constraint_y2);
			
			constraintList.add(constraint);
		}
		return constraintList;
	}

	private List<RequirementPhenomenon> getRequirementPhenomenonList(List<?> phenomenonElementList) {
		List<RequirementPhenomenon> phenomenonList = new ArrayList<RequirementPhenomenon> ();
		for(Object object : phenomenonElementList) {
			Element phenomenonElement = (Element)object;
			
			int phenomenon_no = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_no"));
			String phenomenon_name = phenomenonElement.attributeValue("phenomenon_name");
			String phenomentn_type = phenomenonElement.attributeValue("phenomentn_type");
			String phenomenon_from = phenomenonElement.attributeValue("phenomenon_from");
			String phenomenon_to = phenomenonElement.attributeValue("phenomenon_to");
			String phenomenon_constraint = phenomenonElement.attributeValue("phenomenon_constraint");
			int phenomenon_requirement = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_requirement"));
			
			RequirementPhenomenon phenomenon = new RequirementPhenomenon();
			phenomenon.setPhenomenon_no(phenomenon_no);
			phenomenon.setPhenomenon_name(phenomenon_name);
			phenomenon.setPhenomenon_type(phenomentn_type);
			phenomenon.setPhenomenon_constraint(phenomenon_constraint);
			phenomenon.setPhenomenon_requirement(phenomenon_requirement);
			phenomenon.setPhenomenon_from(phenomenon_from);
			phenomenon.setPhenomenon_to(phenomenon_to);
			
			phenomenonList.add(phenomenon);
		}
		return phenomenonList;
	}

	private List<Reference> getReferenceList(Element referenceListElement) {
		List<Reference> referenceList = new ArrayList<Reference>();
		List<?> referenceElementList = referenceListElement.elements("Element");
		for(Object object : referenceElementList) {
			Element referenceElement = (Element)object;
			List<?> phenomenonElementList = referenceElement.elements("Phenomenon");
			
			int reference_no = Integer.parseInt(referenceElement.attributeValue("reference_no"));
			String reference_name = referenceElement.attributeValue("reference_name");
			String reference_description = referenceElement.attributeValue("reference_description");
			String reference_from = referenceElement.attributeValue("reference_from");
			String reference_to = referenceElement.attributeValue("reference_to");
			List<RequirementPhenomenon> phenomenonList = getRequirementPhenomenonList(phenomenonElementList);
			String reference_locality = referenceElement.attributeValue("reference_locality");
			String[] locality= reference_locality.split(",");
			int reference_x1 = Integer.parseInt(locality[0]);
			int reference_x2 = Integer.parseInt(locality[1]);
			int reference_y1 = Integer.parseInt(locality[2]);
			int reference_y2 = Integer.parseInt(locality[3]);
			
			Reference reference = new Reference();
			reference.setReference_no(reference_no);
			reference.setReference_name(reference_name);
			reference.setReference_description(reference_description);
			reference.setReference_from(reference_from);
			reference.setReference_to(reference_to);
			reference.setPhenomenonList(phenomenonList);
			reference.setReference_x1(reference_x1);
			reference.setReference_x2(reference_x2);
			reference.setReference_y1(reference_y1);
			reference.setReference_y2(reference_y2);
			
			referenceList.add(reference);
		}
		return referenceList;
	}

	public void saveProject(Project project, String projectAddress) {
		Document document = DocumentHelper.createDocument();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");

		Machine machine = project.getContextDiagram().getMachine();
		List<ProblemDomain> problemDomains = project.getContextDiagram().getProblemDomainList();
		List<Requirement> requirements = project.getProblemDiagram().getRequirementList(); 
		List<Interface> interfaces = project.getContextDiagram().getInterfaceList();
		List<Reference> references = project.getProblemDiagram().getReferenceList();
		List<Constraint> constraints = project.getProblemDiagram().getConstraintList();
		List<Cell> cellList = new ArrayList<Cell>();
		
		Element mxGraphModelElement = document.addElement("mxGraphModel");
		Element rootElement = mxGraphModelElement.addElement("root");
		Element mxCellEle0 = rootElement.addElement("mxCell");
		mxCellEle0.addAttribute("id", String.valueOf(0));
		Element mxCellEle1 = rootElement.addElement("mxCell");
		mxCellEle1.addAttribute("id", String.valueOf(1));
		mxCellEle1.addAttribute("parent", String.valueOf(0));
		int i = 2;
		if(machine != null) {
			Cell cell = new Cell();
			cell.setId(i);
			cell.setValue(machine.getMachine_shortName());
			cell.setType("machine");
			cellList.add(cell);
			
			Element mxCellEle = rootElement.addElement("mxCell");
			mxCellEle.addAttribute("id", String.valueOf(i++));
			mxCellEle.addAttribute("parent", String.valueOf(1));
			mxCellEle.addAttribute("abbr", machine.getMachine_shortName());
			mxCellEle.addAttribute("vertex", String.valueOf(1));
			mxCellEle.addAttribute("style", "shape=machine");
			mxCellEle.addAttribute("value", machine.getMachine_name());
			Element mxGeometryEle = mxCellEle.addElement("mxGeometry");
			mxGeometryEle.addAttribute("as", "geometry");
			mxGeometryEle.addAttribute("height", String.valueOf(machine.getMachine_w()));
			mxGeometryEle.addAttribute("width", String.valueOf(machine.getMachine_h()));
			mxGeometryEle.addAttribute("y", String.valueOf(machine.getMachine_y()));
			mxGeometryEle.addAttribute("x", String.valueOf(machine.getMachine_x()));
			
		}
		
		if(problemDomains != null) {
			for(ProblemDomain problemDomain: problemDomains) {
				Cell cell = new Cell();
				cell.setId(i);
				cell.setValue(problemDomain.getProblemdomain_shortname());
				cell.setType("domain");
				cellList.add(cell);
				
				Element mxCellEle = rootElement.addElement("mxCell");
				mxCellEle.addAttribute("id", String.valueOf(i++));
				mxCellEle.addAttribute("parent", String.valueOf(1));
				mxCellEle.addAttribute("abbr", problemDomain.getProblemdomain_shortname());
				mxCellEle.addAttribute("vertex", String.valueOf(1));
				mxCellEle.addAttribute("style", "shape=domain");
				mxCellEle.addAttribute("value", problemDomain.getProblemdomain_name());
				Element mxGeometryEle = mxCellEle.addElement("mxGeometry");
				mxGeometryEle.addAttribute("as", "geometry");
				mxGeometryEle.addAttribute("height", String.valueOf(problemDomain.getProblemdomain_w()));
				mxGeometryEle.addAttribute("width", String.valueOf(problemDomain.getProblemdomain_h()));
//				mxGeometryEle.addAttribute("height", String.valueOf(problemDomain.getProblemdomain_h()));
//				mxGeometryEle.addAttribute("width", String.valueOf(problemDomain.getProblemdomain_w()));
				mxGeometryEle.addAttribute("y", String.valueOf(problemDomain.getProblemdomain_y()));
				mxGeometryEle.addAttribute("x", String.valueOf(problemDomain.getProblemdomain_x()));
			}
		}
		
		if(requirements != null) {
			for(Requirement requirement: requirements) {
				Cell cell = new Cell();
				cell.setId(i);
				cell.setValue(requirement.getRequirement_context());
				cell.setType("requirement");
				cellList.add(cell);
				
				Element mxCellEle = rootElement.addElement("mxCell");
				mxCellEle.addAttribute("id", String.valueOf(i++));
				mxCellEle.addAttribute("parent", String.valueOf(1));
				mxCellEle.addAttribute("vertex", String.valueOf(1));
				mxCellEle.addAttribute("style", "shape=requirement");
				mxCellEle.addAttribute("value", requirement.getRequirement_context());
				Element mxGeometryEle = mxCellEle.addElement("mxGeometry");
				mxGeometryEle.addAttribute("as", "geometry");
				mxGeometryEle.addAttribute("height", String.valueOf(requirement.getRequirement_w() * 2));
				mxGeometryEle.addAttribute("width", String.valueOf(requirement.getRequirement_h() * 2));
				mxGeometryEle.addAttribute("y", String.valueOf(requirement.getRequirement_y()));
				mxGeometryEle.addAttribute("x", String.valueOf(requirement.getRequirement_x()));
			}
		}
		
		if(interfaces != null) {
			for(Interface inte: interfaces) {
				String from = inte.getInterface_from();
				String to = inte.getInterface_to();
				List<Phenomenon> phenomenons = inte.getPhenomenonList();
				
				List<String> desList = new ArrayList<String>();
				boolean set = false;
				for(Phenomenon phenomenon: phenomenons) {
					set = false;
					for(String des: desList) {
						int j = desList.indexOf(des);
						if(phenomenon.getPhenomenon_from().equals(des.split("!")[0])) {
							des = des + "," + phenomenon.getPhenomenon_name();
							desList.set(j, des);
							set = true;
							break;
						}
					}
					if(set == false) {
						String des = phenomenon.getPhenomenon_from() + "!{" + phenomenon.getPhenomenon_name();
						desList.add(des);
					}
					
				}
				
				String property = "";
				String value = "";
				for(int j = 0; j < desList.size(); j ++ ) {
					property = property + inte.getInterface_name() + ( j + 1 ) + ":" + desList.get(j) + "};";
					value = value + inte.getInterface_name() + ( j + 1 ) + ",";
				}
				
				Element mxCellEle = rootElement.addElement("mxCell");
				mxCellEle.addAttribute("id", String.valueOf(i++));
				mxCellEle.addAttribute("parent", String.valueOf(1));
				mxCellEle.addAttribute("value", value);
				mxCellEle.addAttribute("property", property);
				mxCellEle.addAttribute("edge", String.valueOf(1));
				
				int target = 0;
				int source = 0;
				
				for(Cell cell: cellList) {
					if(cell.getValue().equals(from)) {
						mxCellEle.addAttribute("source", String.valueOf(cell.getId()));
						if(cell.getType() == "domain") {
							source = cell.getId();
						}
					}else if(cell.getValue().equals(to)) {
						mxCellEle.addAttribute("target", String.valueOf(cell.getId()));
						if(cell.getType() == "domain") {
							target = cell.getId();
						}
					}
				}
				
				Element mxGeometryEle = mxCellEle.addElement("mxGeometry");
				mxGeometryEle.addAttribute("as", "geometry");
				mxGeometryEle.addAttribute("relative", String.valueOf(1));
				if(source != 0) {
					Element mxPointEle = mxGeometryEle.addElement("mxPoint");
					mxPointEle.addAttribute("as", "targetPoint");
					mxPointEle.addAttribute("y", String.valueOf(inte.getInterface_y1()));
					mxPointEle.addAttribute("x", String.valueOf(inte.getInterface_x1()));
				}
				if(target != 0) {
					Element mxPointEle = mxGeometryEle.addElement("mxPoint");
					mxPointEle.addAttribute("as", "sourcePoint");
					mxPointEle.addAttribute("y", String.valueOf(inte.getInterface_y2()));
					mxPointEle.addAttribute("x", String.valueOf(inte.getInterface_x2()));
				}
			}
		}
		
		if(references != null) {
			for(Reference reference: references) {
				String from = reference.getReference_from();
				String to = reference.getReference_to();
				List<RequirementPhenomenon> phenomenons = reference.getPhenomenonList();
				
				List<String> desList = new ArrayList<String>();
				boolean set = false;
				for(Phenomenon phenomenon: phenomenons) {
					set = false;
					for(String des: desList) {
						int j = desList.indexOf(des);
						if(phenomenon.getPhenomenon_from().equals(des.split("!")[0])) {
							des = des + "," + phenomenon.getPhenomenon_name();
							desList.set(j, des);
							set = true;
							break;
						}
					}
					if(set == false) {
						String des = phenomenon.getPhenomenon_from() + "!{" + phenomenon.getPhenomenon_name();
						desList.add(des);
					}
					
				}
				
				String property = "";
				String value = "";
				for(int j = 0; j < desList.size(); j ++ ) {
					property = property + reference.getReference_name() + ( j + 1 ) + ":" + desList.get(j) + "};";
					value = value + reference.getReference_name() + ( j + 1 ) + ",";
//					if(property == null) {
//						property = reference.getReference_name() + ( j + 1 ) + 
//								":" + desList.get(j) + "}";
//					}else {
//						property = property + "," + inte.getInterface_name() + ( j + 1 ) + 
//								":" + desList.get(j) + "}";
//					}
//					if(value == null) {
//						value = inte.getInterface_name() + ( j + 1 );
//					}else {
//						value = value + "," + inte.getInterface_name() + ( j + 1 );
//					}
				}
				
				Element mxCellEle = rootElement.addElement("mxCell");
				mxCellEle.addAttribute("id", String.valueOf(i++));
				mxCellEle.addAttribute("parent", String.valueOf(1));
				mxCellEle.addAttribute("style", "dashed");
				mxCellEle.addAttribute("value", value);
				mxCellEle.addAttribute("property", property);
				mxCellEle.addAttribute("edge", String.valueOf(1));
				
				int target = 0;
				int source = 0;
				
				for(Cell cell: cellList) {
					if(cell.getValue().equals(from)) {
						mxCellEle.addAttribute("source", String.valueOf(cell.getId()));
						if(cell.getType() == "domain") {
							source = cell.getId();
						}
					}else if(cell.getValue().equals(to)) {
						mxCellEle.addAttribute("target", String.valueOf(cell.getId()));
						if(cell.getType() == "domain") {
							target = cell.getId();
						}
					}
				}
				
				Element mxGeometryEle = mxCellEle.addElement("mxGeometry");
				mxGeometryEle.addAttribute("as", "geometry");
				mxGeometryEle.addAttribute("relative", String.valueOf(1));
				if(source != 0) {
					Element mxPointEle = mxGeometryEle.addElement("mxPoint");
					mxPointEle.addAttribute("as", "targetPoint");
					mxPointEle.addAttribute("y", String.valueOf(reference.getReference_y1()));
					mxPointEle.addAttribute("x", String.valueOf(reference.getReference_x1()));
				}
				if(target != 0) {
					Element mxPointEle = mxGeometryEle.addElement("mxPoint");
					mxPointEle.addAttribute("as", "sourcePoint");
					mxPointEle.addAttribute("y", String.valueOf(reference.getReference_y2()));
					mxPointEle.addAttribute("x", String.valueOf(reference.getReference_x2()));
				}
			}
		}
		
		if(constraints != null) {
			for(Constraint constraint: constraints) {
				String from = constraint.getConstraint_from();
				String to = constraint.getConstraint_to();
				List<RequirementPhenomenon> phenomenons = constraint.getPhenomenonList();
				
				List<String> desList = new ArrayList<String>();
				boolean set = false;
				for(Phenomenon phenomenon: phenomenons) {
					set = false;
					for(String des: desList) {
						int j = desList.indexOf(des);
						if(phenomenon.getPhenomenon_from().equals(des.split("!")[0])) {
							des = des + "," + phenomenon.getPhenomenon_name();
							desList.set(j, des);
							set = true;
							break;
						}
					}
					if(set == false) {
						String des = phenomenon.getPhenomenon_from() + "!{" + phenomenon.getPhenomenon_name();
						desList.add(des);
					}
					
				}
				
				String property = "";
				String value = "";
				for(int j = 0; j < desList.size(); j ++ ) {
					property = property + constraint.getConstraint_name() + ( j + 1 ) + ":" + desList.get(j) + "};";
					value = value + constraint.getConstraint_name() + ( j + 1 ) + ",";
//					if(property == null) {
//						property = reference.getReference_name() + ( j + 1 ) + 
//								":" + desList.get(j) + "}";
//					}else {
//						property = property + "," + inte.getInterface_name() + ( j + 1 ) + 
//								":" + desList.get(j) + "}";
//					}
//					if(value == null) {
//						value = inte.getInterface_name() + ( j + 1 );
//					}else {
//						value = value + "," + inte.getInterface_name() + ( j + 1 );
//					}
				}
				
				Element mxCellEle = rootElement.addElement("mxCell");
				mxCellEle.addAttribute("id", String.valueOf(i++));
				mxCellEle.addAttribute("parent", String.valueOf(1));
				mxCellEle.addAttribute("style", "arrow");
				mxCellEle.addAttribute("value", value);
				mxCellEle.addAttribute("property", property);
				mxCellEle.addAttribute("edge", String.valueOf(1));
				
				int target = 0;
				int source = 0;
				
//				for(Cell cell: cellList) {
//					if(cell.getValue().equals(from) && cell.getType() == "domain") {
//						source = cell.getId();
//						mxCellEle.addAttribute("source", String.valueOf(source));
//					}else if(cell.getValue().equals(to) && cell.getType() == "domain") {
//						target = cell.getId();
//						mxCellEle.addAttribute("target", String.valueOf(target));
//					}
//				}
				for(Cell cell: cellList) {
					if(cell.getValue().equals(from)) {
						mxCellEle.addAttribute("source", String.valueOf(cell.getId()));
						if(cell.getType() == "domain") {
							source = cell.getId();
						}
					}else if(cell.getValue().equals(to)) {
						mxCellEle.addAttribute("target", String.valueOf(cell.getId()));
						if(cell.getType() == "domain") {
							target = cell.getId();
						}
					}
				}
				
				Element mxGeometryEle = mxCellEle.addElement("mxGeometry");
				mxGeometryEle.addAttribute("as", "geometry");
				mxGeometryEle.addAttribute("relative", String.valueOf(1));
				if(source != 0) {
					Element mxPointEle = mxGeometryEle.addElement("mxPoint");
					mxPointEle.addAttribute("as", "targetPoint");
					mxPointEle.addAttribute("y", String.valueOf(constraint.getConstraint_y1()));
					mxPointEle.addAttribute("x", String.valueOf(constraint.getConstraint_x1()));
				}
				if(target != 0) {
					Element mxPointEle = mxGeometryEle.addElement("mxPoint");
					mxPointEle.addAttribute("as", "sourcePoint");
					mxPointEle.addAttribute("y", String.valueOf(constraint.getConstraint_y2()));
					mxPointEle.addAttribute("x", String.valueOf(constraint.getConstraint_x2()));
				}
			}
		}

		File xmlFile = new File(addressService.getDownloadRootAddress() +"GXNU.xml");
		if(xmlFile.exists()==true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
			//创建一个输出流对象
			Writer out = new FileWriter(xmlFile);
			//创建一个dom4j创建xml的对象
			XMLWriter writer = new XMLWriter(out, format);
			//调用write方法将doc文档写到指定路径
			writer.write(document);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	} 	
}

