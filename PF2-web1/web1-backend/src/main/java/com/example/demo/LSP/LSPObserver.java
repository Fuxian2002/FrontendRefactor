package com.example.demo.LSP;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

// import com.alexander.solovyov.TreeDifferences;
import com.example.demo.bean.Project;
import com.example.demo.service.ASTService;
import com.example.demo.service.AddressService;
import com.example.demo.service.FileOperation;
import com.example.demo.service.FileService;
import com.example.demo.service.GitUtil;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.EMFTreeContext;
import com.github.gumtreediff.tree.ITree;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.DependsOn;
import com.example.demo.util.SpringContextUtil;

//@Log4j2(topic = "log")
public abstract class LSPObserver {

//	@Autowired // 自动装配
	FileService fileService;
	ASTService astService = new ASTService();
	FileOperation fileOperation = new FileOperation();
	LSPSubject subject = null;
	protected Session session;
	protected String editorType;
	protected String uri;
	protected String username;

	protected String startTime;
	protected int moveTimes = 0;
	protected Project project = null;
	protected String pf = null;

	protected String lastestProjectAddress;
	protected String rootAddress;
	protected String userAddress;

	protected String lastPath;
	protected String editorPath;
	protected String newestPath;

	protected EMFTreeContext lastContext;
	protected EMFTreeContext newestContext;
	private final static Logger lsp_logger = LogManager.getLogger("lsp");
	private final static Logger move_logger = LogManager.getLogger("move");
	private final static Logger log = LogManager.getLogger("log");
	
	public void recordLSPMessage(String message) {
		System.out.println("sid:" + session.hashCode() + session.getId() + ",lsp:"
				+ message);
	}

	public EMFTreeContext getNewestContext() {
		return newestContext;
	}

	public void setNewestContext(EMFTreeContext newestContext) {
		this.newestContext = newestContext;
	}

	protected EMFTreeContext editorContext;

	protected int id;
	protected String lastVersion;
	protected String version;

	protected int nodeNumber;
	protected int linkNumber;
	protected int ASTnodeNumber;
	String projectAddress;
	String projectVersion;
	protected int errorTimes = 0;

	// =========================get & set=================
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	// ==========================初始化====================

