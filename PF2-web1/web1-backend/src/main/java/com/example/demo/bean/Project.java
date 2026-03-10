package com.example.demo.bean;

import com.example.demo.LSP.bean.DiagramContentChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class Project {
	private long time = 0;
	private String title;	//项目名
	private IntentDiagram intentDiagram;//任务意图
	private ContextDiagram contextDiagram;	//上下文图
	private ProblemDiagram problemDiagram;	//问题图
	private List<ScenarioGraph> scenarioGraphList; 	//子情景图
	private ScenarioGraph fullScenarioGraph; 	//完整的情景图
	private List<ScenarioGraph> newScenarioGraphList; 	//分解得到的情景图
	private List<SubProblemDiagram> subProblemDiagramList;	//子问题图
	private List<Trace> traceList;  // 追溯关系
	private List<Dependence> dataDependenceList;  // 数据依赖
	private List<Dependence> controlDependenceList;  // 控制依赖

	static public Project copyProject(Project pro) {
		Project project = new Project();
		project.setTitle(pro.getTitle());
		project.setIntentDiagram(
				IntentDiagram.copyIntentDiagram(pro.intentDiagram));
		project.setContextDiagram(
				ContextDiagram.copyContextDiagram(pro.contextDiagram));
		project.setProblemDiagram(
				ProblemDiagram.copyProblemDiagram(pro.problemDiagram));
		project.getProblemDiagram()
				.setContextDiagram(project.getContextDiagram());
		return project;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public IntentDiagram getIntentDiagram() {
		return intentDiagram;
	}
	public void setIntentDiagram(IntentDiagram intentDiagram) {
		this.intentDiagram = intentDiagram;
	}
	public ContextDiagram getContextDiagram() {
		return contextDiagram;
	}
	public void setContextDiagram(ContextDiagram contextDiagram) {
		this.contextDiagram = contextDiagram;
	}
	public ProblemDiagram getProblemDiagram() {
		return problemDiagram;
	}
	public void setProblemDiagram(ProblemDiagram problemDiagram) {
		this.problemDiagram = problemDiagram;
	}
	public List<ScenarioGraph> getScenarioGraphList() {
		return scenarioGraphList;
	}
	public void setScenarioGraphList(List<ScenarioGraph> scenarioGraphList) {
		this.scenarioGraphList = scenarioGraphList;
	}
	public List<SubProblemDiagram> getSubProblemDiagramList() {
		return subProblemDiagramList;
	}
	public void setSubProblemDiagramList(List<SubProblemDiagram> subProblemDiagramList) {
		this.subProblemDiagramList = subProblemDiagramList;
	}
	public ScenarioGraph getFullScenarioGraph() {
		return fullScenarioGraph;
	}
	public void setFullScenarioGraph(ScenarioGraph fullScenarioGraph) {
		this.fullScenarioGraph = fullScenarioGraph;
	}
	public List<ScenarioGraph> getNewScenarioGraphList() {
		return newScenarioGraphList;
	}
	public void setNewScenarioGraphList(List<ScenarioGraph> newScenarioGraphList) {
		this.newScenarioGraphList = newScenarioGraphList;
	}
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public List<Trace> getTraceList() {
		return traceList;
	}

	public void setTraceList(List<Trace> traceList) {
		this.traceList = traceList;
	}

	public List<Dependence> getDataDependenceList() {
		return dataDependenceList;
	}

	public void setDataDependenceList(List<Dependence> dataDependenceList) {
		this.dataDependenceList = dataDependenceList;
	}

	public List<Dependence> getControlDependenceList() {
		return controlDependenceList;
	}

	public void setControlDependenceList(List<Dependence> controlDependenceList) {
		this.controlDependenceList = controlDependenceList;
	}

	// 将project中#修改为_
	public static Project getCorrectProject(Project project) {
		System.out.println("将project中#修改为_");
		String title = project.getTitle() == null ? ""
				: project.getTitle().replaceAll("#", "_");
		project.setTitle(title);
		if (project.getIntentDiagram().getSystem() != null) {
			String shortName = project.getIntentDiagram().getSystem()
					.getShortname();
			if (shortName != null)
				shortName = shortName.replaceAll("#", "_");
			project.getIntentDiagram().getSystem().setShortname(shortName);
		}
		for (ExternalEntity pd : project.getIntentDiagram()
				.getExternalEntityList()) {
			String shortname = pd.getShortname().replaceAll("#", "_");
			pd.setShortname(shortname);
		}
		for (Intent req : project.getIntentDiagram()
				.getIntentList()) {
			String shortname = req.getShortname().replaceAll("#", "_");
			req.setShortname(shortname);
		}
		for (Interface interfacee : project.getIntentDiagram()
				.getInterfaceList()) {
			for (Phenomenon phenomenon : interfacee.getPhenomenonList()) {
				String name = phenomenon.getPhenomenon_name().replaceAll("#", "_");
				phenomenon.setPhenomenon_name(name);
			}
		}
		for (Constraint constraint : project.getIntentDiagram()
				.getConstraintList()) {
			for (Phenomenon phenomenon : constraint.getPhenomenonList()) {
				String name = phenomenon.getPhenomenon_name().replaceAll("#", "_");
				phenomenon.setPhenomenon_name(name);
			}
		}
		for (Reference reference : project.getIntentDiagram()
				.getReferenceList()) {
			for (Phenomenon phenomenon : reference.getPhenomenonList()) {
				String name = phenomenon.getPhenomenon_name().replaceAll("#", "_");
				phenomenon.setPhenomenon_name(name);
			}
		}
		if (project.getContextDiagram().getMachine() != null) {
			String shortName = project.getContextDiagram().getMachine()
					.getShortname();
			if (shortName != null)
				shortName = shortName.replaceAll("#", "_");
			project.getContextDiagram().getMachine().setShortname(shortName);
		}

		for (ProblemDomain pd : project.getContextDiagram()
				.getProblemDomainList()) {
			String shortname = pd.getShortname().replaceAll("#", "_");
			pd.setShortname(shortname);
		}
		for (Requirement req : project.getProblemDiagram()
				.getRequirementList()) {
//			System.out.println("requirement_shortname: "+req.getShortname());
			String shortname = req.getShortname().replaceAll("#", "_");
//			System.out.println("shortname: "+shortname);
			req.setShortname(shortname);
		}
		for (Interface interfacee : project.getContextDiagram()
				.getInterfaceList()) {
			for (Phenomenon phenomenon : interfacee.getPhenomenonList()) {
				String name = phenomenon.getPhenomenon_name().replaceAll("#", "_");
				phenomenon.setPhenomenon_name(name);
			}
		}
		for (Constraint constraint : project.getProblemDiagram()
				.getConstraintList()) {
			for (Phenomenon phenomenon : constraint.getPhenomenonList()) {
				String name = phenomenon.getPhenomenon_name().replaceAll("#", "_");
				phenomenon.setPhenomenon_name(name);
			}
		}
		for (Reference reference : project.getProblemDiagram()
				.getReferenceList()) {
			for (Phenomenon phenomenon : reference.getPhenomenonList()) {
				String name = phenomenon.getPhenomenon_name().replaceAll("#", "_");
				phenomenon.setPhenomenon_name(name);
			}
		}

		return project;
	}
	private Machine getMachine() {
		if(contextDiagram == null){
			return null;
		}
		return getContextDiagram().getMachine();
	}
	private void setMachine(Machine machine) {
		this.contextDiagram.setMachine(machine);
		this.problemDiagram.getContextDiagram().setMachine(machine);
	}
	private void changeMachine(Machine machine) {
		this.contextDiagram.getMachine().setName(machine.getName());
		this.contextDiagram.getMachine().setShortname(machine.getShortname());
		this.problemDiagram.getContextDiagram().getMachine().setName(machine.getName());
		this.problemDiagram.getContextDiagram().getMachine().setShortname(machine.getShortname());
	}
	public ArrayList<DiagramContentChangeEvent> changeMachineWithNewProject(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> contentChanges) {
		Machine newMachine = newProject.getMachine();
		Machine old = new Machine(getMachine());
		if ((newMachine == null || newMachine.getName() == null
				|| newMachine.getShortname() == null)
				&& getMachine() != null && getMachine().getName() != null
				&& getMachine().getShortname() != null) {
			setMachine(null);
			contentChanges.add(
					new DiagramContentChangeEvent("mac", "delete", old, null));
		}
		if ((getMachine() == null || getMachine().getName() == null
				|| getMachine().getShortname() == null)
				&& newMachine != null) {
			this.setMachine(newProject.getMachine());
			contentChanges.add(new DiagramContentChangeEvent("mac", "add", null,
					newMachine));
		} else {
			if ((newMachine != null && getMachine() != null)
					&& (!getMachine().getName().contentEquals(newMachine.getName())
						|| !getMachine().getShortname().contentEquals(newMachine.getShortname()))) {
				changeMachine(newProject.getMachine());
				contentChanges.add(new DiagramContentChangeEvent("mac",
						"change", old, getMachine()));
			}
		}
		return contentChanges;
	}
	public ArrayList<DiagramContentChangeEvent> changeProblemDomainWithNewProject(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> ContentChanges) {
		ArrayList<ProblemDomain> oldList = (ArrayList<ProblemDomain>) getProblemDomainList();
		ArrayList<ProblemDomain> newList = (ArrayList<ProblemDomain>) newProject
				.getProblemDomainList();
		// change & add
		for (ProblemDomain newItem : newList) {
			boolean isFind = false;
			for (ProblemDomain item : oldList) {
				ProblemDomain old = new ProblemDomain(item);
				boolean isChange = false;
				if (newItem.getName().contentEquals(item.getName())) {
					if (!newItem.getShortname().contentEquals(item.getShortname())) {
						item.setShortname(newItem.getShortname());
						isChange = true;
					}
					if (newItem.getType() != null && !newItem.getType().contentEquals(item.getType())) {
						item.setType(newItem.getType());
						isChange = true;
					}
					item.setProperty(newItem.getProperty());
					isFind = true;
					if (isChange)
						ContentChanges.add(new DiagramContentChangeEvent("pro",
								"change", old, item));
					break;
				} else if (newItem.getShortname().contentEquals(item.getShortname())) {
					if (!newItem.getName().contentEquals(item.getName())) {
						item.setName(newItem.getName());
						isChange = true;
						// console.log("change Name")
					}
					if (newItem.getType() != null && !newItem.getType()
							.contentEquals(item.getType())) {
						item.setType(newItem.getType());
						isChange = true;
						// console.log("change type = ",newItem.getType())
					}
					item.setProperty(newItem.getProperty());
					isFind = true;
					if (isChange)
						ContentChanges.add(new DiagramContentChangeEvent("pro",
								"change", old, item));
					break;
				}
			}
			if (!isFind) {
				oldList.add(newItem);
				ContentChanges.add(new DiagramContentChangeEvent("pro", "add",
						null, newItem));
				// console.log("add ",newItem)
			}
		}
		// delete
		int len = oldList.size();
		for (int i = len - 1; i >= 0; i--) {
			ProblemDomain item = oldList.get(i);
			boolean isFind = false;
			for (ProblemDomain newItem : newList) {
				if (newItem.getName().contentEquals(item.getName())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				oldList.remove(i);
				// console.log("delete ",item)
				ContentChanges.add(new DiagramContentChangeEvent("pro",
						"delete", item, null));
			}
		}
		setProblemDomainList();
		return ContentChanges;
	}

	private List<ProblemDomain> getProblemDomainList() {
		if(contextDiagram == null){
			return null;
		}
		return contextDiagram.getProblemDomainList();
	}

	private boolean setProblemDomainList() {
		problemDiagram.getContextDiagram()
				.setProblemDomainList(getProblemDomainList());
		return true;
	}

	public ArrayList<DiagramContentChangeEvent> changeRequirementWithNewProject(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> ContentChanges) {
		ArrayList<Requirement> oldList = (ArrayList<Requirement>) getRequirementList();
		ArrayList<Requirement> newList = (ArrayList<Requirement>) newProject
				.getRequirementList();
		// change & add
		for (Requirement nItem : newList) {
			boolean isFind = false;
			Requirement newItem = new Requirement(nItem);
			for (Requirement item : oldList) {
				Requirement old = new Requirement(item);
				boolean isChanged = false;
				if (newItem.getShortname().contentEquals(item.getShortname())) {
					if (!newItem.getName().contentEquals(item.getName())) {
						isChanged = true;
						item.setName(newItem.getName());
						// console.log("change Name")
					}
					isFind = true;
					if (isChanged)
						ContentChanges.add(new DiagramContentChangeEvent("req",
								"change", old, item));
					break;
				} else if (newItem.getName().contentEquals(item.getName())) {
					if (!newItem.getShortname()
							.contentEquals(item.getShortname())) {
						isChanged = true;
						item.setShortname(newItem.getShortname());
					}
					isFind = true;
					if (isChanged)
						ContentChanges.add(new DiagramContentChangeEvent("req",
								"change", old, item));
					break;
				}

			}
			if (!isFind) {
				oldList.add(newItem);
				ContentChanges.add(new DiagramContentChangeEvent("req", "add",
						null, newItem));
				// console.log("add ",newItem)
			}
		}
		// delete
		int len = oldList.size();
		for (int i = len - 1; i >= 0; i--) {
			Requirement item = oldList.get(i);
			boolean isFind = false;
			for (Requirement newItem : newList) {
				if (newItem.getName().contentEquals(item.getName())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				oldList.remove(i);
				// console.log("delete ",item)
				ContentChanges.add(new DiagramContentChangeEvent("req",
						"delete", item, null));
			}
		}
		return ContentChanges;
	}

	private List<Requirement> getRequirementList() {
		if(problemDiagram == null){
			return null;
		}
		return problemDiagram.getRequirementList();
	}
	//context:interface
	public ArrayList<DiagramContentChangeEvent> changeInterfaceWithNewProject(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> contentChanges) {
		System.out.println("changeInterfaceWithNewProject");
		// change & add
		List<Interface> newList = newProject.getInterfaceList();
		List<Interface> oldList = this.getInterfaceList();
		for (Interface newItem : newList) {
			boolean isFind = false;
			for (Interface item : oldList) {
				Interface old = new Interface(item);
				boolean isChangeName = false;
				if (newItem.getInterface_from().contentEquals(item.getInterface_from())
						&& newItem.getInterface_to().contentEquals(item.getInterface_to())) {
					if (newItem.getName() != null
							&& (item.getName() == null
								|| item.getName() != null
								&& !newItem.getName().contentEquals(item.getName()))) {
						item.setName(newItem.getName());
						item.setInterface_description(newItem.getInterface_description());
						isChangeName = true;
					}
					// deal with phe
					boolean isChangePhe = this.changePhenomenon(
							item.getPhenomenonList(), newItem.getPhenomenonList());
					if (isChangeName || isChangePhe ) {
						contentChanges.add(new DiagramContentChangeEvent("int",
								"change", old, item));
					}
					isFind = true;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("int", "add",
						null, newItem));
				oldList.add(newItem);
			}
		}
		for (int i = oldList.size() - 1; i >= 0; i--) {
			Interface item = oldList.get(i);
			boolean isFind = false;
			for (Interface newItem : newList) {
				if (newItem.getInterface_from().contentEquals(item.getInterface_from())
						&& newItem.getInterface_to().contentEquals(item.getInterface_to())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("int",
						"delete", item, null));
				oldList.remove(i);
			}
		}
		return null;
	}
	//intentdiagram:interface
	public ArrayList<DiagramContentChangeEvent> changeInterfaceWithNewProject2(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> contentChanges) {
		System.out.println("changeInterfaceWithNewProject2");
		// change & add
		List<Interface> newList = newProject.getInterfaceList2();
		List<Interface> oldList = this.getInterfaceList2();
		for (Interface newItem : newList) {
			boolean isFind = false;
			for (Interface item : oldList) {
				Interface old = new Interface(item);
				boolean isChangeName = false;
				if (newItem.getInterface_from().contentEquals(item.getInterface_from())
						&& newItem.getInterface_to().contentEquals(item.getInterface_to())) {
					if (newItem.getName() != null
							&& (item.getName() == null || item.getName() != null && !newItem.getName()
							.contentEquals(item.getName()))) {
						item.setName(newItem.getName());
						item.setInterface_description(newItem.getInterface_description());
						isChangeName = true;
					}
					// deal with phe
					boolean isChangePhe = this.changePhenomenon(
							item.getPhenomenonList(),
							newItem.getPhenomenonList());
					if (isChangeName || isChangePhe) {
						contentChanges.add(new DiagramContentChangeEvent("int",
								"change", old, item));
					}
					isFind = true;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("int", "add",
						null, newItem));
				oldList.add(newItem);
			}
		}
		for (int i = oldList.size() - 1; i >= 0; i--) {
			Interface item = oldList.get(i);
			boolean isFind = false;
			for (Interface newItem : newList) {
				if (newItem.getInterface_from().contentEquals(item.getInterface_from())
						&& newItem.getInterface_to().contentEquals(item.getInterface_to())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("int",
						"delete", item, null));
				oldList.remove(i);
			}
		}
		return null;
	}

	private List<Interface> getInterfaceList() {
		if(contextDiagram == null){
			return null;
		}
		return contextDiagram.getInterfaceList();
	}
	private List<Interface> getInterfaceList2() {
		if(intentDiagram == null){
			return null;
		}
		return intentDiagram.getInterfaceList();
	}
	public boolean changePhenomenon(List<Phenomenon> oldList,
									List<Phenomenon> newList) {
		boolean isChange = false;
		// change & add
		for (Phenomenon newItem : newList) {
			boolean isFind = false;
			for (Phenomenon item : oldList) {
				if (newItem.getPhenomenon_name().contentEquals(item.getPhenomenon_name())) {
					if (!item.getPhenomenon_type().contentEquals(newItem.getPhenomenon_type())) {
						isChange = true;
						item.setPhenomenon_type(newItem.getPhenomenon_type());
					}
					isFind = true;
				}
			}
			if (!isFind) {
				isChange = true;
				oldList.add(newItem);
			}
		}
		// delete
		int len = oldList.size();
		for (int i = len - 1; i >= 0; i--) {
			Phenomenon item = oldList.get(i);
			boolean isFind = false;
			for (Phenomenon newItem : newList) {
				if (newItem.getPhenomenon_name().contentEquals(item.getPhenomenon_name())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				isChange = true;
				oldList.remove(i);
			}
		}
		return isChange;
	}

	private boolean changeRequirementPhenomenon(
			List<RequirementPhenomenon> oldList,
			List<RequirementPhenomenon> newList) {
		boolean isChange = false;
		// change & add
		for (RequirementPhenomenon newItem : newList) {
			boolean isFind = false;
			for (RequirementPhenomenon item : oldList) {
				if (newItem.getPhenomenon_name().contentEquals(item.getPhenomenon_name())) {
					if (!item.getPhenomenon_type().contentEquals(newItem.getPhenomenon_type())) {
						isChange = true;
						item.setPhenomenon_type(newItem.getPhenomenon_type());
					}
					isFind = true;
				}
			}
			if (!isFind) {
				isChange = true;
				oldList.add(newItem);
			}
		}
		// delete
		int len = oldList.size();
		for (int i = len - 1; i >= 0; i--) {
			RequirementPhenomenon item = oldList.get(i);
			boolean isFind = false;
			for (RequirementPhenomenon newItem : newList) {
				if (newItem.getPhenomenon_name().contentEquals(item.getPhenomenon_name())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				isChange = true;
				oldList.remove(i);
			}
		}
		return isChange;
	}

	private List<Constraint> getConstraintList() {
		if(problemDiagram == null){
			return null;
		}
		return problemDiagram.getConstraintList();
	}

	private List<Reference> getReferenceList() {
		if(problemDiagram == null){
			return null;
		}
		return problemDiagram.getReferenceList();
	}
	private List<Constraint> getConstraintList2() {
		if(intentDiagram == null){
			return null;
		}
		return intentDiagram.getConstraintList();
	}

	private List<Reference> getReferenceList2() {
		if(intentDiagram == null){
			return null;
		}
		return intentDiagram.getReferenceList();
	}
	public ArrayList<DiagramContentChangeEvent> changeReferenceWithNewProject(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> contentChanges) {
		System.out.println("changeReferenceWithNewProject");
		List<Reference> newList = newProject.getReferenceList();
		List<Reference> oldList = this.getReferenceList();
		for (Reference newItem : newList) {
			boolean isFind = false;
			for (Reference item : oldList) {
				Reference old = new Reference(item);
				boolean isChangeName = false;
				if (newItem.getReference_from().contentEquals(item.getReference_from())
						&& newItem.getReference_to().contentEquals(item.getReference_to())) {
					if (newItem.getName() != null && (item.getName() == null
							|| item.getName() != null && !newItem.getName()
							.contentEquals(item.getName()))) {
						item.setName(newItem.getName());
						item.setReference_description(newItem.getReference_description());
						isChangeName = true;
					}
					// deal with phe
					boolean isChangePhe = this.changeRequirementPhenomenon(
							item.getPhenomenonList(),
							newItem.getPhenomenonList());
					if (isChangeName || isChangePhe) {
						contentChanges.add(new DiagramContentChangeEvent("ref",
								"change", new Reference(old),
								new Reference(item)));
					}
					isFind = true;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("ref", "add",
						null, new Reference(newItem)));
				oldList.add(newItem);
			}
		}
		for (int i = oldList.size() - 1; i >= 0; i--) {
			Reference item = oldList.get(i);
			boolean isFind = false;
			for (Reference newItem : newList) {
				if (newItem.getReference_from().contentEquals(item.getReference_from())
						&& newItem.getReference_to().contentEquals(item.getReference_to())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("ref",
						"delete", new Reference(oldList.get(i)), null));
				oldList.remove(i);
			}
		}
		return contentChanges;
	}
	public ArrayList<DiagramContentChangeEvent> changeConstraintWithNewProject(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> contentChanges) {
		List<Constraint> newList = newProject.getConstraintList();
		List<Constraint> oldList = this.getConstraintList();
		for (Constraint newItem : newList) {
			boolean isFind = false;
			for (Constraint item : oldList) {
				Constraint old = new Constraint(item);
				boolean isChangeName = false;
				if (newItem.getConstraint_from().contentEquals(item.getConstraint_from())
						&& newItem.getConstraint_to().contentEquals(item.getConstraint_to())) {
					if (newItem.getName() != null && (item.getName() == null
							|| item.getName() != null && !newItem.getName()
							.contentEquals(item.getName()))) {
						item.setName(newItem.getName());
						item.setConstraint_description(newItem.getConstraint_description());
						isChangeName = true;
					}
					// deal with phe
					boolean isChangePhe = this.changeRequirementPhenomenon(
							item.getPhenomenonList(),
							newItem.getPhenomenonList());
					if (isChangeName || isChangePhe) {
						contentChanges.add(new DiagramContentChangeEvent("con",
								"change", old, new Constraint(item)));
					}
					isFind = true;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("con", "add",
						null, new Constraint(newItem)));
				oldList.add(newItem);
			}
		}
		for (int i = oldList.size() - 1; i >= 0; i--) {
			Constraint item = oldList.get(i);
			boolean isFind = false;
			for (Constraint newItem : newList) {
				if (newItem.getConstraint_from().contentEquals(item.getConstraint_from())
						&& newItem.getConstraint_to().contentEquals(item.getConstraint_to())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				contentChanges
						.add(new DiagramContentChangeEvent("con", "delete",
								new Constraint(oldList.get(i)), null));
				oldList.remove(i);
			}
		}
		return contentChanges;
	}
	public ArrayList<DiagramContentChangeEvent> changeReferenceWithNewProject2(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> contentChanges) {
		System.out.println("changeReferenceWithNewProject2");
		List<Reference> newList = newProject.getReferenceList2();
		List<Reference> oldList = this.getReferenceList2();
		for (Reference newItem : newList) {
			boolean isFind = false;
			for (Reference item : oldList) {
				Reference old = new Reference(item);
				boolean isChangeName = false;
				if (newItem.getReference_from().contentEquals(item.getReference_from())
						&& newItem.getReference_to().contentEquals(item.getReference_to())) {
					if (newItem.getName() != null && (item.getName() == null
							|| item.getName() != null && !newItem.getName()
							.contentEquals(item.getName()))) {
						item.setName(newItem.getName());
						item.setReference_description(newItem.getReference_description());
						isChangeName = true;
					}
					// deal with intentphe
					boolean isChangePhe = this.changeRequirementPhenomenon(
							item.getPhenomenonList(),
							newItem.getPhenomenonList());
					if (isChangeName || isChangePhe) {
						contentChanges.add(new DiagramContentChangeEvent("ref",
								"change", new Reference(old),
								new Reference(item)));
					}
					isFind = true;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("ref", "add",
						null, new Reference(newItem)));
				oldList.add(newItem);
			}
		}
		for (int i = oldList.size() - 1; i >= 0; i--) {
			Reference item = oldList.get(i);
			boolean isFind = false;
			for (Reference newItem : newList) {
				if (newItem.getReference_from().contentEquals(item.getReference_from())
						&& newItem.getReference_to().contentEquals(item.getReference_to())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("ref",
						"delete", new Reference(oldList.get(i)), null));
				oldList.remove(i);
			}
		}
		return contentChanges;
	}
	public ArrayList<DiagramContentChangeEvent> changeConstraintWithNewProject2(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> contentChanges) {
		List<Constraint> newList = newProject.getConstraintList2();
		List<Constraint> oldList = this.getConstraintList2();
		for (Constraint newItem : newList) {
			boolean isFind = false;
			for (Constraint item : oldList) {
				Constraint old = new Constraint(item);
				boolean isChangeName = false;
				if (newItem.getConstraint_from().contentEquals(item.getConstraint_from())
						&& newItem.getConstraint_to().contentEquals(item.getConstraint_to())) {
					if (newItem.getName() != null && (item.getName() == null
							|| item.getName() != null && !newItem.getName()
							.contentEquals(item.getName()))) {
						item.setName(newItem.getName());
						item.setConstraint_description(newItem.getConstraint_description());
						isChangeName = true;
					}
					// deal with intentphe
					boolean isChangePhe = this.changeRequirementPhenomenon(
							item.getPhenomenonList(),
							newItem.getPhenomenonList());
					if (isChangeName || isChangePhe) {
						contentChanges.add(new DiagramContentChangeEvent("con",
								"change", old, new Constraint(item)));
					}
					isFind = true;
				}
			}
			if (!isFind) {
				contentChanges.add(new DiagramContentChangeEvent("con", "add",
						null, new Constraint(newItem)));
				oldList.add(newItem);
			}
		}
		for (int i = oldList.size() - 1; i >= 0; i--) {
			Constraint item = oldList.get(i);
			boolean isFind = false;
			for (Constraint newItem : newList) {
				if (newItem.getConstraint_from().contentEquals(item.getConstraint_from())
						&& newItem.getConstraint_to().contentEquals(item.getConstraint_to())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				contentChanges
						.add(new DiagramContentChangeEvent("con", "delete",
								new Constraint(oldList.get(i)), null));
				oldList.remove(i);
			}
		}
		return contentChanges;
	}

	private ESystem getSystem() {
		if(intentDiagram == null){
			return null;
		}
		return getIntentDiagram().getSystem();
	}
	private void setSystem(ESystem system) {
		this.intentDiagram.setSystem(system);
	}
	private void changeSystem(ESystem system) {
		this.intentDiagram.getSystem().setName(system.getName());
		this.intentDiagram.getSystem().setShortname(system.getShortname());
	}
	public ArrayList<DiagramContentChangeEvent> changeSystemWithNewProject(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> contentChanges) {
		ESystem newSystem = newProject.getSystem();
		ESystem old = new ESystem(getSystem());
		if ((newSystem == null || newSystem.getName() == null
				|| newSystem.getShortname() == null)
				&& getSystem() != null && getSystem().getName() != null
				&& getSystem().getShortname() != null) {
			setSystem(null);
			contentChanges.add(
					new DiagramContentChangeEvent("sys", "delete", old, null));
		}
		if ((getSystem() == null || getSystem().getName() == null
				|| getSystem().getShortname() == null)
				&& newSystem != null) {
			this.setSystem(newProject.getSystem());
			contentChanges.add(new DiagramContentChangeEvent("sys", "add", null,
					newSystem));
		} else {
			if ((newSystem != null && getSystem() != null)
					&& (!getSystem().getName()
					.contentEquals(newSystem.getName())
					|| !getSystem().getShortname().contentEquals(
					newSystem.getShortname()))) {
				changeSystem(newProject.getSystem());
				contentChanges.add(new DiagramContentChangeEvent("sys",
						"change", old, getSystem()));
			}
		}
		return contentChanges;
	}

	public ArrayList<DiagramContentChangeEvent> changeExternalEntityWithNewProject(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> ContentChanges) {
		ArrayList<ExternalEntity> oldList = (ArrayList<ExternalEntity>) getExternalEntityList();
		ArrayList<ExternalEntity> newList = (ArrayList<ExternalEntity>) newProject
				.getExternalEntityList();
		// change & add
		for (ExternalEntity newItem : newList) {
			boolean isFind = false;
			for (ExternalEntity item : oldList) {
				ExternalEntity old = new ExternalEntity(item);
				boolean isChange = false;
				if (newItem.getName().contentEquals(item.getName())) {
					if (!newItem.getShortname()
							.contentEquals(item.getShortname())) {
						item.setShortname(newItem.getShortname());
						isChange = true;
					}
					isFind = true;
					if (isChange)
						ContentChanges.add(new DiagramContentChangeEvent("ext",
								"change", old, item));
					break;
				} else if (newItem.getShortname()
						.contentEquals(item.getShortname())) {
					if (!newItem.getName().contentEquals(item.getName())) {
						item.setName(newItem.getName());
						isChange = true;
						// console.log("change Name")
					}
					isFind = true;
					if (isChange)
						ContentChanges.add(new DiagramContentChangeEvent("ext",
								"change", old, item));
					break;
				}
			}
			if (!isFind) {
				oldList.add(newItem);
				ContentChanges.add(new DiagramContentChangeEvent("ext", "add",
						null, newItem));
				// console.log("add ",newItem)
			}
		}
		// delete
		int len = oldList.size();
		for (int i = len - 1; i >= 0; i--) {
			ExternalEntity item = oldList.get(i);
			boolean isFind = false;
			for (ExternalEntity newItem : newList) {
				if (newItem.getName().contentEquals(item.getName())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				oldList.remove(i);
				// console.log("delete ",item)
				ContentChanges.add(new DiagramContentChangeEvent("ext",
						"delete", item, null));
			}
		}
		setExternalEntityList();
		return ContentChanges;
	}

	private List<ExternalEntity> getExternalEntityList() {
		if(intentDiagram == null){
			return null;
		}
		return intentDiagram.getExternalEntityList();
	}

	private boolean setExternalEntityList() {
		intentDiagram.setExternalEntityList(getExternalEntityList());
		return true;
	}

	public ArrayList<DiagramContentChangeEvent> changeIntentWithNewProject(
			Project newProject,
			ArrayList<DiagramContentChangeEvent> ContentChanges) {
		ArrayList<Intent> oldList = (ArrayList<Intent>) getIntentList();
		ArrayList<Intent> newList = (ArrayList<Intent>) newProject.getIntentList();
		// change & add
		for (Intent nItem : newList) {
			boolean isFind = false;
			Intent newItem = new Intent(nItem);
			for (Intent item : oldList) {
				Intent old = new Intent(item);
				boolean isChanged = false;
				if (newItem.getShortname().contentEquals(item.getShortname())) {
					if (!newItem.getName().contentEquals(item.getName())) {
						isChanged = true;
						item.setName(newItem.getName());
						// console.log("change Name")
					}
					isFind = true;
					if (isChanged)
						ContentChanges.add(new DiagramContentChangeEvent("tas",
								"change", old, item));
					break;
				} else if (newItem.getName().contentEquals(item.getName())) {
					if (!newItem.getShortname()
							.contentEquals(item.getShortname())) {
						isChanged = true;
						item.setShortname(newItem.getShortname());
					}
					isFind = true;
					if (isChanged)
						ContentChanges.add(new DiagramContentChangeEvent("tas",
								"change", old, item));
					break;
				}

			}
			if (!isFind) {
				oldList.add(newItem);
				ContentChanges.add(new DiagramContentChangeEvent("tas", "add",
						null, newItem));
				// console.log("add ",newItem)
			}
		}
		// delete
		int len = oldList.size();
		for (int i = len - 1; i >= 0; i--) {
			Intent item = oldList.get(i);
			boolean isFind = false;
			for (Intent newItem : newList) {
				if (newItem.getName().contentEquals(item.getName())) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				oldList.remove(i);
				// console.log("delete ",item)
				ContentChanges.add(new DiagramContentChangeEvent("tas",
						"delete", item, null));
			}
		}
		return ContentChanges;
	}

	private List<Intent> getIntentList() {
		if(intentDiagram == null){
			return null;
		}
		return intentDiagram.getIntentList();
	}
}
