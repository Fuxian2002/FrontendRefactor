package com.example.demo.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.example.demo.bean.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
	private String reqModelDir = AddressService.reqModelDir;
	private String devModelDir = AddressService.devModelDir;

	private String commanAddress = AddressService.rootAddress;
	private String rootAddress_user = AddressService.userAddress;
	
//	private String commanAddress = "/home/backend_resources/PF/Project/";
//	private String rootAddress_user = "/home/backend_resources/PF/UserProject/";
	private final String rootAddress = AddressService.rootAddress;
	private String pfRootAddress = AddressService.pfRootAddress;
	private String owlRootAddress = AddressService.owlRootAddress;
	private String pOwlRootAddress = AddressService.pOwlRootAddress;
	private String eOwlRootAddress = AddressService.eOwlRootAddress;
	private String downloadRootAddress = AddressService.downloadRootAddress;

	public String getCommanAddress() {
		return commanAddress;
	}

	// ============保存文件，包括保存到硬盘和执行git命令=============
//	public void addProject(String userAdd, MultipartFile file, String branch) {
//		addFile(file, userAdd, branch);
//	}
//
//	public void addEOwl(MultipartFile file, String branch) {
//		addFile(file, eOwlRootAddress, branch);
//	}
//
//	public void addPOwl(MultipartFile file, String branch) {
//		addFile(file, pOwlRootAddress, branch);
//	}

