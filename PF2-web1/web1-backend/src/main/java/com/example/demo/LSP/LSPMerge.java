package com.example.demo.LSP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// import com.alexander.solovyov.TreeDifferences;
import com.example.demo.service.ASTService;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.EMFTreeContext;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TimeAction;
import com.github.gumtreediff.tree.TimeTree;

//import lombok.extern.log4j.Log4j2;
//@Log4j2(topic = "log")
public class LSPMerge {
	static int num = 0;

	private final static Logger log = LogManager.getLogger("log");
	
	/**  
	 * @Description: 根据editor修改newest,并修改时间戳（为什么不按照论文的方法？根据匹配结果修改时间戳？？？）
	 * @param message
	 * @param newest
	 * @param editor
	 * @return
	 */ 
	public static EMFTreeContext two_way_merge(String message,
			EMFTreeContext newest, EMFTreeContext editor) {
		String version = "_" + newest.getVersion().substring(36, 40) + "_"
				+ editor.getVersion().substring(36, 40);

		int number = num++;
		// System.out.println("two_way_merge==========");
		// ASTService.resetId(newest.getRoot(), 0);
		Matcher matcher_newest_editor = Matchers.getInstance()
				.getMatcher(newest.getRoot(), editor.getRoot());
		matcher_newest_editor.match();
		MappingStore mappings_newest_editor = matcher_newest_editor
				.getMappings();
		ActionGenerator actionGenerator = new ActionGenerator(newest.getRoot(),
				editor.getRoot(),
				mappings_newest_editor);
		actionGenerator.generate();
		List<Action> actions_newest_editor = actionGenerator.getActions();

		diff1(number + "		" + message + "_newest_remote" + version,
				newest.deriveTree(), editor.deriveTree());
		for (ITree tree : newest.getRoot().getTrees()) {
			ITree dst = mappings_newest_editor.getDst(tree);
			if (((TimeTree) tree).isDelete() && dst != null
					&& !((TimeTree) dst).isDelete()) {
				((TimeTree) tree).setDelete(false);
				long time = ((TimeTree) dst).getTime();
				((TimeTree) tree).setTime(time);
			} else if (!((TimeTree) tree).isDelete() && dst != null
					&& ((TimeTree) dst).isDelete()) {
				((TimeTree) tree).setDelete(true);
				long time = ((TimeTree) dst).getTime();
				((TimeTree) tree).setTime(time);
			}
		}
		for (Action action : actions_newest_editor) {
			// if (action.getNode().getParent() == null) {// root
			// continue;
			// }
			if (action instanceof Delete) {
				if (((TimeTree) action.getNode()).isDelete())
					continue;
				((TimeTree) action.getNode()).setDelete(true);
				long time = ((TimeTree) editor.getRoot()).getTime();
				((TimeTree) action.getNode()).setTime(time);
			} else if (action instanceof Insert) {
				TimeTree node = (TimeTree) action.getNode().deepCopy();
				// int id = ASTService.resetId(node, newest.getId());
				// newest.setId(id);
				TimeTree parent = (TimeTree) ((Insert) action).getParent();
				int pos = ((Insert) action).getPosition();
				boolean isFind = false;
				for (Action act : actions_newest_editor) {
					if (act.getNode() == parent && act instanceof Insert) {
						isFind = true;
					}
				}
				if (!isFind) {
					if (parent.getChildren().size() > pos) {
						parent.insertChild(node, pos);
					} else {
						parent.addChild(node);
						// diff("insert error pos=" + pos, newest, editor);
					}
				}
			} else if (action instanceof Update) {
				TimeTree node = (TimeTree) action.getNode();
				String value = ((Update) action).getValue();
				node.setLabel(value);
				node.setDelete(false);
				long time = ((TimeTree) editor.getRoot()).getTime();
				node.setTime(time);
			} else if (action instanceof Move) {
				TimeTree node = (TimeTree) action.getNode();
				TimeTree parent = (TimeTree) ((Move) action).getParent();
				int pos = ((Move) action).getPosition();
				node.getParent().getChildren().remove(node);
				if (parent.getChildren().size() > pos) {
					parent.insertChild(node, pos);
				} else {
					parent.addChild(node);
					// diff("move error pos=" + pos, newest.deriveTree(),
					// editor.deriveTree());
				}
				((TimeTree) action.getNode()).setDelete(false);
				long time = ((TimeTree) editor.getRoot()).getTime();
				node.setTime(time);
			}
		}
		ASTService.resetId(newest.getRoot(), 0);
		newest.setT0(editor.getT0());
		newest.setT1(editor.getT1());
		newest.setSt0(editor.getSt0());
		newest.setSt1(editor.getSt1());
		newest.setSt2(editor.getSt2());
		newest.setName(editor.getName());
		newest.setEmail(editor.getEmail());
		// get version

		return newest.deriveTree();
	}

