package com.example.demo.bean;

import java.awt.BorderLayout;

import java.util.ArrayList;
//import javax.swing.JDialog;
//import javax.swing.JLabel;
//import javax.swing.JScrollPane;
//import javax.swing.JTree;
//import javax.swing.tree.DefaultMutableTreeNode;




public class OntologyShow //extends JDialog
{
  Ontology ont;
//  BorderLayout borderLayout1 = new BorderLayout();
//  JLabel jLabel1 = new JLabel();
//  JScrollPane js;
//  JTree tree;
//  DefaultMutableTreeNode root;

//  public static void main(String[] args)
//  {
//	    OntologyShow os = new OntologyShow();
//	    os.setVisible(true);
//	    
//
//  }

  public OntologyShow(String address)
  {
    this.ont = new Ontology(address);
//    try {
//      jbInit();
//    }
//    catch (Exception e) {
//      e.printStackTrace();
//    }
  }

//  private void deel(DefaultMutableTreeNode dn, String name) {
//    System.out.println("Now we deel the class "+ name );
//    ArrayList<String> ll = this.ont.getSubClasses(name);
//    System.out.println(name+" has "+ll.size()+" subclasses");
//    for (int i = 0; i < ll.size(); i++) {
//      System.out.println("OK");
//      String n = ll.get(i);
//      System.out.println(n+" is a subclass of "+name);
//      n = n.substring(n.indexOf("#") + 1);
//      DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(n);
//      dn.add(tmp);
//      deel(tmp, ll.get(i));
//    }
//  }
  
  public ArrayList<String> deel (String name) {
	  ArrayList<String> re = new ArrayList<String>();
      re = this.ont.getSubClasses(name);
	  return re;
  }

//  private void jbInit() throws Exception {
//	 System.out.println("OK from jbInit");
//	 this.ont.getOntClasses();
//    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//    double width = d.getWidth();
//    double height = d.getHeight();
//    setLocation((int)width / 2 - 200, (int)height / 2 - 150);
//    setSize(400, 300);
//    setTitle("Ontology");
//    this.jLabel1.setText("Show the ontology.");
//    getContentPane().setLayout(this.borderLayout1);
//    getContentPane().add(this.jLabel1, "North");
//
//    this.root = new DefaultMutableTreeNode("Thing");
//    this.tree = new JTree(this.root);
//    this.js = new JScrollPane(this.tree);
//    getContentPane().add(this.js, "Center");
//    System.out.println("OK FORMER");
//    deel(this.root, "Thing");
//    System.out.println("OK BACK");
//    this.tree.updateUI();
//  }
}