//	public void addFile(MultipartFile file, String userAdd, String branch) {
//		try {
//			GitUtil.gitCheckout(branch, userAdd);  // 切换分支
//			GitUtil.currentBranch(userAdd);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		// 保存文件
//		saveUploadFile(file, userAdd);
//
//		try {
//			GitUtil.RecordUploadProjAt("addfile", userAdd, ".");
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (GitAPIException e) {
//			e.printStackTrace();
//		}// 执行git add，git commit命令
//	}
	public void addFile(MultipartFile file, String userAdd, String branch) {
		// 保存文件
		saveUploadFile(file, userAdd);
	}
	// ===================创建版本库=================
	// 创建项目、owl的文件夹、版本库及分支
	public boolean setProject(String proAddress, String branch) {
		return set(proAddress, branch);
	}

//	public boolean setOwl(String branch) {
//		return set(owlRootAddress, branch);
//	}

	public boolean setPOwl(String branch) {
		return set(pOwlRootAddress, branch);
	}

	public boolean setEOwl(String branch) {
		return set(eOwlRootAddress, branch);
	}

//	public boolean set(String userAdd, String branch) {
//		boolean res = false;
//		File file = new File(userAdd);
//		if (!file.exists()) {
//			try {
//				// 创建版本库
//				Repository repository = GitUtil.createRepository(userAdd);
//				// commit
//				GitUtil.RecordUploadProjAt("set", userAdd, userAdd);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			res = true;
//		}
//		try {
//			if (!GitUtil.branchNameExist(branch, userAdd)) {	// 创建分支
//				GitUtil.createBranch(branch, userAdd);
//			}
//			GitUtil.gitCheckout(branch, userAdd);  // 切换分支
//			GitUtil.currentBranch(userAdd);  // 返回userAdd目录下的分支
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		return res;
//	}
	public boolean set(String userAdd, String branch) {
		boolean res = false;
		File dir = new File(userAdd);
		System.out.println("set:"+userAdd);
		// 判断address目录是否存在，不存在则创建
		if(!dir.exists() || !dir.isDirectory()) {
			System.out.println("不存在----新建");
			dir.mkdir();
			setRootProject(userAdd,branch);
		}
		res = true;
		return res;
	}
	private void setRootProject(String userAdd, String branch) {
		String projectXmlAddr = userAdd + "Project.xml";
		File xmlFile = new File(projectXmlAddr);
		try {
			if (!xmlFile.exists())
				xmlFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Document document = DocumentHelper.createDocument();;
		try {
			Element projectElement = document.addElement("project");
			Element titleElement = projectElement.addElement("title");
			titleElement.setText(branch);
			Element fileListElement = projectElement.addElement("fileList");
//			fileListElement.addElement("RequirementVersion");

			// 保存Project.xml文件
			StringWriter strWtr = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter xmlWriter = new XMLWriter(strWtr, format);
			xmlWriter.write(document);
			xmlFile.createNewFile();
			OutputFormat outputFormat=OutputFormat.createPrettyPrint();//输出格式
			outputFormat.setEncoding("utf-8");
			outputFormat.setIndent(true);
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile),outputFormat);
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// ===========保存文件到硬盘,不包括git操作===========
	// 保存pf文件到pfRootAddress
	public void addpfFile(MultipartFile file) {
		saveUploadFile(file, pfRootAddress);
	}

	// 保存pf文件到userAdd
	public void saveUploadFile(MultipartFile mf, String address) {
		String filePath = address + mf.getOriginalFilename();
		System.out.println("要创建的文件：" + filePath);

		// 判断address目录是否存在，不存在则创建
		File dir = new File(address);
		if(!dir.exists() && !dir.isDirectory()) {
			dir.mkdir();
		}

		File file = new File(filePath);
		try {
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			mf.transferTo(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ================= 保存项目 =======================
	// ==================保存项目===================
	// 切换分支，保存，并commit
	public boolean saveProject(String userAdd, Project project, String branch) {
		setProject(userAdd, branch);// 创建并切换分支
		userAdd += reqModelDir +"/";
		setProject(userAdd, branch);// 创建并切换分支
		saveProject(userAdd, branch, project);
//		try {// commit
//			GitUtil.RecordUploadProjAt("save", userAdd, ".");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return true;
	}

	// 只保存文件，不涉及git操作,保存在rootAddress目录下（传参确定rootAddress）
	public boolean saveProject(String userAdd, String branch, Project project) {
		versionInfo current_version = null;
		boolean result = true;
		System.out.println("saveProject address:" + userAdd);
		Document document = DocumentHelper.createDocument();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		
		Element projectElement = document.addElement("project");
		Element titleElement = projectElement.addElement("title");
		Element fileListElement = projectElement.addElement("fileList");
		Element intentDiagramElement = fileListElement.addElement("IntentDiagram");
		Element contextDiagramElement = fileListElement.addElement("ContextDiagram");
		Element problemDiagramElement = fileListElement.addElement("ProblemDiagram");
		
		String title = project.getTitle();
		titleElement.setText(title);
		IntentDiagram tmp_ID = project.getIntentDiagram();
		ContextDiagram tmp_CD = project.getContextDiagram();
		ProblemDiagram tmp_PD = project.getProblemDiagram();
		System.out.println("tmp_CD1"+tmp_CD);
		System.out.println("tmp_PD1"+tmp_PD);
		if (tmp_ID != null) {
			intentDiagramElement.setText("IntentDiagram");
			IntentDiagram tmp_save = saveIntentDiagram(userAdd, branch, tmp_ID);
			System.out.println("IntentDiagram1"+tmp_save);
		}
		if (tmp_CD != null) {
			contextDiagramElement.setText("ContextDiagram");
			ContextDiagram tmp_save = saveContextDiagram(userAdd,branch, tmp_CD);
			System.out.println("ContextDiagram1"+tmp_save);
		}
		if (tmp_PD != null) {
			problemDiagramElement.setText("ProblemDiagram");
			ProblemDiagram problemDiagram = saveProblemDiagram(userAdd,branch, tmp_PD);
			System.out.println("ProblemDiagram1"+problemDiagram);
		}
		List<ScenarioGraph> senarioGraphList = project.getScenarioGraphList();	
		System.out.println("senarioGraphList1"+senarioGraphList);
		if(senarioGraphList != null) {
			Element senarioGraphListElement = fileListElement.addElement("SenarioGraphList");
			for(ScenarioGraph sg: senarioGraphList) {
				Element senarioGraphElement = senarioGraphListElement.addElement("SenarioGraph");
				senarioGraphElement.addText(sg.getTitle());
				saveScenarioGraph(userAdd, sg, "haveRequirement");
			}
		}
		
		ScenarioGraph tmp_FSG = project.getFullScenarioGraph();
		if(tmp_FSG != null) {
			Element fullScenarioGraphElement = fileListElement.addElement("FullScenarioGraph");
			fullScenarioGraphElement.setText("FullScenarioGraph");
			saveScenarioGraph(userAdd, tmp_FSG, null);
		}
		
		List<ScenarioGraph> newSenarioGraphList = project.getNewScenarioGraphList();
		if(newSenarioGraphList != null) {
			Element newSenarioGraphListElement = fileListElement.addElement("NewSenarioGraphList");
			for(ScenarioGraph sg: newSenarioGraphList) {
				Element senarioGraphElement = newSenarioGraphListElement.addElement("SenarioGraph");
				senarioGraphElement.addText(sg.getTitle());
				saveScenarioGraph(userAdd, sg, null);
			}
		}
		
		List<SubProblemDiagram> subProblemDiagramList = project.getSubProblemDiagramList();
		if(subProblemDiagramList != null) {
			Element subProblemDiagramListElement = fileListElement.addElement("SubProblemDiagramList");
			for(SubProblemDiagram spd: subProblemDiagramList) {
				Element subProblemDiagramElement = subProblemDiagramListElement.addElement("SubProblemDiagram");
				subProblemDiagramElement.addText(spd.getTitle());
				saveSubProblemDiagram(userAdd,spd);
			}
		}

		// 保存追溯关系
		Element traceListElement = fileListElement.addElement("TraceList");
		List<Trace> traceList = project.getTraceList();
		if(traceList != null) {
			traceListElement.setText("TraceList");
			List<Trace> tmp_save = saveTraceList(userAdd, traceList);
		}

		// 保存数据依赖关系
		Element dataDependenceListElement = fileListElement.addElement("DataDependenceList");
		List<Dependence> dataDependenceList = project.getDataDependenceList();
		if(dataDependenceList != null) {
			dataDependenceListElement.setText("DataDependenceList");
			List<Dependence> tmp_save = saveDataDependenceList(userAdd, dataDependenceList);
		}

		// 保存控制依赖关系
		Element controlDependenceListElement = fileListElement.addElement("ControlDependenceList");
		List<Dependence> controlDependenceList = project.getControlDependenceList();
		if(controlDependenceList != null) {
			controlDependenceListElement.setText("ControlDependenceList");
			List<Dependence> tmp_save = saveControlDependenceList(userAdd, controlDependenceList);
		}

		StringWriter strWtr = new StringWriter();
		File xmlFile = new File(userAdd + "Project.xml");
		if(xmlFile.exists()==true) {
			xmlFile.delete();
		}
		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			xmlFile.createNewFile();
//			XMLWriter out = new XMLWriter(new FileWriter(xmlFile));
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile), format);
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
//		if(result) {
//			try {
//				current_version = GitUtil.currentVersion(userAdd);
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (GitAPIException e) {
//				e.printStackTrace();
//			}
//		}
		return true;
	}

	private IntentDiagram saveIntentDiagram(String userAdd, String projectAddress, IntentDiagram intentDiagram) {
		Document document = DocumentHelper.createDocument();
		File xmlFile = new File(userAdd + "/IntentDiagram.xml");
		Element intentDiagramElement = document.addElement("IntentDiagram");
		Element titleElement = intentDiagramElement.addElement("title");
		Element systemElement = intentDiagramElement.addElement("System");
		Element externalEntityListElement = intentDiagramElement.addElement("ExternalEntity");
		Element intentListElement = intentDiagramElement.addElement("Intent");
		Element constraintListElement = intentDiagramElement.addElement("Constraint");
		Element referenceListElement = intentDiagramElement.addElement("Reference");
		Element interfaceListElement = intentDiagramElement.addElement("Interface");
		titleElement.setText("IntentDiagram");
		ESystem system = intentDiagram.getSystem();
		List<ExternalEntity> externalEntityList = intentDiagram.getExternalEntityList();
		List<Intent> intentList = intentDiagram.getIntentList();
		List<Interface> interfaceList = intentDiagram.getInterfaceList();
		List<Constraint> constraintList = intentDiagram.getConstraintList();
		List<Reference> referenceList = intentDiagram.getReferenceList();
		if (system != null && system.getShortname() != null) {
			systemElement.addAttribute("system_name", system.getSystem_name().replaceAll("\n", "&#x000A"));
			systemElement.addAttribute("system_shortname", system.getSystem_shortName());
			StringBuffer re = new StringBuffer();
			re.append(system.getSystem_x());
			re.append(",");
			re.append(system.getSystem_y());
			re.append(",");
			re.append(system.getSystem_h());
			re.append(",");
			re.append(system.getSystem_w());
			systemElement.addAttribute("system_locality", re.toString());
		}
		if (externalEntityList.size() > 0) {
			for (int i = 0; i < externalEntityList.size(); i++) {
				ExternalEntity tmp_PD = externalEntityList.get(i);
				String externalentity_no = String.valueOf(tmp_PD.getExternalentity_no());
				String externalentity_name = tmp_PD.getExternalentity_name().replaceAll("\n", "&#x000A");
				String externalentity_shortname = tmp_PD.getExternalentity_shortname();
				StringBuffer re = new StringBuffer();
				re.append(tmp_PD.getExternalentity_x());
				re.append(",");
				re.append(tmp_PD.getExternalentity_y());
				re.append(",");
				re.append(tmp_PD.getExternalentity_h());
				re.append(",");
				re.append(tmp_PD.getExternalentity_w());
				String externalentity_locality = re.toString();
				Element externalEntityElement = externalEntityListElement.addElement("Element");
				externalEntityElement.addAttribute("externalentity_no", externalentity_no);
				externalEntityElement.addAttribute("externalentity_name", externalentity_name);
				externalEntityElement.addAttribute("externalentity_shortname", externalentity_shortname);
				externalEntityElement.addAttribute("externalentity_locality", externalentity_locality);
			}
		}
		if (intentList.size() > 0){
			for (int i = 0; i < intentList.size(); i++) {
				Intent tmp_req = intentList.get(i);
				String intent_no = String.valueOf(tmp_req.getIntent_no());
				String intent_context = tmp_req.getIntent_context().replaceAll("\n", "&#x000A");
				String intent_shortname = tmp_req.getIntent_shortname();
				StringBuffer re = new StringBuffer();
				re.append(tmp_req.getIntent_x());
				re.append(",");
				re.append(tmp_req.getIntent_y());
				re.append(",");
				re.append(tmp_req.getIntent_h());
				re.append(",");
				re.append(tmp_req.getIntent_w());
				String intent_locality = re.toString();
				Element intentElement = intentListElement.addElement("Element");
				intentElement.addAttribute("intent_no", intent_no);
				intentElement.addAttribute("intent_context", intent_context);
				intentElement.addAttribute("intent_shortname", intent_shortname);
				intentElement.addAttribute("intent_locality", intent_locality);
			}

		}
		if (interfaceList.size() > 0) {
			for (int i = 0; i < interfaceList.size(); i++) {
				Interface tmp_i = interfaceList.get(i);
				List<Phenomenon> phenomenonElementList = tmp_i.getPhenomenonList();
				String interface_no = String.valueOf(tmp_i.getInterface_no());
				String interface_name = tmp_i.getInterface_name();
				String interface_description = tmp_i.getInterface_description();
				String interface_from = tmp_i.getInterface_from().replaceAll("\n", "&#x000A");
				String interface_to = tmp_i.getInterface_to().replaceAll("\n", "&#x000A");
				StringBuffer re = new StringBuffer();
				re.append(tmp_i.getInterface_x1());
				re.append(",");
				re.append(tmp_i.getInterface_x2());
				re.append(",");
				re.append(tmp_i.getInterface_y1());
				re.append(",");
				re.append(tmp_i.getInterface_y1());

				String interface_locality = re.toString();

				Element interfaceElement = interfaceListElement.addElement("Element");
				interfaceElement.addAttribute("interface_description", interface_description);
				interfaceElement.addAttribute("interface_no", interface_no);
				interfaceElement.addAttribute("interface_locality", interface_locality);
				interfaceElement.addAttribute("interface_from", interface_from);
				interfaceElement.addAttribute("interface_to", interface_to);
				interfaceElement.addAttribute("interface_name", interface_name);
				if(phenomenonElementList != null) {
					for (int j = 0; j < phenomenonElementList.size(); j++) {
						Phenomenon tmp_p = phenomenonElementList.get(j);
						String phenomenon_no = String.valueOf(tmp_p.getPhenomenon_no());
						String phenomenon_name = tmp_p.getPhenomenon_name();
						String phenomenon_shortname = tmp_p.getPhenomenon_shortname();
						String phenomenon_type = tmp_p.getPhenomenon_type();
						String phenomenon_from = tmp_p.getPhenomenon_from();
						String phenomenon_to = tmp_p.getPhenomenon_to();
						Element phenomenonElement = interfaceElement.addElement("Phenomenon");
						phenomenonElement.addAttribute("phenomenon_no", phenomenon_no);
						phenomenonElement.addAttribute("phenomenon_name", phenomenon_name);
						phenomenonElement.addAttribute("phenomenon_shortname", phenomenon_shortname);
						phenomenonElement.addAttribute("phenomenon_type", phenomenon_type);
						phenomenonElement.addAttribute("phenomenon_from", phenomenon_from);
						phenomenonElement.addAttribute("phenomenon_to", phenomenon_to);
					}
				}
			}
		}
		if (constraintList.size() > 0) {
			for (int i = 0; i < constraintList.size(); i++) {
				Constraint tmp_c = constraintList.get(i);
				List<RequirementPhenomenon> phenomenonElementList = tmp_c.getPhenomenonList();
				String constraint_no = String.valueOf(tmp_c.getConstraint_no());
				String constraint_name = tmp_c.getConstraint_name();
				String constraint_description = tmp_c.getConstraint_description();
				String constraint_from = tmp_c.getConstraint_from().replaceAll("\n", "&#x000A");
				String constraint_to = tmp_c.getConstraint_to().replaceAll("\n", "&#x000A");
				StringBuffer re = new StringBuffer();
				re.append(tmp_c.getConstraint_x1());
				re.append(",");
				re.append(tmp_c.getConstraint_x2());
				re.append(",");
				re.append(tmp_c.getConstraint_y1());
				re.append(",");
				re.append(tmp_c.getConstraint_y1());

				String constraint_locality = re.toString();

				Element constraintElement = constraintListElement.addElement("Element");
				constraintElement.addAttribute("constraint_description", constraint_description);
				constraintElement.addAttribute("constraint_no", constraint_no);
				constraintElement.addAttribute("constraint_locality", constraint_locality);
				constraintElement.addAttribute("constraint_from", constraint_from);
				constraintElement.addAttribute("constraint_to", constraint_to);
				constraintElement.addAttribute("constraint_name", constraint_name);
				if(phenomenonElementList != null) {
					for (int j = 0; j < phenomenonElementList.size(); j++) {
						RequirementPhenomenon tmp_p = phenomenonElementList.get(j);
						String phenomenon_no = String.valueOf(tmp_p.getPhenomenon_no());
						String phenomenon_name = tmp_p.getPhenomenon_name();
						String phenomenon_shortname = tmp_p.getPhenomenon_shortname();
						String phenomenon_type = tmp_p.getPhenomenon_type();
						String phenomenon_from = tmp_p.getPhenomenon_from().replaceAll("\n", "&#x000A");
						String phenomenon_to = tmp_p.getPhenomenon_to().replaceAll("\n", "&#x000A");
						String phenomenon_constraint = tmp_p.getPhenomenon_constraint();
						String phenomenon_requirement = String.valueOf(tmp_p.getPhenomenon_requirement());
						Element phenomenonElement = constraintElement.addElement("Phenomenon");
						phenomenonElement.addAttribute("phenomenon_no", phenomenon_no);
						phenomenonElement.addAttribute("phenomenon_name", phenomenon_name);
						phenomenonElement.addAttribute("phenomenon_shortname", phenomenon_shortname);
						phenomenonElement.addAttribute("phenomenon_type", phenomenon_type);
						phenomenonElement.addAttribute("phenomenon_from", phenomenon_from);
						phenomenonElement.addAttribute("phenomenon_to", phenomenon_to);
						phenomenonElement.addAttribute("phenomenon_constraint", phenomenon_constraint);
						phenomenonElement.addAttribute("phenomenon_requirement", phenomenon_requirement);
					}
				}
			}
		}
		if (referenceList.size() > 0) {
			for (int i = 0; i < referenceList.size(); i++) {
				Reference tmp_c = referenceList.get(i);
				List<RequirementPhenomenon> phenomenonElementList = tmp_c.getPhenomenonList();
				String Reference_no = String.valueOf(tmp_c.getReference_no());
				String Reference_name = tmp_c.getReference_name();
				String Reference_description = tmp_c.getReference_description();
				String Reference_from = tmp_c.getReference_from().replaceAll("\n", "&#x000A");
				String Reference_to = tmp_c.getReference_to().replaceAll("\n", "&#x000A");
				StringBuffer re = new StringBuffer();
				re.append(tmp_c.getReference_x1());
				re.append(",");
				re.append(tmp_c.getReference_x2());
				re.append(",");
				re.append(tmp_c.getReference_y1());
				re.append(",");
				re.append(tmp_c.getReference_y1());

				String Reference_locality = re.toString();

				Element ReferenceElement = referenceListElement.addElement("Element");
				ReferenceElement.addAttribute("reference_description", Reference_description);
				ReferenceElement.addAttribute("reference_no", Reference_no);
				ReferenceElement.addAttribute("reference_locality", Reference_locality);
				ReferenceElement.addAttribute("reference_from", Reference_from);
				ReferenceElement.addAttribute("reference_to", Reference_to);
				ReferenceElement.addAttribute("reference_name", Reference_name);
				if(phenomenonElementList != null) {
					for (int j = 0; j < phenomenonElementList.size(); j++) {
						RequirementPhenomenon tmp_p = phenomenonElementList.get(j);
						String phenomenon_no = String.valueOf(tmp_p.getPhenomenon_no());
						String phenomenon_name = tmp_p.getPhenomenon_name();
						String phenomenon_shortname = tmp_p.getPhenomenon_shortname();
						String phenomenon_type = tmp_p.getPhenomenon_type();
						String phenomenon_from = tmp_p.getPhenomenon_from().replaceAll("\n", "&#x000A");
						String phenomenon_to = tmp_p.getPhenomenon_to().replaceAll("\n", "&#x000A");
						String phenomenon_constraint = tmp_p.getPhenomenon_constraint();
						String phenomenon_requirement = String.valueOf(tmp_p.getPhenomenon_requirement());
						Element phenomenonElement = ReferenceElement.addElement("Phenomenon");
						phenomenonElement.addAttribute("phenomenon_no", phenomenon_no);
						phenomenonElement.addAttribute("phenomenon_shortname", phenomenon_shortname);
						phenomenonElement.addAttribute("phenomenon_name", phenomenon_name);
						phenomenonElement.addAttribute("phenomenon_type", phenomenon_type);
						phenomenonElement.addAttribute("phenomenon_from", phenomenon_from);
						phenomenonElement.addAttribute("phenomenon_to", phenomenon_to);
						phenomenonElement.addAttribute("phenomenon_constraint", phenomenon_constraint);
						phenomenonElement.addAttribute("phenomenon_requirement", phenomenon_requirement);
					}
				}
			}
		}

		StringWriter strWtr = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xmlFile.exists() == true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
//			XMLWriter out = new XMLWriter(new FileWriter(xmlFile));
//			// XMLWriter out = new XMLWriter(new FileWriter(file), format);

			OutputFormat outputFormat=OutputFormat.createPrettyPrint();//输出格式
			outputFormat.setEncoding("utf-8");
			outputFormat.setIndent(true);
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile),outputFormat);
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return intentDiagram;
	}

	private ContextDiagram saveContextDiagram(String userAdd, String projectAddress, ContextDiagram contextDiagram) {
		Document document = DocumentHelper.createDocument();
		System.out.println("saveContextDiagram");
		File xmlFile = new File(userAdd + "/ContextDiagram.xml");
		Element contextDiagramElement = document.addElement("ContextDiagram");
		Element titleElement = contextDiagramElement.addElement("title");
		Element machineElement = contextDiagramElement.addElement("Machine");
		Element problemDomainListElement = contextDiagramElement.addElement("ProblemDomain");
		Element givenDomainListElement = problemDomainListElement.addElement("GivenDomain");
		Element designDomainListElement = problemDomainListElement.addElement("DesignDomain");
		Element interfaceListElement = contextDiagramElement.addElement("Interface");
		titleElement.setText("ContextDiagram");
		Machine machine = contextDiagram.getMachine();
		List<ProblemDomain> problemDomainList = contextDiagram.getProblemDomainList();
		List<Interface> interfaceList = contextDiagram.getInterfaceList();
		if (machine != null) {
			machineElement.addAttribute("machine_name", machine.getMachine_name().replaceAll("\n", "&#x000A"));
			machineElement.addAttribute("machine_shortname", machine.getMachine_shortName());
			StringBuffer re = new StringBuffer();
			re.append(machine.getMachine_x());
			re.append(",");
			re.append(machine.getMachine_y());
			re.append(",");
			re.append(machine.getMachine_h());
			re.append(",");
			re.append(machine.getMachine_w());
			machineElement.addAttribute("machine_locality", re.toString());
		}
		if (problemDomainList.size() > 0) {
			for (int i = 0; i < problemDomainList.size(); i++) {
				ProblemDomain tmp_PD = problemDomainList.get(i);
				String problemdomain_property = tmp_PD.getProblemdomain_property();
				String problemdomain_no = String.valueOf(tmp_PD.getProblemdomain_no());
				String problemdomain_name = tmp_PD.getProblemdomain_name().replaceAll("\n", "&#x000A");
				String problemdomain_shortname = tmp_PD.getProblemdomain_shortname();
				String problemdomain_type = tmp_PD.getProblemdomain_type();
				StringBuffer re = new StringBuffer();
				re.append(tmp_PD.getProblemdomain_x());
				re.append(",");
				re.append(tmp_PD.getProblemdomain_y());
				re.append(",");
				re.append(tmp_PD.getProblemdomain_h());
				re.append(",");
				re.append(tmp_PD.getProblemdomain_w());
				String problemdomain_locality = re.toString();
				if (problemdomain_property.equals("GivenDomain")) {
					Element givenDomainElement = givenDomainListElement.addElement("Element");
					givenDomainElement.addAttribute("problemdomain_no", problemdomain_no);
					givenDomainElement.addAttribute("problemdomain_name", problemdomain_name);
					givenDomainElement.addAttribute("problemdomain_shortname", problemdomain_shortname);
					givenDomainElement.addAttribute("problemdomain_type", problemdomain_type);
					givenDomainElement.addAttribute("problemdomain_locality", problemdomain_locality);
				} else {
					Element designDomainElement = designDomainListElement.addElement("Element");
					designDomainElement.addAttribute("problemdomain_no", problemdomain_no);
					designDomainElement.addAttribute("problemdomain_name", problemdomain_name);
					designDomainElement.addAttribute("problemdomain_shortname", problemdomain_shortname);
					designDomainElement.addAttribute("problemdomain_type", problemdomain_type);
					designDomainElement.addAttribute("problemdomain_locality", problemdomain_locality);
				}
			}
		}
		if (interfaceList.size() > 0) {
			for (int i = 0; i < interfaceList.size(); i++) {
				Interface tmp_i = interfaceList.get(i);
				List<Phenomenon> phenomenonElementList = tmp_i.getPhenomenonList();
				String interface_no = String.valueOf(tmp_i.getInterface_no());
				String interface_name = tmp_i.getInterface_name();
				String interface_description = tmp_i.getInterface_description();
				String interface_from = tmp_i.getInterface_from().replaceAll("\n", "&#x000A");
				String interface_to = tmp_i.getInterface_to().replaceAll("\n", "&#x000A");
				StringBuffer re = new StringBuffer();
				re.append(tmp_i.getInterface_x1());
				re.append(",");
				re.append(tmp_i.getInterface_x2());
				re.append(",");
				re.append(tmp_i.getInterface_y1());
				re.append(",");
				re.append(tmp_i.getInterface_y1());

				String interface_locality = re.toString();

				Element interfaceElement = interfaceListElement.addElement("Element");
				interfaceElement.addAttribute("interface_description", interface_description);
				interfaceElement.addAttribute("interface_no", interface_no);
				interfaceElement.addAttribute("interface_locality", interface_locality);
				interfaceElement.addAttribute("interface_from", interface_from);
				interfaceElement.addAttribute("interface_to", interface_to);
				interfaceElement.addAttribute("interface_name", interface_name);

				for (int j = 0; j < phenomenonElementList.size(); j++) {
					Phenomenon tmp_p = phenomenonElementList.get(j);
					String phenomenon_no = String.valueOf(tmp_p.getPhenomenon_no());
					String phenomenon_name = tmp_p.getPhenomenon_name();
					String phenomenon_type = tmp_p.getPhenomenon_type();
					String phenomenon_from = tmp_p.getPhenomenon_from();
					String phenomenon_to = tmp_p.getPhenomenon_to();
					Element phenomenonElement = interfaceElement.addElement("Phenomenon");
					phenomenonElement.addAttribute("phenomenon_no", phenomenon_no);
					phenomenonElement.addAttribute("phenomenon_name", phenomenon_name);
					phenomenonElement.addAttribute("phenomenon_type", phenomenon_type);
					phenomenonElement.addAttribute("phenomenon_from", phenomenon_from);
					phenomenonElement.addAttribute("phenomenon_to", phenomenon_to);
				}
			}
		}
		StringWriter strWtr = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xmlFile.exists() == true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile));
			// XMLWriter out = new XMLWriter(new FileWriter(file), format);
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contextDiagram;
	}

	private ProblemDiagram saveProblemDiagram(String userAdd, String projectAddress, ProblemDiagram problemDiagram) {
		Document document = DocumentHelper.createDocument();
		File xmlFile = new File(userAdd + "/ProblemDiagram.xml");
		Element problemDiagramElement = document.addElement("ProblemDiagram");
		Element contextDiagramElement = problemDiagramElement.addElement("ContextDiagram");
		Element titleElement = problemDiagramElement.addElement("title");
		Element requirementListElement = problemDiagramElement.addElement("Requirement");
		Element constraintListElement = problemDiagramElement.addElement("Constraint");
		Element referenceListElement = problemDiagramElement.addElement("Reference");
		contextDiagramElement.setText("ContextDiagram");
		titleElement.setText("ProblemDiagram");
		List<Requirement> requirementList = problemDiagram.getRequirementList();
		List<Constraint> constraintList = problemDiagram.getConstraintList();
		List<Reference> referenceList = problemDiagram.getReferenceList();

		for (int i = 0; i < requirementList.size(); i++) {
			Requirement tmp_req = requirementList.get(i);
			String requirement_no = String.valueOf(tmp_req.getRequirement_no());
			String requirement_context = tmp_req.getRequirement_context().replaceAll("\n", "&#x000A");
			StringBuffer re = new StringBuffer();
			re.append(tmp_req.getRequirement_x());
			re.append(",");
			re.append(tmp_req.getRequirement_y());
			re.append(",");
			re.append(tmp_req.getRequirement_h());
			re.append(",");
			re.append(tmp_req.getRequirement_w());
			String requirement_locality = re.toString();
			Element requirementElement = requirementListElement.addElement("Element");
			requirementElement.addAttribute("requirement_no", requirement_no);
			requirementElement.addAttribute("requirement_context", requirement_context);
			requirementElement.addAttribute("requirement_locality", requirement_locality);
		}

		if (constraintList.size() > 0) {
			for (int i = 0; i < constraintList.size(); i++) {
				Constraint tmp_c = constraintList.get(i);
				List<RequirementPhenomenon> phenomenonElementList = tmp_c.getPhenomenonList();
				String constraint_no = String.valueOf(tmp_c.getConstraint_no());
				String constraint_name = tmp_c.getConstraint_name();
				String constraint_description = tmp_c.getConstraint_description();
				String constraint_from = tmp_c.getConstraint_from().replaceAll("\n", "&#x000A");
				String constraint_to = tmp_c.getConstraint_to().replaceAll("\n", "&#x000A");
				StringBuffer re = new StringBuffer();
				re.append(tmp_c.getConstraint_x1());
				re.append(",");
				re.append(tmp_c.getConstraint_x2());
				re.append(",");
				re.append(tmp_c.getConstraint_y1());
				re.append(",");
				re.append(tmp_c.getConstraint_y1());

				String constraint_locality = re.toString();

				Element constraintElement = constraintListElement.addElement("Element");
				constraintElement.addAttribute("constraint_description", constraint_description);
				constraintElement.addAttribute("constraint_no", constraint_no);
				constraintElement.addAttribute("constraint_locality", constraint_locality);
				constraintElement.addAttribute("constraint_from", constraint_from);
				constraintElement.addAttribute("constraint_to", constraint_to);
				constraintElement.addAttribute("constraint_name", constraint_name);

				for (int j = 0; j < phenomenonElementList.size(); j++) {
					RequirementPhenomenon tmp_p = phenomenonElementList.get(j);
					String phenomenon_no = String.valueOf(tmp_p.getPhenomenon_no());
					String phenomenon_name = tmp_p.getPhenomenon_name();
					String phenomenon_type = tmp_p.getPhenomenon_type();
					String phenomenon_from = tmp_p.getPhenomenon_from().replaceAll("\n", "&#x000A");
					String phenomenon_to = tmp_p.getPhenomenon_to().replaceAll("\n", "&#x000A");
					String phenomenon_constraint = tmp_p.getPhenomenon_constraint();
					String phenomenon_requirement = String.valueOf(tmp_p.getPhenomenon_requirement());
					Element phenomenonElement = constraintElement.addElement("Phenomenon");
					phenomenonElement.addAttribute("phenomenon_no", phenomenon_no);
					phenomenonElement.addAttribute("phenomenon_name", phenomenon_name);
					phenomenonElement.addAttribute("phenomenon_type", phenomenon_type);
					phenomenonElement.addAttribute("phenomenon_from", phenomenon_from);
					phenomenonElement.addAttribute("phenomenon_to", phenomenon_to);
					phenomenonElement.addAttribute("phenomenon_constraint", phenomenon_constraint);
					phenomenonElement.addAttribute("phenomenon_requirement", phenomenon_requirement);
				}
			}
		}

		if (referenceList.size() > 0) {
			for (int i = 0; i < referenceList.size(); i++) {
				Reference tmp_c = referenceList.get(i);
				List<RequirementPhenomenon> phenomenonElementList = tmp_c.getPhenomenonList();
				String Reference_no = String.valueOf(tmp_c.getReference_no());
				String Reference_name = tmp_c.getReference_name();
				String Reference_description = tmp_c.getReference_description();
				String Reference_from = tmp_c.getReference_from().replaceAll("\n", "&#x000A");
				String Reference_to = tmp_c.getReference_to().replaceAll("\n", "&#x000A");
				StringBuffer re = new StringBuffer();
				re.append(tmp_c.getReference_x1());
				re.append(",");
				re.append(tmp_c.getReference_x2());
				re.append(",");
				re.append(tmp_c.getReference_y1());
				re.append(",");
				re.append(tmp_c.getReference_y1());

				String Reference_locality = re.toString();

				Element ReferenceElement = referenceListElement.addElement("Element");
				ReferenceElement.addAttribute("reference_description", Reference_description);
				ReferenceElement.addAttribute("reference_no", Reference_no);
				ReferenceElement.addAttribute("reference_locality", Reference_locality);
				ReferenceElement.addAttribute("reference_from", Reference_from);
				ReferenceElement.addAttribute("reference_to", Reference_to);
				ReferenceElement.addAttribute("reference_name", Reference_name);

				for (int j = 0; j < phenomenonElementList.size(); j++) {
					RequirementPhenomenon tmp_p = phenomenonElementList.get(j);
					String phenomenon_no = String.valueOf(tmp_p.getPhenomenon_no());
					String phenomenon_name = tmp_p.getPhenomenon_name();
					String phenomenon_type = tmp_p.getPhenomenon_type();
					String phenomenon_from = tmp_p.getPhenomenon_from().replaceAll("\n", "&#x000A");
					String phenomenon_to = tmp_p.getPhenomenon_to().replaceAll("\n", "&#x000A");
					String phenomenon_constraint = tmp_p.getPhenomenon_constraint();
					String phenomenon_requirement = String.valueOf(tmp_p.getPhenomenon_requirement());
					Element phenomenonElement = ReferenceElement.addElement("Phenomenon");
					phenomenonElement.addAttribute("phenomenon_no", phenomenon_no);
					phenomenonElement.addAttribute("phenomenon_name", phenomenon_name);
					phenomenonElement.addAttribute("phenomenon_type", phenomenon_type);
					phenomenonElement.addAttribute("phenomenon_from", phenomenon_from);
					phenomenonElement.addAttribute("phenomenon_to", phenomenon_to);
					phenomenonElement.addAttribute("phenomenon_constraint", phenomenon_constraint);
					phenomenonElement.addAttribute("phenomenon_requirement", phenomenon_requirement);
				}
			}
		}

		StringWriter strWtr = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xmlFile.exists() == true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile));
			// XMLWriter out = new XMLWriter(new FileWriter(file), format);
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return problemDiagram;
	}

	public boolean saveScenarioGraph(String address, ScenarioGraph SG, String string) {
		boolean res = false;
		//创建一个xml文档
		Document doc = DocumentHelper.createDocument();
		//用于格式化xml内容和设置头部标签
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		
		Element root = doc.addElement("ScenarioGraph");	//创建根节点
		
		Element titleElement = root.addElement("title");	//在root节点下创建一个名为title的节点
		titleElement.setText(SG.getTitle());
		
		if(string != null) {
			Element requirementElement = root.addElement("Requirement");	//在root节点下创建一个名为Requirement的节点
			if (SG.getRequirement() != null) {
				requirementElement.setText(SG.getRequirement().replaceAll("\n", "&#x000A"));
			}
		}
		
		Element nodeListElement = root.addElement("NodeList");	//在root节点下创建一个名为NodeList的节点
		Element intNodeElement = nodeListElement.addElement("IntNode");
		Element behIntListElement = intNodeElement.addElement("BehIntNode");
		Element connIntListElement = intNodeElement.addElement("ConnIntNode");
		Element expIntListElement = intNodeElement.addElement("ExpIntNode");

		List<Node> intNodeList = SG.getIntNodeList();
		Element intFromElement;
		Element intToElement;
		Element preConditionElement;
		Element postConditionElement;
		for(Node intNode: intNodeList) {
			List<Node> intFromNodeList = getIntFromNodeList(intNode, SG);
			List<Node> intToNodeList = getIntToNodeList(intNode, SG);
			Phenomenon pre_condition = intNode.getPre_condition();
			Phenomenon post_condition = intNode.getPost_condition();
			if(intNode.getNode_type().equals("BehInt")) {
				Element behIntElement = behIntListElement.addElement("Element");
				String locality = intNode.getNode_x() + "," + intNode.getNode_y();
				behIntElement.addAttribute("node_no", String.valueOf(intNode.getNode_no()));
				behIntElement.addAttribute("node_locality", locality);
				if(intFromNodeList.size() == 0) {
					intFromElement = behIntElement.addElement("from");
				}else {
					for(Node node: intFromNodeList) {
						intFromElement = behIntElement.addElement("from");
						locality = node.getNode_x() + "," + node.getNode_y();
						intFromElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
						intFromElement.addAttribute("node_type", node.getNode_type());
						intFromElement.addAttribute("node_locality", locality);
					}
				}
				if(intToNodeList.size() == 0) {
					intToElement = behIntElement.addElement("to");
				}else {
					for(Node node: intToNodeList) {
						intToElement = behIntElement.addElement("to");
						locality = node.getNode_x() + "," + node.getNode_y();
						intToElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
						intToElement.addAttribute("node_type", node.getNode_type());
						intToElement.addAttribute("node_locality", locality);
					}
				}
				if(pre_condition != null) {
					preConditionElement = behIntElement.addElement("pre_condition");
					preConditionElement.addAttribute("phenomenon_no", String.valueOf(pre_condition.getPhenomenon_no()));
					preConditionElement.addAttribute("phenomenon_name", pre_condition.getPhenomenon_name());
					preConditionElement.addAttribute("phenomenon_type", pre_condition.getPhenomenon_type());
					preConditionElement.addAttribute("phenomenon_from", pre_condition.getPhenomenon_from());
					preConditionElement.addAttribute("phenomenon_to", pre_condition.getPhenomenon_to());
				}
				if(post_condition != null) {
					postConditionElement = behIntElement.addElement("post_condition");
					postConditionElement.addAttribute("phenomenon_no", String.valueOf(post_condition.getPhenomenon_no()));
					postConditionElement.addAttribute("phenomenon_name", post_condition.getPhenomenon_name());
					postConditionElement.addAttribute("phenomenon_type", post_condition.getPhenomenon_type());
					postConditionElement.addAttribute("phenomenon_from", post_condition.getPhenomenon_from());
					postConditionElement.addAttribute("phenomenon_to", post_condition.getPhenomenon_to());
				}
			} else if(intNode.getNode_type().equals("ConnInt")) {
				Element connIntElement = connIntListElement.addElement("Element");
				String locality = intNode.getNode_x() + "," + intNode.getNode_y();
				connIntElement.addAttribute("node_no", String.valueOf(intNode.getNode_no()));
				connIntElement.addAttribute("node_locality", locality);
				if(intFromNodeList.size() == 0) {
					intFromElement = connIntElement.addElement("from");
				}else {
					for(Node node: intFromNodeList) {
						intFromElement = connIntElement.addElement("from");
						locality = node.getNode_x() + "," + node.getNode_y();
						intFromElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
						intFromElement.addAttribute("node_type", node.getNode_type());
						intFromElement.addAttribute("node_locality", locality);
					}
				}
				if(intToNodeList.size() == 0) {
					intToElement = connIntElement.addElement("to");
				}else {
					for(Node node: intToNodeList) {
						intToElement = connIntElement.addElement("to");
						locality = node.getNode_x() + "," + node.getNode_y();
						intToElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
						intToElement.addAttribute("node_type", node.getNode_type());
						intToElement.addAttribute("node_locality", locality);
					}
				}
				if(pre_condition != null) {
					preConditionElement = connIntElement.addElement("pre_condition");
					preConditionElement.addAttribute("phenomenon_no", String.valueOf(pre_condition.getPhenomenon_no()));
					preConditionElement.addAttribute("phenomenon_name", pre_condition.getPhenomenon_name());
					preConditionElement.addAttribute("phenomenon_type", pre_condition.getPhenomenon_type());
					preConditionElement.addAttribute("phenomenon_from", pre_condition.getPhenomenon_from());
					preConditionElement.addAttribute("phenomenon_to", pre_condition.getPhenomenon_to());
				}
				if(post_condition != null) {
					postConditionElement = connIntElement.addElement("post_condition");
					postConditionElement.addAttribute("phenomenon_no", String.valueOf(post_condition.getPhenomenon_no()));
					postConditionElement.addAttribute("phenomenon_name", post_condition.getPhenomenon_name());
					postConditionElement.addAttribute("phenomenon_type", post_condition.getPhenomenon_type());
					postConditionElement.addAttribute("phenomenon_from", post_condition.getPhenomenon_from());
					postConditionElement.addAttribute("phenomenon_to", post_condition.getPhenomenon_to());
				}
			} else if(intNode.getNode_type().equals("ExpInt")) {
				Element expIntElement = expIntListElement.addElement("Element");
				String locality = intNode.getNode_x() + "," + intNode.getNode_y();
				expIntElement.addAttribute("node_no", String.valueOf(intNode.getNode_no()));
				expIntElement.addAttribute("node_locality", locality);
				if(intFromNodeList.size() == 0) {
					intFromElement = expIntElement.addElement("from");
				}else {
					for(Node node: intFromNodeList) {
						intFromElement = expIntElement.addElement("from");
						locality = node.getNode_x() + "," + node.getNode_y();
						intFromElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
						intFromElement.addAttribute("node_type", node.getNode_type());
						intFromElement.addAttribute("node_locality", locality);
					}
				}
				if(intToNodeList.size() == 0) {
					intToElement = expIntElement.addElement("to");
				}else {
					for(Node node: intToNodeList) {
						intToElement = expIntElement.addElement("to");
						locality = node.getNode_x() + "," + node.getNode_y();
						intToElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
						intToElement.addAttribute("node_type", node.getNode_type());
						intToElement.addAttribute("node_locality", locality);
					}
				}
				if(pre_condition != null) {
					preConditionElement = expIntElement.addElement("pre_condition");
					preConditionElement.addAttribute("phenomenon_no", String.valueOf(pre_condition.getPhenomenon_no()));
					preConditionElement.addAttribute("phenomenon_name", pre_condition.getPhenomenon_name());
					preConditionElement.addAttribute("phenomenon_type", pre_condition.getPhenomenon_type());
					preConditionElement.addAttribute("phenomenon_from", pre_condition.getPhenomenon_from());
					preConditionElement.addAttribute("phenomenon_to", pre_condition.getPhenomenon_to());
				}
				if(post_condition != null) {
					postConditionElement = expIntElement.addElement("post_condition");
					postConditionElement.addAttribute("phenomenon_no", String.valueOf(post_condition.getPhenomenon_no()));
					postConditionElement.addAttribute("phenomenon_name", post_condition.getPhenomenon_name());
					postConditionElement.addAttribute("phenomenon_type", post_condition.getPhenomenon_type());
					postConditionElement.addAttribute("phenomenon_from", post_condition.getPhenomenon_from());
					postConditionElement.addAttribute("phenomenon_to", post_condition.getPhenomenon_to());
				}
			} 
		}
		
		Element ctrlNodeElement = nodeListElement.addElement("ControlNode");
		Element startNodeListElement =  ctrlNodeElement.addElement("StartNode");
		Element endNodeListElement =  ctrlNodeElement.addElement("EndNode");
		Element decisionNodeListElement =  ctrlNodeElement.addElement("DecisionNode");
		Element mergeNodeListElement =  ctrlNodeElement.addElement("MergeNode");
		Element branchNodeListElement =  ctrlNodeElement.addElement("BranchNode");
		Element delayNodeListElement = ctrlNodeElement.addElement("DelayNode");
		
		List<CtrlNode> ctrlNodeList = SG.getCtrlNodeList();
		Element nodeElement;
		Element fromElement;
		Element toElement;
		if(ctrlNodeList != null) {
			for(CtrlNode ctrlNode: ctrlNodeList) {
				if(ctrlNode == null || ctrlNode.getNode_type() == null) {
					continue;
				}
				List<Node> fromNodeList = getFromNodeList(ctrlNode, SG);
				List<Node> toNodeList = getToNodeList(ctrlNode, SG);
				switch (ctrlNode.getNode_type()){
				case "Start":
					nodeElement =  startNodeListElement.addElement("Element");
					String locality = ctrlNode.getNode_x() + "," + ctrlNode.getNode_y();
					nodeElement.addAttribute("node_no", String.valueOf(ctrlNode.getNode_no()));
					nodeElement.addAttribute("node_locality", locality);
					if(fromNodeList.size() == 0) {
						fromElement = nodeElement.addElement("from");
					}else {
						for(Node node: fromNodeList) {
							fromElement = nodeElement.addElement("from");
							locality = node.getNode_x() + "," + node.getNode_y();
							fromElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							fromElement.addAttribute("node_type", node.getNode_type());
							fromElement.addAttribute("node_locality", locality);
						}
					}
					if(toNodeList.size() == 0) {
						toElement = nodeElement.addElement("to");
					}else {
						for(Node node: toNodeList) {
							toElement = nodeElement.addElement("to");
							locality = node.getNode_x() + "," + node.getNode_y();
							toElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							toElement.addAttribute("node_type", node.getNode_type());
							toElement.addAttribute("node_locality", locality);
						}
					}
					break;
				case "End":
					nodeElement =  endNodeListElement.addElement("Element");
					locality = ctrlNode.getNode_x() + "," + ctrlNode.getNode_y();
					nodeElement.addAttribute("node_no", String.valueOf(ctrlNode.getNode_no()));
					nodeElement.addAttribute("node_locality", locality);
					if(fromNodeList.size() == 0) {
						fromElement = nodeElement.addElement("from");
					}else {
						for(Node node: fromNodeList) {
							fromElement = nodeElement.addElement("from");
							locality = node.getNode_x() + "," + node.getNode_y();
							fromElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							fromElement.addAttribute("node_type", node.getNode_type());
							fromElement.addAttribute("node_locality", locality);
						}
					}
					if(toNodeList.size() == 0) {
						toElement = nodeElement.addElement("to");
					}else {
						for(Node node: toNodeList) {
							toElement = nodeElement.addElement("to");
							locality = node.getNode_x() + "," + node.getNode_y();
							toElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							toElement.addAttribute("node_type", node.getNode_type());
							toElement.addAttribute("node_locality", locality);
						}
					}
					break;
				case "Decision":
					nodeElement =  decisionNodeListElement.addElement("Element");
					locality = ctrlNode.getNode_x() + "," + ctrlNode.getNode_y();
					nodeElement.addAttribute("node_no", String.valueOf(ctrlNode.getNode_no()));
					nodeElement.addAttribute("node_locality", locality);
					nodeElement.addAttribute("node_text", ctrlNode.getNode_text());
//					nodeElement.addAttribute("node_consition1", ctrlNode.getNode_consition1());
//					nodeElement.addAttribute("node_consition2", ctrlNode.getNode_consition2());
					if(fromNodeList.size() == 0) {
						fromElement = nodeElement.addElement("from");
					}else {
						for(Node node: fromNodeList) {
							fromElement = nodeElement.addElement("from");
							locality = node.getNode_x() + "," + node.getNode_y();
							fromElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							fromElement.addAttribute("node_type", node.getNode_type());
							fromElement.addAttribute("node_locality", locality);
						}
					}
					if(toNodeList.size() == 0) {
						toElement = nodeElement.addElement("to");
					}else {
						for(Node node: toNodeList) {
							toElement = nodeElement.addElement("to");
							locality = node.getNode_x() + "," + node.getNode_y();
							toElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							toElement.addAttribute("node_type", node.getNode_type());
							toElement.addAttribute("node_locality", locality);
						}
					}
					break;
				case "Merge":
					nodeElement =  mergeNodeListElement.addElement("Element");
					locality = ctrlNode.getNode_x() + "," + ctrlNode.getNode_y();
					nodeElement.addAttribute("node_no", String.valueOf(ctrlNode.getNode_no()));
					nodeElement.addAttribute("node_locality", locality);
					if(fromNodeList.size() == 0) {
						fromElement = nodeElement.addElement("from");
					}else {
						for(Node node: fromNodeList) {
							fromElement = nodeElement.addElement("from");
							locality = node.getNode_x() + "," + node.getNode_y();
							fromElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							fromElement.addAttribute("node_type", node.getNode_type());
							fromElement.addAttribute("node_locality", locality);
						}
					}
					if(toNodeList.size() == 0) {
						toElement = nodeElement.addElement("to");
					}else {
						for(Node node: toNodeList) {
							toElement = nodeElement.addElement("to");
							locality = node.getNode_x() + "," + node.getNode_y();
							toElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							toElement.addAttribute("node_type", node.getNode_type());
							toElement.addAttribute("node_locality", locality);
						}
					}
					break;
				case "Branch":
					nodeElement =  branchNodeListElement.addElement("Element");
					locality = ctrlNode.getNode_x() + "," + ctrlNode.getNode_y();
					nodeElement.addAttribute("node_no", String.valueOf(ctrlNode.getNode_no()));
					nodeElement.addAttribute("node_locality", locality);
					if(fromNodeList.size() == 0) {
						fromElement = nodeElement.addElement("from");
					}else {
						for(Node node: fromNodeList) {
							fromElement = nodeElement.addElement("from");
							locality = node.getNode_x() + "," + node.getNode_y();
							fromElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							fromElement.addAttribute("node_type", node.getNode_type());
							fromElement.addAttribute("node_locality", locality);
						}
					}
					if(toNodeList.size() == 0) {
						toElement = nodeElement.addElement("to");
					}else {
						for(Node node: toNodeList) {
							toElement = nodeElement.addElement("to");
							locality = node.getNode_x() + "," + node.getNode_y();
							toElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							toElement.addAttribute("node_type", node.getNode_type());
							toElement.addAttribute("node_locality", locality);
						}
					}
					break;
				case "Delay":
					nodeElement = delayNodeListElement.addElement("Element");
					locality = ctrlNode.getNode_x() + "," + ctrlNode.getNode_y();
					nodeElement.addAttribute("node_no", String.valueOf(ctrlNode.getNode_no()));
					nodeElement.addAttribute("node_locality", locality);
					nodeElement.addAttribute("node_text", ctrlNode.getNode_text());
					nodeElement.addAttribute("delay_type", ctrlNode.getDelay_type());
					if(fromNodeList.size() == 0) {
						fromElement = nodeElement.addElement("from");
					}else {
						for(Node node: fromNodeList) {
							fromElement = nodeElement.addElement("from");
							locality = node.getNode_x() + "," + node.getNode_y();
							fromElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							fromElement.addAttribute("node_type", node.getNode_type());
							fromElement.addAttribute("node_locality", locality);
						}
					}
					if(toNodeList.size() == 0) {
						toElement = nodeElement.addElement("to");
					}else {
						for(Node node: toNodeList) {
							toElement = nodeElement.addElement("to");
							locality = node.getNode_x() + "," + node.getNode_y();
							toElement.addAttribute("node_no", String.valueOf(node.getNode_no()));
							toElement.addAttribute("node_type", node.getNode_type());
							toElement.addAttribute("node_locality", locality);
						}
					}
					break;
				}
			}
		}
		
		Element lineListElement = root.addElement("LineList");	//在root节点下创建一个名为LineList的节点
		Element behOrderListElement = lineListElement.addElement("BehOrder");
		Element behEnableListElement = lineListElement.addElement("BehEnable");
		Element synchronyListElement = lineListElement.addElement("Synchrony");
		Element expOrderListElement = lineListElement.addElement("ExpOrder");
		Element expEnableListElement = lineListElement.addElement("ExpEnable");
		
		List<Line> lineList = SG.getLineList();
		if(lineList != null) {
			for(Line line: lineList) {
				Element lineElement;
				String line_no = String.valueOf(line.getLine_no());
				String from_no = String.valueOf(line.getFromNode().getNode_no());
				String from_type = line.getFromNode().getNode_type();
				String from_locality = line.getFromNode().getNode_x() + "," + line.getFromNode().getNode_y();
				String to_locality = line.getToNode().getNode_x() + "," + line.getToNode().getNode_y();
				String to_no = String.valueOf(line.getToNode().getNode_no());
				String to_type = line.getToNode().getNode_type();
				switch(line.getLine_type()) {
				case "BehOrder":
					lineElement = behOrderListElement.addElement("Element");
					lineElement.addAttribute("line_no", line_no);
					lineElement.addAttribute("turnings", line.getTurnings());
					lineElement.addAttribute("from_locality", from_locality);
					lineElement.addAttribute("from_no", from_no);
					lineElement.addAttribute("from_type", from_type);
					lineElement.addAttribute("to_locality", to_locality);
					lineElement.addAttribute("to_no", to_no);
					lineElement.addAttribute("to_type", to_type);
					lineElement.addAttribute("line_condition", line.getCondition());
					break;
				case "BehEnable":
					lineElement = behEnableListElement.addElement("Element");
					lineElement.addAttribute("line_no", line_no);
					lineElement.addAttribute("turnings", line.getTurnings());
					lineElement.addAttribute("from_locality", from_locality);
					lineElement.addAttribute("from_no", from_no);
					lineElement.addAttribute("from_type", from_type);
					lineElement.addAttribute("to_locality", to_locality);
					lineElement.addAttribute("to_no", to_no);
					lineElement.addAttribute("to_type", to_type);
					lineElement.addAttribute("line_condition", line.getCondition());
					break;
				case "Synchrony":
					lineElement = synchronyListElement.addElement("Element");
					lineElement.addAttribute("line_no", line_no);
					lineElement.addAttribute("turnings", line.getTurnings());
					lineElement.addAttribute("from_locality", from_locality);
					lineElement.addAttribute("from_no", from_no);
					lineElement.addAttribute("from_type", from_type);
					lineElement.addAttribute("to_locality", to_locality);
					lineElement.addAttribute("to_no", to_no);
					lineElement.addAttribute("to_type", to_type);
					lineElement.addAttribute("line_condition", line.getCondition());
					break;
				case "ExpOrder":
					lineElement = expOrderListElement.addElement("Element");
					lineElement.addAttribute("line_no", line_no);
					lineElement.addAttribute("turnings", line.getTurnings());
					lineElement.addAttribute("from_locality", from_locality);
					lineElement.addAttribute("from_no", from_no);
					lineElement.addAttribute("from_type", from_type);
					lineElement.addAttribute("to_locality", to_locality);
					lineElement.addAttribute("to_no", to_no);
					lineElement.addAttribute("to_type", to_type);
					lineElement.addAttribute("line_condition", line.getCondition());
					break;
				case "ExpEnable":
					lineElement = expEnableListElement.addElement("Element");
					lineElement.addAttribute("line_no", line_no);
					lineElement.addAttribute("turnings", line.getTurnings());
					lineElement.addAttribute("from_locality", from_locality);
					lineElement.addAttribute("from_no", from_no);
					lineElement.addAttribute("from_type", from_type);
					lineElement.addAttribute("to_locality", to_locality);
					lineElement.addAttribute("to_no", to_no);
					lineElement.addAttribute("to_type", to_type);
					lineElement.addAttribute("line_condition", line.getCondition());
					break;
				}
			}
		}
//		File xmlFile = new File(rootAddress + projectAddress + "/" + SG.getTitle() + ".xml");
		File xmlFile = new File(address + SG.getTitle() + ".xml");
		if(xmlFile.exists() == true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
			//创建一个输出流对象
			Writer out = new FileWriter(xmlFile);
			//创建一个dom4j创建xml的对象
			XMLWriter writer = new XMLWriter(out, format);
			//调用write方法将doc文档写到指定路径
			writer.write(doc);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private void saveSubProblemDiagram(String address, SubProblemDiagram spd) {
		Document document = DocumentHelper.createDocument();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		
		Element spdElement = document.addElement("SubProblemDiagram");
		Element titleElement = spdElement.addElement("title");
		Element machineElement = spdElement.addElement("Machine");
		Element problemDomainListElement = spdElement.addElement("ProblemDomain");
		Element requireElement = spdElement.addElement("Requirement");
		Element interfaceListElement = spdElement.addElement("Interface");
		Element constraintListElement = spdElement.addElement("Constraint");
		Element ReferenceListElement = spdElement.addElement("Reference");
		
		titleElement.addText(spd.getTitle());
		
		Machine machine = spd.getMachine();
		String locality = machine.getMachine_x() + "," + machine.getMachine_y() 
							+ "," + machine.getMachine_h() + "," + machine.getMachine_w();
		machineElement.addAttribute("machine_name", machine.getMachine_name().replaceAll("\n", "&#x000A"));
		machineElement.addAttribute("machine_shortname", machine.getMachine_shortName());
		machineElement.addAttribute("machine_locality", locality);
		
		Element givenDomainListElement = problemDomainListElement.addElement("GivenDomain");
		Element designDomainListElement = problemDomainListElement.addElement("DesignDomain");
		List<ProblemDomain> problemDomainList = spd.getProblemDomainList();
		for(ProblemDomain problemDomain: problemDomainList) {
			Element problemDomainElement;
			locality = problemDomain.getProblemdomain_x() + "," + problemDomain.getProblemdomain_y()
								+ "," + problemDomain.getProblemdomain_h() + "," + problemDomain.getProblemdomain_w(); 
			if(problemDomain.getProblemdomain_property().equals("GivenDomain")) {
				problemDomainElement = givenDomainListElement.addElement("Element");
				problemDomainElement.addAttribute("problemdomain_no", String.valueOf(problemDomain.getProblemdomain_no()));
				problemDomainElement.addAttribute("problemdomain_name", problemDomain.getProblemdomain_name().replaceAll("\n", "&#x000A"));
				problemDomainElement.addAttribute("problemdomain_shortname", problemDomain.getProblemdomain_shortname());
				problemDomainElement.addAttribute("problemdomain_type", problemDomain.getProblemdomain_type());
				problemDomainElement.addAttribute("problemdomain_locality", locality);
			}else {
				problemDomainElement = designDomainListElement.addElement("Element");
				problemDomainElement.addAttribute("problemdomain_no", String.valueOf(problemDomain.getProblemdomain_no()));
				problemDomainElement.addAttribute("problemdomain_name", problemDomain.getProblemdomain_name().replaceAll("\n", "&#x000A"));
				problemDomainElement.addAttribute("problemdomain_shortname", problemDomain.getProblemdomain_shortname());
				problemDomainElement.addAttribute("problemdomain_type", problemDomain.getProblemdomain_type());
				problemDomainElement.addAttribute("problemdomain_locality", locality);
			}
		}
		
		Requirement requirement = spd.getRequirement();
		locality = requirement.getRequirement_x() + "," + requirement.getRequirement_y()
				 + "," + requirement.getRequirement_h() + "," + requirement.getRequirement_w();
		requireElement.addAttribute("requirement_no", String.valueOf(requirement.getRequirement_no()));
		requireElement.addAttribute("requirement_context", requirement.getRequirement_context().replaceAll("\n", "&#x000A"));
		requireElement.addAttribute("requirement_locality", locality);
		
		List<Interface> interfaceList = spd.getInterfaceList();
		for(Interface inte: interfaceList) {
			Element interfaceElement = interfaceListElement.addElement("Element");
			locality = inte.getInterface_x1() + "," + inte.getInterface_x2() + "," + inte.getInterface_y1() + "," + inte.getInterface_y2();
			interfaceElement.addAttribute("interface_no", String.valueOf(inte.getInterface_no()));
			interfaceElement.addAttribute("interface_name", inte.getInterface_name());
			interfaceElement.addAttribute("interface_description", inte.getInterface_description());
			interfaceElement.addAttribute("interface_locality", locality);
			interfaceElement.addAttribute("interface_from", inte.getInterface_from().replaceAll("\n", "&#x000A"));
			interfaceElement.addAttribute("interface_to", inte.getInterface_to().replaceAll("\n", "&#x000A"));
			
			List<Phenomenon> phenomenonList = inte.getPhenomenonList();
			for(Phenomenon phenomenon: phenomenonList) {
				Element phenomenonElement = interfaceElement.addElement("Phenomenon");
				phenomenonElement.addAttribute("phenomenon_no", String.valueOf(phenomenon.getPhenomenon_no()));
				phenomenonElement.addAttribute("phenomenon_name", phenomenon.getPhenomenon_name());
				phenomenonElement.addAttribute("phenomenon_type", phenomenon.getPhenomenon_type());
				phenomenonElement.addAttribute("phenomenon_from", phenomenon.getPhenomenon_from().replaceAll("\n", "&#x000A"));
				phenomenonElement.addAttribute("phenomenon_to", phenomenon.getPhenomenon_to().replaceAll("\n", "&#x000A"));
			}
		}
		List<Constraint> constraintList = spd.getConstraintList();
		for(Constraint constraint: constraintList) {
			Element constraintElement = constraintListElement.addElement("Element");
			locality = constraint.getConstraint_x1() + "," + constraint.getConstraint_x2() + "," + constraint.getConstraint_y1() + "," + constraint.getConstraint_y2();
			constraintElement.addAttribute("constraint_no", String.valueOf(constraint.getConstraint_no()));
			constraintElement.addAttribute("constraint_name", constraint.getConstraint_name());
			constraintElement.addAttribute("constraint_description", constraint.getConstraint_description());
			constraintElement.addAttribute("constraint_constraint", constraint.getConstraint_constraint());
			constraintElement.addAttribute("constraint_locality", locality);
			constraintElement.addAttribute("constraint_from", constraint.getConstraint_from().replaceAll("\n", "&#x000A"));
			constraintElement.addAttribute("constraint_to", constraint.getConstraint_to().replaceAll("\n", "&#x000A"));
			
			List<RequirementPhenomenon> phenomenonList = constraint.getPhenomenonList();
			for(RequirementPhenomenon phenomenon: phenomenonList) {
				Element phenomenonElement = constraintElement.addElement("Phenomenon");
				phenomenonElement.addAttribute("phenomenon_no", String.valueOf(phenomenon.getPhenomenon_no()));
				phenomenonElement.addAttribute("phenomenon_name", phenomenon.getPhenomenon_name());
				phenomenonElement.addAttribute("phenomenon_type", phenomenon.getPhenomenon_type());
				phenomenonElement.addAttribute("phenomenon_from", phenomenon.getPhenomenon_from().replaceAll("\n", "&#x000A"));
				phenomenonElement.addAttribute("phenomenon_to", phenomenon.getPhenomenon_to().replaceAll("\n", "&#x000A"));
				phenomenonElement.addAttribute("phenomenon_constraint", phenomenon.getPhenomenon_constraint());
				phenomenonElement.addAttribute("phenomenon_requirement", String.valueOf(phenomenon.getPhenomenon_requirement()));
			}
		}
		
		List<Reference> referenceList = spd.getReferenceList();
		for(Reference reference: referenceList) {
			Element referenceElement = ReferenceListElement.addElement("Element");
			locality = reference.getReference_x1() + "," + reference.getReference_x2() + "," + reference.getReference_y1() + "," + reference.getReference_y2();
			referenceElement.addAttribute("reference_no", String.valueOf(reference.getReference_no()));
			referenceElement.addAttribute("reference_name", reference.getReference_name());
			referenceElement.addAttribute("reference_description", reference.getReference_description());
			referenceElement.addAttribute("reference_constraint", reference.getReference_constraint());
			referenceElement.addAttribute("reference_locality", locality);
			referenceElement.addAttribute("reference_from", reference.getReference_from().replaceAll("\n", "&#x000A"));
			referenceElement.addAttribute("reference_to", reference.getReference_to().replaceAll("\n", "&#x000A"));
			
			List<RequirementPhenomenon> phenomenonList = reference.getPhenomenonList();
			for(RequirementPhenomenon phenomenon: phenomenonList) {
				Element phenomenonElement = referenceElement.addElement("Phenomenon");
				phenomenonElement.addAttribute("phenomenon_no", String.valueOf(phenomenon.getPhenomenon_no()));
				phenomenonElement.addAttribute("phenomenon_name", phenomenon.getPhenomenon_name());
				phenomenonElement.addAttribute("phenomenon_type", phenomenon.getPhenomenon_type());
				phenomenonElement.addAttribute("phenomenon_from", phenomenon.getPhenomenon_from().replaceAll("\n", "&#x000A"));
				phenomenonElement.addAttribute("phenomenon_to", phenomenon.getPhenomenon_to().replaceAll("\n", "&#x000A"));
				phenomenonElement.addAttribute("phenomenon_constraint", phenomenon.getPhenomenon_constraint());
				phenomenonElement.addAttribute("phenomenon_requirement", String.valueOf(phenomenon.getPhenomenon_requirement()));
			}
		}
		
//		File xmlFile = new File(rootAddress + projectAddress + "/" + spd.getTitle() + ".xml");
		File xmlFile = new File(address + spd.getTitle() + ".xml");
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

	private List<Trace> saveTraceList(String address, List<Trace> traceList) {
		Document document = DocumentHelper.createDocument();
		File xmlFile = new File(address + "TraceList.xml");
		Element traceListElement = document.addElement("TraceList");
		for(Trace trace: traceList) {
			Element traceElement = traceListElement.addElement("Trace");
			Element traceSourceElement = traceElement.addElement("Source");
			Element traceTargetListElement = traceElement.addElement("TargetList");
			Element traceTypeElement = traceElement.addElement("Type");
			traceSourceElement.setText(trace.getSource());
			for(String target: trace.getTarget()) {
				Element traceTargetElement = traceTargetListElement.addElement("Target");
				traceTargetElement.setText(target);
			}
			traceTypeElement.setText(trace.getType());
		}

		StringWriter strWtr = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xmlFile.exists() == true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile));
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return traceList;
	}

	private List<Dependence> saveDataDependenceList(String address, List<Dependence> dataDependenceList) {
		Document document = DocumentHelper.createDocument();
		File xmlFile = new File(address + "DataDependenceList.xml");
		Element dataDependenceListElement = document.addElement("DataDependenceList");
		for(Dependence dependence: dataDependenceList) {
			Element dataDependenceElement = dataDependenceListElement.addElement("DataDependence");
			Element dependenceSourceElement = dataDependenceElement.addElement("Source");
			Element dependenceDataListElement = dataDependenceElement.addElement("DataList");
			Element dependenceTargetElement = dataDependenceElement.addElement("Target");
			dependenceSourceElement.setText(dependence.getSource());
			for(String data: dependence.getData()) {
				Element dependenceDataElement = dependenceDataListElement.addElement("Data");
				dependenceDataElement.setText(data);
			}
			dependenceTargetElement.setText(dependence.getTarget());
		}

		StringWriter strWtr = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xmlFile.exists() == true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile));
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataDependenceList;
	}

	private List<Dependence> saveControlDependenceList(String address, List<Dependence> controlDependenceList) {
		Document document = DocumentHelper.createDocument();
		File xmlFile = new File(address + "ControlDependenceList.xml");
		Element controlDependenceListElement = document.addElement("ControlDependenceList");
		for(Dependence dependence: controlDependenceList) {
			Element controlDependenceElement = controlDependenceListElement.addElement("ControlDependence");
			Element dependenceSourceElement = controlDependenceElement.addElement("Source");
			Element dependenceDeviceListElement = controlDependenceElement.addElement("DeviceList");
			Element dependenceTargetElement = controlDependenceElement.addElement("Target");
			dependenceSourceElement.setText(dependence.getSource());
			for(String dev: dependence.getData()) {
				Element dependenceDeviceElement = dependenceDeviceListElement.addElement("Device");
				dependenceDeviceElement.setText(dev);
			}
			dependenceTargetElement.setText(dependence.getTarget());
		}

		StringWriter strWtr = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter xmlWriter = new XMLWriter(strWtr, format);
		try {
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xmlFile.exists() == true) {
			xmlFile.delete();
		}
		try {
			xmlFile.createNewFile();
			XMLWriter out = new XMLWriter(new FileWriter(xmlFile));
			out.write(document);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return controlDependenceList;
	}
	
	// ===================获取项目=====================
	// ==================从xml文件中读取Project====================
	public Project getProject(String userAdd, String branch, String version) {
		Project project = new Project();
		SAXReader saxReader = new SAXReader();
		String addressDir = userAdd + version + "/";
		String projectXmlAddr = userAdd + "Project.xml";
//		List<versionInfo> versions = searchVersionInfo(userAdd, branch);
//		try {
//			GitUtil.gitCheckout(branch, userAdd);  // 切换分支
//			GitUtil.rollback(branch, userAdd, branch, version, versions);  // 回滚到特定的版本
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		try {
			File projectDir = new File(userAdd);
			if(!projectDir.exists()){
				return null;
			}
			File xmlFile = new File(projectXmlAddr);
			Document document = saxReader.read(xmlFile);
			Element projectElement = document.getRootElement();
			Element titleElement = projectElement.element("title");
			Element fileListElement = projectElement.element("fileList");

			List<?> reqVerElementList = fileListElement.elements("RequirementVersion");
			if(reqVerElementList != null) {
				boolean verexist = false;
				for(Object ve: reqVerElementList) {
					Element verElem = (Element) ve;
					Element idElement = verElem.element("id");
					if(version.equals(idElement.getText())) {
						verexist = true;
						Element reqModelListElement = verElem.element("RequirementsModelList");
						Element intentDiagramElement = reqModelListElement.element("IntentDiagram");
						Element contextDiagramElement = reqModelListElement.element("ContextDiagram");
						Element problemDiagramElement = reqModelListElement.element("ProblemDiagram");
						Element senarioGraphListElement = reqModelListElement.element("ScenarioGraphList");
						Element fullSenarioGraphElement = reqModelListElement.element("FullScenarioGraph");
						Element newSenarioGraphListElement = reqModelListElement.element("NewScenarioGraphList");
						Element subProblemDiagramListElement = reqModelListElement.element("SubProblemDiagramList");
						Element traceListElement = reqModelListElement.element("TraceList");
						Element dataDependenceListElement = reqModelListElement.element("DataDependenceList");
						Element controlDependenceListElement = reqModelListElement.element("ControlDependenceList");

						String title = titleElement.getText();
						IntentDiagram intentDiagram = getIntentDiagram(addressDir, branch, intentDiagramElement.getText());
						ContextDiagram contextDiagram = getContextDiagram(addressDir, branch, contextDiagramElement.getText());
						ProblemDiagram problemDiagram = getProblemDiagram(addressDir, branch, problemDiagramElement.getText());
                        if (problemDiagram != null) {
                            problemDiagram.setContextDiagram(contextDiagram);
                        }
                        List<Trace> traceList = new ArrayList<>();
						List<Dependence> dataDependenceList = new ArrayList<>();
						List<Dependence> controlDependenceList = new ArrayList<>();
						if(traceListElement != null) {
							traceList = getTraceList(addressDir, branch, traceListElement.getText());
						}
						if(dataDependenceListElement != null) {
							dataDependenceList = getDataDependenceList(addressDir, branch, dataDependenceListElement.getText());
						}
						if(controlDependenceListElement != null) {
							controlDependenceList = getControlDependenceList(addressDir, branch, controlDependenceListElement.getText());
						}
						project.setTitle(title);
						project.setIntentDiagram(intentDiagram);
						project.setContextDiagram(contextDiagram);
						if(problemDiagram != null) {
							problemDiagram.setContextDiagram(contextDiagram);
						}
						project.setProblemDiagram(problemDiagram);
						if(senarioGraphListElement != null) {
							List<?> senarioGraphElementList = senarioGraphListElement.elements("ScenarioGraph");
							List<ScenarioGraph> scenarioGraphList = getScenarioGraphList(addressDir, senarioGraphElementList, "haveRequirement");
							project.setScenarioGraphList(scenarioGraphList);
						} else {
							List<ScenarioGraph> sgList = new ArrayList<ScenarioGraph>();
							project.setScenarioGraphList(sgList);
						}
						if(subProblemDiagramListElement != null) {
							List<?> subProblemDiagramElementList = subProblemDiagramListElement.elements("SubProblemDiagram");
							List<SubProblemDiagram> subProblemDiagramList = getSubProblemDiagramList(addressDir, subProblemDiagramElementList);
							project.setSubProblemDiagramList(subProblemDiagramList);
						} else {
							List<SubProblemDiagram> spdList = new ArrayList<SubProblemDiagram>();
							project.setSubProblemDiagramList(spdList);
						}
						project.setTraceList(traceList);
						project.setDataDependenceList(dataDependenceList);
						project.setControlDependenceList(controlDependenceList);
					}
				}
				if(!verexist){
					return null;
				}
			}else {
				return null;
			}

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return project;
	}
	public Project getProject(String userAdd, String branch) {
		Project project = new Project();
		SAXReader saxReader = new SAXReader();
//		List<versionInfo> versions = searchVersionInfo(userAdd, branch);
//		try {
//			GitUtil.gitCheckout(branch, userAdd);  // 切换分支
//			GitUtil.rollback(branch, userAdd, branch, version, versions);  // 回滚到特定的版本
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		try {
			File xmlFile = new File(userAdd + reqModelDir +"/Project.xml");
			if(!xmlFile.exists()){
				File projectDir = new File(userAdd);
				if(projectDir.exists()){
					project.setTitle(branch);
					ContextDiagram cd = new ContextDiagram();
					cd.setTitle("ContextDiagram");
					ProblemDiagram pd = new ProblemDiagram();
					pd.setTitle("ProblemDiagram");
					pd.setContextDiagram(cd);
					project.setContextDiagram(cd);
					project.setProblemDiagram(pd);
					saveProject(userAdd + reqModelDir +"/", project, branch);
					return project;
				}else {
					return null;
				}
			}

			Document document = saxReader.read(xmlFile);
			Element projectElement = document.getRootElement();
			Element titleElement = projectElement.element("title");
			Element fileListElement = projectElement.element("fileList");
			Element intentDiagramElement = fileListElement.element("IntentDiagram");
			Element contextDiagramElement = fileListElement.element("ContextDiagram");
			Element problemDiagramElement = fileListElement.element("ProblemDiagram");
			Element senarioGraphListElement = fileListElement.element("SenarioGraphList");
			Element fullSenarioGraphElement = fileListElement.element("FullScenarioGraph");
			Element newSenarioGraphListElement = fileListElement.element("NewSenarioGraphList");
			Element subProblemDiagramListElement = fileListElement.element("SubProblemDiagramList");
			Element traceListElement = fileListElement.element("TraceList");
			Element dataDependenceListElement = fileListElement.element("DataDependenceList");
			Element controlDependenceListElement = fileListElement.element("ControlDependenceList");

			String title = titleElement.getText();
			String intentDiagramName = intentDiagramElement.getText();
			String contextDiagramName = contextDiagramElement.getText();
			String problemDiagramName = problemDiagramElement.getText();
			IntentDiagram intentDiagram = getIntentDiagram(userAdd+ reqModelDir +"/", branch, intentDiagramName);
			ContextDiagram contextDiagram = getContextDiagram(userAdd+ reqModelDir +"/", branch, contextDiagramName);
			ProblemDiagram problemDiagram = getProblemDiagram(userAdd+ reqModelDir +"/", branch, problemDiagramName);
            if (problemDiagram != null) {
                problemDiagram.setContextDiagram(contextDiagram);
            }
            List<Trace> traceList = new ArrayList<>();
			List<Dependence> dataDependenceList = new ArrayList<>();
			List<Dependence> controlDependenceList = new ArrayList<>();
			if(traceListElement != null) {
				traceList = getTraceList(userAdd+ reqModelDir +"/", branch, traceListElement.getText());
			}
			if(dataDependenceListElement != null) {
				dataDependenceList = getDataDependenceList(userAdd+ reqModelDir +"/", branch, dataDependenceListElement.getText());
			}
			if(controlDependenceListElement != null) {
				controlDependenceList = getControlDependenceList(userAdd+ reqModelDir +"/", branch, controlDependenceListElement.getText());
			}

			project.setTitle(title);
			project.setIntentDiagram(intentDiagram);
			project.setContextDiagram(contextDiagram);
			project.setProblemDiagram(problemDiagram);
			project.setTraceList(traceList);
			project.setDataDependenceList(dataDependenceList);
			project.setControlDependenceList(controlDependenceList);

			if(senarioGraphListElement != null) {
				List<?> senarioGraphElementList = senarioGraphListElement.elements("SenarioGraph");
				List<ScenarioGraph> scenarioGraphList = getScenarioGraphList(userAdd+ reqModelDir +"/", senarioGraphElementList, "haveRequirement");
				project.setScenarioGraphList(scenarioGraphList);
			} else {
				List<ScenarioGraph> sgList = new ArrayList<ScenarioGraph>();
				project.setScenarioGraphList(sgList);
			}
//			if(fullSenarioGraphElement != null) {
//				ScenarioGraph fullScenarioGraph = getFullScenarioGraph(userAdd);
//				project.setFullScenarioGraph(fullScenarioGraph);
//			}
//			if(newSenarioGraphListElement != null) {
//				List<?> newSenarioGraphElementList = newSenarioGraphListElement.elements("SenarioGraph");
//				List<ScenarioGraph> newScenarioGraphList = getScenarioGraphList(userAdd, newSenarioGraphElementList, null);
//				project.setNewScenarioGraphList(newScenarioGraphList);
//			}
			if(subProblemDiagramListElement != null) {
				List<?> subProblemDiagramElementList = subProblemDiagramListElement.elements("SubProblemDiagram");
				List<SubProblemDiagram> subProblemDiagramList = getSubProblemDiagramList(userAdd+ reqModelDir +"/", subProblemDiagramElementList);
				project.setSubProblemDiagramList(subProblemDiagramList);
			} else {
				List<SubProblemDiagram> spdList = new ArrayList<SubProblemDiagram>();
				project.setSubProblemDiagramList(spdList);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return project;
	}

	private ScenarioGraph getFullScenarioGraph(String address) {
		ScenarioGraph fullScenarioGraph = new ScenarioGraph();
		SAXReader saxReader = new SAXReader();
		try {
			File senarioGraphFile = new File(address + "ScenarioGraph.xml");
			if(!senarioGraphFile.exists()) {
				return null;
			}
			Document document = saxReader.read(senarioGraphFile);
			
			Element senarioGraphElement = document.getRootElement();
			Element titleElement = senarioGraphElement.element("title");
			Element nodeListElement = senarioGraphElement.element("NodeList");
			Element lineListElement = senarioGraphElement.element("LineList");
			Element intNodeListElement = nodeListElement.element("IntNode");
			Element ctrlNodeListElement = nodeListElement.element("ControlNode");
			
			String title = titleElement.getText();
			List<Node> intNodeList = getIntNodeList(intNodeListElement);
			List<CtrlNode> controlNodeList = getControlNodeList(ctrlNodeListElement);
			List<Line> lineList = getLineList(intNodeList, lineListElement, address);
			
			fullScenarioGraph.setTitle(title);
			fullScenarioGraph.setIntNodeList(intNodeList);
			fullScenarioGraph.setCtrlNodeList(controlNodeList);
			fullScenarioGraph.setLineList(lineList);
		}catch (DocumentException e) {
			e.printStackTrace();
		}
		return fullScenarioGraph;
	}

	public ScenarioGraph getScenarioGraph(String address) {
		// 读取指定的情景图文件
		ScenarioGraph fullScenarioGraph = new ScenarioGraph();
		SAXReader saxReader = new SAXReader();
		try {
			File senarioGraphFile = new File(address);
			if(!senarioGraphFile.exists()) {
				return null;
			}
			System.out.println("getScenarioGraph:" + address);
			Document document = saxReader.read(senarioGraphFile);

			Element senarioGraphElement = document.getRootElement();
			Element titleElement = senarioGraphElement.element("title");
			Element nodeListElement = senarioGraphElement.element("NodeList");
			Element lineListElement = senarioGraphElement.element("LineList");
			Element intNodeListElement = nodeListElement.element("IntNode");
			Element ctrlNodeListElement = nodeListElement.element("ControlNode");

			String title = titleElement.getText();
			List<Node> intNodeList = getIntNodeList(intNodeListElement);
			List<CtrlNode> controlNodeList = getControlNodeList(ctrlNodeListElement);
			List<Line> lineList = getLineList(intNodeList, lineListElement, address);

			fullScenarioGraph.setTitle(title);
			fullScenarioGraph.setIntNodeList(intNodeList);
			fullScenarioGraph.setCtrlNodeList(controlNodeList);
			fullScenarioGraph.setLineList(lineList);
		}catch (DocumentException e) {
			e.printStackTrace();
		}
		return fullScenarioGraph;
	}
	public ScenarioGraph getScenarioGraph(String address,String projectaddress) {
		// 读取指定的情景图文件
		System.out.println("getScenarioGraph1926:" + address);
		ScenarioGraph fullScenarioGraph = new ScenarioGraph();
		SAXReader saxReader = new SAXReader();
		try {
			File senarioGraphFile = new File(address);
			if(!senarioGraphFile.exists()) {
				return null;
			}
			Document document = saxReader.read(senarioGraphFile);

			Element senarioGraphElement = document.getRootElement();
			Element titleElement = senarioGraphElement.element("title");
			Element nodeListElement = senarioGraphElement.element("NodeList");
			Element lineListElement = senarioGraphElement.element("LineList");
			Element intNodeListElement = nodeListElement.element("IntNode");
			Element ctrlNodeListElement = nodeListElement.element("ControlNode");

			String title = titleElement.getText();
			List<Node> intNodeList = getIntNodeList(intNodeListElement);
			List<CtrlNode> controlNodeList = getControlNodeList(ctrlNodeListElement);
			List<Line> lineList = getLineList(intNodeList, lineListElement, projectaddress);

			fullScenarioGraph.setTitle(title);
			fullScenarioGraph.setIntNodeList(intNodeList);
			fullScenarioGraph.setCtrlNodeList(controlNodeList);
			fullScenarioGraph.setLineList(lineList);
		}catch (DocumentException e) {
			e.printStackTrace();
		}
		return fullScenarioGraph;
	}
	private List<Line> getLineList(List<Node> intNodeList, Element lineListElement, String address) {
		List<Line> lineList = new ArrayList<Line>();
		
		Element behOrderListElement = lineListElement.element("BehOrder");
		Element behEnableListElement = lineListElement.element("BehEnable");
		Element synchronyListElement = lineListElement.element("Synchrony");
		Element expOrderListElement = lineListElement.element("ExpOrder");
		Element expEnableListElement = lineListElement.element("ExpEnable");
		
		lineList.addAll(getLineList(intNodeList, behOrderListElement,"BehOrder",address));
		lineList.addAll(getLineList(intNodeList, behEnableListElement,"BehEnable",address));
		lineList.addAll(getLineList(intNodeList, synchronyListElement,"Synchrony",address));
		lineList.addAll(getLineList(intNodeList, expOrderListElement,"ExpOrder",address));
		lineList.addAll(getLineList(intNodeList, expEnableListElement,"ExpEnable",address));
		
		return lineList;
	}

	private Collection<? extends Line> getLineList(List<Node> intNodeList, Element lineListElement, String lineType,
			String address) {
		List<Phenomenon> phenomenonList = getPhenomenonList(address);
		List<Line> lineList = new ArrayList<Line>();
		List<?> lineElementList = lineListElement.elements("Element");
		for(Object object : lineElementList) {
			Element lineElement = (Element) object;
			String line_no = lineElement.attributeValue("line_no");
			String from_no = lineElement.attributeValue("from_no");
			String from_type = lineElement.attributeValue("from_type");
			String from_locality = lineElement.attributeValue("from_locality");
			String to_no = lineElement.attributeValue("to_no");
			String to_type = lineElement.attributeValue("to_type");
			String to_locality = lineElement.attributeValue("to_locality");
			String turnings = lineElement.attributeValue("turnings");
			String condition = lineElement.attributeValue("line_condition");
			String[] fromLocality= from_locality.split(",");
			int from_x = Integer.parseInt(fromLocality[0]);
			int from_y = Integer.parseInt(fromLocality[1]);
			String[] toLocality= to_locality.split(",");
			int to_x = Integer.parseInt(toLocality[0]);
			int to_y = Integer.parseInt(toLocality[1]);
			Phenomenon pre_condition1 = getPreCondition(from_no, from_type, intNodeList, phenomenonList);
			Phenomenon post_condition1 = getPostCondition(from_no, from_type, intNodeList, phenomenonList);
			Phenomenon pre_condition2 = getPreCondition(to_no, to_type, intNodeList, phenomenonList);
			Phenomenon post_condition2 = getPostCondition(to_no, to_type, intNodeList, phenomenonList);
			Node fromNode = new Node();
			Node toNode = new Node();
			fromNode.setNode_no(Integer.parseInt(from_no));
			fromNode.setNode_type(from_type);
			fromNode.setNode_x(from_x);
			fromNode.setNode_y(from_y);
			if(pre_condition1 != null) {
				fromNode.setPre_condition(pre_condition1);
			}
			if(post_condition1 != null) {
				fromNode.setPost_condition(post_condition1);
			}
			toNode.setNode_no(Integer.parseInt(to_no));
			toNode.setNode_type(to_type);
			toNode.setNode_x(to_x);
			toNode.setNode_y(to_y);
			if(pre_condition2 != null) {
				toNode.setPre_condition(pre_condition2);
			}
			if(post_condition2 != null) {
				toNode.setPost_condition(post_condition2);
			}
			
			Line line = new Line();
			line.setLine_no(Integer.parseInt(line_no));
			line.setLine_type(lineType);
			line.setFromNode(fromNode);
			line.setToNode(toNode);
			line.setTurnings(turnings);
			line.setCondition(condition);
			lineList.add(line);
		}
		return lineList;
	}

	private IntentDiagram getIntentDiagram(String userAdd, String projectAddress, String intentDiagramName) {
		if(!intentDiagramName.contains(".xml")){
			intentDiagramName = intentDiagramName + ".xml";
		}
		IntentDiagram intentDiagram = new IntentDiagram();
		System.out.println(userAdd + intentDiagramName );
		SAXReader saxReader = new SAXReader();
		try {
			File intentDiagramFile = new File(userAdd + intentDiagramName );
			if (!intentDiagramFile.exists()) {
				System.out.println("文件不存在");
				return null;
			}

			Document document = saxReader.read(intentDiagramFile);

			Element intentDiagramElement = document.getRootElement();
			Element titleElement = intentDiagramElement.element("title");
			Element systemElement = intentDiagramElement.element("System");
			Element externalEntityListElement = intentDiagramElement.element("ExternalEntity");
			Element intentListElement = intentDiagramElement.element("Intent");
			Element constraintListElement = intentDiagramElement.element("Constraint");
			Element referenceListElement = intentDiagramElement.element("Reference");
			Element interfaceListElement = intentDiagramElement.element("Interface");

			String title = titleElement.getText();
			ESystem system = getESystem(systemElement);
			List<ExternalEntity> externalEntityList = getExternalEntity(externalEntityListElement);
			List<Interface> interfaceList = getInterfaceList(interfaceListElement,true);
			List<Intent> intentList = getIntentList(intentListElement);
			List<Constraint> constraintList = getConstraintList(constraintListElement,true);
			List<Reference> referenceList = getReferenceList(referenceListElement,true);

			intentDiagram.setTitle(title);
			intentDiagram.setSystem(system);
			intentDiagram.setExternalEntityList(externalEntityList);
			intentDiagram.setIntentList(intentList);
			intentDiagram.setInterfaceList(interfaceList);
			intentDiagram.setConstraintList(constraintList);
			intentDiagram.setReferenceList(referenceList);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		return intentDiagram;
	}

	private ContextDiagram getContextDiagram(String userAdd, String projectAddress,  String contextDiagramName) {
		if(!contextDiagramName.contains(".xml")){
			contextDiagramName = contextDiagramName + ".xml";
		}
		ContextDiagram contextDiagram = new ContextDiagram();
		System.out.println(userAdd + contextDiagramName );
		SAXReader saxReader = new SAXReader();
		try {
			File contextDiagramFile = new File(userAdd + contextDiagramName );
			if (!contextDiagramFile.exists()) {
				System.out.println("getContextDiagram 文件不存在");
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
			List<ProblemDomain> problemDomainList = getProblemDomainList(problemDomainListElement);
			List<Interface> interfaceList = getInterfaceList(interfaceListElement, false);

			contextDiagram.setTitle(title);
			contextDiagram.setMachine(machine);
			contextDiagram.setProblemDomainList(problemDomainList);
			contextDiagram.setInterfaceList(interfaceList);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		return contextDiagram;
	}

	private ProblemDiagram getProblemDiagram(String userAdd, String projectAddress,String problemDiagramName) {
		if(!problemDiagramName.contains(".xml")){
			problemDiagramName = problemDiagramName + ".xml";
		}
		ProblemDiagram problemDiagram = new ProblemDiagram();
		SAXReader saxReader = new SAXReader();
		try {
			File problemDiagramFile = new File(userAdd + problemDiagramName );
			if (!problemDiagramFile.exists()) {
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
//			ContextDiagram contextDiagram = getContextDiagram(userAdd, projectAddress, contextDiagramName);
			List<Requirement> requirementList = getRequirementList(requirementListElement);
			List<Constraint> constraintList = getConstraintList(constraintListElement, false);
			List<Reference> referenceList = getReferenceList(referenceListElement, false);

			problemDiagram.setTitle(title);
//			problemDiagram.setContextDiagram(contextDiagram);
			problemDiagram.setRequirementList(requirementList);
			problemDiagram.setConstraintList(constraintList);
			problemDiagram.setReferenceList(referenceList);
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		return problemDiagram;
	}

	private List<Trace> getTraceList(String userAdd, String projectAddress, String traceListName) {
		if(!traceListName.contains(".xml")){
			traceListName = traceListName + ".xml";
		}
		List<Trace> traceList = new ArrayList<>();
		SAXReader saxReader = new SAXReader();
		try {
			File traceListFile = new File(userAdd + traceListName);
			if(!traceListFile.exists()) {
				return null;
			}
			Document document = saxReader.read(traceListFile);
			Element traceListElement = document.getRootElement();
			List<Element> traceElementList = traceListElement.elements("Trace");
			for(Element elem: traceElementList) {
				Trace trace = getTrace(elem);
				traceList.add(trace);
			}

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return traceList;
	}

	private List<Dependence> getDataDependenceList(String userAdd, String projectAddress, String dataDependenceListName) {
		if(!dataDependenceListName.contains(".xml")){
			dataDependenceListName = dataDependenceListName + ".xml";
		}
		List<Dependence> dataDependenceList = new ArrayList<>();
		SAXReader saxReader = new SAXReader();
		try {
			File dataDependenceListFile = new File(userAdd + dataDependenceListName);
			if(!dataDependenceListFile.exists()) {
				return null;
			}
			Document document = saxReader.read(dataDependenceListFile);
			Element dataDependenceListElement = document.getRootElement();
			List<Element> dataDependenceElementList = dataDependenceListElement.elements("DataDependenceList");
			for(Element elem: dataDependenceElementList) {
				Dependence dataDependence = getDataDependence(elem);
				dataDependenceList.add(dataDependence);
			}

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return dataDependenceList;
	}

	private List<Dependence> getControlDependenceList(String userAdd, String projectAddress, String controlDependenceListName) {
		if(!controlDependenceListName.contains(".xml")){
			controlDependenceListName = controlDependenceListName + ".xml";
		}
		List<Dependence> controlDependenceList = new ArrayList<>();
		SAXReader saxReader = new SAXReader();
		try {
			File controlDependenceListFile = new File(userAdd + controlDependenceListName);
			if(!controlDependenceListFile.exists()) {
				return null;
			}
			Document document = saxReader.read(controlDependenceListFile);
			Element controlDependenceListElement = document.getRootElement();
			List<Element> controlDependenceElementList = controlDependenceListElement.elements("ControlDependenceList");
			for(Element elem: controlDependenceElementList) {
				Dependence controlDependence = getControlDependence(elem);
				controlDependenceList.add(controlDependence);
			}

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return controlDependenceList;
	}
	
	private List<ScenarioGraph> getScenarioGraphList(String address, List<?> senarioGraphElementList, String requirementFlag) {
		List<ScenarioGraph> scenarioGraphList = new ArrayList<ScenarioGraph> ();
		for(Object sge : senarioGraphElementList) {
			Element sgEle = (Element) sge;
			String senarioGraphName = sgEle.getText();
			if(!senarioGraphName.contains(".xml")){
				senarioGraphName = senarioGraphName + ".xml";
			}
			SAXReader saxReader = new SAXReader();
			try {
//				File senarioGraphFile = new File(rootAddress + projectAddress + "\\"+ version + "\\" + senarioGraphName + ".xml");
				File senarioGraphFile = new File(address + senarioGraphName );
				if(!senarioGraphFile.exists()) {
					return null;
				}
				Document document = saxReader.read(senarioGraphFile);
				
				Element senarioGraphElement = document.getRootElement();
				Element titleElement = senarioGraphElement.element("title");
				Element requirementElement = null;
				if(requirementFlag != null) {
					requirementElement = senarioGraphElement.element("Requirement");
				}
				
				Element nodeListElement = senarioGraphElement.element("NodeList");
				Element lineListElement = senarioGraphElement.element("LineList");
				Element intNodeListElement = nodeListElement.element("IntNode");
				Element ctrlNodeListElement = nodeListElement.element("ControlNode");
				
				String title = titleElement.getText();
				String requirement = null;
				if(requirementElement != null) {
					requirement = requirementElement.getText().replaceAll("&#x000A", "\n");
				}
				List<Node> intNodeList = getIntNodeList(intNodeListElement);
				List<CtrlNode> controlNodeList = getControlNodeList(ctrlNodeListElement);
				List<Line> lineList = getLineList(lineListElement, address, senarioGraphElementList);
				
				ScenarioGraph scenarioGraph = new ScenarioGraph();
				scenarioGraph.setTitle(title);
				scenarioGraph.setRequirement(requirement);
				scenarioGraph.setIntNodeList(intNodeList);
				scenarioGraph.setCtrlNodeList(controlNodeList);
				scenarioGraph.setLineList(lineList);
				
				scenarioGraphList.add(scenarioGraph);
			}catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return scenarioGraphList;
	}
	
	private List<Node> getIntNodeList(Element intNodeListElement) {
		List<Node> intNodeList = new ArrayList<Node>();
		
		Element behIntNodeListElement = intNodeListElement.element("BehIntNode");
		Element connIntNodeListElement = intNodeListElement.element("ConnIntNode");
		Element expIntNodeListElement = intNodeListElement.element("ExpIntNode");
		
		if(behIntNodeListElement != null) {
			List<?> behIntNodeElementList = behIntNodeListElement.elements("Element");
			for(Object object : behIntNodeElementList) {
				Element behIntNodeElement = (Element)object;
				Node behIntNode = getIntNode(behIntNodeElement,"BehInt");
				intNodeList.add(behIntNode);
			}
		}
		if(connIntNodeListElement != null) {
			List<?> connexpIntNodeElementList = connIntNodeListElement.elements("Element");
			for(Object object : connexpIntNodeElementList) {
				Element connexpIntNodeElement = (Element)object;
				Node connIntNode = getIntNode(connexpIntNodeElement,"ConnInt");
				intNodeList.add(connIntNode);
			}
		}
		if(expIntNodeListElement != null) {
			List<?> expIntNodeElementList = expIntNodeListElement.elements("Element");
			for(Object object : expIntNodeElementList) {
				Element expIntNodeElement = (Element)object;
				Node expIntNode = getIntNode(expIntNodeElement,"ExpInt");
				intNodeList.add(expIntNode);
			}
		}
					
		return intNodeList;
	}
	
	private Node getIntNode(Element intNodeElement, String node_type) {
		List<?> fromNodeListElement = intNodeElement.elements("from");
		List<?> toNodeListElement = intNodeElement.elements("to");
		int node_no = Integer.parseInt(intNodeElement.attributeValue("node_no"));
		String node_locality = intNodeElement.attributeValue("node_locality");
		String[] locality= node_locality.split(",");
		int node_x = Integer.parseInt(locality[0]);
		int node_y = Integer.parseInt(locality[1]);
		List<Node> node_fromList = getNodeList(fromNodeListElement);
		List<Node> node_toList = getNodeList(toNodeListElement);
		
		Node intNode = new Node();
		intNode.setNode_no(node_no);
		intNode.setNode_type(node_type);
		intNode.setNode_x(node_x);
		intNode.setNode_y(node_y);
		intNode.setNode_fromList(node_fromList);
		intNode.setNode_toList(node_toList);
		
		Element pre_conditionElement = intNodeElement.element("pre_condition");
		Element post_conditionElement = intNodeElement.element("post_condition");
		if(pre_conditionElement != null) {
			String phenomenon_name1 = pre_conditionElement.attributeValue("phenomenon_name");
			if(phenomenon_name1 != null) {
				Phenomenon pre_condition = getPhenomenon(pre_conditionElement);
				intNode.setPre_condition((Phenomenon) pre_condition);
			}
		}
		if(post_conditionElement != null) {
			String phenomenon_name2 = post_conditionElement.attributeValue("phenomenon_name");
			if(phenomenon_name2 != null) {
				Phenomenon post_condition = getPhenomenon(post_conditionElement);
				intNode.setPost_condition((Phenomenon) post_condition);
			}
		}
	
		return intNode;
	}
	
	private Phenomenon getPhenomenon(Element phenomenonElement) {
		Phenomenon phenomenon = new Phenomenon();
		int phenomenon_no = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_no"));
		String phenomenon_name = phenomenonElement.attributeValue("phenomenon_name");
		String phenomenon_type = phenomenonElement.attributeValue("phenomenon_type");
		String phenomenon_from = phenomenonElement.attributeValue("phenomenon_from").replaceAll("&#x000A", "\n");
		String phenomenon_to = phenomenonElement.attributeValue("phenomenon_to").replaceAll("&#x000A", "\n");

		phenomenon.setPhenomenon_no(phenomenon_no);
		phenomenon.setPhenomenon_name(phenomenon_name);
		phenomenon.setPhenomenon_type(phenomenon_type);
		phenomenon.setPhenomenon_from(phenomenon_from);
		phenomenon.setPhenomenon_to(phenomenon_to);

		return phenomenon;
	}

	private List<CtrlNode> getControlNodeList(Element controlNodeListElement) {
		List<CtrlNode> controlNodeList = new ArrayList<CtrlNode>();
		
		Element startNodeListElement = controlNodeListElement.element("StartNode");
		Element endNodeListElement = controlNodeListElement.element("EndNode");
		Element decisionNodeListElement = controlNodeListElement.element("DecisionNode");
		Element mergeNodeListElement = controlNodeListElement.element("MergeNode");
		Element branchNodeListElement = controlNodeListElement.element("BranchNode");
		Element delayNodeListElement = controlNodeListElement.element("DelayNode");
		
		List<?> startNodeElementList = startNodeListElement.elements("Element");
		List<?> endNodeElementList = endNodeListElement.elements("Element");
		List<?> decisionNodeElementList = decisionNodeListElement.elements("Element");
		List<?> mergeNodeElementList = mergeNodeListElement.elements("Element");
		List<?> branchNodeElementList = branchNodeListElement.elements("Element");
		List<?> delayNodeElementList = null;
		if(delayNodeListElement != null)
			delayNodeElementList = delayNodeListElement.elements("Element");
		
		controlNodeList.addAll(getControlNodeList(startNodeElementList,"Start"));
		controlNodeList.addAll(getControlNodeList(endNodeElementList,"End"));
		controlNodeList.addAll(getControlNodeList(decisionNodeElementList,"Decision"));
		controlNodeList.addAll(getControlNodeList(mergeNodeElementList,"Merge"));
		controlNodeList.addAll(getControlNodeList(branchNodeElementList,"Branch"));
		if(delayNodeElementList != null)
			controlNodeList.addAll(getControlNodeList(delayNodeElementList,"Delay"));
		
		return controlNodeList;
	}
	
	private List<CtrlNode> getControlNodeList(List<?> controllNodeElementList,String nodeType){
		List<CtrlNode> controlNodeList = new ArrayList<CtrlNode>();
		for(Object object : controllNodeElementList) {
			Element controlNodeElement = (Element) object;
			List<?> fromNodeListElement = controlNodeElement.elements("from");
			List<?> toNodeListElement = controlNodeElement.elements("to");
			
			int node_no = Integer.parseInt(controlNodeElement.attributeValue("node_no"));
			String node_type = nodeType;
			String node_locality =controlNodeElement.attributeValue("node_locality");
			String[] locality= node_locality.split(",");
			int node_x = Integer.parseInt(locality[0]);
			int node_y = Integer.parseInt(locality[1]);
			List<Node> node_fromList = getNodeList(fromNodeListElement);
			List<Node> node_toList = getNodeList(toNodeListElement);
			
			CtrlNode controlNode = new CtrlNode();
			controlNode.setNode_no(node_no);
			controlNode.setNode_type(node_type);
			controlNode.setNode_x(node_x);
			controlNode.setNode_y(node_y);
			controlNode.setNode_fromList(node_fromList);
			controlNode.setNode_toList(node_toList);
			
			if(nodeType.equals("Decision") || nodeType.equals("Delay")) {
				String node_text = controlNodeElement.attributeValue("node_text");
//				String node_consition1 = controlNodeElement.attributeValue("node_consition1");
//				String node_consition2 = controlNodeElement.attributeValue("node_consition2");
				controlNode.setNode_text(node_text);
//				controlNode.setNode_consition1(node_consition1);
//				controlNode.setNode_consition2(node_consition2);
			}
			
			if(nodeType.equals("Delay")) {
				String delay_type = controlNodeElement.attributeValue("delay_type");
				controlNode.setDelay_type(delay_type);
			}
			
			controlNodeList.add(controlNode);
		}
		return controlNodeList;
	}

	private List<Node> getNodeList(List<?> nodeListElement) {
		List<Node> nodeList = new ArrayList<Node>();
		for(Object object : nodeListElement) {
			Element nodeElement = (Element) object;
			if(nodeElement.attributes().size() == 0) {
				continue;
			}
			int node_no = Integer.parseInt(nodeElement.attributeValue("node_no"));
			String node_type = nodeElement.attributeValue("node_type");
			String node_locality = nodeElement.attributeValue("node_locality");
			String[] locality= node_locality.split(",");
			int node_x = Integer.parseInt(locality[0]);
			int node_y = Integer.parseInt(locality[1]);
			
			Node node = new Node();
			node.setNode_no(node_no);
			node.setNode_type(node_type);
			node.setNode_x(node_x);
			node.setNode_y(node_y);
			nodeList.add(node);
		}
		return nodeList;
	}
	
	private List<Line> getLineList(Element lineListElement, String address, List<?> senarioGraphElementList) {
		List<Line> lineList = new ArrayList<Line>();
		
		Element behOrderListElement = lineListElement.element("BehOrder");
		Element behEnableListElement = lineListElement.element("BehEnable");
		Element synchronyListElement = lineListElement.element("Synchrony");
		Element expOrderListElement = lineListElement.element("ExpOrder");
		Element expEnableListElement = lineListElement.element("ExpEnable");
		
		lineList.addAll(getLineList(behOrderListElement,"BehOrder",address,senarioGraphElementList));
		lineList.addAll(getLineList(behEnableListElement,"BehEnable",address,senarioGraphElementList));
		lineList.addAll(getLineList(synchronyListElement,"Synchrony",address,senarioGraphElementList));
		lineList.addAll(getLineList(expOrderListElement,"ExpOrder",address,senarioGraphElementList));
		lineList.addAll(getLineList(expEnableListElement,"ExpEnable",address,senarioGraphElementList));
		
		return lineList;
	}

	private List<Line> getLineList(Element lineListElement, String lineType, String address, List<?> senarioGraphElementList) {
		List<Node> intNodeList = getIntNodeList(address, senarioGraphElementList);
		List<Phenomenon> phenomenonList = getPhenomenonList(address);
		List<Line> lineList = new ArrayList<Line>();
		List<?> lineElementList = lineListElement.elements("Element");
		for(Object object : lineElementList) {
			Element lineElement = (Element) object;
			String line_no = lineElement.attributeValue("line_no");
			String from_no = lineElement.attributeValue("from_no");
			String from_type = lineElement.attributeValue("from_type");
			String from_locality = lineElement.attributeValue("from_locality");
			String to_no = lineElement.attributeValue("to_no");
			String to_type = lineElement.attributeValue("to_type");
			String to_locality = lineElement.attributeValue("to_locality");
			String turnings = lineElement.attributeValue("turnings");
			String condition = lineElement.attributeValue("line_condition");
			String[] fromLocality= from_locality.split(",");
			int from_x = Integer.parseInt(fromLocality[0]);
			int from_y = Integer.parseInt(fromLocality[1]);
			String[] toLocality= to_locality.split(",");
			int to_x = Integer.parseInt(toLocality[0]);
			int to_y = Integer.parseInt(toLocality[1]);
			Phenomenon pre_condition1 = getPreCondition(from_no, from_type, intNodeList, phenomenonList);
			Phenomenon post_condition1 = getPostCondition(from_no, from_type, intNodeList, phenomenonList);
			Phenomenon pre_condition2 = getPreCondition(to_no, to_type, intNodeList, phenomenonList);
			Phenomenon post_condition2 = getPostCondition(to_no, to_type, intNodeList, phenomenonList);
			Node fromNode = new Node();
			Node toNode = new Node();
			fromNode.setNode_no(Integer.parseInt(from_no));
			fromNode.setNode_type(from_type);
			fromNode.setNode_x(from_x);
			fromNode.setNode_y(from_y);
			if(pre_condition1 != null) {
				fromNode.setPre_condition(pre_condition1);
			}
			if(post_condition1 != null) {
				fromNode.setPost_condition(post_condition1);
			}
			toNode.setNode_no(Integer.parseInt(to_no));
			toNode.setNode_type(to_type);
			toNode.setNode_x(to_x);
			toNode.setNode_y(to_y);
			if(pre_condition2 != null) {
				toNode.setPre_condition(pre_condition2);
			}
			if(post_condition2 != null) {
				toNode.setPost_condition(post_condition2);
			}
			
			Line line = new Line();
			line.setLine_no(Integer.parseInt(line_no));
			line.setLine_type(lineType);
			line.setFromNode(fromNode);
			line.setToNode(toNode);
			line.setTurnings(turnings);
			line.setCondition(condition);
			lineList.add(line);
		}
		return lineList;
	}
	
	private List<Node> getIntNodeList(String address, List<?> senarioGraphElementList) {
		List<Node> intNodeList = new ArrayList<Node>();
		for(Object sge : senarioGraphElementList) {
			Element sgEle = (Element) sge;
			String senarioGraphName = sgEle.getText();
			SAXReader saxReader = new SAXReader();
			try {
				File senarioGraphFile = new File(address + senarioGraphName + ".xml");
				if(!senarioGraphFile.exists()) {
					return null;
				}
				Document document = saxReader.read(senarioGraphFile);
				Element senarioGraphElement = document.getRootElement();
				Element nodeListElement = senarioGraphElement.element("NodeList");
				Element intNodeListElement = nodeListElement.element("IntNode");
				intNodeList.addAll(getIntNodeList(intNodeListElement));
			}catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return intNodeList;
	}
	
	private List<Phenomenon> getPhenomenonList(String address){
		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
		SAXReader saxReader = new SAXReader();
		SAXReader saxReader1 = new SAXReader();
		if(!address.contains(reqModelDir+"/")){
			address=address+ reqModelDir+"/";
		}
		try {
			File contextDiagramFile = new File(address + "ContextDiagram.xml");
			if (!contextDiagramFile.exists()) {
				System.out.println("getPhenomenonList文件不存在");
				return null;
			}
			File problemDiagramFile = new File(address + "ProblemDiagram.xml");
			if (!problemDiagramFile.exists()) {
				System.out.println("getPhenomenonList文件不存在:"+address+ "ProblemDiagram.xml");
				return null;
			}
			Document document = saxReader.read(contextDiagramFile);
			Document document1 = saxReader1.read(problemDiagramFile);
			Element contextDiagramElement = document.getRootElement();
			
			Element interfaceListElement = contextDiagramElement.element("Interface");
			List<?> interfaceElementList = interfaceListElement.elements("Element");
			for (Object object : interfaceElementList) {
				Element interfaceElement = (Element) object;
				List<?> phenomenonElementList = interfaceElement.elements("Phenomenon");
				phenomenonList.addAll(getPhenomenonList(phenomenonElementList));
			}
			Element problemDiagramElement = document1.getRootElement();
			Element constraintListElement = problemDiagramElement.element("Constraint");
			List<Constraint> constraintList = getConstraintList(constraintListElement, false);
			for(Constraint constraint: constraintList) {
				phenomenonList.addAll(constraint.getPhenomenonList());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return phenomenonList;
	}
	
	private Phenomenon getPreCondition(String no, String type, List<Node> intNodeList, List<Phenomenon> phenomenonList) {
		if(type.equals("BehInt") || type.equals("ConnInt") || type.equals("ExpInt")) {
			if(intNodeList == null) return null;
			for(int i = intNodeList.size() - 1; i >= 0; i--) {
				Node intNode = intNodeList.get(i);
				if(intNode.getNode_no() == Integer.parseInt(no) && intNode.getNode_type().equals(type)) {
					if(intNode.getPre_condition() != null) {
						for(Phenomenon phenomenon: phenomenonList) {
							if(intNode.getPre_condition().getPhenomenon_no() == phenomenon.getPhenomenon_no()) {
								return phenomenon;
							}
						}
					}
				}
			}
		}
		return null; 	
	}
	
	private Phenomenon getPostCondition(String no, String type, List<Node> intNodeList, List<Phenomenon> phenomenonList) {
		if(type.equals("BehInt") || type.equals("ConnInt") || type.equals("ExpInt")) {
			if(intNodeList == null) return null;
			for(int i = intNodeList.size() - 1; i >= 0; i--) {
				Node intNode = intNodeList.get(i);
				if(intNode.getNode_no() == Integer.parseInt(no) && intNode.getNode_type().equals(type)) {
					if(intNode.getPost_condition() != null) {
						for(Phenomenon phenomenon: phenomenonList) {
							if(intNode.getPost_condition().getPhenomenon_no() == phenomenon.getPhenomenon_no()) {
								return phenomenon;
							}
						}
					}
				}
			}
		}
		return null; 	
	}
	
	private List<SubProblemDiagram> getSubProblemDiagramList(String address, List<?> subProblemDiagramElementList) {
		List<SubProblemDiagram> subProblemDiagramList = new ArrayList<SubProblemDiagram> ();
		for(Object spde : subProblemDiagramElementList) {
			Element spdEle = (Element) spde;
			String subProblemDiagramName = spdEle.getText();
			if(!subProblemDiagramName.contains(".xml")){
				subProblemDiagramName = subProblemDiagramName + ".xml";
			}
			SAXReader saxReader = new SAXReader();
			try {
//				File subProblemDiagramFile = new File(rootAddress + projectAddress + "\\"+ version + "\\" + subProblemDiagramName + ".xml");
				File subProblemDiagramFile = new File(address + subProblemDiagramName );
				if(!subProblemDiagramFile.exists()) {
					return null;
				}
				Document document = saxReader.read(subProblemDiagramFile);
				
				Element subProblemDiagramElement = document.getRootElement();
				Element titleElement = subProblemDiagramElement.element("title");
				Element machineElement = subProblemDiagramElement.element("Machine");
				Element problemDomainListElement = subProblemDiagramElement.element("ProblemDomain");
				Element requirementElement = subProblemDiagramElement.element("Requirement");
				Element interfaceListElement = subProblemDiagramElement.element("Interface");
				Element constraintListElement = subProblemDiagramElement.element("Constraint");
				Element referenceListElement = subProblemDiagramElement.element("Reference");

				String title = titleElement.getText();
				Machine machine = getMachine(machineElement);
				List<ProblemDomain> problemDomainList= getProblemDomainList(problemDomainListElement);
				Requirement requirement = getRequirement(requirementElement);
				List<Interface> interfaceList = getInterfaceList(interfaceListElement, false);
				List<Constraint> constraintList = getConstraintList(constraintListElement, false);
				List<Reference> referenceList = getReferenceList(referenceListElement, false);
				
				SubProblemDiagram subProblemDiagram = new SubProblemDiagram();
				subProblemDiagram.setTitle(title);
				subProblemDiagram.setMachine(machine);
				subProblemDiagram.setProblemDomainList(problemDomainList);
				subProblemDiagram.setRequirement(requirement);
				subProblemDiagram.setInterfaceList(interfaceList);
				subProblemDiagram.setConstraintList(constraintList);
				subProblemDiagram.setReferenceList(referenceList);
				
				subProblemDiagramList.add(subProblemDiagram);
			}catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return subProblemDiagramList;
	}
	
	private Machine getMachine(Element machineElement) {
		// if(machineElement==null)
		// return null;
		Machine machine = new Machine();
		String machine_name = machineElement.attributeValue("machine_name");
		// if(machine_name!=null)
		machine_name = machine_name.replaceAll("&#x000A", "\n");
		String machine_shortName = machineElement.attributeValue("machine_shortname");
		String machine_locality = machineElement.attributeValue("machine_locality");
		String[] locality = machine_locality.split(",");
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

		List<ProblemDomain> problemDomainList = new ArrayList<ProblemDomain>();

		Element givenDomainListElement = problemDomainListElement.element("GivenDomain");
		Element designDomainListElement = problemDomainListElement.element("DesignDomain");
		List<?> givenDomainElementList = givenDomainListElement.elements("Element");
		List<?> designDomainElementList = designDomainListElement.elements("Element");

		for (Object object : givenDomainElementList) {
			ProblemDomain problemDomain = new ProblemDomain();
			Element givenDomainElement = (Element) object;

			int problemdomain_no = Integer.parseInt(givenDomainElement.attributeValue("problemdomain_no"));
			String problemdomain_name = givenDomainElement.attributeValue("problemdomain_name");
			problemdomain_name = problemdomain_name.replaceAll("&#x000A", "\n");
			String problemdomain_shortname = givenDomainElement.attributeValue("problemdomain_shortname");
			String problemdomain_type = givenDomainElement.attributeValue("problemdomain_type");
			String problemdomain_property = "GivenDomain";
			String problemdomain_locality = givenDomainElement.attributeValue("problemdomain_locality");
			String[] locality = problemdomain_locality.split(",");
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
		for (Object object : designDomainElementList) {
			Element designDomainElement = (Element) object;

			int problemdomain_no = Integer.parseInt(designDomainElement.attributeValue("problemdomain_no"));
			String problemdomain_name = designDomainElement.attributeValue("problemdomain_name");
			String problemdomain_shortname = designDomainElement.attributeValue("problemdomain_shortname");
			String problemdomain_type = designDomainElement.attributeValue("problemdomain_type");
			String problemdomain_property = "DesignDomain";
			String problemdomain_locality = designDomainElement.attributeValue("problemdomain_locality");
			String[] locality = problemdomain_locality.split(",");
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

	private Intent getIntent(Element intentElement) {

		Intent intent = new Intent();

		int intent_no = Integer.parseInt(intentElement.attributeValue("intent_no"));
		String intent_context = intentElement.attributeValue("intent_context").
				replaceAll("&#x000A","\n");
		String intent_locality = intentElement.attributeValue("intent_locality");
		String intent_shortname = intentElement.attributeValue("intent_shortname");
		if (intent_shortname == null) {
//			intent_shortname = intent_context;
			intent_shortname = "intent"+ intent_no;
		}
//		System.out.println("intent_shortname:"+intent_shortname);
		String[] locality = intent_locality.split(",");
		int intent_x = Integer.parseInt(locality[0]);
		int intent_y = Integer.parseInt(locality[1]);
		int intent_h = Integer.parseInt(locality[2]);
		int intent_w = Integer.parseInt(locality[3]);

		intent.setIntent_no(intent_no);
		intent.setIntent_context(intent_context);
		intent.setIntent_shortname(intent_shortname);
		intent.setIntent_x(intent_x);
		intent.setIntent_y(intent_y);
		intent.setIntent_h(intent_h);
		intent.setIntent_h(intent_w);

		return intent;
	}

	private List<Intent> getIntentList(Element intentListElement) {
		List<?> intentElementList = intentListElement.elements("Element");
		List<Intent> intentList = new ArrayList<Intent>();
		for (Object object : intentElementList) {
			Element intentElement = (Element) object;

			Intent intent = getIntent(intentElement);

			intentList.add(intent);
		}
		return intentList;
	}

	private List<ExternalEntity> getExternalEntity(Element externalEntityListElement) {
		List<ExternalEntity> externalEntityList = new ArrayList<ExternalEntity>();
		List<?> externalEntityElementList = externalEntityListElement.elements("Element");

		for (Object object : externalEntityElementList) {
			ExternalEntity externalEntity = new ExternalEntity();
			Element externalEntityElement = (Element) object;

			int externalentity_no = Integer.parseInt(externalEntityElement.attributeValue("externalentity_no"));
			String externalentity_name = externalEntityElement.attributeValue("externalentity_name");
			externalentity_name = externalentity_name.replaceAll("&#x000A", "\n");
			String externalentity_shortname = externalEntityElement.attributeValue("externalentity_shortname");
			String externalentity_locality = externalEntityElement.attributeValue("externalentity_locality");
			String[] locality = externalentity_locality.split(",");
			int externalentity_x = Integer.parseInt(locality[0]);
			int externalentity_y = Integer.parseInt(locality[1]);
			int externalentity_h = Integer.parseInt(locality[2]);
			int externalentity_w = Integer.parseInt(locality[3]);

			externalEntity.setExternalentity_no(externalentity_no);
			externalEntity.setExternalentity_name(externalentity_name);
			externalEntity.setExternalentity_shortname(externalentity_shortname);
			externalEntity.setExternalentity_x(externalentity_x);
			externalEntity.setExternalentity_y(externalentity_y);
			externalEntity.setExternalentity_h(externalentity_h);
			externalEntity.setExternalentity_w(externalentity_w);

			externalEntityList.add(externalEntity);
		}

		return externalEntityList;
	}

	private ESystem getESystem(Element systemElement) {
		if(systemElement == null){
			return null;
		}
		ESystem system = new ESystem();
		String system_name = systemElement.attributeValue("system_name");
		if(system_name!=null) {
			system_name = system_name.replaceAll("&#x000A", "\n");
			system.setSystem_name(system_name);
		}
		String system_shortName = systemElement.attributeValue("system_shortname");
		system.setSystem_shortName(system_shortName);
		String system_locality = systemElement.attributeValue("system_locality");
		if(system_locality != null) {
			String[] locality = system_locality.split(",");
			int system_x = Integer.parseInt(locality[0]);
			int system_y = Integer.parseInt(locality[1]);
			int system_h = Integer.parseInt(locality[2]);
			int system_w = Integer.parseInt(locality[3]);
			system.setSystem_h(system_h);
			system.setSystem_w(system_w);
			system.setSystem_x(system_x);
			system.setSystem_y(system_y);
		}
		return system;
	}



	private List<Interface> getInterfaceList(Element interfaceListElement,Boolean isIntent) {

		List<Interface> interfaceList = new ArrayList<Interface>();
		List<?> interfaceElementList = interfaceListElement.elements("Element");

		for (Object object : interfaceElementList) {
			Element interfaceElement = (Element) object;
			List<?> phenomenonElementList = interfaceElement.elements("Phenomenon");
			int interface_no = Integer.parseInt(interfaceElement.attributeValue("interface_no"));
			String interface_name = interfaceElement.attributeValue("interface_name");
			String interface_description = interfaceElement.attributeValue("interface_description");
			String interface_from = interfaceElement.attributeValue("interface_from").replaceAll("&#x000A", "\n");
			String interface_to = interfaceElement.attributeValue("interface_to").replaceAll("&#x000A", "\n");
			String interface_locality = interfaceElement.attributeValue("interface_locality");
			List<Phenomenon> phenomenonList = getPhenomenonList(phenomenonElementList);
			String[] locality = interface_locality.split(",");
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
			inte.setIsintent(isIntent);
			interfaceList.add(inte);
		}

		return interfaceList;
	}

	private List<Phenomenon> getPhenomenonList(List<?> phenomenonElementList) {

		List<Phenomenon> phenomenonList = new ArrayList<Phenomenon>();
		for (Object object : phenomenonElementList) {
			Element phenomenonElement = (Element) object;

			int phenomenon_no = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_no"));
			String phenomenon_name = phenomenonElement.attributeValue("phenomenon_name");
			String phenomenon_type = phenomenonElement.attributeValue("phenomenon_type");
			String phenomenon_from = phenomenonElement.attributeValue("phenomenon_from").replaceAll("&#x000A", "\n");
			;
			String phenomenon_to = phenomenonElement.attributeValue("phenomenon_to").replaceAll("&#x000A", "\n");
			;

			Phenomenon phenomenon = new Phenomenon();
			phenomenon.setPhenomenon_no(phenomenon_no);
			phenomenon.setPhenomenon_name(phenomenon_name);
			phenomenon.setPhenomenon_type(phenomenon_type);
			phenomenon.setPhenomenon_from(phenomenon_from);
			phenomenon.setPhenomenon_to(phenomenon_to);

			phenomenonList.add(phenomenon);
		}
		return phenomenonList;
	}

	private List<Requirement> getRequirementList(Element requirementListElement) {

		List<?> requirementElementList = requirementListElement.elements("Element");
		List<Requirement> requirementList = new ArrayList<Requirement>();
		for (Object object : requirementElementList) {
			Element requirementElement = (Element) object;

			Requirement requirement = getRequirement(requirementElement);

			requirementList.add(requirement);
		}
		return requirementList;
	}

	private Requirement getRequirement(Element requirementElement) {

		Requirement requirement = new Requirement();

		int requirement_no = Integer.parseInt(requirementElement.attributeValue("requirement_no"));
		String requirement_context = requirementElement.attributeValue("requirement_context").replaceAll("&#x000A",
				"\n");
		;
		String requirement_locality = requirementElement.attributeValue("requirement_locality");
		String[] locality = requirement_locality.split(",");
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

	private List<Constraint> getConstraintList(Element constraintListElement,Boolean isIntent) {

		List<Constraint> constraintList = new ArrayList<Constraint>();
		List<?> constraintElementList = constraintListElement.elements("Element");
		for (Object object : constraintElementList) {
			Element constraintElement = (Element) object;
			List<?> phenomenonElementList = constraintElement.elements("Phenomenon");
			int constraint_no = Integer.parseInt(constraintElement.attributeValue("constraint_no"));
			String constraint_name = constraintElement.attributeValue("constraint_name");
			String constraint_description = constraintElement.attributeValue("constraint_description");
			String constraint_from = constraintElement.attributeValue("constraint_from").
					replaceAll("&#x000A", "\n");
			String constraint_to = constraintElement.attributeValue("constraint_to").
					replaceAll("&#x000A", "\n");
			List<RequirementPhenomenon> phenomenonList = getRequirementPhenomenonList(phenomenonElementList);
			String constraint_locality = constraintElement.attributeValue("constraint_locality");
			String[] locality = constraint_locality.split(",");
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
			constraint.setIsintent(isIntent);
			constraintList.add(constraint);
		}
		return constraintList;
	}

	private List<RequirementPhenomenon> getRequirementPhenomenonList(List<?> phenomenonElementList) {

		List<RequirementPhenomenon> phenomenonList = new ArrayList<RequirementPhenomenon>();
		for (Object object : phenomenonElementList) {
			Element phenomenonElement = (Element) object;

			int phenomenon_no = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_no"));
			String phenomenon_name = phenomenonElement.attributeValue("phenomenon_name");
			String phenomenon_type = phenomenonElement.attributeValue("phenomenon_type");
			String phenomenon_from = phenomenonElement.attributeValue("phenomenon_from").replaceAll("&#x000A", "\n");
			;
			String phenomenon_to = phenomenonElement.attributeValue("phenomenon_to").replaceAll("&#x000A", "\n");
			;
			String phenomenon_constraint = phenomenonElement.attributeValue("phenomenon_constraint");
			int phenomenon_requirement = Integer.parseInt(phenomenonElement.attributeValue("phenomenon_requirement"));

			RequirementPhenomenon phenomenon = new RequirementPhenomenon();
			phenomenon.setPhenomenon_no(phenomenon_no);
			phenomenon.setPhenomenon_name(phenomenon_name);
			phenomenon.setPhenomenon_type(phenomenon_type);
			phenomenon.setPhenomenon_constraint(phenomenon_constraint);
			phenomenon.setPhenomenon_requirement(phenomenon_requirement);
			phenomenon.setPhenomenon_from(phenomenon_from);
			phenomenon.setPhenomenon_to(phenomenon_to);

			phenomenonList.add(phenomenon);
		}
		return phenomenonList;
	}

	private List<Reference> getReferenceList(Element referenceListElement,Boolean isIntent) {

		List<Reference> referenceList = new ArrayList<Reference>();
		List<?> referenceElementList = referenceListElement.elements("Element");
		for (Object object : referenceElementList) {
			Element referenceElement = (Element) object;
			List<?> phenomenonElementList = referenceElement.elements("Phenomenon");
			int reference_no = Integer.parseInt(referenceElement.attributeValue("reference_no"));
			String reference_name = referenceElement.attributeValue("reference_name");
			String reference_description = referenceElement.attributeValue("reference_description");
			String reference_from = referenceElement.attributeValue("reference_from").replaceAll("&#x000A", "\n");
			String reference_to = referenceElement.attributeValue("reference_to").replaceAll("&#x000A", "\n");
			List<RequirementPhenomenon> phenomenonList = getRequirementPhenomenonList(phenomenonElementList);
			String reference_locality = referenceElement.attributeValue("reference_locality");
			String[] locality = reference_locality.split(",");
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
			reference.setIsintent(isIntent);
			referenceList.add(reference);
		}
		return referenceList;
	}

	private Trace getTrace(Element elem) {
		if(elem == null) {
			return null;
		}
		Element sourceElement = elem.element("Source");
		Element targetListElement = elem.element("TargetList");
		Element typeElement = elem.element("Type");
		Trace trace = new Trace();
		trace.setSource(sourceElement.getText());
		trace.setTarget(new ArrayList<>());
		trace.setType(typeElement.getText());
		for(Object object: targetListElement.elements("Target")) {
			Element problem = (Element) object;
			trace.getTarget().add(problem.getText());
		}

		return trace;
	}

	private Dependence getDataDependence(Element elem) {
		if(elem == null) {
			return null;
		}
		Element sourceElement = elem.element("Source");
		Element dataListElement = elem.element("DataList");
		Element targetElement = elem.element("Target");
		Dependence dependence = new Dependence();
		dependence.setSource(sourceElement.getText());
		dependence.setData(new HashSet<>());
		dependence.setTarget(targetElement.getText());
		for(Object object: dataListElement.elements("Data")) {
			Element data = (Element) object;
			dependence.getData().add(data.getText());
		}

		return dependence;
	}

	private Dependence getControlDependence(Element elem) {
		if(elem == null) {
			return null;
		}
		Element sourceElement = elem.element("Source");
		Element deviceListElement = elem.element("DeviceList");
		Element targetElement = elem.element("Target");
		Dependence dependence = new Dependence();
		dependence.setSource(sourceElement.getText());
		dependence.setData(new HashSet<>());
		dependence.setTarget(targetElement.getText());
		for(Object object: deviceListElement.elements("Device")) {
			Element device = (Element) object;
			dependence.getData().add(device.getText());
		}

		return dependence;
	}

	// =====================格式转换==========================
	// 转为广西师大格式
	// ================转为广西师大xml格式====================
	public boolean format(String userAdd, Project project, String branch) {
		try {
			GitUtil.gitCheckout(branch, userAdd);  // 切换分支
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		this.setProject(userAdd, branch);
		TransXML.saveProject(project, userAdd);
		return true;
	}

	public List<Node> getFromNodeList(CtrlNode ctrlNode, ScenarioGraph SG) {
		List<Node> nodeList = new ArrayList<Node>();
		List<Line> lineList = SG.getLineList();
		if (lineList == null) {
			return nodeList;
		}
		for (int i = 0; i < lineList.size(); i++) {
			Line line = lineList.get(i);
			if (line.getToNode().getNode_type().equals(ctrlNode.getNode_type())
					&& line.getToNode().getNode_no() == ctrlNode.getNode_no()) {
				nodeList.add(line.getFromNode());
			}
		}
		return nodeList;
	}

	public List<Node> getToNodeList(CtrlNode ctrlNode, ScenarioGraph SG) {
		List<Node> nodeList = new ArrayList<Node>();
		List<Line> lineList = SG.getLineList();
		if (lineList == null) {
			return nodeList;
		}
		for (int i = 0; i < lineList.size(); i++) {
			Line line = lineList.get(i);
			if (line.getFromNode().getNode_type().equals(ctrlNode.getNode_type())
					&& line.getFromNode().getNode_no() == ctrlNode.getNode_no()) {
				nodeList.add(line.getToNode());
			}
		}
		return nodeList;
	}
	
	public List<Node> getIntFromNodeList(Node intNode, ScenarioGraph SG) {
		List<Node> nodeList = new ArrayList<Node>();
		List<Line> lineList = SG.getLineList();
		if (lineList == null) {
			return nodeList;
		}
		for (int i = 0; i < lineList.size(); i++) {
			Line line = lineList.get(i);
			if(line.getLine_type().equals("BehOrder") || line.getLine_type().equals("ExpOrder")) {
				if (line.getToNode().getNode_type().equals(intNode.getNode_type())
						&& line.getToNode().getNode_no() == intNode.getNode_no()) {
					nodeList.add(line.getFromNode());
				}
			}
		}
		return nodeList;
	}

	public List<Node> getIntToNodeList(Node intNode, ScenarioGraph SG) {
		List<Node> nodeList = new ArrayList<Node>();
		List<Line> lineList = SG.getLineList();
		if (lineList == null) {
			return nodeList;
		}
		for (int i = 0; i < lineList.size(); i++) {
			Line line = lineList.get(i);
			if(line.getLine_type().equals("BehOrder") || line.getLine_type().equals("ExpOrder")) {
				if (line.getFromNode().getNode_type().equals(intNode.getNode_type())
						&& line.getFromNode().getNode_no() == intNode.getNode_no()) {
					nodeList.add(line.getToNode());
				}
			}
		}
		return nodeList;
	}

	// =====================下载相关===========================
	public void download(String userAdd, String fileName, HttpServletResponse resp, String branch) {
		try {
			GitUtil.gitCheckout(branch, userAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean result = fileToZip(userAdd, downloadRootAddress, fileName);
		System.out.println("download---" + "userAdd:" + userAdd + "  fileName:" + fileName + "  result:" + result);
		if (!result) {
			return;
		}
		try {
			GitUtil.RecordUploadProjAt("download", userAdd, ".");
		} catch (Exception e) {

			e.printStackTrace();
		}
		fileName = fileName + ".zip";
		String downFileName = fileName;
		if(fileName.getBytes().length != fileName.length()) {
			// fileName中包含中文
			downFileName = "Project.zip";
		}

		DataInputStream in = null;
		OutputStream out = null;
		try {
			resp.reset();// 清空输出流

			// fileName = URLEncoder.encode(fileName,"UTF-8");
			//fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
			System.out.println("fileName:"+ fileName);
			resp.setCharacterEncoding("UTF-8");
			resp.setHeader("Content-disposition", "attachment; filename=" + downFileName);// 设定输出文件头
			resp.setContentType("application/msexcel");// 定义输出类型
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.setHeader("Cache-Control", "no-cache");
			// 输入流：本地文件路径
			in = new DataInputStream(new FileInputStream(new File(downloadRootAddress + fileName)));
			// 输出流
			out = resp.getOutputStream();
			// 输出文件
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.reset();
			try {
				OutputStreamWriter writer = new OutputStreamWriter(resp.getOutputStream(), "UTF-8");
				String data = "<script language='javascript'>alert(\"\\u64cd\\u4f5c\\u5f02\\u5e38\\uff01\");</script>";
				writer.write(data);
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void format_download(String userAdd, String fileName, HttpServletResponse resp, String branch) {
		try {
			GitUtil.gitCheckout(branch, userAdd);
		} catch (Exception e) {

			e.printStackTrace();
		}
		fileName = "GXNU" + ".xml";
		DataInputStream in = null;
		OutputStream out = null;
		try {
			resp.reset();// 清空输出流

			// fileName = URLEncoder.encode(fileName,"UTF-8");
			fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
			resp.setCharacterEncoding("UTF-8");
			resp.setHeader("Content-disposition", "attachment; filename=" + fileName);// 设定输出文件头
			resp.setContentType("application/msexcel");// 定义输出类型
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.setHeader("Cache-Control", "no-cache");
			// 输入流：本地文件路径
			in = new DataInputStream(new FileInputStream(new File(downloadRootAddress + fileName)));
			// 输出流
			out = resp.getOutputStream();
			// 输出文件
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.reset();
			try {
				OutputStreamWriter writer = new OutputStreamWriter(resp.getOutputStream(), "UTF-8");
				String data = "<script language='javascript'>alert(\"\\u64cd\\u4f5c\\u5f02\\u5e38\\uff01\");</script>";
				writer.write(data);
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下
	 * 
	 * @param sourceFilePath :待压缩的文件路径
	 * @param zipFilePath    :压缩后存放路径
	 * @param fileName       :压缩后文件的名称
	 * @return
	 */
	public boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName) {
		boolean flag = false;
		File sourceFile = new File(sourceFilePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		if (sourceFile.exists() == false) {
			System.out.println("待压缩的文件目录：" + sourceFilePath + "不存在.");
		} else {
			try {
				File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
				if (zipFile.exists()) {
					zipFile.delete();
				}
				File[] sourceFiles = sourceFile.listFiles();
				if (null == sourceFiles || sourceFiles.length < 1) {
					System.out.println("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
				} else {
					fos = new FileOutputStream(zipFile);
					zos = new ZipOutputStream(new BufferedOutputStream(fos));
					byte[] bufs = new byte[1024 * 10];
					for (int i = 0; i < sourceFiles.length; i++) {
						if (sourceFiles[i].isDirectory()) {
							continue;
						}
						// 创建ZIP实体，并添加进压缩包
						ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
						zos.putNextEntry(zipEntry);
						// 读取待压缩的文件并写进压缩包里
						fis = new FileInputStream(sourceFiles[i]);
						bis = new BufferedInputStream(fis, 1024 * 10);
						int read = 0;
						while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
							zos.write(bufs, 0, read);
						}
					}
					flag = true;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				// 关闭流
				try {
					if (null != bis)
						bis.close();
					if (null != zos)
						zos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return flag;
	}

	// ======================点击open时，查找所有项目，owl文件 ==========================

	// ==================查找项目（owl）及版本====================
//	public List<String> searchProject(String userAdd) {
//		return searchbranch(userAdd);
//	}

	// 搜索rootAddress路径下的项目（rootAddress路径下子文件夹名称）
	public List<String> searchProject(String userAdd) {

		List<String> projectNames = new ArrayList<String>();
		File file = new File(userAdd);
		File [] fileName = file.listFiles();
		if(fileName==null || fileName.length==0){
			return projectNames;
		}
		for(File f : fileName){
			System.out.println("filename" + f.getName());
			if(f.isDirectory()){
				projectNames.add(f.getName());
			}
		}
		return projectNames;
	}
	public List<String> searchPOwl() {
		return searchbranch(pOwlRootAddress);
	}

	public List<String> searchEOwl() {
		return searchbranch(eOwlRootAddress);
	}

	public List<String> searchbranch(String userAdd) {

		Map<String, String> dicLits = new HashMap<String, String>();
		try {
			dicLits = GitUtil.gitAllBranch(userAdd);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (GitAPIException e) {

			e.printStackTrace();
		}
		List<String> projectNames = new ArrayList<String>();
		// key
		for (String key : dicLits.keySet()) {
			if (key.equals("master")) {
				continue;
			}
			projectNames.add(key);
		}
		return projectNames;
	}

	// ================点击特定项目时，查找所有版本 =========================
	public List<String> searchVersion(String userAdd, String branch) {
		List<String> projectVersions = new ArrayList<String>();
		SAXReader saxReader = new SAXReader();
		try {
			File xmlFile = new File(userAdd + "Project.xml");
			Document document = saxReader.read(xmlFile);
			Element projectElement = document.getRootElement();
			Element fileListElement = projectElement.element("fileList");
			List<?> reqVersionElementList = fileListElement.elements("RequirementVersion");
			if(reqVersionElementList != null) {
				for(Object ve: reqVersionElementList) {
					Element verElem = (Element) ve;
					Element idElement = verElem.element("id");
					if(idElement!=null) {
						projectVersions.add(idElement.getText());
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return projectVersions;
	}
//	public List<String> searchVersion(String userAdd, String branch) {
//		List<String> projectVersions = new ArrayList<String>();
//		List<versionInfo> versions = searchVersionInfo(userAdd, branch);
//		if (versions != null) {
//			for (versionInfo version : versions) {
//				projectVersions.add(version.getTime());
//			}
//		}
//		return projectVersions;
//	}

	public List<String> searchOwlVersion(String branch) {
		return searchVersion1(owlRootAddress, branch);
		
	}

	public List<String> searchVersion1(String userAdd, String branch) {

		List<String> owlVersions = new ArrayList<String>();
		List<versionInfo> versions = searchVersionInfo(userAdd, branch);
		if (versions != null) {
			for (versionInfo version : versions) {
				owlVersions.add(version.getTime());
			}
		}
		return owlVersions;
	}

	// 获取分支下的所有版本
	public List<versionInfo> searchVersionInfo(String userAdd, String branch) {

		setProject(userAdd, branch);

		List<versionInfo> versions = new ArrayList<versionInfo>();
		try {
			GitUtil.gitCheckout(branch, userAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String command = "git reflog " + branch;
		File check = new File(userAdd);

		List<String> vs = new ArrayList<String>();
		String commitVersion = null;

		try {
			Process p1 = Runtime.getRuntime().exec(command, null, check);
			BufferedReader br = new BufferedReader(new InputStreamReader(p1.getInputStream()));
			String s;

			while ((s = br.readLine()) != null) {
				if (s.indexOf("commit") != -1) {
					commitVersion = s.split(" ")[0];
					vs.add(commitVersion);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String v : vs) {
			String versionCommand = "git show " + v;
			try {
				Process p2 = Runtime.getRuntime().exec(versionCommand, null, check);
				BufferedReader br = new BufferedReader(new InputStreamReader(p2.getInputStream()));
				String str = null;
				String time = null;
				String versionId = null;

				while ((str = br.readLine()) != null) {
					if (str.startsWith("commit")) {
						versionId = str.split(" ")[1].substring(0, 7);
					}
					if (str.startsWith("Date:")) {
						str = str.substring(8);
						time = str.substring(0, str.length() - 6);
						str = br.readLine();
						String value = br.readLine().split("0")[0];
						if (value.indexOf("upload") != -1) {
							if (value.indexOf("uploadproject") != -1) {
								continue;
							} else if (value.indexOf("uploadfile") != -1) {
								if (versions.size() > 0) {
									if (versions.get(versions.size() - 1).getCommand().indexOf("uploadfile") != -1) {
										continue;
									}
								}
							} else {
								continue;
							}
						}
						if (value.indexOf("download") != -1) {
							continue;
						}
						versionInfo version = new versionInfo();
						version.setVersionId(versionId);
						version.setTime(time);
						version.setCommand(value);
						versions.add(version);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return versions;
	}

	// ====================== owl相关 ==============================

	// =============================owl相关=============================
	public String[] pOntShowGetNodes(String userAdd, String fileName, String nodeName, String branch) {
		// String[] re = null;
		try {
			GitUtil.gitCheckout(branch, userAdd);
		} catch (Exception e) {

			e.printStackTrace();
		}
		String owlAdd = userAdd + fileName;
		OntologyShow os = new OntologyShow(owlAdd);
		ArrayList<String> al = os.deel(nodeName);
		int length = al.size();
		String[] re = new String[length];
		for (int i = 0; i < length; i++) {
			re[i] = al.get(i);
		}
		return re;
	}

	public ArrayList<MyOntClass> GetProblemDomains(String userAdd, String address, String branch) {
		try {
			GitUtil.gitCheckout(branch, userAdd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String owlAdd = userAdd + address + ".owl";
		System.out.println("owlAdd:"+owlAdd);
		EnvEntity ee = new EnvEntity(owlAdd);
		return ee.getProblemDomains();
	}

	public String[] eOntShowGetNodes(String userAdd, String fileName, String nodeName, String branch) {
		try {
			GitUtil.gitCheckout(branch, userAdd);
		} catch (Exception e) {

			e.printStackTrace();
		}
		String owlAdd = userAdd + fileName;
		EnvEntity ee = new EnvEntity(owlAdd);
		ArrayList<String> al = ee.deel(nodeName);
		int length = al.size();
		String[] re = new String[length];
		for (int i = 0; i < length; i++) {
			re[i] = al.get(i);
		}
		return re;
	}

	// 拷贝owl文件到project目录下
	public boolean copyEOwl(String userAdd, String proBranch, String owlBranch) {
		return copyOwl(userAdd, proBranch, eOwlRootAddress, owlBranch);
	}

	public boolean copyPOwl(String userAdd, String proBranch, String owlBranch) {
		return copyOwl(userAdd, proBranch, pOwlRootAddress, owlBranch);
	}

	public boolean copyOwl(String userAdd, String proBranch, String owlRootAddress, String owlBranch) {
		// 切换owl分支读取owl文件
		try {
			GitUtil.gitCheckout(owlBranch, owlRootAddress);
			GitUtil.currentBranch(owlRootAddress);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// 切换project分支,若不存在，则新建分支
		boolean res = setProject(userAdd, proBranch);
		if (res == false)
			return false;
		try {
			GitUtil.gitCheckout(proBranch, userAdd);
			GitUtil.currentBranch(userAdd);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// 移动owl文件
		String fromPath = owlRootAddress + owlBranch + ".owl";
		String toPath = userAdd + owlBranch + ".owl";
		FileOperation.copyFile(fromPath, toPath);

		// commit
		try {
			GitUtil.RecordUploadProjAt("uploadowlfile", userAdd, ".");
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return res;
	}

	// ==============================pf相关=============================
	public boolean copyPfFile(String userAdd, String fileName, String branch) {
		// 切换project分支,若不存在，则新建分支
		boolean res = setProject(userAdd, branch);
		if (res == false)
			return false;
		try {
			GitUtil.gitCheckout(branch, userAdd);
			GitUtil.currentBranch(userAdd);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// 移动pf文件
		String fromPath = pfRootAddress + fileName + ".pf";
		String toPath = userAdd + fileName + ".pf";
		FileOperation.copyFile(fromPath, toPath);

		// commit
		try {
			GitUtil.RecordUploadProjAt("uploadpffile", userAdd, ".");
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return res;
	}
	
	public void downloadFile(String userName, String projectName, String version, String fileName, HttpServletResponse response) {
		System.out.println("downloadFile");
		String address;
		if(userName.equals("")) {
			address = commanAddress;
		}else {
			address = rootAddress_user + userName + "/";
		}
		address = address + projectName + "/";
		if (fileName != null) {            
			//设置文件路径            
			File file = new File(address + fileName);            
			if (file.exists()) {                
				response.setHeader("content-type", "application/octet-stream");                
				response.setContentType("application/octet-stream");                
				try {                    
					response.setHeader("Content-Disposition", "attachment;filename="+new String(fileName.getBytes("utf-8"),"ISO-8859-1"));                
				} catch (UnsupportedEncodingException e) {                    
					e.printStackTrace();                
				}                
				byte[] buffer = new byte[1024];                
				FileInputStream fis = null;                
				BufferedInputStream bis = null;                
				try {                    
					fis = new FileInputStream(file);                    
					bis = new BufferedInputStream(fis);                    
					OutputStream os = response.getOutputStream();                    
					int i = bis.read(buffer);                    
					while (i != -1) {                        
						os.write(buffer, 0, i);                        
						i = bis.read(buffer);                    
					}               
				} catch (Exception e) {                    
					e.printStackTrace();                
				} finally {                    
					if (bis != null) {                        
						try {                            
							bis.close();                        
						} catch (IOException e) {                            
							e.printStackTrace();                        
						}                    
					}                    
					if (fis != null) {                        
						try {                            
							fis.close();                        
						} catch (IOException e) {                            
							e.printStackTrace();                       
						}                    
					}                
				}            
			}
		}
	}

	public boolean findOwl(String userName, String projectName, String version) {
		boolean res = false;
		String address;
		if(userName.equals("")) {
			address = commanAddress;
		}else {
			address = rootAddress_user + userName + "/";
		}
		address = address + projectName + "/";
		List<versionInfo> versions = searchVersionInfo(address, projectName);
		try {
			if(!GitUtil.currentBranch(address).equals(projectName)) {
				GitUtil.gitCheckout(projectName, address);
			}  //切换分支
			GitUtil.rollback(address, version, versions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		File dictionary = new File(address);
        File[] files = dictionary.listFiles();
        if (files != null) {
        	for (File file : files) {
        		if (!file.isDirectory()) {
                    System.out.println("文件:" + file.getAbsolutePath());
                    if(file.getName().endsWith(".owl")) {
                    	res = true;
                    	return res;
                    }
                }
            }
        }
		return res;
	}
	
	public List<String> searchEowlVersion(String branch) {
		List<String> projectVersions = new ArrayList<String>();
		List<versionInfo> versions = searchVersionInfo(eOwlRootAddress, branch);
		if(versions != null) {
			for(versionInfo version: versions) {
				projectVersions.add(version.getTime());
			}
		}
		return projectVersions;
	}
	
	public boolean copyOwl(String userName, String projectName, String project_version, String owl, String eowl_version) {
		System.out.println("copy");
		boolean res = false;
		// 切换owl分支读取owl文件
		List<versionInfo> ewol_versions = searchVersionInfo(eOwlRootAddress, owl);
		String address;
		if(userName.equals("")) {
			address = commanAddress;
		}else {
			address = rootAddress_user + userName + "/";
		}
		address = address + projectName + "/";
		List<versionInfo> project_versions = searchVersionInfo(address, projectName);
		try {
			//切换到eowl的当前版本
			if(!GitUtil.currentBranch(eOwlRootAddress).equals(owl)) {
				GitUtil.gitCheckout(owl, eOwlRootAddress);
			}
			GitUtil.rollback(eOwlRootAddress, eowl_version, ewol_versions);
			//切换到project的当前版本
			if(!GitUtil.currentBranch(address).equals(projectName)) {
				GitUtil.gitCheckout(projectName, address);
			}
			GitUtil.rollback(address, project_version, project_versions);
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		
		File dictionary = new File(address);
	      File[] files = dictionary.listFiles();
	      if (files != null) {
	      	for (File file : files) {
	      		if (!file.isDirectory()) {
	      			if(file.getName().endsWith(".owl")) {
	      				file.delete();
	                }
	            }
	        }
	      }
		// 移动owl文件
		String fromPath = eOwlRootAddress + owl + ".owl";
		String toPath = address + owl + ".owl";
		System.out.println(fromPath + " " + toPath);
		FileOperation.copyFile(fromPath, toPath);

		// commit
		try {
			GitUtil.RecordUploadProjAt("upload_eowl", address, ".");
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return res;
	}
	
	public EnvEntity getEnvEntity(String projectName) {
		String address = rootAddress + projectName + "/";
	   	File dictionary = new File(address);
      	File[] files = dictionary.listFiles();
      	if (files != null) {
      		for (File file : files) {
      			if (!file.isDirectory()) {
      				System.out.println("文件:" + file.getPath());
      				if(file.getName().endsWith(".owl")) {
      			    	EnvEntity envEntity = new EnvEntity(file.getPath());
      			    	return envEntity;
                	}
            	}
        	}
      	}
      	return null;
	}

	public Boolean writeTxt(String address, String content) {
		// 创建address指定的txt文件，并将content字符串写进去

		try {
			File file = new File(address);
			System.out.println("writeTxt:" + address);
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(content);
			bufferedWriter.flush();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
