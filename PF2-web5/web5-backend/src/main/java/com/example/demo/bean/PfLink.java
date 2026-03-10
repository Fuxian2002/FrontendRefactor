package com.example.demo.bean;

import java.util.List;

public class PfLink {
    private String from;
    private String type;
    private String to;
    private List<String> pheList;
    private String name;

    public PfLink(String from, String type, String to, List<String> pheList, String name) {
        super();
        this.from = from;
        this.type = type;
        this.pheList = pheList;
        this.to = to;
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPheList() {
        return pheList;
    }

    public void setPheList(List<String> pheList) {
        this.pheList = pheList;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        String res = from;
        if (type == null)
            res += " --";
        else
            res += " " + type;
        res += " " + to;
        if (!pheList.isEmpty() && !pheList.equals("")) {
            res += " {";
            String phes = "";
            for (String phe : pheList) {
                if(phes.equals("")){
                    phes += phe;
                }else {
                    phes += "; " + phe;
                }
            }
            res += phes;
            res += "}";
        }
        if (name != null && !name.contentEquals(""))
            res += " \"" + name + "\"";
//		System.out.println("PfLink.toString:"+res);
        return res;
    }
}