	public LSPObserver(Session session, String uri, String editorType,
			String username) {
		super();
		this.fileService = SpringContextUtil.getBean(FileService.class);
		AddressService addressService = SpringContextUtil.getBean(AddressService.class);
		this.userAddress = addressService.getUserAddress();
		this.session = session;
		this.username = username;
		this.uri = uri;
		this.editorType = editorType;
		lastestProjectAddress = addressService.getLastestProjectAddress();
		rootAddress = lastestProjectAddress + uri + "/" + getEditorType()
				+ session.getId() + "/";
		lastPath = rootAddress + "last.xml";
		newestPath = rootAddress + "newest.xml";
		editorPath = rootAddress + "editor.xml";
		System.out.println("LSPObserver-----rootAddress:"+rootAddress);
		File dir = new File(rootAddress);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public LSPObserver(Session session, String uri, String editorType,
			String projectAddress, String version, String username) {
		super();
		this.fileService = SpringContextUtil.getBean(FileService.class);
		AddressService addressService = SpringContextUtil.getBean(AddressService.class);
		this.userAddress = addressService.getUserAddress();
		this.username = username;
		this.projectAddress = projectAddress;
		this.projectVersion = version;
		this.session = session;
		this.username = username;
		this.uri = uri;
		this.editorType = editorType;
		lastestProjectAddress = addressService.getLastestProjectAddress();
		rootAddress = lastestProjectAddress + uri + "/" + getEditorType()
				+ session.getId() + "/";
		lastPath = rootAddress + "last.xml";
		newestPath = rootAddress + "newest.xml";
		editorPath = rootAddress + "editor.xml";
		System.out.println("LSPObserver-----rootAddress:"+rootAddress);
		File dir = new File(rootAddress);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public abstract boolean getDataAndInitContext();

	public void iniContext() {
		// FileOperation.copyFile(editorPath, rootAddress +
		// System.currentTimeMillis() +
		// "_initial_newest.xml");
		// FileOperation.copyFile(editorPath, rootAddress +
		// System.currentTimeMillis() +
		// "_initial_editor.xml");
		lastContext = ASTService.generateXmlAST(editorPath);
		lastContext.setLastVersion(lastContext.getVersion());
		lastContext.setName(getEditorType() + getSession().getId());
		lastContext.setEmail(getSession().toString());

		newestContext = lastContext.deriveTree();
		editorContext = lastContext.deriveTree();

		List<ITree> des = lastContext.getRoot().getDescendants();
		ASTnodeNumber = des.size();
		// System.out.println(getUri() + "." + getEditorType() + session.getId()
		// +
		// ".newestContext.version ="
		// + newestContext.getVersion());
		// System.out.println(getUri() + "." + getEditorType() + session.getId()
		// +
		// ".newestContext.lastVersion="
		// + newestContext.getLastVersion());
	}

	public boolean unregister() {
		deleteFile();
		LSPObservers.getObserverSet().remove(this);
		subject.detach(this);
		return true;
	}

	public Session getSession() {
		return this.session;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public LSPSubject getSubject() {
		return subject;
	}

	public void setSubject(LSPSubject subject) {
		this.subject = subject;
	}

	// if not first observer,setTree will be called
	public void setTreeAddGetLastestData(EMFTreeContext newest) {
		lastContext = newest.deriveTree();
		newestContext = newest.deriveTree();
		editorContext = newest.deriveTree();
		diff1(getEditorType() + getSession().getId() + "setTree_newest",
				newestContext, editorContext);

		// System.out.println(
		// getEditorType() + session.getId() + ".newestContext.version =" +
		// newestContext.getVersion());
		// System.out.println(
		// getEditorType() + session.getId() + ".newestContext.lastVersion=" +
		// newestContext.getLastVersion());
	}

	public void setTree(EMFTreeContext newest) {
		lastContext = newest;
		newestContext = newest;
		editorContext = newest;
		// System.out.println(
		// getEditorType() + session.getId() + ".newestContext.version =" +
		// newestContext.getVersion());
		// System.out.println(
		// getEditorType() + session.getId() + ".newestContext.lastVersion=" +
		// newestContext.getLastVersion());
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
			System.out.println("tree.size = " + context1.getRoot().getId()
					+ ",actions.size = " + actions.size());
		for (Action action : actions) {
			System.out.println(action.toString());
		}
		// TreeDifferences app = new TreeDifferences(message, context1,
		// context2,
		// actions, mappings);
		// app.setVisible(true);
	}

	public void generateLocAST(long time) {

	}

	public synchronized void notifyClient(String message, String method) {

	}

	// ==============change==============
	public abstract boolean change(String message);

	public boolean change(long time,long ST0) {
		System.out.println("change(long time,long ST0)");
		String edi = FileOperation.readFromFile(editorPath);
		String ne = FileOperation.readFromFile(newestPath);
//		System.out.println(editorPath);
//		System.out.println(edi);
		if (edi.contentEquals(ne)) {
			// no change in yuyi
			moveTimes++;
			return false;
		}

		editorContext = ASTService.generateXmlAST(editorPath, time);
		editorContext.setLastVersion(lastVersion);
		editorContext.setT0(time);
		editorContext.setSt0(ST0);
		editorContext.setT1(System.currentTimeMillis());
		editorContext.setName(getEditorType() + getSession().getId());
		editorContext.setEmail(getSession().toString());
		// if origin version has error, then let editorContext version as
		// org(lastContext)
		String messageString="";
		if (lastContext == null) {
			lastContext = editorContext.deriveTree();
			newestContext = editorContext.deriveTree();
			System.out.println(
					getEditorType() + session.getId() + "lastContext == null");
			newestContext.setFlag1(1);// 直接替换
			messageString=getEditorType()+" "+getSession().getId()+": 1XX,无父版本，直接替换";
		} else if (editorContext.getLastVersion() == null) {
			System.out.println(
					getEditorType() + session.getId()
							+ " ======= editorContext.LastVersion == null =========");
		} else if (editorContext.getLastVersion()
				.contentEquals(lastContext.getVersion())) {
			// if edi.lastVersion == lastVersion == newestVersion, then 2-way
			// merge
			// first change since trunk notify a new Version
			newestContext = two_way_merge(newestContext, editorContext);
			newestContext.setFlag1(2);// two_way_merge
			messageString=getEditorType()+" "+getSession().getId()+": 2XX,更新时间戳,lastversion="+editorContext.getLastVersion();
		} else if (editorContext.getLastVersion() != null
				&& !editorContext.getLastVersion().contentEquals(lastContext.getVersion())) {
			// if edi.lastVersion != lastVersion,then return
			// change based on the previous version of the trunk
			System.out.println(
					getEditorType() + session.getId()
							+ " change based on the previous version of the trunk!!!");
			three_way_merge(editorContext);
			newestContext.setFlag1(3);// three_way_merge
			messageString=getEditorType()+" "+getSession().getId()+": 3XX,三路合并,editorContext.getLastVersion()="
					+editorContext.getLastVersion()+",lastContext.getVersion()"+lastContext.getVersion();
		} else {
			System.out.println(getEditorType() + session.getId()
					+ "======= error================");
		}

		System.out.println(messageString);
		// 生成新的 newest.xml for debug
		File newestFile = ASTService.generateEMFXmlFile(newestContext.getRoot(),
				newestPath);
		FileOperation.copyFile(newestPath, rootAddress
				+ System.currentTimeMillis() + "_change_newest.xml");

		// set version & lastVersion
		newestContext.setLastVersion(lastContext.getVersion());
		String version = FileOperation.getFileSha1(newestFile);
		newestContext.setVersion(version);
		return true;
	}

	public EMFTreeContext two_way_merge(EMFTreeContext newest,
			EMFTreeContext editor) {
		diff1(getEditorType() + session.getId()
				+ ": before_two_way_merge newest editor", newest.deriveTree(),
				editor.deriveTree());
		newest = LSPMerge.two_way_merge(getEditorType() + session.getId(),
				newest.deriveTree(), editor.deriveTree());
		diff1(getEditorType() + session.getId() + " after_two_way_merge",
				newest.deriveTree(), newest.deriveTree());
		return newest.deriveTree();
	}

	public void update(EMFTreeContext remote) {
		// System.out.println("...... " + getEditorType() + getSession().getId()
		// + "
		// update ......");
	}

	// 编辑器传来的修改，进行 3-way merge
	private EMFTreeContext three_way_merge(EMFTreeContext remote) {
		System.out.println("three_way_merge(EMFTreeContext remote)");
		// find org treeContext
		EMFTreeContext last = getLastContext(remote);
		String lastVersion = newestContext.getVersion();
		String versions = getUri() + "three way merge_"
				+ last.getVersion().substring(36, 40) + "_"
				+ newestContext.getVersion().substring(36, 40) + "_"
				+ remote.getVersion().substring(36, 40);
		newestContext = LSPMerge.three_way_merge(getUri() + "three way merge",
				last.deriveTree(),
				newestContext.deriveTree(), remote.deriveTree());
		// 生成新的 newest.xml for debug
		File newestFile = ASTService.generateEMFXmlFile(newestContext.getRoot(),
				newestPath);
		FileOperation.copyFile(newestPath, rootAddress
				+ System.currentTimeMillis() + "_three_way_merge_newest.xml");
		String version = FileOperation.getFileSha1(newestFile);
		System.out.println(versions + " --> " + version);
		newestContext.setLastVersion(lastVersion);
		newestContext.setVersion(version);
		newestContext.setT0(remote.getT0());
		newestContext.setSt0(remote.getSt0());
		newestContext.setT1(remote.getT1());
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
		System.out.println("getLastContext");
		String lastVersion = remote.getLastVersion();
		GitUtil.rollback(subject.gitAddress, lastVersion);
		EMFTreeContext last = remote.deriveTree();
		last.setVersion(lastVersion);
		last.setRoot(ASTService.rebuildASTFromFile(subject.gitPath));
		return last;
	}

	protected EMFTreeContext three_way_merge(EMFTreeContext last,
			EMFTreeContext newest, EMFTreeContext remote) {
		long time = System.currentTimeMillis();
		String lastPath = rootAddress + editorType+"_"+session.getId()+"_"+time+"_"+lastVersion.substring(36,40) + "_last.xml";
		ASTService.generateEMFXmlFile(last.getRoot(),lastPath);
		ASTService.writeASTToFile(last.getRoot(), rootAddress,	lastPath+".txt");
		String remotePath = rootAddress + editorType+"_"+session.getId()+"_"+ time+"_"+remote.getVersion().substring(36,40) + "_remote.xml";
		ASTService.generateEMFXmlFile(remote.getRoot(),remotePath);
		ASTService.writeASTToFile(remote.getRoot(), rootAddress,	remotePath+".txt");
		String oldPath = rootAddress + editorType+"_"+session.getId()+"_"+ time+"_"+newest.getVersion().substring(36,40) + "_newest.xml";
		ASTService.generateEMFXmlFile(newest.getRoot(),oldPath);
		ASTService.writeASTToFile(newest.getRoot(), rootAddress,	oldPath+".txt");
			
		newest = LSPMerge.three_way_merge(
				getEditorType() + session.getId() + "_three_way_merge", last,
				newest, remote);
		
		// EMF AST -> EMF file
		File newestFile = ASTService.generateEMFXmlFile(newest.getRoot(), newestPath);
		String version = FileOperation.getFileSha1(newestFile);
		String mergePath = rootAddress + editorType+"_"+session.getId()+"_"+ time+"_"+version.substring(36,40) + "_merge.xml";
		FileOperation.copyFile(newestPath, mergePath);
		ASTService.writeASTToFile(newest.getRoot(), rootAddress,	mergePath+".txt");
		
		// set version & lastVersion
		newest.setLastVersion(remote.getVersion());
		newest.setVersion(version);
		newest.setT0(remote.getT0());
		newest.setT1(remote.getT1());
		newest.setT2(remote.getT2());
		newest.setSt0(remote.getSt0());
		newest.setSt1(remote.getSt1());
		newest.setFlag3(3);
		return newest;
	}

	public void deleteFile() {
		// record moveTimes
		recordMoveTimesLog();
		FileOperation.DeleteFolder(rootAddress);
	}

	public void recordMoveTimes(int moveTimes) {
	}

	public void recordMoveTimesLog() {
		if (moveTimes > 0) {
			String time = Calendar.getInstance().getTime().toString();
			String str = "uri:" + uri + ",editType:" + getEditorType()
					+ ",sid:" + session
					+ ",startTime:" + startTime + ",endTime:" + time
					+ ",moveTimes:"
					+ moveTimes;
			System.out.println(str);
		}
	}

}