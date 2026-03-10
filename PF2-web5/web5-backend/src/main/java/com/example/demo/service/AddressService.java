package com.example.demo.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
public class AddressService {

	public static String devModelDir;
	public static String reqModelDir;
	public static String rootAddress;
	public static String userAddress;
	public static String downloadRootAddress;
	public static String pfRootAddress;
	public static String owlRootAddress;
	public static String pOwlRootAddress;
	public static String eOwlRootAddress;
	public static String tmpRootAddress;

	public static String getDevModelDir() {
		return devModelDir;
	}

	public void setDevModelDir(String devModelDir) {
		AddressService.devModelDir = devModelDir;
	}

	public static String getReqModelDir() {
		return reqModelDir;
	}

	public void setReqModelDir(String reqModelDir) {
		AddressService.reqModelDir = reqModelDir;
	}

	public static String getRootAddress() {
		return rootAddress;
	}

	public void setRootAddress(String rootAddress) {
		AddressService.rootAddress = rootAddress;
	}

	public static String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		AddressService.userAddress = userAddress;
	}

	public static String getDownloadRootAddress() {
		return downloadRootAddress;
	}

	public void setDownloadRootAddress(String downloadRootAddress) {
		AddressService.downloadRootAddress = downloadRootAddress;
	}

	public static String getPfRootAddress() {
		return pfRootAddress;
	}

	public void setPfRootAddress(String pfRootAddress) {
		AddressService.pfRootAddress = pfRootAddress;
	}

	public static String getOwlRootAddress() {
		return owlRootAddress;
	}

	public void setOwlRootAddress(String owlRootAddress) {
		AddressService.owlRootAddress = owlRootAddress;
	}

	public static String getpOwlRootAddress() {
		return pOwlRootAddress;
	}

	public void setpOwlRootAddress(String pOwlRootAddress) {
		AddressService.pOwlRootAddress = pOwlRootAddress;
	}

	public static String geteOwlRootAddress() {
		return eOwlRootAddress;
	}

	public void seteOwlRootAddress(String eOwlRootAddress) {
		AddressService.eOwlRootAddress = eOwlRootAddress;
	}

	public static String getTmpRootAddress() {
		return tmpRootAddress;
	}

	public void setTmpRootAddress(String tmpRootAddress) {
		AddressService.tmpRootAddress = tmpRootAddress;
	}

}
