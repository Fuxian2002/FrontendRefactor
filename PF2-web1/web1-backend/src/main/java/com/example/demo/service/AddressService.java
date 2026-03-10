package com.example.demo.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
public class AddressService {

	private String devModelDir;
	private String reqModelDir;
	private String rootAddress;
	private String userAddress;
	private String downloadRootAddress;
	private String pfRootAddress;
	private String owlRootAddress;
	private String pOwlRootAddress;
	private String eOwlRootAddress;
	private String tmpRootAddress;
	// 修改springboot后， 保持文件时相对路径存到springboot路径下，因此使用绝对路径
	// xtext只能识别相对路径
	private String lastestProjectAddress;

	public String getDevModelDir() {
		return devModelDir;
	}

	public void setDevModelDir(String devModelDir) {
		this.devModelDir = devModelDir;
	}

	public String getReqModelDir() {
		return reqModelDir;
	}

	public void setReqModelDir(String reqModelDir) {
		this.reqModelDir = reqModelDir;
	}

	public String getRootAddress() {
		return rootAddress;
	}

	public void setRootAddress(String rootAddress) {
		this.rootAddress = rootAddress;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getDownloadRootAddress() {
		return downloadRootAddress;
	}

	public void setDownloadRootAddress(String downloadRootAddress) {
		this.downloadRootAddress = downloadRootAddress;
	}

	public String getPfRootAddress() {
		return pfRootAddress;
	}

	public void setPfRootAddress(String pfRootAddress) {
		this.pfRootAddress = pfRootAddress;
	}

	public String getOwlRootAddress() {
		return owlRootAddress;
	}

	public void setOwlRootAddress(String owlRootAddress) {
		this.owlRootAddress = owlRootAddress;
	}

	public String getpOwlRootAddress() {
		return pOwlRootAddress;
	}

	public void setpOwlRootAddress(String pOwlRootAddress) {
		this.pOwlRootAddress = pOwlRootAddress;
	}

	public String geteOwlRootAddress() {
		return eOwlRootAddress;
	}

	public void seteOwlRootAddress(String eOwlRootAddress) {
		this.eOwlRootAddress = eOwlRootAddress;
	}

	public String getTmpRootAddress() {
		return tmpRootAddress;
	}

	public void setTmpRootAddress(String tmpRootAddress) {
		this.tmpRootAddress = tmpRootAddress;
	}

	public String getLastestProjectAddress() {
		return lastestProjectAddress;
	}

	public void setLastestProjectAddress(String lastestProjectAddress) {
		this.lastestProjectAddress = lastestProjectAddress;
	}
}
