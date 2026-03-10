package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.bean.DirectedLine;

public class Test {
	/**当前路径*/
    private static List<String> nowPath = new ArrayList<>();
    
    /**
     * @param nodeList
     * @param source 起始节点
     * @param target 目标节点
     */
    public static void findAllPath(List<DirectedLine> nodeList,String source,String target){
        if(nowPath.contains(source)){
            System.out.println("这是一个环:"+nowPath);
            nowPath.remove(nowPath.size()-1);
            return;
        }
        for(int i = 0 ;i<nodeList.size();i++){
        	DirectedLine node = nodeList.get(i);
            if(node.getSource().equals(source)){
                nowPath.add(node.getSource());
                if(node.getTarget().equals(target)){
                    nowPath.add(node.getTarget());
                    System.out.println("这是一条路径:"+nowPath);
                    /*因为添加了终点路径,所以要返回两次*/
                    nowPath.remove(nowPath.size()-1);
                    nowPath.remove(nowPath.size()-1);
                    /*已经找到路径,返回上层找其他路径*/
                    continue;
                }
                findAllPath(nodeList,node.getTarget(),target );
            }
        }
        /*如果找不到下个节点,返回上层*/
    if(nowPath.size()>0){
            nowPath.remove(nowPath.size()-1);
        }
    }
 
    
    /**
     * 测试
     */
    public static void main(String[] args){
//        List<DirectedLine> list = new ArrayList<>();
//        list.add(new DirectedLine("Start1", "Branch1"));
//        list.add(new DirectedLine("Branch1", "BehInt1"));
//        list.add(new DirectedLine("BehInt1", "Merge1"));
//        list.add(new DirectedLine("Merge1", "BehInt3"));
//        list.add(new DirectedLine("BehInt3", "Branch2"));
//        list.add(new DirectedLine("Branch2", "BehInt2"));
//        list.add(new DirectedLine("BehInt2", "Merge2"));
//        list.add(new DirectedLine("Merge2", "BehInt4"));
//        list.add(new DirectedLine("BehInt4", "End1"));
//        list.add(new DirectedLine("Branch1", "Delay1"));
//        list.add(new DirectedLine("Delay1", "Merge1"));
//        list.add(new DirectedLine("Branch2", "Delay2"));
//        list.add(new DirectedLine("Delay2", "Merge2"));
//
// 
//        findAllPath(list,"Start1","End1");
    	
    	
    }
    
}
