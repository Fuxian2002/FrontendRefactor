package com.example.demo.bean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;

public class Ontology {
	String uri = "http://www.owl-ontologies.com/Ontology1206426899.owl#";

	private String fileName = null;

	OntModel schema = null;
	InfModel model = null;

	public Ontology(String address) {
		this.fileName = address;
		// System.out.println("this ontology address is : " + address);
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		this.schema = ModelFactory.createOntologyModel();
		try {

			this.schema.read(new FileInputStream(this.fileName), "");
			// this.schema.read(new FileInputStream(myfile), "");

		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException:" + this.fileName);
		}
		this.model = ModelFactory.createInfModel(reasoner, this.schema);

		// ArrayList<String> subofDomain = this.getSubClasses("Domain");
		// System.out.println(subofDomain.size());
		// OntClass c = this.getOntClass("Domain");
		// OntClass sbofc = c.getSuperClass();
		// System.out.println("name of sbofc is "+sbofc.getLocalName());
	}

	private ArrayList<String> getThingSub(StmtIterator si) {
		ArrayList<String> ll = new ArrayList<String>();
		ArrayList show = new ArrayList();
		while (si.hasNext()) {
			String s = si.next().toString();
			String name = s.substring(s.indexOf("[") + 1, s.indexOf(","));
			boolean zhaodao = false;
			for (int j = 0; j <= show.size() - 1; j++) {
				ShowLei sl = (ShowLei) show.get(j);
				if (name.equals(sl.name)) {
					sl.num += 1;
					zhaodao = true;
				}
			}
			if (!zhaodao) {
				ShowLei sll = new ShowLei();
				sll.name = s.substring(s.indexOf("[") + 1, s.indexOf(","));
				sll.num = 1;
				show.add(sll);
			}
		}
		for (int i = 0; i <= show.size() - 1; i++) {
			ShowLei sl = (ShowLei) show.get(i);
			if ((sl.num != 1) || (sl.name.indexOf(this.uri) == -1))
				continue;
			ll.add(sl.name);
		}

		return ll;
	}

	public ArrayList<String> getOntClasses() {
		ArrayList<String> re = new ArrayList<String>();
		for (Iterator<OntClass> i = schema.listClasses(); i.hasNext();) {
			OntClass c = i.next();
			// System.out.println(c.getLocalName());
			// ArrayList<String> sb = this.getSubClasses(c.getLocalName());
			re.add(c.getLocalName());
		}
		return re;

	}

	public ArrayList<String> getSubClasses(String name) {
		ArrayList<String> re = new ArrayList<String>();

		if (name.equals("Thing")) {
			for (Iterator<OntClass> i = schema.listClasses(); i.hasNext();) {
				OntClass c = i.next();
				// System.out.println(c.getLocalName());

				if (!c.hasSuperClass()) {
					// System.out.println(c.getLocalName() + " does not have super class");
					// System.out.println(c.getLocalName());
					re.add(c.getLocalName());
				} else {
					OntClass spc = c.getSuperClass();
					if (spc.getLocalName().equals(c.getLocalName())) {
						re.add(c.getLocalName());
					}
				}

			}
		}

		else {
			OntClass c = this.getOntClass(name);

			for (Iterator<OntClass> i = c.listSubClasses(); i.hasNext();) {
				// System.out.println("ok from listsubclasses");
				OntClass sb = i.next();

				// System.out.println(sb.getLocalName());
				re.add(sb.getLocalName());
			}
		}
		return re;
	}

	public OntClass getOntClass(String name) {

		for (Iterator<OntClass> i = schema.listClasses(); i.hasNext();) {
			OntClass c = i.next();
			if (c.getLocalName() != null)
				if (c.getLocalName().equals(name)) {
					return c;
				}
		}
		return null;
	}

	class ShowLei {
		String name;
		int num;

		ShowLei() {
		}
	}
}
