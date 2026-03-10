package com.example.demo.bean;

import java.util.ArrayList;
import java.util.List;

public class StrategyAdvice {

    // 指定某一解耦策略
    private String title;

    // 建议内容
    private List<String> content;

    public StrategyAdvice() {
    }

    public StrategyAdvice(String title) {
        this.title = title;
        this.content = new ArrayList<String>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
}
