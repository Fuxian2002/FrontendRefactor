package com.example.demo.bean;

public class ProjectRecord {
    private String id;  // 项目记录ID，唯一标识一个项目记录
    private String strategyId;  // 使用的解耦策略ID
    private Project project;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
