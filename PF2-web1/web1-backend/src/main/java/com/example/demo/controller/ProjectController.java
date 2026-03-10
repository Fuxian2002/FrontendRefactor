package com.example.demo.controller;


import java.util.List;

import com.example.demo.bean.*;
import com.example.demo.bean.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ProjectService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/project")
public class ProjectController {
	@Autowired	//自动装配
	ProjectService projectService;

	@RequestMapping(value="/getPhenomenon",method = RequestMethod.POST)
	@ResponseBody
	public List<Phenomenon> getPhenomenon(@RequestBody Project project) {
		List<Phenomenon> phenomenonList = projectService.getPhenomenon(project);
		return phenomenonList;
	}

	@RequestMapping(value="/getReference",method = RequestMethod.POST)
	@ResponseBody
	public List<RequirementPhenomenon> getRequirementPhenomenon(@RequestBody Project project) {
		List<RequirementPhenomenon> phenomenonList = projectService.getRequirementPhenomenon(project);
		return phenomenonList;
	}

	@RequestMapping(value="/checkPD",method = RequestMethod.POST)
	@ResponseBody
	public boolean checkPD(@RequestBody Project project) {
		boolean res = projectService.checkPD(project.getProblemDiagram());
		return res;
	}

	@RequestMapping(value="/checkCorrectness",method = RequestMethod.POST)
	@ResponseBody
	public List<Error> checkCorrectness(@RequestBody Project project) {
		List<Error> errorList = projectService.checkCorrectness(project);
		return errorList;
	}

	@RequestMapping(value="/checkWellFormed",method = RequestMethod.POST)
	@ResponseBody
	public List<Error> checkWellFormed(@RequestBody Project project) {
		List<Error> errorList = projectService.checkWellFormed(project);
		return errorList;
	}

	@RequestMapping(value="/checkCorrectContext",method = RequestMethod.POST)
	@ResponseBody
	public List<Error> checkCorrectContext(@RequestBody Project project) {
		List<Error> errorList = projectService.checkCorrectContext(project);
		return errorList;
	}

	@RequestMapping(value="/checkCorrectProblem",method = RequestMethod.POST)
	@ResponseBody
	public List<Error> checkCorrectProblem(@RequestBody Project project) {
		List<Error> errorList = projectService.checkCorrectProblem(project);
		return errorList;
	}

	@RequestMapping(value="/getSubProblemDiagram",method = RequestMethod.POST)
	@ResponseBody
	public Project getSubProblemDiagram(@RequestBody Project project) {
		project = projectService.getSubProblemDiagram(project);
		return project;
	}

	@RequestMapping(value="/getBehIntList/{sgName}",method = RequestMethod.POST)
	@ResponseBody
	public List<Phenomenon> getBehIntList(@PathVariable("sgName") String sgName,@RequestBody Project project) {
		List<Phenomenon> pheList = projectService.getBehIntList(project,sgName);
		return pheList;
	}

	@RequestMapping(value="/getExpIntList/{sgName}",method = RequestMethod.POST)
	@ResponseBody
	public List<Phenomenon> getExpIntList(@PathVariable("sgName") String sgName,@RequestBody Project project) {
		List<Phenomenon> pheList = projectService.getExpIntList(project,sgName);
		return pheList;
	}

	@RequestMapping(value="/getScenarioGraph",method = RequestMethod.POST)
	@ResponseBody
	public Project getScenarioGraph(@RequestBody Project project, String userName, String projectName) {
		System.out.println("getScenarioGraph:userName:" + userName + "  projectName:" + projectName);
		String branch = projectName;
		project = projectService.getScenarioGraphs(project, userName, branch);
		return project;
	}

	@RequestMapping(value="/getConnIntList/{sgName}",method = RequestMethod.POST)
	@ResponseBody
	public List<Phenomenon> getConnIntList(@PathVariable("sgName") String sgName,@RequestBody Project project) {
		List<Phenomenon> pheList = projectService.getConnIntList(project,sgName,"ConnInt");
		return pheList;
	}

	@RequestMapping(value="/getIntList/{sgName}",method = RequestMethod.POST)
	@ResponseBody
	public List<Phenomenon> getIntList(@PathVariable("sgName") String sgName,@RequestBody Project project) {
		List<Phenomenon> pheList = projectService.getIntList(project,sgName);
		return pheList;
	}

	@RequestMapping(value="/getFullExpIntList/{sgName}",method = RequestMethod.POST)
	@ResponseBody
	public List<Phenomenon> getFullExpIntList(@PathVariable("sgName") String sgName,@RequestBody Project project) {
		List<Phenomenon> pheList = projectService.getFullExpIntList(project,sgName);
		return pheList;
	}

	@RequestMapping(value="/getFullScenarioGraph",method = RequestMethod.POST)
	@ResponseBody
	public Project getFullScenarioGraph(@RequestBody Project project) {
		project = projectService.getCombinedScenarioGraph(project);
		return project;
	}

	@RequestMapping(value="/getBreakdownScenarioGraph",method = RequestMethod.POST)
	@ResponseBody
	public Project getBreakdownScenarioGraph(@RequestBody Project project) {
		project = projectService.getBreakdownScenarioGraph(project);
		return project;
	}

}
