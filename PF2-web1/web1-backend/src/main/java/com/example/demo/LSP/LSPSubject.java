package com.example.demo.LSP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.LSP.bean.OpenTimeRecoder;
import com.example.demo.service.ASTService;
import com.example.demo.service.AddressService;
import com.example.demo.service.FileOperation;
import com.example.demo.service.GitUtil;
import com.example.demo.util.SpringContextUtil;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.EMFTreeContext;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TimeTree;

import lombok.extern.log4j.Log4j2;

//@Log4j2(topic = "log")
public class LSPSubject {

	ASTService astService = new ASTService();

	private String uri;
	private String editorType;
	protected int id;
	protected String rootAddress;
	protected String gitAddress;
	protected String newestPath;

	protected String gitPath;
	protected EMFTreeContext newestContext;
	// private final static Logger syn_time_logger =
	// LoggerFactory.getLogger("syn_time");
	private final static Logger syn_logger = LogManager.getLogger("syn");
	private final static Logger urilogger = LogManager.getLogger("uri");
	private final static Logger log = LogManager.getLogger("log");

	public EMFTreeContext getNewestContext() {
		return newestContext;
	}

	public void setNewestContext(EMFTreeContext newestContext) {
		this.newestContext = newestContext;
	}

	protected ITree newestTree = null;

	protected CopyOnWriteArraySet<LSPObserver> observerSet = new CopyOnWriteArraySet<LSPObserver>();

	private String branch;

	public int ASTnodeNumber;
	public int nodeNumber;

	public int linkNumber;
	public int pfLen = 0;

	public int getPfLen() {
		return pfLen;
	}

	public void setPfLen(int pfLen) {
		this.pfLen = pfLen;
	}

	public LSPSubject(String uri, LSPObserver observer) throws Exception {
		super();
		this.uri = uri;
		this.observerSet.add(observer);
		observer.setSubject(this);
		AddressService addressService = SpringContextUtil.getBean(AddressService.class);
		rootAddress = addressService.getLastestProjectAddress() + uri + "/";
		gitAddress = rootAddress + "git/";
		gitPath = gitAddress + "newest.txt";
		branch = uri;
		newestPath = rootAddress + "newest.xml";
		System.out.println("LSPSubject-----rootAddress:"+rootAddress);
		// save newestContext to git
		if (observer.newestContext != null) {
			newestContext = observer.newestContext.deriveTree();
			GitUtil.createRepository1(gitAddress);
			GitUtil.gitCheckout(branch, gitAddress); // 切换分支
			ASTService.writeASTToFile(newestContext.getRoot(), gitAddress,
					gitPath);
			GitUtil.commit(newestContext.getVersion(), gitAddress,
					newestContext.getName(), newestContext.getEmail());
		}
		// System.out.println("========== new " + getUri() + " subject
		// ===========");
		// System.out.println("subject " + getUri() + ".newest.lastVersion = " +
		// newestContext.getLastVersion());
		// System.out.println("subject " + getUri() + ".newest.version = " +
		// newestContext.getVersion());
		// for (LSPSubject subject : LSPSubjects.getSubjectSet()) {
		// System.out.println(subject.toString());
		// }
	}

	public LSPSubject(String uri, String projectAddress, String version,
			LSPObserver observer) throws Exception {
		super();
		this.uri = uri;
		this.observerSet.add(observer);
		observer.setSubject(this);
		AddressService addressService = SpringContextUtil.getBean(AddressService.class);
		rootAddress = addressService.getLastestProjectAddress() + uri + "/";
		gitAddress = rootAddress + "git/";
		gitPath = gitAddress + "newest.txt";
		branch = uri;
		newestPath = rootAddress + "newest.xml";
		System.out.println("LSPSubject2-----rootAddress:"+rootAddress);
		// get project or pf, and initContext
		observer.getDataAndInitContext();

		// save newestContext to git
		if (observer.newestContext != null) {
			newestContext = observer.newestContext.deriveTree();
			GitUtil.createRepository1(gitAddress);
			GitUtil.gitCheckout(branch, gitAddress); // 切换分支
			ASTService.writeASTToFile(newestContext.getRoot(), gitAddress,
					gitPath);
			GitUtil.commit(newestContext.getVersion(), gitAddress,
					newestContext.getName(), newestContext.getEmail());
		}
		if (observer.getEditorType().contentEquals("text"))
			setPfLen(observer.pf.length());
		// System.out.println("========== new " + getUri() + " subject
		// ===========");
		for (LSPSubject subject : LSPSubjects.getSubjectSet()) {
			System.out.println(subject.toString());
		}
	}

