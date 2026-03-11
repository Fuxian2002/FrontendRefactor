package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.bean.MyOntClass;
import com.example.demo.bean.Project;
import com.example.demo.service.AddressService;
import com.example.demo.service.FileService;
import com.example.demo.service.ProblemEditor;
import com.example.demo.service.ProjectService;

@CrossOrigin
@RestController
// @Controller
@RequestMapping("/file")
@DependsOn("addressService")
public class FileController {
	@Autowired	// 自动装配
	FileService fileService;
	@Autowired	// 自动装配
	ProjectService projectService;
	private String reqModelPrefix = AddressService.reqModelDir;
	private String devModelPrefix = AddressService.devModelDir;
	private String owlRootAddress = AddressService.owlRootAddress;
	private String rootAddress = AddressService.rootAddress;
	private String userAddress = AddressService.userAddress;
	private String pOwlRootAddress = AddressService.pOwlRootAddress;
	private String eOwlRootAddress = AddressService.eOwlRootAddress;

	@RequestMapping("/upload/{projectAddress}")
	public boolean upload(@RequestParam String username, @RequestParam("xmlFile") MultipartFile uploadFile,
			@PathVariable("projectAddress") String projectName)
			throws IOException {
		if (uploadFile == null) {
			System.out.println("上传失败，无法找到文件！");
		}
		String fileName = uploadFile.getOriginalFilename();
		if (!fileName.endsWith(".xml")) {
			System.out.println("上传失败，请选择xml文件！");
		}
		fileService.addFile(uploadFile, getUserAdd(username)+projectName+"/", projectName);
		System.out.println(fileName + "上传成功");
		return true;
	}

	@RequestMapping("/uploadOwl/{projectAddress}/{type}")
	public boolean uploadOwl(@RequestParam String username, @RequestParam("xmlFile") MultipartFile uploadFile,
			@PathVariable("projectAddress") String projectAddress, @PathVariable("type") String type)
			throws IOException {
		if (uploadFile == null) {
			System.out.println("上传失败，无法找到文件！");
		}
		// BMP、JPG、JPEG、PNG、GIF
		String fileName = uploadFile.getOriginalFilename();
		if (!fileName.endsWith(".owl")) {
			System.out.println("上传失败，请选择owl文件！");
		}
		// 逻辑处理
		String owlBranch = fileName.substring(0, fileName.length() - 4);
		String userAdd = getUserAdd(username);

		// 存入PF/powl或PF/eowl文件夹下
		if (type.contentEquals("eowl")) {
			fileService.setEOwl(owlBranch);
			fileService.addFile(uploadFile, eOwlRootAddress, owlBranch);
			fileService.copyEOwl(userAdd+projectAddress+"/", projectAddress, owlBranch);// 存入PF/Project文件夹下
		} else {
			fileService.setPOwl(owlBranch);
			fileService.addFile(uploadFile, pOwlRootAddress, owlBranch);
			fileService.copyPOwl(userAdd+projectAddress+"/", projectAddress, owlBranch);// 存入PF/Project文件夹下
		}
		return true;
	}

	@RequestMapping(value = "/setProject/{projectAddress}", method = RequestMethod.POST)
	@ResponseBody
	public boolean setProject(@RequestParam String username, @PathVariable("projectAddress") String projectAddress) {
		String branch = projectAddress;
		String userAdd = getUserAdd(username);
		boolean result = fileService.setProject(userAdd+branch+"/", branch);
		if(!result){
			return false;
		}
		boolean result2 = fileService.setProject(userAdd+branch+"/"+reqModelPrefix+"/", branch);
		return result;
	}

	@RequestMapping(value = "/moveOwl/{type}/{projectAddress}/{owl}/{version}", method = RequestMethod.POST)
	@ResponseBody
	public boolean moveOwl(@RequestParam String username, @PathVariable("projectAddress") String projectAddress,
			@PathVariable("type") String type, @PathVariable("owl") String owl,
			@PathVariable("version") String version) {
		System.out.println(type);
		String userAdd = getUserAdd(username);
		if (type.contentEquals("eowl"))
			return fileService.copyEOwl(userAdd+projectAddress+"/", projectAddress, owl);
		else if (type.contentEquals("powl"))
			return fileService.copyPOwl(userAdd+projectAddress+"/", projectAddress, owl);
		else
			return false;
	}