	public static EMFTreeContext three_way_merge(String message,
			EMFTreeContext last, EMFTreeContext newest,
			EMFTreeContext remote) {
		if (newest.getVersion().contentEquals(remote.getVersion())) {
			System.out.print(
					"newest.getVersion().contentEquals(remote.getVersion())= "
							+ newest.getVersion());
			return null;
		}
		String version = "_" + last.getVersion().substring(36, 40) + "_"
				+ newest.getVersion().substring(36, 40) + "_"
				+ remote.getVersion().substring(36, 40);
		int number = num++;
		// diff last newest
		Matcher matcher_last_newest = Matchers.getInstance()
				.getMatcher(last.getRoot(), newest.getRoot());
		matcher_last_newest.match();
		MappingStore mappings_last_newest = matcher_last_newest.getMappings();
		ActionGenerator actionGenerator_last_newest = new ActionGenerator(
				last.getRoot(), newest.getRoot(),
				mappings_last_newest);
		actionGenerator_last_newest.generate();
		List<Action> actions_last_newest = actionGenerator_last_newest
				.getActions();
		for (ITree tree : last.getRoot().getTrees()) {
			ITree dst = mappings_last_newest.getDst(tree);
			if (((TimeTree) tree).isDelete() && dst != null
					&& !((TimeTree) dst).isDelete()) {
				((TimeTree) tree).setDelete(false);
				long time = ((TimeTree) dst).getTime();
				((TimeTree) tree).setTime(time);
			} else if (!((TimeTree) tree).isDelete() && dst != null
					&& ((TimeTree) dst).isDelete()) {
				((TimeTree) tree).setDelete(true);
				long time = ((TimeTree) dst).getTime();
				((TimeTree) tree).setTime(time);
			}
		}

		// if (actions_last_newest.size() > 10)
		diff(number + "		" + message + "_last_newest" + version,
				last.deriveTree(), newest.deriveTree());
		// diff last remote
		Matcher matcher_last_remote = Matchers.getInstance()
				.getMatcher(last.getRoot(), remote.getRoot());
		matcher_last_remote.match();
		MappingStore mappings_last_remote = matcher_last_remote.getMappings();
		ActionGenerator actionGenerator_last_editor = new ActionGenerator(
				last.getRoot(), remote.getRoot(),
				mappings_last_remote);
		actionGenerator_last_editor.generate();
		List<Action> actions_last_remote = actionGenerator_last_editor
				.getActions();
		for (ITree tree : last.getRoot().getTrees()) {
			ITree dst = mappings_last_remote.getDst(tree);
			if (((TimeTree) tree).isDelete() && dst != null
					&& !((TimeTree) dst).isDelete()) {
				((TimeTree) tree).setDelete(false);
				long time = ((TimeTree) dst).getTime();
				((TimeTree) tree).setTime(time);
			}
		}
		// if (actions_last_remote.size() > 10)
		diff(number + "		" + message + "_last_remote" + version,
				last.deriveTree(), remote.deriveTree());

		List<TimeAction> actions = new ArrayList<TimeAction>();

		conflict(actions, newest, remote, actions_last_newest,
				actions_last_remote, mappings_last_newest,
				mappings_last_remote);

		conflict(actions, remote, newest, actions_last_remote,
				actions_last_newest, mappings_last_remote,
				mappings_last_newest);

		// 删除重复添加的内容(完全相同的子树以及相同属性不同属性值的子树)
		List<TimeAction> rootInsert = new ArrayList<TimeAction>();
		for (TimeAction timeAction : actions) {
			Action action = timeAction.getAction();
			if (action instanceof Insert) {
				TimeTree parent = (TimeTree) ((Insert) action).getParent();
				if (!isInsert(actions, parent)) {
					rootInsert.add(timeAction);
				}
			}
		}
		for (int i = rootInsert.size() - 1; i > 0; i--) {
			// System.out.println("rootInsert[" + i + "] " +
			// rootInsert.get(i).getAction().toString());
			for (int j = i - 1; j >= 0; j--) {
				if (equal(rootInsert.get(i), rootInsert.get(j), actions)) {
					System.out
							.println("rootInsert[" + i + "] = rootInsert[" + j
									+ "] =" + rootInsert.get(i).getAction().toString());
					removeActionAndChild(rootInsert.get(i), actions);
				}
			}
		}
		for (int i = rootInsert.size() - 1; i > 0; i--) {
			for (int j = i - 1; j >= 0; j--) {
				if (equal2(rootInsert.get(i), rootInsert.get(j), actions)) {
					System.out.print("rootInsert[" + i + "] 和 rootInsert[" + j
							+ "] 是同一属性，重复");
					removeActionAndChild(rootInsert.get(i), actions);
				}
			}
		}

		EMFTreeContext llast = last.deriveTree();
		for (TimeAction action : actions) {
			changeAST(last, action, actions);
		}

		// if (actions.size() > 10)
		diff(number + "		" + message + "_after_merge" + version, llast,
				last.deriveTree());
		// reset id
		ASTService.resetId(last.getRoot(), 0);
		return last;
	}