	public String toString() {
		String str = "\t" + this.uri + "\n";
		for (LSPObserver o : observerSet) {
			str += "\t\t" + o.getEditorType() + "\t" + o.getSession().getId()
					+ "\n";
		}
		return str;
	}

	public void attach_old(LSPObserver observer) throws Exception {
		// System.out.println("****** " + observer.getClass().getSimpleName() +
		// " " +
		// observer.getSession().getId()
		// + " attach to subject " + this.uri + " ******");
		observerSet.add(observer);
		observer.setSubject(this);
		// System.out.println(toString());
		if (newestContext != null) {
			observer.setTree(newestContext.deriveTree());
		} else {
			if (observer.newestContext != null) {
				newestContext = observer.newestContext.deriveTree();
				GitUtil.createRepository1(gitAddress);
				GitUtil.gitCheckout(branch, gitAddress);
				ASTService.writeASTToFile(newestContext.getRoot(), gitAddress,
						gitPath);
				GitUtil.commit(newestContext.getVersion(), gitAddress,
						newestContext.getName(),
						newestContext.getEmail());
			}
		}
		// System.out.println(this.observerSet);
	}

	public void attach(LSPObserver observer) throws Exception {
		// System.out.println("****** " + observer.getClass().getSimpleName() +
		// " " +
		// observer.getSession().getId()
		// + " attach to subject " + this.uri + " ******");
		observerSet.add(observer);
		observer.setSubject(this);
		// System.out.println(toString());
		if (newestContext != null) {
			observer.setTreeAddGetLastestData(newestContext.deriveTree());
		} else {
			System.out.print("subject.newestContext == null");
			observer.getDataAndInitContext();
			if (observer.newestContext != null) {
				newestContext = observer.newestContext.deriveTree();
				GitUtil.createRepository1(gitAddress);
				GitUtil.gitCheckout(branch, gitAddress);
				ASTService.writeASTToFile(newestContext.getRoot(), gitAddress,
						gitPath);
				GitUtil.commit(newestContext.getVersion(), gitAddress,
						newestContext.getName(),
						newestContext.getEmail());
			}
		}

	}

	public void detach(LSPObserver observer) {
		observerSet.remove(observer);
		// System.out.println(
		// "===== " + observer.getEditorType() + observer.session.getId() + "
		// detach
		// from " + getUri() + " =====");
		// System.out.println(this.observerSet);
		if (this.observerSet.isEmpty()) {
			deleteFile();
			LSPSubjects.getSubjectSet().remove(this);
			// System.out.println("delete LSPSubject " + getUri());
		}
	}

	public void detach(Session session) {
		for (LSPObserver o : observerSet) {
			if (o.session == session) {
				observerSet.remove(o);
			}
		}

		// System.out.println(session.getId() + " detach from " + getUri());
		// System.out.println(this.observerSet);
		if (this.observerSet.isEmpty()) {
			deleteFile();
			LSPSubjects.getSubjectSet().remove(this);
			// System.out.println("delete LSPSubject " + getUri());
		}
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String content) {
		this.editorType = content;
	}

	public CopyOnWriteArraySet<LSPObserver> getObserverSet() {
		return observerSet;
	}

	public void setObserverSet(CopyOnWriteArraySet<LSPObserver> observerSet) {
		this.observerSet = observerSet;
	}

