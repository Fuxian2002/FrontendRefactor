package com.example.demo.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.impl.CompositeNode;
import org.eclipse.xtext.nodemodel.impl.CompositeNodeWithSemanticElement;
import org.eclipse.xtext.nodemodel.impl.HiddenLeafNode;
import org.eclipse.xtext.nodemodel.impl.LeafNode;
import org.eclipse.xtext.nodemodel.impl.RootNode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

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
import com.github.gumtreediff.gen.antlr3.xml.EMFTreeGenerator;
import com.github.gumtreediff.tree.EMFTreeContext;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TimeTree;
import com.github.gumtreediff.tree.Tree;

import pf.PfStandaloneSetup;

public class ASTService {
	// reset id of AST
	public static int resetId(ITree tree, int id) {
		// tree.setDepth(0);
		for (ITree child : tree.getChildren()) {
			id = resetId(child, id);
		}
		tree.setId(id++);
		return id;
	}
	//
	// public static int resetId(ITree tree, int id, int depth) {
	// tree.setDepth(depth++);
	// for (ITree child : tree.getChildren()) {
	// id = resetId(child, id, depth);
	// }
	// tree.setId(id++);
	// return id;
	// }

	// add time for locTime
	public static ITree addTime(Tree tree, long time) {
		return TimeTree.deepCopy(tree, time);
	}

	// ================== project =============================
	/**
	 * @Title: generateXmlFile
	 * @Description: 根据AST生成 xml文件(Project)
	 * @param @param tree : AST
	 * @param @param path : XML
	 * @return void 返回类型
	 * @throws
	 */
//	public static void generateProjectXmlFile(ITree tree, String path) {
//		ITree root = getProjectParent(tree);
//		// String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?";
//		String str = "";
//		// System.out.println(str);
//		try {
//			File writename = new File(path); // 相对路径，如果没有则要建立一个新的output。txt文件
//			if (writename.exists())
//				writename.delete();
//			writename.createNewFile(); // 创建新文件
//			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
//			out.write(str);
//			out.flush(); // 把缓存区内容压入文件
//			out.close(); // 最后记得关闭文件
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static ITree getProjectParent(ITree tree) {
		for (ITree child : tree.getChildren()) {
			if (child.getLabel().contentEquals("project"))
				return tree;
		}
		for (ITree child : tree.getChildren()) {
			ITree res = getProjectParent(child);
			if (res != null) {
				return res;
			}
		}
		return null;
	}

	public static ITree getEMFParent(ITree tree) {
		for (ITree child : tree.getChildren()) {
			if (child.getLabel().contentEquals("pf:ProblemDiagram"))
				return tree;
		}
		for (ITree child : tree.getChildren()) {
			ITree res = getEMFParent(child);
			if (res != null) {
				return res;
			}
		}
		return null;
	}