	private static void removeActionAndChild(TimeAction timeAction,
			List<TimeAction> actions) {
		if (timeAction.getAction() instanceof Insert) {
			// System.out.println("removeActionAndChild " +
			// timeAction.getAction().toString());
			actions.remove(timeAction);
			int len = 0;
			if (timeAction.getAction().getNode().getChildren() != null) {
				len = timeAction.getAction().getNode().getChildren().size();
			}
			for (int i = 0; i < len; i++) {
				TimeAction tc = getChildAction(timeAction, actions, i);
				if (tc != null)
					removeActionAndChild(tc, actions);
			}
		}

	}

	public static boolean equal(TimeAction ta1, TimeAction ta2,
			List<TimeAction> actions) {
		boolean res = true;
		if (ta1 != null && ta2 != null && ta1.getAction() instanceof Insert
				&& ta2.getAction() instanceof Insert
				&& ta1.getAction().getNode().getType() == ta2.getAction()
						.getNode().getType()
				&& ta1.getAction().getNode().getLabel() != null
				&& ta2.getAction().getNode().getLabel() != null
				&& ta1.getAction().getNode().getLabel()
						.contentEquals(ta2.getAction().getNode().getLabel())
				&& ta1.getAction().getNode().getChildren() != null
				&& ta2.getAction().getNode().getChildren() != null
				&& ta1.getAction().getNode().getChildren().size() == ta2
						.getAction().getNode().getChildren().size()) {
			int len = ta1.getAction().getNode().getChildren().size();

			// System.out.println("childern.len=" + len);
			for (int i = 0; i < len; i++) {
				TimeAction tc1 = getChildAction(ta1, actions, i);
				TimeAction tc2 = getChildAction(ta2, actions, i);
				if (tc1 == null) {
					System.err.println("tc1[" + i + "]= null");
					return false;
				} else if (tc2 == null) {
					System.err.println("tc2[" + i + "]= null");
					return false;
				}
				// System.out.println("tc1[" + i + "]=" +
				// tc1.getAction().toString());
				// System.out.println("tc2[" + i + "]=" +
				// tc2.getAction().toString());
				res = equal(tc1, tc2, actions);
				// System.out.println("res=" + res);
				if (!res) {
					return res;
				}

			}
		} else {
			return false;
		}

		return true;
	}

	public static boolean equal2(TimeAction ta1, TimeAction ta2,
			List<TimeAction> actions) {
		boolean res = true;

		List<TimeAction> tc1 = new ArrayList<TimeAction>();
		List<TimeAction> tc2 = new ArrayList<TimeAction>();
		if (ta1 != null && ta2 != null && ta1.getAction() instanceof Insert
				&& ta2.getAction() instanceof Insert
				&& ta1.getAction().getNode().getParent() == ta2.getAction()
						.getNode().getParent()
				&& ta1.getAction().getNode().getType() == ta2.getAction()
						.getNode().getType()
				&& ta1.getAction().getNode().getLabel() != null
				&& ta2.getAction().getNode().getLabel() != null
				&& ta1.getAction().getNode().getLabel()
						.contentEquals(ta2.getAction().getNode().getLabel())
				&& ta1.getAction().getNode().getChildren() != null
				&& ta2.getAction().getNode().getChildren() != null
				&& ta1.getAction().getNode().getChildren().size() == 2
				&& ta2.getAction().getNode().getChildren().size() == 2) {
			// 确保子树只有3个节点
			for (int i = 0; i < 2; i++) {
				ITree t1 = ta1.getAction().getNode().getChild(i);
				if (t1.getChildren() != null && t1.getChildren().size() > 0) {
					return false;
				}
				ITree t2 = ta2.getAction().getNode().getChild(i);
				if (t2.getChildren() != null && t2.getChildren().size() > 0) {
					return false;
				}
			}
			for (int i = 0; i < 2; i++) {
				TimeAction tc1i = getChildAction(ta1, actions, i);
				TimeAction tc2i = getChildAction(ta2, actions, i);
				if (tc1i == null) {
					System.err.println("tc1[" + i + "]= null");
					return false;
				} else if (tc2i == null) {
					System.err.println("tc2[" + i + "]= null");
					return false;
				}
				tc1.add(tc1i);
				tc2.add(tc2i);
				// 属性必须一样，属性值可以不一样
				if (tc1i.getAction().getNode().getType() != tc2i.getAction()
						.getNode().getType()
						|| i == 0
								&& tc1i.getAction().getNode().getLabel() != tc2i
										.getAction().getNode().getLabel()) {
					return false;
				}
			}
			if (ta1.getTime() > ta2.getTime()) {
				ta2.setTime(ta1.getTime());
				tc2.get(0).setTime(tc1.get(0).getTime());
				tc2.get(1).setTime(tc1.get(1).getTime());
			}
			return true;
		}

		return false;
	}