	@Async
	public void setValue(EMFTreeContext edi,String from) throws Exception {
		long ST1=System.currentTimeMillis();
		EMFTreeContext temp;
		synchronized (this) {
			String message = "";
			long time1 = new Date().getTime();
			if (newestContext == null) {
				// System.out.println(getUri() + " subject newestContext ==
				// null");
				newestContext = edi;
				newestContext.setFlag2(1);
				message = "from "+ from + ": "+edi.getFlag1()+"1X,无当前版本，直接替换";
			}
			// if edi == newest, then return
			// E commict V1, T notiry V0,
			// E merge V1 and V0 => merE, commit
			// T merge V1 and V0 => merT, notify
			// T receive merE, merE== merT, do nothing
			else if (edi.getVersion()
					.contentEquals(newestContext.getVersion())) {
				// System.out.println(getUri() + " subject edi = newest");
				// System.out.println("edi.getLastVersion() = " +
				// edi.getLastVersion());
				// System.out.println("edi.getVersion() = " + edi.getVersion());
				return;
			}

			// if edi.last == newest, then set newest = edi
			else if (edi.getLastVersion()
					.contentEquals(newestContext.getVersion())) {
				// System.out.println(getUri() + " subject 收到基于最新版的修改");
				// System.out.println("edi.getLastVersion() = " +
				// edi.getLastVersion());
				// System.out.println("edi.getVersion() = " + edi.getVersion());
				newestContext = edi.deriveTree();
				newestContext.setFlag2(1);
				message =  "from "+ from +": "+edi.getFlag1()+"1X,基于当前版本修改，直接替换";
			}
			// if edi.last != newest && edi!=newest, then find org to 3-way
			// merge
			else {
				// System.out.println(getUri() + " subject three_way_merge");
				three_way_merge(edi);
				newestContext.setFlag2(3);
				message =  "from "+ from +": "+edi.getFlag1()+"3X,三路合并";
				
			}

			System.out.println(message);
			// save newestContext to git
			GitUtil.gitCheckout(branch, gitAddress); // 切换分支
			ASTService.writeASTToFile(newestContext.getRoot(), gitAddress,
					gitPath);
			GitUtil.commit(newestContext.getVersion(), gitAddress,
					newestContext.getName(), newestContext.getEmail());

			long time2 = new Date().getTime();
			long T2 = time2 - time1;
			newestContext.setT2(T2);
			newestContext.setSt1(ST1);
//			System.out.println("subject change:"+newestContext.time());
			temp = newestContext.deriveTree();
		}
		temp.setFrom(from);
		//
		ASTService.generateEMFXmlFile(newestContext.getRoot(), newestPath);
		SAXReader saxReader = new SAXReader();
		try {
			File xmlFile = new File(newestPath);
			Document document = saxReader.read(xmlFile);
		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.print("parse error in subject:" + uri);
			return;
		}
		// notify Observer
		notifyObserver(temp);
	}

	private EMFTreeContext three_way_merge(EMFTreeContext remote) {
		// find org treeContext
		EMFTreeContext last = getLastContext(remote);
		String lastVersion = newestContext.getVersion();
		String versions = getUri() + "_three way merge_"
				+ last.getVersion().substring(36, 40) + "_"
				+ newestContext.getVersion().substring(36, 40) + "_"
				+ remote.getVersion().substring(36, 40);
		
		long time = System.currentTimeMillis();
		String lastPath = rootAddress + time+"_"+last.getVersion().substring(36, 40) + "_last.xml";
		ASTService.generateEMFXmlFile(last.getRoot(),lastPath);
		ASTService.writeASTToFile(last.getRoot(), rootAddress, lastPath+".txt");
		String oldPath = rootAddress + time+"_"+newestContext.getVersion().substring(36, 40) + "_newest.xml";
		ASTService.generateEMFXmlFile(newestContext.getRoot(),oldPath);
		ASTService.writeASTToFile(newestContext.getRoot(), rootAddress, oldPath+".txt");
		String remotePath = rootAddress + time+"_"+remote.getVersion().substring(36, 40) + "_remote.xml";
		ASTService.generateEMFXmlFile(remote.getRoot(),remotePath);
		ASTService.writeASTToFile(remote.getRoot(), rootAddress, remotePath+".txt");
		
		
		newestContext = LSPMerge.three_way_merge(getUri() + "_three way merge",
				last.deriveTree(),
				newestContext.deriveTree(), remote.deriveTree());
		

		
		
		// 生成新的 newest.xml for debug
		File newestFile = ASTService.generateEMFXmlFile(newestContext.getRoot(),newestPath);
		String version = FileOperation.getFileSha1(newestFile);
		String mergePathString=rootAddress + time +"_"+ version.substring(36, 40)+"_merge.xml";
		FileOperation.copyFile(newestPath, mergePathString);
		ASTService.writeASTToFile(newestContext.getRoot(), rootAddress, newestPath+".txt");
		System.out.print(versions + "-->" + version.substring(36, 40));
		System.out.println("subject: "+versions + "-->" + version.substring(36, 40));
		newestContext.setLastVersion(lastVersion);
		newestContext.setVersion(version);
		newestContext.setT0(remote.getT0());
		newestContext.setT1(remote.getT1());
		newestContext.setSt0(remote.getSt0());
		newestContext.setSt1(remote.getSt1());
		return newestContext.deriveTree();
	}

