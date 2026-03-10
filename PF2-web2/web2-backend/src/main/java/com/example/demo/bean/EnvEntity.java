package com.example.demo.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// import javax.swing.JDialog;
// import javax.swing.JLabel;
// import javax.swing.JScrollPane;
// import javax.swing.JTree;
// import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;

public class EnvEntity {
	private String filepath;
	String filename;
	OntModel model;
	boolean if_en=false;//是否有 EnvironmentEntity
	ArrayList<String> ems = new ArrayList<String>();

	ArrayList<MyOntClass> mc;
	ArrayList<String> mc_name;

	public EnvEntity(String fileAdd) {
		this.filepath = fileAdd;
		mc = new ArrayList<MyOntClass>();
		mc = this.getOntClasses(filepath);
		setMcName();
	}

	private void setMcName() {
		mc_name = new ArrayList<String>();
		for (int i = 0; i < mc.size(); i++) {
			mc_name.add(mc.get(i).getName());
		}
	}

	public ArrayList<MyOntClass> getProblemDomains() {
		return mc;
	}

	public MyOntClass getProblemDomain(String name) {
		MyOntClass o = new MyOntClass();
		for (int i = 0; i < mc.size(); i++) {
			o = mc.get(i);
			if (o.getName().equals(name))
				break;
		}
		return o;
	}

	/**
	 * 获取环境实体子类对应的实例集合
	 *
	 * @param filepath
	 *                 owl文件集合
	 * @return
	 */
	public ArrayList<MyOntClass> getOntClasses(String filepath) {
		System.out.println("-------getOntClasses-----");
		model = ModelFactory.createOntologyModel();
		// 读取owl文件
		model.read(filepath);
		for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
			OntClass c = i.next();
			//System.out.println(c.getLocalName());
			if(c.getLocalName()!=null && c.getLocalName().equals("EnvironmentEntity")){
				if_en = true;
				break;
			}
		}