	@RequestMapping(value = "/getProject/{projectName}/{version}", method = RequestMethod.GET)
	@ResponseBody
	public Project getProject(@RequestParam String username, @PathVariable("projectName") String projectName,
							  @PathVariable("version") String version) {
		// 直接使用 UserProject 目录，不基于用户名
		String userAdd = userAddress;
		String branch = projectName;
		if(version != null && !version.equals("") && !version.equals("undefined") && !version.equals("null")){
			Project project = fileService.getProject(userAdd+branch+"/", branch, version);
			return project;
		}
		Project project = fileService.getProject(userAdd+branch+"/", branch);
		return project;
	}

	@RequestMapping(value = "/saveProject/{projectAddress}", method = RequestMethod.POST)
	@ResponseBody
	public boolean saveProject(@RequestParam String username, @PathVariable("projectAddress") String projectAddress,
			@RequestBody Project project) {
		System.out.printf("saveProject" + "\n");
		return fileService.saveProject(getUserAdd(username)+projectAddress+"/",project, projectAddress);
	}

	@RequestMapping(value = "/format/{projectAddress}", method = RequestMethod.POST)
	@ResponseBody
	public boolean format(@RequestParam String username, @PathVariable("projectAddress") String projectAddress,
						  @RequestBody Project project) {
		String branch = projectAddress;
		boolean result = false;
		result = fileService.format(getUserAdd(username)+branch+"/"+reqModelPrefix+"/", project, branch);
		return result;
	}

	@RequestMapping(value = "/download/{projectAddress}/{username}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void download(@PathVariable("projectAddress") String projectAddress, @PathVariable("username") String username, HttpServletResponse resp) {
		String branch = projectAddress;
		String fileName = projectAddress.split("/")[0];
		System.out.println("projectAddress:" + projectAddress + "  fileName:" + fileName + "  resp:" + resp);
		fileService.download(getUserAdd(username)+branch+"/", fileName, resp, branch);
	}

	@RequestMapping(value = "/formatdownload/{projectAddress}/{username}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void format_download(@PathVariable("projectAddress") String projectAddress, @PathVariable("username") String username, HttpServletResponse resp) {
		// @RequestParam
		String branch = projectAddress;
		String fileName = projectAddress.split("/")[0];
		fileService.format_download(getUserAdd(username)+branch+"/"+reqModelPrefix+"/", fileName, resp, branch);
	}

	@RequestMapping(value = "/searchProject", method = RequestMethod.GET)
	@ResponseBody
	public List<String> searchProject(@RequestParam String username) {
		// 直接返回 UserProject 下的所有项目，不基于用户名
		List<String> projects = fileService.searchProject(userAddress);
		return projects;
	}

	@RequestMapping(value = "/searchOwl/{type}", method = RequestMethod.GET)
	@ResponseBody
	public List<String> searchOwl(@PathVariable("type") String type) {
		// System.out.println("searchOwl" + type);
		if (type.contentEquals("eowl"))
			return fileService.searchEOwl();
		else
			return fileService.searchPOwl();
	}
	
	@RequestMapping(value="/searchEowlVersion/{eowl}",method = RequestMethod.GET)
	@ResponseBody
	public List<String> searchEowlVersion(@PathVariable("eowl") String eowl) {
		String branch = eowl;
		List<String> versions = fileService.searchEowlVersion(branch);
		return versions;
	}

	@RequestMapping(value = "/searchVersion/{project}", method = RequestMethod.GET)
	@ResponseBody
	public List<String> searchVersion(@RequestParam String username, @PathVariable("project") String project) {
		String branch = project;
		// 直接使用 UserProject 目录，不基于用户名
		List<String> versions = fileService.searchVersion(userAddress+branch+"/", branch);
		return versions;
	}