	/**
	 * @Title: getLastContext
	 * @Description: find lastVersion from git
	 * @param @param lastVersion
	 * @param @param version
	 * @param @return 设定文件
	 * @return EMFTreeContext 返回类型
	 * @throws
	 */
	EMFTreeContext getLastContext(EMFTreeContext remote) {
		String lastVersion = remote.getLastVersion();
		GitUtil.rollback(gitAddress, lastVersion);
		EMFTreeContext last = remote.deriveTree();
		last.setVersion(lastVersion);
		last.setRoot(ASTService.rebuildASTFromFile(gitPath));
		return last;
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
	public void changeAST(EMFTreeContext last, Action action,
			MappingStore mappings) {
		// System.out.println("changeAST");
		// 根据远程action 修改本地 old
		if (action instanceof Insert) {
			ITree x = action.getNode();
			ITree z = ((Insert) action).getParent();
			if (z == null) {// 若无匹配，则添加到根节点上
				last.setRoot(x);
			}
			int k = ((Insert) action).getPosition();
			if (z.getChildren().size() > k)
				z.insertChild(x, k);
			else
				z.addChild(x);
			x.setParent(z);
		} else if (action instanceof Update) {
			ITree w = action.getNode();
			String label = ((Update) action).getValue();
			if (w != null) {
				// System.out.println("\t" + w.toShortString() + "\tsetLabel \t"
				// + label);
				w.setLabel(label);
			}
		} else if (action instanceof Move) {
			ITree w = action.getNode();
			ITree z = ((Move) action).getParent();
			if (w.getParent() != null)
				w.getParent().getChildren().remove(w);
			int k = ((Move) action).getPosition();
			if (z != null && z.getChildren().size() > k)
				z.insertChild(w, k);
			else
				z.getChildren().add(w);

		} else if (action instanceof Delete) {
			TimeTree w = new TimeTree((TimeTree) action.getNode());
			w.setDelete(true);
			TimeTree parent = (TimeTree) mappings
					.getDst(action.getNode().getParent());
			int k = w.getPos();
			if (parent != null && parent.getChildren().size() > k)
				parent.insertChild(w, k);
			else
				parent.getChildren().add(w);
		}
	}

	@Async
	public void notifyObserver(EMFTreeContext newestContext) {
		// System.out.println("============== " + getUri() + " start
		// notifyObserver
		// ==================");
		for (LSPObserver observer : getObserverSet()) {
			// System.out.print(observer.getEditorType() +
			// observer.getSession().getId() +
			// ", ");
			observer.update(newestContext);
		}
		// System.out.println();
	}

	public void deleteFile() {
		// System.out.println("subject " + getUri() + " delete file " +
		// rootAddress + "
		// .....");
		// FileOperation.DeleteFolder(rootAddress);
		// String time = Calendar.getInstance().getTime().toString();
		// new File(rootAddress).renameTo(new File(rootAddress.substring(0,
		// rootAddress.length() - 1) + time + "/"));

		FileOperation.DeleteFolder(rootAddress);
	}

	public boolean recordOpenTime(JSONObject json) {
		JSONObject params = (JSONObject) json.get("params");
		String uri = params.getString("uri");
		long T0 = params.getLongValue("T0");
		long T1 = params.getLongValue("T1");
		OpenTimeRecoder timeRecoder = new OpenTimeRecoder(uri, T0, T1,
				nodeNumber, linkNumber);
		AddressService addressService = SpringContextUtil.getBean(AddressService.class);
		String lastestProjectAddress = addressService.getLastestProjectAddress();
		String filePath = lastestProjectAddress + "OpenTime.log";
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = df.format(date);
		if (timeRecoder.getT1() == 0 || timeRecoder.getT0() == 0) {
			System.out.println(timeRecoder.toString());
			return false;
		}
		try {
			File tofile = new File(filePath);
			FileWriter fw = new FileWriter(tofile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.println(time + ":\t" + timeRecoder.toString());
			// System.out.println(time + ":\t" + timeRecoder.toString());
			pw.close();
			bw.close();
			fw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public void recordSynTime(JSONObject json) {
		JSONObject params = (JSONObject) json.get("params");
		String uri = params.getString("uri");
		String lastVersion = params.getString("lastVersion");
		int nodes = params.getIntValue("nodes");
		int edges = params.getIntValue("edges");
		int ASTNodes = params.getIntValue("ASTNodes");
		long T0 = params.getLongValue("T0");
		long T1 = params.getLongValue("T1");
		long T2 = params.getLongValue("T2");
		long T3 = params.getLongValue("T3");
		long T4 = params.getLongValue("T4");
		int Flag1 = params.getIntValue("Flag1");
		int Flag2 = params.getIntValue("Flag2");
		int Flag3 = params.getIntValue("Flag3");
		String str = "";
		str += "uri:" + uri;
		str += ",\tlastVersion:" + lastVersion;
		str += ",\tnodes:" + nodes;
		str += ",\tedges:" + edges;
		str += ",\tbytes:" + pfLen;
		str += ",\tASTNodes:" + ASTNodes;
		str += ",\tT0:" + T0;
		str += ",\tT1:" + T1;// observer1处理时间
		str += ",\tT2:" + T2;// subject处理时间
		str += ",\tT3:" + T3;// observer1处理时间
		str += ",\tT4:" + T4;
		str += ",\tFlag1:" + Flag1;
		str += ",\tFlag2:" + Flag2;
		str += ",\tFlag3:" + Flag3;
				
		long synTime = T4 - T0;
		str += ",\tsynTime:" + synTime;
		if (T4 == 0 || T0 == 0) {
			System.out.println(str);
			return;
		}
		System.out.print(str);
	}

	public void synTimeRecoder(Session session, JSONObject json) {
		JSONObject params = (JSONObject) json.get("params");
		// String uri = params.getString("uri");
		// String lastVersion = params.getString("lastVersion");
		// long T0 = params.getLongValue("T0");
		// long T1 = params.getLongValue("T1");
		// long T2 = params.getLongValue("T2");
		// long T3 = params.getLongValue("T3");
		// long T4 = params.getLongValue("T4");
		// long Flag1 = params.getLongValue("Flag1");
		// long Flag2 = params.getLongValue("Flag2");
		// long Flag3 = params.getLongValue("Flag3");
		// TimeRecoder timeRecoder = new TimeRecoder(uri, lastVersion, T0, T1,
		// T2, T3,
		// T4, Flag1, Flag2, Flag3);
		// Date date = new Date();
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String time = df.format(date);
		// if (timeRecoder.getT4() == 0 || timeRecoder.getT0() == 0) {
		// System.out.println(timeRecoder.toString());
		// return;
		// }
		System.out.print("sid:" + session.hashCode() + session.getId()
				+ ",syn:" + params);

	}

}
