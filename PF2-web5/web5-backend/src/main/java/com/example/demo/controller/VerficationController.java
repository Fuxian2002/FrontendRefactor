package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.CCSLSet;
import com.example.demo.bean.Project;
import com.example.demo.service.AddressService;
import com.example.demo.service.VerficationService;

@RestController
@CrossOrigin
@RequestMapping("/verfication")
public class VerficationController {
	@Autowired	//自动装配
	VerficationService verficationService;
	
	private String rootAddress = AddressService.rootAddress;
	private String userAddress = AddressService.userAddress;
	
	@RequestMapping(value="/toCCSL",method = RequestMethod.POST)
	@ResponseBody
	public List<CCSLSet> toCCSL(@RequestParam String username, @RequestBody Project project) {
		String userAdd = getUserAdd(username);
		List<CCSLSet> ccslset = verficationService.toCCSL(project, userAdd);
		return ccslset;
	}
	
	public String getUserAdd(String username) {
		String userAdd;
		if (username == null || username == "")
			userAdd = rootAddress;
		else
			userAdd = userAddress + username + "/";
		return userAdd;
	}
}
