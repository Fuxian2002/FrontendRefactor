package com.example.demo;

import com.example.demo.bean.*;
import com.example.demo.service.ProjectService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestJavaRule {

    @Test
    void test() {
        List<String> a = new ArrayList<String>();
        a.add("1");
        a.add("2");
        for(String s: a) {
            if(s.equals("2")) {
                s = "3";
            }
        }
        for(String s: a) {
            System.out.println(s);
        }
    }

    @Test
    void testCkeckStrategyOne() {
        ProjectService projectService = new ProjectService();
        Project project = new Project();
        project.setTitle("StrategyOne");

        ContextDiagram contextDiagram = new ContextDiagram();



    }
}