	private static TimeAction getChildAction(TimeAction ta1,
			List<TimeAction> actions, int i) {
		ITree t1 = ta1.getAction().getNode().getChild(i);
		TimeAction tc1 = null;
		for (TimeAction tc : actions) {
			if (tc.getAction().getNode() == t1) {
				tc1 = tc;
				// System.out.println("find childAction of " +
				// ta1.getAction().toString());
			}
		}
		return tc1;
	}

	public static void conflict(List<TimeAction> actions, EMFTreeContext tc1,
			EMFTreeContext tc2, List<Action> actions1,
			List<Action> actions2, MappingStore mappings1,
			MappingStore mappings2) {
		Iterator<Action> it_actions1 = actions1.iterator();
		while (it_actions1.hasNext()) {
			Action action1 = it_actions1.next();
			boolean conflict = false;

			// get time1
			long time1 = getChangeTime(tc1, mappings1, action1);

			// get parent
			TimeTree parent = getParent(action1);

			boolean deleteAct1 = false;
			// Conflicts involving the same node
			Iterator<Action> it_actions2 = actions2.iterator();
			while (it_actions2.hasNext()) {
				Action action2 = it_actions2.next();

				long time2 = getChangeTime(tc2, mappings2, action2);

				if (action2.getNode() == action1.getNode()) {
					if (time2 < time1) {
						// action1 is valid
						it_actions2.remove();
						remove(actions, action2);
					} else if (time2 > time1) {
						// action2 is valid
						it_actions1.remove();
						deleteAct1 = true;
					} else if (action2.getClass() == action1.getClass()) {
						// delete & delete, action2 is valid
						it_actions1.remove();
						deleteAct1 = true;
					} else {
						System.out.print("action2:\t" + action2.toString());
						System.out.print("action1:t" + action1.toString());
					}
					conflict = true;
					break;
				}
			}
			if (deleteAct1)
				continue;

			// Conflicts involving different nodes
			List<Action> removeActions = new ArrayList<Action>();
			for (int i = 0; i < actions2.size(); i++) {
				Action action2 = actions2.get(i);
				if (action2 instanceof Delete && action2.getNode() == parent) {
					// 若action1时间更新,则parent及叶子兄弟节点的修改都是无效的
					// 若action2时间更新,则node的修改是无效的
					long time2 = getChangeTime(tc2, mappings2, action2);
					if (time2 < time1) {
						// 父节点删除无效
						removeActions.add(action2);
						remove(actions, action2);
						// 祖先节点删除无效
						ITree anc = parent;
						while (anc.getParent() != null) {
							anc = anc.getParent();
							boolean deleteAnc = false;
							for (Action act2 : actions2) {
								if (anc == act2.getNode()
										|| act2 instanceof Delete) {
									removeActions.add(act2);
									remove(actions, act2);
									deleteAnc = true;
								}
							}
							if (!deleteAnc) {
								break;
							}
						}
						// 叶子兄弟节点删除无效
						for (ITree child : action2.getNode().getChildren()) {
							for (Action act2 : actions2) {
								if (child == act2.getNode()
										|| act2 instanceof Delete) {
									removeActions.add(act2);
									remove(actions, act2);
								}
							}
						}
					} else {
						it_actions1.remove();
						deleteAct1 = true;
					}
					conflict = true;
				}
			}
			actions2.removeAll(removeActions);
			if (!deleteAct1)
				actions.add(new TimeAction(time1, action1));
		}
	}

