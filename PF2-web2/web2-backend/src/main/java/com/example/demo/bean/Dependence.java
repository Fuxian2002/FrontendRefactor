package com.example.demo.bean;

import java.util.List;
import java.util.Set;

public class Dependence {
    // Target依赖Source，即Source要先于Target发生
    private String Source;  // 被依赖方
    private Set<String> Data; // 依赖数据/设备
    private String Target;  // 依赖方

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public Set<String> getData() {
        return Data;
    }

    public void setData(Set<String> data) {
        Data = data;
    }

    public String getTarget() {
        return Target;
    }

    public void setTarget(String target) {
        Target = target;
    }
}