		if(if_en){
			ems.add("DataProcessDevice");
			ems.add("LaunchDevice");
			ems.add("ManageControlDevice");
			ems.add("ReceiveDevice");
			ems.add("NaturalEntity");
			ems.add("Analyzer");
			ems.add("Commander");
			ems.add("Maintainer");
			ems.add("SoftwareSystem");
		}else{
			ems.add("Actuator");
			ems.add("Sensor");
		}
		// 结果集合
		ArrayList<MyOntClass> res = new ArrayList<MyOntClass>();
		int id = 1;
		for (Iterator<Individual> j = (Iterator<Individual>)model.listIndividuals(); j.hasNext();){
			Individual ind = j.next();
			OntClass c = ind.getOntClass(true);
			String type = c.getLocalName();
			if(ems.contains(type)){
				System.out.println("class: "+type+"  instance: "+ind.getLocalName());
				MyOntClass o = new MyOntClass();
				o.setId(id++);
				o.setName(ind.getLocalName());
				o.setType(type);
				String npuri = ind.getURI();
				npuri = npuri.substring(0,npuri.indexOf("#")+1);
				System.out.println("uri: "+npuri);
				if(!if_en){
					o.addValues(getIndividualProperty(ind, "has_attribute",npuri));
					o.addSignals(getIndividualProperty(ind,"has_signal",npuri));
					o.addInstructions(getIndividualProperty(ind,"has_instruction",npuri));
					ArrayList<String> timed_aut = getIndividualProperty(ind,"has_SHA",npuri);
					if(timed_aut.size()>0){
						o.setIsdynamic(true);
						if(timed_aut.size()==1){
							o.setTA_name(timed_aut.get(0));
							// 根据状态机名称获取状态机对象
//							OntClass sm = getOntClass(filepath, timed_aut.get(0));
							Individual sm = model.getIndividual(npuri+timed_aut.get(0));
							System.out.println("has_timed_automaton: "+sm.getLocalName());
							// 获取状态机的状态
							o.addStates(getIndividualProperty(sm, "has_state",npuri));
							List<Individual> timeAutomaton = new ArrayList<>();
							timeAutomaton.add(sm);
//							timeAutomaton.add(getOntClass(filepath, timed_aut.get(0)));
							o.setTimedAutomaton(timeAutomaton);
						} else
							System.out.println("Individual" + ind.getLocalName() + "的虚拟机个数大大于1个");
						// 根据value获取对应的状态机OntClass对象
					}
				}
				else{
					if(type.equals("DataProcessDevice") || type.equals("LaunchDevice")
							|| type.equals("ManageControlDevice") || type.equals("ReceiveDevice")) {
						o.addValues(getIndividualProperty(ind, "has_attribute",npuri));
						o.addInstructions(getIndividualProperty(ind,"has_instruction",npuri));
					}
					if (type.equals("DataProcessDevice") || type.equals("LaunchDevice")
							|| type.equals("ManageControlDevice") || type.equals("ReceiveDevice")
							|| type.equals("SoftwareSystem")) {
						ArrayList<String> timed_aut = getIndividualProperty(ind,"has_SHA",npuri);
						if(timed_aut.size()>0){
							o.setIsdynamic(true);
							if(timed_aut.size()==1){
								o.setTA_name(timed_aut.get(0));
								// 根据状态机名称获取状态机对象
//								OntClass sm = getOntClass(filepath, timed_aut.get(0));
								Individual sm = model.getIndividual(npuri+timed_aut.get(0));
								System.out.println("has_SHA: "+sm.getLocalName());
								// 获取状态机的状态
								o.addStates(getIndividualProperty(sm, "has_state",npuri));

								List<Individual> timeAutomaton = new ArrayList<>();
								timeAutomaton.add(sm);
								o.setTimedAutomaton(timeAutomaton);
							} else
								System.out.println("Individual" + ind.getLocalName() + "的虚拟机个数大于1个");
							// 根据value获取对应的状态机OntClass对象
						}
					}
					if(type.equals("Analyzer") || type.equals("Commander")
							|| type.equals("Maintainer")){
						ArrayList<String> timed_aut =getIndividualProperty(ind,"has_command_SHA",npuri);
						if(timed_aut.size()>0){
							o.setIsdynamic(true);
							if(timed_aut.size()==1){
								o.setTA_name(timed_aut.get(0));
								// 根据状态机名称获取状态机对象
//								OntClass sm = getOntClass(filepath, timed_aut.get(0));
								Individual sm = model.getIndividual(npuri+timed_aut.get(0));
								System.out.println("has_command_SHA: "+sm.getLocalName());
								// 获取状态机的状态
								o.addStates(getIndividualProperty(sm, "has_state",npuri));
								List<Individual> timeAutomaton = new ArrayList<>();
								timeAutomaton.add(sm);
								o.setTimedAutomaton(timeAutomaton);
							} else
								System.out.println("Individual" + ind.getLocalName() + "的虚拟机个数大于1个");
							// 根据value获取对应的状态机OntClass对象
						}
					}
				}
				res.add(o);
			}
		}
		return res;
	}

	/**
	 * 根据name值返回对应的OntClass对象
	 *
	 * @param filepath
	 *                 owl文件的绝对路径
	 * @param name
	 *                 Class的名称
	 * @return
	 */
	public OntClass getOntClass(String filepath, String name) {
		model = ModelFactory.createOntologyModel();
		// 读取owl文件
		model.read(filepath);
		// 遍历OntClass
		for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
			OntClass c = i.next();
			if (c.getLocalName() != null)
				if (c.getLocalName().equals(name)) {
					return c;
				}
		}
		return null;
	}

	public Individual getIndvidual(String filepath, String uri) {
		// 读取指定的instance
		model = ModelFactory.createOntologyModel();
		model.read(filepath);
		Individual ind = model.getIndividual(uri);

		return ind;
	}

	/**
	 * 根据property的值，返回对应约束值
	 *
	 * @param c
	 * @param property
	 * @return
	 */
	public ArrayList<String> getRestrictionValue(OntClass c, String property) {
		System.out.println("-------getRestrictionValue-----"+c.getLocalName()+": "+property);
		// 结果集合
		ArrayList<String> res = new ArrayList<String>();
		// c的父类集合
		ExtendedIterator<OntClass> eitr = ((OntClass) c.as(OntClass.class)).listSuperClasses(true);
		while (eitr.hasNext()) {
			OntClass cls = eitr.next();
			System.out.println("super: "+cls.getLocalName());
			// 当前父类对象是否是约束类型
			if (cls.isRestriction()) {
				System.out.println("是约束类型");
				// 当前父类能否转换成约束值类型
				if (cls.canAs(AllValuesFromRestriction.class)) {
					System.out.println("canAs(AllValuesFromRestriction.class)");
//					AllValuesFromRestriction av = r.asAllValuesFromRestriction();
					String values = ((AllValuesFromRestriction) cls.as(AllValuesFromRestriction.class))
							.getAllValuesFrom().getLocalName();
					String prop = ((AllValuesFromRestriction) cls.as(AllValuesFromRestriction.class)).getOnProperty()
							.getLocalName();
//					System.out.println( "AllValuesFrom class " +
//							av.getAllValuesFrom().getURI() +
//							" on property " + av.getOnProperty().getURI() );
					if (prop.equals(property)) {
						res.add(values);
						System.out.println(values+ "  property:"+property);
					}
				}else if (cls.canAs(SomeValuesFromRestriction.class)) {
					System.out.println("canAs(SomeValuesFromRestriction.class)");
//					SomeValuesFromRestriction av = r.asSomeValuesFromRestriction();
					String values = ((SomeValuesFromRestriction) cls.as(SomeValuesFromRestriction.class))
							.getSomeValuesFrom().getLocalName();
					String prop = ((SomeValuesFromRestriction) cls.as(SomeValuesFromRestriction.class)).getOnProperty()
							.getLocalName();
//					System.out.println( "SomeValuesFrom class " +
//							av.getSomeValuesFrom().getURI() +
//							" on property " + av.getOnProperty().getURI() );
					if (prop.equals(property)) {
						res.add(values);
						System.out.println(values+ "  property:"+property);
					}
				}
			}
		}
		return res;
	}

	public ArrayList<String> getIndividualProperty(Individual c, String property, String npuri) {
		System.out.println("-------getIndividualProperty-----"+c.getLocalName()+": "+property);
		// 结果集合
		ArrayList<String> res = new ArrayList<String>();
		ObjectProperty objp=model.getObjectProperty(npuri+property);
		if(objp!=null){
			System.out.println("ObjectProperty: "+objp.getLocalName());
			for(Iterator u=c.listPropertyValues(objp);u.hasNext();){
				String val=u.next().toString();
				val=val.substring(val.indexOf("#")+1);
				System.out.println("propertyvalue: "+val);
				res.add(val);
			}
		}

		return res;
	}

	/**
	 * 当前OntClass的父类是否在ems中
	 *
	 * @param c
	 * @return
	 */
	public String isSuperC(OntClass c) {
		// 遍历c的所有父类
		for (Iterator it = c.listSuperClasses(); it.hasNext();) {
			OntClass sp = (OntClass) it.next();
			// 父类是否在ems中
			if (ems.contains(sp.getLocalName()))
				return sp.getLocalName();
		}
		return "No";
	}

	// private void deel(DefaultMutableTreeNode dn, String name) {
	public ArrayList<String> deel(String name) {
		// LinkedList ll = this.ont.getSubClass(name);
		// for (int i = 0; i <= ll.size() - 1; i++) {
		// String n = ll.get(i).toString();
		// n = n.substring(n.indexOf("#") + 1);
		// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(n);
		// dn.add(tmp);
		// deel(tmp, ll.get(i).toString());
		// }
		ArrayList<String> re = new ArrayList<String>();
		if (name.equals("Thing")) {
			String n1 = "Device";//"Environment Entity";
			if(if_en){
				n1 = "EnvironmentEntity";
			}
			// DefaultMutableTreeNode tmp1 = new DefaultMutableTreeNode(n1);
			re.add(n1);
			// deel(tmp1, n1);

			String n2 = "Attribute";
			// DefaultMutableTreeNode tmp2 = new DefaultMutableTreeNode(n2);
			re.add(n2);
			// deel(tmp2, n2);

			String n7 = "Signal";
			// DefaultMutableTreeNode tmp7 = new DefaultMutableTreeNode(n7);
			re.add(n7);
			// deel(tmp7, n7);

			String n8 = "Instruction";
			re.add(n8);

			String n3 = "Value";
			// DefaultMutableTreeNode tmp3 = new DefaultMutableTreeNode(n3);
			re.add(n3);
			// deel(tmp3, n3);

			String n4 = "TimedAutomaton";//"State Machine";
			// DefaultMutableTreeNode tmp4 = new DefaultMutableTreeNode(n4);
			if(if_en){
				n4="SHA";
			}
			re.add(n4);
			// deel(tmp4, n4);

			String n5 = "State";
			// DefaultMutableTreeNode tmp5 = new DefaultMutableTreeNode(n5);
			re.add(n5);
			// deel(tmp5, n5);

			String n6 = "Transition";
			// DefaultMutableTreeNode tmp6 = new DefaultMutableTreeNode(n6);
			re.add(n6);
			// deel(tmp6, n6);

		} else if (name.equals("Device")) {
			if(if_en){
				re.add("DataProcessDevice");
				re.add("LaunchDevice");
				re.add("ManageControlDevice");
				re.add("ReceiveDevice");
			}else{
				re.add("Actuator");
				re.add("Sensor");
			}
//			for (int i = 0; i < ems.size(); i++) {
//				String n = ems.get(i);
//				// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(n);
//				re.add(n);
//				// deel(tmp,ems.get(i));
//			}
		} else if (name.equals("EnvironmentEntity")) {//Environment Entity
			System.out.println("name.equals(EnvironmentEntity)");
			re.add("Device");
			re.add("NaturalEntity");
			re.add("Operator");
			re.add("SoftwareSystem");
		} else if (ems.contains(name)) {
			for (int i = 0; i < mc.size(); i++) {
				MyOntClass tmp_mc = mc.get(i);
				if (tmp_mc.getType().equals(name)) {
					// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(tmp_mc.getName());
					re.add(tmp_mc.getName());
				}
			}
		} else if (name.equals("Operator")) {
			System.out.println("name.equals(Operator)");
			re.add("Analyzer");
			re.add("Commander");
			re.add("Maintainer");
//			for (int i = 0; i < mc.size(); i++) {
//				MyOntClass tmp_mc = mc.get(i);
//				if (tmp_mc.getType().equals(name)) {
//					// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(tmp_mc.getName());
//					re.add(tmp_mc.getName());
//				}
//			}
		}else if (name.equals("Attribute")) {
			System.out.println("name.equals(Attribute)");
			for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
				OntClass c = i.next();
				System.out.println(c.getLocalName());

				for (Iterator it = c.listSuperClasses(); it.hasNext();) {
					OntClass sp = (OntClass) it.next();

					if (sp.getLocalName() != null)
						if (sp.getLocalName().equals("Attribute")) {
							// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
							re.add(c.getLocalName());
						}

				}
			}
		} else if (name.equals("Value")) {
			System.out.println("name.equals(Value)");
			for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
				OntClass c = i.next();
				System.out.println(c.getLocalName());

				for (Iterator it = c.listSuperClasses(); it.hasNext();) {
					OntClass sp = (OntClass) it.next();

					if (sp.getLocalName() != null)
						if (sp.getLocalName().equals("Value")) {
							// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
							re.add(c.getLocalName());
						}

				}
			}
		} else if (name.equals("TimedAutomaton")) {//State Machine
			System.out.println("name.equals(TimedAutomaton)");
			for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
				OntClass c = i.next();
				System.out.println(c.getLocalName());

				for (Iterator it = c.listSuperClasses(); it.hasNext();) {
					OntClass sp = (OntClass) it.next();

					if (sp.getLocalName() != null)
						if (sp.getLocalName().equals("TimedAutomaton")) {
							// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
							re.add(c.getLocalName());
						}

				}
				// if(c.getLocalName()!=null)
				// if(c.getLocalName().equals("State Machine"))
				// {
				// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
				// re.add(c.getLocalName());
				// }
			}
		}  else if (name.equals("SHA")) {//State Machine
			System.out.println("name.equals(SHA)");
			for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
				OntClass c = i.next();
				System.out.println(c.getLocalName());

				for (Iterator it = c.listSuperClasses(); it.hasNext();) {
					OntClass sp = (OntClass) it.next();

					if (sp.getLocalName() != null)
						if (sp.getLocalName().equals("SHA")) {
							// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
							re.add(c.getLocalName());
						}

				}
			}
		} else if (name.equals("State")) {
			System.out.println("name.equals(State)");
			for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
				OntClass c = i.next();
				System.out.println(c.getLocalName());

				for (Iterator it = c.listSuperClasses(); it.hasNext();) {
					OntClass sp = (OntClass) it.next();

					if (sp.getLocalName() != null)
						if (sp.getLocalName().equals("State")) {
							// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
							re.add(c.getLocalName());
						}

				}
			}
		}
		else if (name.equals("Transition")) {
			System.out.println("name.equals(Transition)");
			for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
				OntClass c = i.next();
				System.out.println(c.getLocalName());

				for (Iterator it = c.listSuperClasses(); it.hasNext();) {
					OntClass sp = (OntClass) it.next();

					if (sp.getLocalName() != null)
						if (sp.getLocalName().equals("Transition")) {
							// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
							re.add(c.getLocalName());
						}

				}
			}
		} else if (name.equals("Signal")) {
			System.out.println("name.equals(Signal)");
			for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
				OntClass c = i.next();
				System.out.println(c.getLocalName());

				for (Iterator it = c.listSuperClasses(); it.hasNext();) {
					OntClass sp = (OntClass) it.next();

					if (sp.getLocalName() != null)
						if (sp.getLocalName().equals("Signal")) {
							// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
							re.add(c.getLocalName());
						}

				}
			}
		}else if (name.equals("Instruction")) {
			System.out.println("name.equals(Instruction)");
			for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
				OntClass c = i.next();
				System.out.println(c.getLocalName());

				for (Iterator it = c.listSuperClasses(); it.hasNext();) {
					OntClass sp = (OntClass) it.next();

					if (sp.getLocalName() != null)
						if (sp.getLocalName().equals("Instruction")) {
							// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
							re.add(c.getLocalName());
						}

				}
			}
		}
//		else if (name.equals("Event")) {
//
//			for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
//				OntClass c = i.next();
//				System.out.println(c.getLocalName());
//
//				for (Iterator it = c.listSuperClasses(); it.hasNext();) {
//					OntClass sp = (OntClass) it.next();
//
//					if (sp.getLocalName() != null)
//						if (sp.getLocalName().equals("Event")) {
//							// DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(c.getLocalName());
//							re.add(c.getLocalName());
//						}
//
//				}
//			}
//		}

		return re;
	}

	public String getFilepath() {
		return filepath;
	}
}
