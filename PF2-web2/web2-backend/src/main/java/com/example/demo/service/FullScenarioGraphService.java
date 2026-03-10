package com.example.demo.service;

import java.util.EnumMap;
import com.example.demo.bean.Project;

public interface FullScenarioGraphService {
	enum FSGProperty{
		ctrlNodeList,
		intNodeList,
		lineList
	}
	
	public EnumMap<FSGProperty, Object> getNodeList(Project project);
}
