package com.example.demo.bean;

import java.util.List;

public class Trace {
    private String source;
    private List<String> target;
    private String type;

    public Trace() {

    }

    public Trace(String type) {
        this.type = type;
    }

    public Trace(String source, String type) {
        this.source = source;
        this.type = type;
    }

    public Trace(String source, List<String> target, String type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