	private static TimeTree getParent(Action action1) {
		TimeTree parent = null;
		if (action1 instanceof Insert) {
			parent = (TimeTree) ((Insert) action1).getParent();
		} else if (action1 instanceof Move) {
			parent = (TimeTree) ((Move) action1).getParent();
		} else if (action1 instanceof Update) {
			parent = (TimeTree) action1.getNode().getParent();
		}
		return parent;
	}

	/**
	 * 若action1已加入到actions中，又与action2冲突，则还需要从actions中删除
	 * 
	 * @param actions
	 * @param action1
	 * @return true if delete, false otherwise
	 */
	private static boolean remove(List<TimeAction> actions, Action action1) {
		// TODO Auto-generated method stub
		for (TimeAction tact : actions) {
			if (tact.getAction() == action1) {
				actions.remove(tact);
				return true;
			}
		}
		return false;

	}

	private static long getChangeTime(EMFTreeContext tc2,
			MappingStore mappings2, Action action2) {
		// get time2
		long time2 = 0;
		if (action2 instanceof Insert) {
			time2 = ((TimeTree) action2.getNode()).getTime();
		} else if (action2 instanceof Delete) {
			time2 = ((TimeTree) tc2.getRoot()).getTime();
		} else {// update move
			TimeTree src = (TimeTree) action2.getNode();
			TimeTree dst = (TimeTree) mappings2.getDst(src);
			if(dst!=null)
				time2 = dst.getTime();
		}
		return time2;
	}

	public static void diff1(String message, EMFTreeContext context1,
			EMFTreeContext context2) {
	}

	public static void diff(String message, EMFTreeContext context1,
			EMFTreeContext context2) {
		Matcher matcher = Matchers.getInstance().getMatcher(context1.getRoot(),
				context2.getRoot());
		matcher.match();
		MappingStore mappings = matcher.getMappings();
		ActionGenerator actionGenerator = new ActionGenerator(
				context1.getRoot(), context2.getRoot(), mappings);
		actionGenerator.generate();
		List<Action> actions = actionGenerator.getActions();
		System.out.println(message);
		if (actions.size() > 0)
			System.out.print("\ttree.size = " + context1.getRoot().getId()
					+ ",actions.size = " + actions.size());
		for (Action action : actions) {
			System.out.print(action.toString());
		}
		// TreeDifferences app = new TreeDifferences(message,context1,context2,actions,mappings);
		// app.setVisible(true);
	}

	/**
	 * @Title: changeAST
	 * @Description:
	 * @param @param last
	 * @param @param action_last_newest
	 * @param @param mappings_last_newest 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void changeAST(EMFTreeContext last, TimeAction timeAction,
			List<TimeAction> actions) {
		Action action = timeAction.getAction();
		long time = timeAction.getTime();
		// System.out.println("changeAST");
		// 根据远程action 修改本地 old
		if (action instanceof Insert) {
			TimeTree node = (TimeTree) action.getNode().deepCopy();
			// int id = ASTService.resetId(node, last.getId());
			// node.setId(id);
			TimeTree parent = (TimeTree) ((Insert) action).getParent();
			int pos = ((Insert) action).getPosition();
			boolean isParentInsert = isInsert(actions, parent);
			if (!isParentInsert)
				if (parent.getChildren().size() > pos) {
					parent.insertChild(node, pos);
				} else {
					parent.addChild(node);
				}

		} else if (action instanceof Update) {
			TimeTree node = (TimeTree) action.getNode();
			String value = ((Update) action).getValue();
			node.setLabel(value);
		} else if (action instanceof Move) {
			TimeTree node = (TimeTree) action.getNode();
			TimeTree parent = (TimeTree) ((Move) action).getParent();
			int pos = ((Move) action).getPosition();
			node.getParent().getChildren().remove(node);

			if (parent.getChildren().size() > pos) {
				parent.insertChild(node, pos);
			} else {
				parent.addChild(node);
			}

		} else if (action instanceof Delete) {
			if (((TimeTree) action.getNode()).isDelete()) {
				return;
			}
			((TimeTree) action.getNode()).setDelete(true);
			((TimeTree) action.getNode()).setTime(time);

		}
	}

	private static boolean isInsert(List<TimeAction> actions, TimeTree node) {

		for (TimeAction timeAct : actions) {
			Action act = timeAct.getAction();
			if (act.getNode() == node && act instanceof Insert) {
				return true;
			}
		}
		return false;
	}

}
