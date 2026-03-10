package com.example.demo.bean;

import java.util.List;

public class EMF {
    String title;
    List<PfNode> nodes;
    List<PfLink> links;
    List<PfPhenomenon> phenomenons;
    public EMF() {
        super();
    }

    public EMF(String title) {
        super();
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PfNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<PfNode> nodes) {
        this.nodes = nodes;
    }

    public List<PfLink> getLinks() {
        return links;
    }

    public void setLinks(List<PfLink> links) {
        this.links = links;
    }

    public List<PfPhenomenon> getPhenomenons() {
        return phenomenons;
    }
    public void setPhenomenons(List<PfPhenomenon> phenomenons) {
        this.phenomenons = phenomenons;
    }

    public String toString() {
        String res = "problem: " + title;
        if (nodes != null)
            for (PfNode node : nodes) {
                res += "\n\n" + node.toString();
            }
        if(phenomenons != null){
            for(PfPhenomenon phenomenon : phenomenons){
                res += "\n\n" + phenomenon.toString();
            }
        }
        if (links != null)
            for (PfLink link : links) {
                res += "\n\n" + link.toString();
            }

        return res;
    }
}