	@RequestMapping(value = "/searchOwlVersion/{owl}", method = RequestMethod.GET)
	@ResponseBody
	public List<String> searchOwlVersion(@PathVariable("owl") String owl) {
		String branch = owl;
		List<String> versions = fileService.searchOwlVersion(branch);
		return versions;
	}

	@RequestMapping("/getProblemDomains/{owlAdd}/{owlName}")
	public ArrayList<MyOntClass> getProblemDomains(@RequestParam String username, @PathVariable("owlAdd") String owlAdd,
			@PathVariable("owlName") String owlName) {
		ArrayList<MyOntClass> re = null;
		String branch = owlAdd;
		re = fileService.GetProblemDomains(getUserAdd(username)+branch+"/", owlName, branch);
		return re;
	}

	@RequestMapping("/getNodes/{projectAddress}/{fileName}/{nodeName}/{type}")
	public String[] getNodes(@RequestParam String username, @PathVariable("projectAddress") String projectAddress,
			@PathVariable("fileName") String fileName, @PathVariable("nodeName") String nodeName,
			@PathVariable("type") String type) {
		// owlName 文件名
		String[] re = null;
		String branch = projectAddress;
		if (type.equals("powl"))
			re = fileService.pOntShowGetNodes(getUserAdd(username)+branch+"/", fileName, nodeName, branch);
		else if (type.equals("eowl"))
			re = fileService.eOntShowGetNodes(getUserAdd(username)+branch+"/", fileName, nodeName, branch);
		return re;
	}

	@RequestMapping("/uploadpf/{projectAddress}")
	public void uploadpf(@RequestParam String username, @RequestParam("xmlFile") MultipartFile uploadFile,
			@PathVariable("projectAddress") String projectAddress) throws IOException {
		String branch = projectAddress;
		if (uploadFile == null) {
			System.out.println("上传失败，无法找到文件！");
		}
		String fileName = uploadFile.getOriginalFilename();
		if (!fileName.endsWith(".pf")) {
			System.out.println("上传失败，请选择pf文件！");
		}else{
			fileName = fileName.replace(".pf","");
		}
		if(branch.equals("undefined")){
			branch = fileName;
		}
		fileService.addpfFile(uploadFile);
		fileService.copyPfFile(getUserAdd(username)+branch+"/", fileName, branch);
	}

	@RequestMapping(value = "/xtextToXmi", method = RequestMethod.POST)
	@ResponseBody
	public Project getXMI(@RequestParam String username,@RequestBody String projectName) {
		File xmlFile = ProblemEditor.performSave(projectName);
		Project project = ProblemEditor.transformXML(xmlFile);
		String branch = projectName.replace(".pf","");
		System.out.println("~~~~~~projectAddress~~~~~~~"+projectName);
		fileService.saveProject(getUserAdd(username)+branch+"/", project, branch);
		return project;
	}
	
	@RequestMapping( value = "/downloadRUCM", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void downloadRUCM(String userName, String projectName, 
			String version, HttpServletResponse resp) {
		fileService.downloadFile(userName, projectName, version, "RUCM.txt", resp);
		//有改动+projectAddress+"/"
	}
	
	@RequestMapping( value = "/findOwl",method = RequestMethod.GET)
	@ResponseBody
	public boolean findOwl(String userName, String projectName, String version) {
		boolean res = fileService.findOwl(userName, projectName, version);
		//有改动+projectAddress+"/"
		return res;
	}
	
	@RequestMapping(value = "/importOwl",method = RequestMethod.GET)
	@ResponseBody
	public boolean importOwl(String userName, String projectName, String project_version, String eowl,String eowl_version) {
		System.out.println("******************import owl*****************************");
		System.out.println(userName + " " + projectName + " " + project_version + " " + eowl + " " + eowl_version);
		return fileService.copyOwl(userName, projectName, project_version, eowl, eowl_version);
		//有改动+projectAddress+"/"
	}

	public String getUserAdd(String username) {
		// 不需要多用户概念，所有项目都存储在共享的 PF_Storage/UserProject 目录
		return userAddress;
	}

}