	/**
	 * @Title: parseAST
	 * @Description: 遍历AST，获取xml文件内容
	 * @param @param tree
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String parseAST(ITree tree) {
//		System.out.println("parseAST");
		if (tree == null) {
			System.out.println("parseAST err : tree = null");
			return "";
		}

		String str = "";
		// 输出
		// for (int i = 1; i < tree.getDepth(); i++)
		// System.out.print("\t");
		// System.out.println(tree.toShortString());
		try {
			if (!((TimeTree) tree).isDelete())
				if (tree.getType() == 10) {
					// if tree is the first child, print >
					for (ITree brother : tree.getParent().getChildren()) {
						if (brother.getType() == 10 && !((TimeTree) brother).isDelete()) {
							if (brother == tree)
								str += ">";
							break;
						}
					}
					if (tree.getChildren() == null || tree.getChildren().size() == 0)
						((TimeTree) tree).setDelete(true);
					else
						str += "\n<";
				} else if (tree.getType() == 11) {
					str += tree.getLabel();
				} else if (tree.getType() == 6) {// 属性
					str += " " + tree.getLabel();
				} else if (tree.getType() == 8) {// 属性值
					str += "=" + tree.getLabel();
				} else if (tree.getType() == 15) {// String <title >insulin </title>
					str += ">" + tree.getLabel();

				}
			// if (str != "") {
			// for (int i = 1; i < tree.getDepth(); i++)
			// System.out.print("\t");
			// System.out.println("------" + str + "------");
			// }
			// 遍历子节点
			for (ITree child : tree.getChildren()) {
				str += parseAST(child);
			}
			// 输出 />
			// <Machine machine_name="controller" machine_shortname="controller"
			// machine_locality="100,250,50,100"/>

			if (!((TimeTree) tree).isDelete() && tree.getType() == 10) {
				boolean isFind = false;
				for (ITree child : tree.getChildren()) {
					if (child.getType() == 10 || child.getType() == 15)
						if (!((TimeTree) child).isDelete())
							isFind = true;
				}
				if (isFind) {
					/*
					 * 1 <Element 2 interface_description="b:LC!{OffPulse,Onpulse}" >3 4 <Phenomenon
					 * phenomenon_no="1" /> 5 <Phenomenon phenomenon_no="2" /> </Element>
					 */
					// <title>ContextDiagram</title>
					// System.out.println("find child.getType()==10 || child.getType()==15");
					str += "</" + tree.getChild(0).getLabel() + ">\n";
				} else {
					// <DesignDomain/>
					// <Machine machine_name="Light Controller" machine_shortname="LC"
					// machine_locality="280,180,0,0"/>
					str += "/>";
				}
				// <title>insulin </title>
				// for (int i = 1; i < tree.getDepth(); i++)
				// System.out.print("\t");
				// System.out.println("******" + str + "******");
			}
			// System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static EMFTreeContext generateXmlAST(String filePath) {
		System.out.println("---------------generateXmlAST,time: "+filePath);
		EMFTreeGenerator emfTreeGenerator = new EMFTreeGenerator();
		try {
			return emfTreeGenerator.generateFromFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static EMFTreeContext generateXmlAST(String filePath, long time) {
		System.out.println("---------------generateXmlAST,time: "+filePath);
		EMFTreeGenerator emfTreeGenerator = new EMFTreeGenerator();
		try {
			return emfTreeGenerator.generateFromFile(filePath, time);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获取最新的project
//	public static Project getProject(String projectAddress) {
//		Project project = new Project();
//		SAXReader saxReader = new SAXReader();
//		try {
//			File xmlFile = new File(projectAddress);
//			Document document = saxReader.read(xmlFile);
//			Element projectElement = document.getRootElement();
//			Element titleElement = projectElement.element("title");
//			Element contextDiagramElement = projectElement.element("ContextDiaagram");
//			Element problemDiagramElement = projectElement.element("ProblemDiagram");
//			String title = titleElement.getText();
//
//			Element cdtitleElement = contextDiagramElement.element("title");
//			Element machineElement = contextDiagramElement.element("Machine");
//			Element problemDomainListElement = contextDiagramElement.element("ProblemDomain");
//			Element interfaceListElement = contextDiagramElement.element("Interface");
//
//			String cdtitle = cdtitleElement.getText();
//			Machine machine = getMachine(machineElement);
//			List<ProblemDomain> problemDomainList = getProblemDomainList(problemDomainListElement);
//			List<Interface> interfaceList = getInterfaceList(interfaceListElement);
//
//			ContextDiagram contextDiagram = new ContextDiagram();
//			contextDiagram.setTitle(title);
//			contextDiagram.setMachine(machine);
//			contextDiagram.setProblemDomainList(problemDomainList);
//			contextDiagram.setInterfaceList(interfaceList);
//
//			Element pdtitleElement = problemDiagramElement.element("title");
//			Element requirementListElement = problemDiagramElement.element("Requirement");
//			Element constraintListElement = problemDiagramElement.element("Constraint");
//			Element referenceListElement = problemDiagramElement.element("Reference");
//
//			String pdtitle = pdtitleElement.getText();
//			List<Requirement> requirementList = getRequirementList(requirementListElement);
//			List<Constraint> constraintList = getConstraintList(constraintListElement);
//			List<Reference> referenceList = getReferenceList(referenceListElement);
//
//			ProblemDiagram problemDiagram = new ProblemDiagram();
//			problemDiagram.setTitle(pdtitle);
//			problemDiagram.setContextDiagram(contextDiagram);
//			problemDiagram.setRequirementList(requirementList);
//			problemDiagram.setConstraintList(constraintList);
//			problemDiagram.setReferenceList(referenceList);
//
//			project.setTitle(title);
//			project.setContextDiagram(contextDiagram);
//			project.setProblemDiagram(problemDiagram);
//		} catch (DocumentException e) {
//
//			e.printStackTrace();
//		}
//		return project;
//	}

//	private static Machine getMachine(Element machineElement) {
//		// TODO Auto-generated method stub
//		Machine machine = new Machine();
//
//		String name = machineElement.attributeValue("machine_name");
//		if (name == null)
//			return null;
//		name = name.replaceAll("&#x000A", "\n");
//		String shortname = machineElement.attributeValue("machine_shortname");
//		String machine_locality = machineElement.attributeValue("machine_locality");
//		String[] locality = machine_locality.split(",");
//		int x = Integer.parseInt(locality[0]);
//		int y = Integer.parseInt(locality[1]);
//		int h = Integer.parseInt(locality[2]);
//		int w = Integer.parseInt(locality[3]);
//
//		machine.setName(name);
//		machine.setShortname(shortname);
//		machine.setH(h);
//		machine.setW(w);
//		machine.setX(x);
//		machine.setY(y);
//
//		return machine;
//	}
//
//	private static List<ProblemDomain> getProblemDomainList(Element problemDomainListElement) {
//		// TODO Auto-generated method stub
//		List<ProblemDomain> problemDomainList = new ArrayList<ProblemDomain>();
//
//		Element givenDomainListElement = problemDomainListElement.element("GivenDomain");
//		Element designDomainListElement = problemDomainListElement.element("DesignDomain");
//		List<?> givenDomainElementList = givenDomainListElement.elements("Element");
//		List<?> designDomainElementList = designDomainListElement.elements("Element");
//
//		for (Object object : givenDomainElementList) {
//			ProblemDomain problemDomain = new ProblemDomain();
//			Element givenDomainElement = (Element) object;
//
//			int no = Integer.parseInt(givenDomainElement.attributeValue("problemdomain_no"));
//			String name = givenDomainElement.attributeValue("problemdomain_name");
//			name = name.replaceAll("&#x000A", "\n");
//			String shortname = givenDomainElement.attributeValue("problemdomain_shortname");
//			String type = givenDomainElement.attributeValue("problemdomain_type");
//			String property = "GivenDomain";
//			String problemdomain_locality = givenDomainElement.attributeValue("problemdomain_locality");
//			String[] locality = problemdomain_locality.split(",");
//			int x = Integer.parseInt(locality[0]);
//			int y = Integer.parseInt(locality[1]);
//			int h = Integer.parseInt(locality[2]);
//			int w = Integer.parseInt(locality[3]);
//
//			problemDomain.setNo(no);
//			problemDomain.setName(name);
//			problemDomain.setShortname(shortname);
//			problemDomain.setType(type);
//			problemDomain.setProperty(property);
//			problemDomain.setX(x);
//			problemDomain.setY(y);
//			problemDomain.setH(h);
//			problemDomain.setW(w);
//
//			problemDomainList.add(problemDomain);
//		}
//		for (Object object : designDomainElementList) {
//			Element designDomainElement = (Element) object;
//
//			int no = Integer.parseInt(designDomainElement.attributeValue("problemdomain_no"));
//			String name = designDomainElement.attributeValue("problemdomain_name");
//			String shortname = designDomainElement.attributeValue("problemdomain_shortname");
//			String type = designDomainElement.attributeValue("problemdomain_type");
//			String property = "DesignDomain";
//			String problemdomain_locality = designDomainElement.attributeValue("problemdomain_locality");
//			String[] locality = problemdomain_locality.split(",");
//			int x = Integer.parseInt(locality[0]);
//			int y = Integer.parseInt(locality[1]);
//			int h = Integer.parseInt(locality[2]);
//			int w = Integer.parseInt(locality[3]);
//
//			ProblemDomain problemDomain = new ProblemDomain();
//			problemDomain.setNo(no);
//			problemDomain.setName(name);
//			problemDomain.setShortname(shortname);
//			problemDomain.setType(type);
//			problemDomain.setProperty(property);
//			problemDomain.setX(x);
//			problemDomain.setY(y);
//			problemDomain.setH(h);
//			problemDomain.setW(w);
//
//			problemDomainList.add(problemDomain);
//		}
//
//		return problemDomainList;
//	}
//
//	private static List<Interface> getInterfaceList(Element interfaceListElement) {
//		// TODO Auto-generated method stub
//		List<Interface> interfaceList = new ArrayList<Interface>();
//		List<?> interfaceElementList = interfaceListElement.elements("Element");
//
//		for (Object object : interfaceElementList) {
//			Element interfaceElement = (Element) object;
//			List<?> phenomenonElementList = interfaceElement.elements("Phenomenon");
//
//			int no = Integer.parseInt(interfaceElement.attributeValue("interface_no"));
//			String name = interfaceElement.attributeValue("interface_name");
//			String description = interfaceElement.attributeValue("interface_description");
//			String from = interfaceElement.attributeValue("interface_from").replaceAll("&#x000A", "\n");
//			;
//			String to = interfaceElement.attributeValue("interface_to").replaceAll("&#x000A", "\n");
//			;
//			String interface_locality = interfaceElement.attributeValue("interface_locality");
//			List<Phenomenon> phenomenonList = getPhenomenonList(phenomenonElementList);
//			String[] locality = interface_locality.split(",");
//			int x1 = Integer.parseInt(locality[0]);
//			int x2 = Integer.parseInt(locality[1]);
//			int y1 = Integer.parseInt(locality[2]);
//			int y2 = Integer.parseInt(locality[3]);
//
//			Interface inte = new Interface();
//			inte.setInterface_no(no);
//			inte.setInterface_name(name);
//			inte.setInterface_description(description);
//			inte.setInterface_from(from);
//			inte.setInterface_to(to);
//			inte.setPhenomenonList(phenomenonList);
//			inte.setInterface_x1(x1);
//			inte.setInterface_y1(y1);
//			inte.setInterface_x2(x2);
//			inte.setInterface_y2(y2);
//
//			interfaceList.add(inte);
//		}
//
//		return interfaceList;
//	}
//
//	private static List<Phenomenon> getPhenomenonList(List<?> phenomenonElementList) {
//		// TODO Auto-generated method stub
//		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
//		for (Object object : phenomenonElementList) {
//			Element phenomenonElement = (Element) object;
//
//			int phe_no = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_no"));
//			String phe_name = phenomenonElement.attributeValue("phenomenon_name");
//			String phe_type = phenomenonElement.attributeValue("phenomenon_type");
//			String phe_from = phenomenonElement.attributeValue("phenomenon_from").replaceAll("&#x000A", "\n");
//			;
//			String phe_to = phenomenonElement.attributeValue("phenomenon_to").replaceAll("&#x000A", "\n");
//			;
//
//			Phenomenon phenomenon = new Phenomenon();
//			phenomenon.setPhenomenon_no(phe_no);
//			phenomenon.setPhenomenon_name(phe_name);
//			phenomenon.setPhenomenon_type(phe_type);
//			phenomenon.setPhenomenon_from(phe_from);
//			phenomenon.setPhenomenon_to(phe_to);
//
//			phenomenonList.add(phenomenon);
//		}
//		return phenomenonList;
//	}
//
//	private static List<Requirement> getRequirementList(Element requirementListElement) {
//		// TODO Auto-generated method stub
//		List<?> requirementElementList = requirementListElement.elements("Element");
//		List<Requirement> requirementList = new ArrayList<Requirement>();
//		for (Object object : requirementElementList) {
//			Element requirementElement = (Element) object;
//
//			Requirement requirement = getRequirement(requirementElement);
//
//			requirementList.add(requirement);
//		}
//		return requirementList;
//	}

//	private static Requirement getRequirement(Element requirementElement) {
//		// TODO Auto-generated method stub
//		Requirement requirement = new Requirement();
//
//		int no = Integer.parseInt(requirementElement.attributeValue("requirement_no"));
//		String name = requirementElement.attributeValue("requirement_context").replaceAll("&#x000A", "\n");
//		String shortname = requirementElement.attributeValue("requirement_shortname");
//		if (shortname == null) {
//			shortname = name.replaceAll(" ", "");
//		}
//		String requirement_locality = requirementElement.attributeValue("requirement_locality");
//		String[] locality = requirement_locality.split(",");
//		int x = Integer.parseInt(locality[0]);
//		int y = Integer.parseInt(locality[1]);
//		int h = Integer.parseInt(locality[2]);
//		int w = Integer.parseInt(locality[3]);
//
//		requirement.setNo(no);
//		requirement.setName(name);
//		requirement.setShortname(shortname);
//		requirement.setX(x);
//		requirement.setY(y);
//		requirement.setH(h);
//		requirement.setW(w);
//
//		return requirement;
//	}
//
//	private static List<Constraint> getConstraintList(Element constraintListElement) {
//		// TODO Auto-generated method stub
//		List<Constraint> constraintList = new ArrayList<Constraint>();
//		List<?> constraintElementList = constraintListElement.elements("Element");
//		for (Object object : constraintElementList) {
//			Element constraintElement = (Element) object;
//			List<?> phenomenonElementList = constraintElement.elements("Phenomenon");
//
//			int no = Integer.parseInt(constraintElement.attributeValue("constraint_no"));
//			String name = constraintElement.attributeValue("constraint_name");
//			String description = constraintElement.attributeValue("constraint_description");
//			String from = constraintElement.attributeValue("constraint_from").replaceAll("&#x000A", "\n");
//			;
//			String to = constraintElement.attributeValue("constraint_to").replaceAll("&#x000A", "\n");
//			;
//			List<RequirementPhenomenon> phenomenonList = getRequirementPhenomenonList(phenomenonElementList);
//			String constraint_locality = constraintElement.attributeValue("constraint_locality");
//			String[] locality = constraint_locality.split(",");
//			int x1 = Integer.parseInt(locality[0]);
//			int x2 = Integer.parseInt(locality[1]);
//			int y1 = Integer.parseInt(locality[2]);
//			int y2 = Integer.parseInt(locality[3]);
//
//			Constraint constraint = new Constraint();
//			constraint.setConstraint_no(no);
//			constraint.setConstraint_name(name);
//			constraint.setConstraint_description(description);
//			constraint.setConstraint_from(from);
//			constraint.setConstraint_to(to);
//			constraint.setPhenomenonList(phenomenonList);
//			constraint.setConstraint_x1(x1);
//			constraint.setConstraint_x2(x2);
//			constraint.setConstraint_y1(y1);
//			constraint.setConstraint_y2(y2);
//
//			constraintList.add(constraint);
//		}
//		return constraintList;
//	}
//
//	private static List<RequirementPhenomenon> getRequirementPhenomenonList(List<?> phenomenonElementList) {
//		// TODO Auto-generated method stub
//		List<RequirementPhenomenon> phenomenonList = new ArrayList<RequirementPhenomenon>();
//		for (Object object : phenomenonElementList) {
//			Element phenomenonElement = (Element) object;
//
//			int phe_no = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_no"));
//			String phe_name = phenomenonElement.attributeValue("phenomenon_name");
//			String phe_type = phenomenonElement.attributeValue("phenomenon_type");
//			String phe_from = phenomenonElement.attributeValue("phenomenon_from").replaceAll("&#x000A", "\n");
//			;
//			String phe_to = phenomenonElement.attributeValue("phenomenon_to").replaceAll("&#x000A", "\n");
//			;
//			String phe_constraint = phenomenonElement.attributeValue("phenomenon_constraint");
//			int phe_requirement = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_requirement"));
//
//			RequirementPhenomenon phenomenon = new RequirementPhenomenon();
//			phenomenon.setPhenomenon_no(phe_no);
//			phenomenon.setPhenomenon_name(phe_name);
//			phenomenon.setPhenomenon_type(phe_type);
//			phenomenon.setPhenomenon_constraint(phe_constraint);
//			phenomenon.setPhenomenon_requirement(phe_requirement);
//			phenomenon.setPhenomenon_from(phe_from);
//			phenomenon.setPhenomenon_to(phe_to);
//
//			phenomenonList.add(phenomenon);
//		}
//		return phenomenonList;
//	}

//	private static List<Reference> getReferenceList(Element referenceListElement) {
//		// TODO Auto-generated method stub
//		List<Reference> referenceList = new ArrayList<Reference>();
//		List<?> referenceElementList = referenceListElement.elements("Element");
//		for (Object object : referenceElementList) {
//			Element referenceElement = (Element) object;
//			List<?> phenomenonElementList = referenceElement.elements("Phenomenon");
//
//			int no = Integer.parseInt(referenceElement.attributeValue("reference_no"));
//			String name = referenceElement.attributeValue("reference_name");
//			String description = referenceElement.attributeValue("reference_description");
//			String from = referenceElement.attributeValue("reference_from").replaceAll("&#x000A", "\n");
//			;
//			String to = referenceElement.attributeValue("reference_to").replaceAll("&#x000A", "\n");
//			;
//			List<RequirementPhenomenon> phenomenonList = getRequirementPhenomenonList(phenomenonElementList);
//			String reference_locality = referenceElement.attributeValue("reference_locality");
//			String[] locality = reference_locality.split(",");
//			int x1 = Integer.parseInt(locality[0]);
//			int x2 = Integer.parseInt(locality[1]);
//			int y1 = Integer.parseInt(locality[2]);
//			int y2 = Integer.parseInt(locality[3]);
//
//			Reference reference = new Reference();
//			reference.setReference_no(no);
//			reference.setReference_name(name);
//			reference.setReference_description(description);
//			reference.setReference_from(from);
//			reference.setReference_to(to);
//			reference.setPhenomenonList(phenomenonList);
//			reference.setReference_x1(x1);
//			reference.setReference_x2(x2);
//			reference.setReference_y1(y1);
//			reference.setReference_y2(y2);
//
//			referenceList.add(reference);
//		}
//		return referenceList;
//	}

	// ===================== pf =======================
//	private static Deque<ITree> trees = new ArrayDeque<>();
//	static int Root_Node = 0;
//	static int Composite_Node_With_Semantic_Element = 1;
//	static int Composite_Node = 2;
//	static int Hidden_Leaf_Node = 3;
//	static int Leaf_Node = 4;
//	static int id = 0;

	/**
	 * @Title: generatePfFile
	 * @Description: 根据AST生成pf文件
	 * @param @param tree
	 * @param @param rootAddress
	 * @param @param path 设定文件
	 * @return void 返回类型
	 * @throws
	 */
//	@Deprecated
//	public static void generatePfFile(ITree tree, String rootAddress, String path) {
//
//		System.out.println("generatePf");
//		String str = getPf(tree);
//		FileOperation.writeToFile(str, rootAddress, path);
//
//	}

	/**
	 * @Title: getPf
	 * @Description: 遍历 pf AST 获取pf文本
	 * @param @param tree
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
//	@Deprecated
//	public static String getPf(ITree tree) {
//		String str = "";
//		// 输出
//		// System.out.print(tree.toShortString()+","+tree.getId()+"\t\t");
//		// if(tree.getParent()!=null)
//		// System.out.println(tree.getParent().toShortString()+","+tree.getParent().getId());
//		// else
//		// System.out.println("root");
//		if (tree.getType() == Hidden_Leaf_Node || tree.getType() == Leaf_Node)
//			str += tree.getLabel();
//
//		// 遍历子节点
//		for (ITree child : tree.getChildren()) {
//			str += getPf(child);
//		}
//		return str;
//	}

//	// 读取文件获取pf
//	public String getPf(String projectAddress) {
//		String res = "";
//		try {
//			InputStream is = new FileInputStream(projectAddress);
//			int iAvail = is.available();
//			byte[] bytes = new byte[iAvail];
//			is.read(bytes);
//			res = new String(bytes);
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return res;
//	}

	// 判断pf文件是否有语法错误
	public static boolean isSyntaxError(String filePath) {
		System.out.println("isSyntaxError："+filePath);
		File file = new File(filePath);
		PfStandaloneSetup.doSetup();
		XtextResourceSet resourceSet = new XtextResourceSet();
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		URI uri = URI.createURI(file.getPath());
		XtextResource xtextResource = (XtextResource) resourceSet.getResource(uri, true);
		IParseResult parseResult = xtextResource.getParseResult();
		EList<Diagnostic> error = xtextResource.getErrors();
		if (error.isEmpty()) {
			return false;
		}
		System.err.println("error: "+error.toString());
		return true;
	}

	/**
	 * @Title: generateNoErrorPfAST
	 * @Description: 根据pf文件生成AST，若有错误则返回null
	 * @param @param filePath
	 * @param @return 设定文件
	 * @return TreeContext 返回类型
	 * @throws
	 */
//	@Deprecated
//	public static EMFTreeContext generateNoErrorPfAST(String filePath) {
//		File file = new File(filePath);
//		PfStandaloneSetup.doSetup();
//		XtextResourceSet resourceSet = new XtextResourceSet();
//		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
//		URI uri = URI.createURI(file.getPath());
//		XtextResource xtextResource = (XtextResource) resourceSet.getResource(uri, true);
//		IParseResult parseResult = xtextResource.getParseResult();
//		EList<Diagnostic> error = xtextResource.getErrors();
//		if (error.isEmpty()) {
//			ICompositeNode rootNode = parseResult.getRootNode();
//			EMFTreeContext context = new EMFTreeContext();
//			id = 0;
//			buildTree(context, rootNode);
//			return context;
//		}
//		return null;
//	}

//	// 根据pf文件生成AST
//	@Deprecated
//	public static EMFTreeContext generatePfAST(String filePath) {
//		File file = new File(filePath);
//		PfStandaloneSetup.doSetup();
//		XtextResourceSet resourceSet = new XtextResourceSet();
//		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
//		URI uri = URI.createURI(file.getPath());
//		XtextResource xtextResource = (XtextResource) resourceSet.getResource(uri, true);
//		IParseResult parseResult = xtextResource.getParseResult();
//
//		ICompositeNode rootNode = parseResult.getRootNode();
//		EMFTreeContext context = new EMFTreeContext();
//		id = 0;
//		buildTree(context, rootNode);
//		return context;
//
//	}

	// 将Xtext格式AST转换为GumTree格式语法树
//	@Deprecated
//	public static void buildTree(EMFTreeContext context, INode node) {
//		String tokenName = "";
//		int type = 0;
//		String label = node.getText();
//		if (node instanceof HiddenLeafNode) {// LeafNode的子类
//			// TerminalRuleImpl 空格 换行
//			// System.out.println("============HiddenLeafNode============");
//			// System.out.println("$"+node.getText()+"#");
//			// label = node.getText();
//			tokenName = "3HiddenLeafNode";
//			type = Hidden_Leaf_Node;
//		} else if (node instanceof LeafNode) {
//			// KeywordImpl problem:
//			// RuleCallImpl LightControler LC "Light Controller"
//			// EnumLiteralDeclarationImpl M
//			// System.out.println("============LeafNode============");
//			// System.out.println("$"+node.getText()+"#");
//			// label = node.getText();
//			tokenName = "4LeafNode";
//			type = Leaf_Node;
//		} else if (node instanceof RootNode) {// ParserRuleImpl
//			// System.out.println("============0 rootnode============");
//			label = ITree.NO_LABEL;
//			tokenName = "0RootNode";
//			type = Root_Node;
//			// for(INode child : ((ICompositeNode) node).getChildren()) {
//			// buildTree(context,child);
//			// }
//		} else if (node instanceof CompositeNodeWithSemanticElement) {
//			// RuleCallImpl
//			// System.out.println("==============1 CompositeNodeWithSemanticElement =====");
//			label = ITree.NO_LABEL;
//			tokenName = "1CompositeNodeWithSemanticElement";
//			type = Composite_Node_With_Semantic_Element;
//			// for(INode child : ((ICompositeNode) node).getChildren()) {
//			// buildTree(context,child);
//			// }
//		} else if (node instanceof CompositeNode) {
//			// RuleCallImpl
//			// System.out.println("============2 CompositeNode =====");
//			label = ITree.NO_LABEL;
//			tokenName = "2CompositeNode";
//			type = Composite_Node;
//			// for(INode child : ((ICompositeNode) node).getChildren()) {
//			// buildTree(context,child);
//			// }
//		}
//		ITree t = context.createTree(type, label, tokenName);
//
//		int start = node.getTotalOffset();
//		int length = node.getTotalLength();
//		t.setPos(start);
//		t.setLength(length);
//		t.setId(id);
//		id++;
//
//		if (trees.isEmpty())
//			context.setRoot(t);
//		else
//			t.setParentAndUpdateChildren(trees.peek());
//		// System.out.println(t.toShortString()+","+t.getId());
//		if (node instanceof ICompositeNode) {
//			trees.push(t);
//			for (INode child : ((ICompositeNode) node).getChildren()) {
//				buildTree(context, child);
//			}
//			trees.pop();
//		}
//	}

	// ======================== EMF ======================
	/**
	 * @Title: generateEMFXmlFile
	 * @Description: 根据AST生成 xml文件(emf)
	 * @param @param tree : AST
	 * @param @param path : XML
	 * @return void 返回类型
	 * @throws
	 */
	public static File generateEMFXmlFile(ITree tree, String path) {
//		System.out.println("```````````generateEMFXmlFile");
		ITree root = getEMFParent(tree);
		String str = "";
		str += "<?xml version=\"1.0\" encoding=\"ASCII\"?";
		str += parseAST(root);
//		System.out.println(str);
		str = str.replace("\n\n", "\n");
		str = str.replace("><", ">\n<");
		// System.out.println(str);
		try {
			File writename = new File(path); // 相对路径，如果没有则要建立一个新的output。txt文件
			if (writename.exists())
				writename.delete();
			writename.createNewFile(); // 创建新文件
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			out.write(str);
			out.flush(); // 把缓存区内容压入文件
			out.close(); // 最后记得关闭文件
			return writename;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Title: writeASTToFile
	 * @Description: write ast to file, every node a line
	 * @param @param tree
	 * @param @param rootAddress
	 * @param @param locPath 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void writeASTToFile(ITree tree, String rootAddress, String locPath) {
		FileOperation.writeToFile(tree.toTreeString(), rootAddress, locPath);
	}

	public static ITree rebuildASTFromFile(String locPath) {
		System.out.println("rebuildASTFromFile----"+locPath);
		// 一行对应一个节点
		ITree root = new TimeTree(0, "", 0);
		File f = new File(locPath);
		FileReader fre;
		try {
			fre = new FileReader(f);
			BufferedReader bre = new BufferedReader(fre);
			String str = "";
			while ((str = bre.readLine()) != null) {
				ITree t = rebuildASTFromString(str, root);
				if (t.getParent() == null)
					root = t;
			}
			bre.close();
			fre.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return root;
	}

	public static ITree rebuildASTFromString(String str, ITree root) {
//		System.out.println("rebuildASTFromString-----: "+str);
		// 一行对应一个节点
		ITree tree = null;
		String[] slist = str.split(",");
		// type,label,id,time,parent
		int type = Integer.parseInt(slist[0].trim());
		String label = slist[1].trim();
		int id = Integer.parseInt(slist[2].trim());
		long time = Long.parseLong(slist[3].trim());
		tree = new TimeTree(type, label, time);
		tree.setId(id);
		int parentId = Integer.parseInt(slist[4].trim());
		if (parentId >= 0) {
			ITree parent = root;
			for (ITree t : root.getTrees()) {
				if (t.getId() == parentId) {
					parent = t;
					break;
				}
			}
			tree.setParentAndUpdateChildren(parent);
		} else {
			root = tree;
		}

		boolean isDelete = Boolean.getBoolean(slist[5].trim());
		((TimeTree) tree).setDelete(isDelete);
		return tree;
	}
}
