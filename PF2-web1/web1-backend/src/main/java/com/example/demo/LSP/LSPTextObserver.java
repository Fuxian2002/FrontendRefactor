package com.example.demo.LSP;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.EMF;
import com.example.demo.service.ASTService;
import com.example.demo.service.EMFService;
import com.example.demo.service.FileOperation;
import com.github.gumtreediff.tree.EMFTreeContext;

import lombok.extern.log4j.Log4j2;

//@Log4j2(topic = "log")
public class LSPTextObserver extends LSPObserver {
	boolean isSyn = false;
	int times = 0;
	private final static Logger log = LogManager.getLogger("log");

	// ==========================初始化====================
	public LSPTextObserver(Session session, String uri, String pf,
			String username) {
		super(session, uri, "text", username);
		if (toEMFFile(pf)) {
			iniContext();
			recordNodeNumber();
		}
		notifyClient("registered", "registered");
	}

	public LSPTextObserver(Session session, String uri, String projectAddress,
			String version, String username) {
		super(session, uri, "text", projectAddress, version, username);
	}

	public void recordNodeNumber() {
		EMF emf = EMFService.getEMF(editorPath);
		nodeNumber = emf.getNodes().size();
	}

	public void recordASTNodeNumber() {

	}

	public boolean toEMFFile(String pf) {
		this.pf = pf;
		System.out.println("toEMFFile："+pf+"  rootAddress: "+rootAddress+"  editorPath: "+editorPath);
		if (!EMFService.pf2emfFile(pf, rootAddress, editorPath)) {
			System.err.println("pf2emfFile error");
			return false;
		}
		FileOperation.copyFile(editorPath, newestPath);
		return true;
	}

	public void setTreeAddGetLastestData(EMFTreeContext newest) {
		super.setTreeAddGetLastestData(newest);
		// EMF AST -> EMF file
		ASTService.generateEMFXmlFile(newest.getRoot(), newestPath);
		// FileOperation.copyFile(newestPath, rootAddress +
		// System.currentTimeMillis() +
		// "_setTree_newest.xml");

		// emf file-> pf

		pf = EMFService.EMFToPf(newestPath);
		if (subject.getPfLen() == 0)
			subject.setPfLen(pf.length());
		notifyClient("registered", "registered");
	}

	public void setTree(EMFTreeContext newest) {
		super.setTree(newest);
		diff1(getEditorType() + session.getId() + ": setTree_newest_editor",
				newestContext, editorContext);
		// EMF AST -> EMF file
		ASTService.generateEMFXmlFile(newest.getRoot(), newestPath);
		// FileOperation.copyFile(newestPath, rootAddress +
		// System.currentTimeMillis() +
		// "_setTree_newest.xml");
		System.out.println(newestPath);

		// emf file-> pf
		pf = EMFService.EMFToPf(newestPath);
		System.out.println("pf = " + pf);
		notifyClient("registered", "registered");
	}

	// ========================change=====================
	public boolean change(String message) {
		long ST0=System.currentTimeMillis();
		recordLSPMessage(message);
		EMFTreeContext temp = syn_change(message, ST0);
		if (temp == null)
			return false;
		else {
			try {
				subject.setValue(temp, editorType + " " + session.getId());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	public synchronized EMFTreeContext syn_change(String message,long ST0) {
		System.out.println(editorType+" "+session.getId()+" syn_change: "+message);
		JSONObject json = JSONObject.parseObject(message);
		JSONObject params = (JSONObject) json.get("params");
		JSONObject textDocument = (JSONObject) params.get("textDocument");
		long time = textDocument.getLongValue("changeTime");
		if (textDocument.getString("lastVersion") != null)
			lastVersion = textDocument.getString("lastVersion");
		if (lastVersion == null) {

		} else if (lastContext.getVersion() == null) {
			System.out.println("lastContext.getVersion()==null");
		} else if (!lastContext.getVersion().contentEquals(lastVersion)) {
			System.out.println("text change based on last version!");
			return null;
		}
		pf = (String) params.get("text");
		if (!EMFService.pf2emfFile(pf, rootAddress, editorPath)) {
			errorTimes++;
			return null;
		}
		// 根据editorContext 修改newestContext
		if (change(time,ST0)) {
			newestContext.setFlag2(0);
			newestContext.setFlag3(0);
			notifyClient("update", "changeLastVersion");
			return newestContext.deriveTree();
		} else {
			moveTimes++;
		}
		return null;
	}

	// =============================merge========================
	// 更新文本， 生成merge.xml
	public synchronized void update(EMFTreeContext remote) {
		super.update(remote);

		long st2 = new Date().getTime();
		String message = "";
		if (newestContext == null) {
			message = "text " + session.getId() +": from="+remote.getFrom()+","
					+ remote.getFlag1()+remote.getFlag2()+ "1,newestContext=null,直接替换";
			newestContext = remote.deriveTree();
			lastContext = remote.deriveTree();
			newestContext.setFlag3(1);
			ASTService.generateEMFXmlFile(newestContext.getRoot(), newestPath);
		}
		if (remote.getVersion().contentEquals(newestContext.getVersion())) {
			message = "t e x t " + session.getId() + "，Flag3=1,自己的修改，不做任何处理";
			System.out.println(message);
			// 自己的修改，不做任何处理
			lastContext = remote.deriveTree();
			newestContext.setFlag3(1);
			newestContext.setSt2(st2);
			notifyClient("update", "changeLastVersion");
			return;
		}
		if (remote.getLastVersion().contentEquals(newestContext.getVersion())) {
			message = "t e x t " + session.getId() +": from="+remote.getFrom()+","
					+ remote.getFlag1()+remote.getFlag2()+ "1,自从trunk上次传来版本后没有修改,直接替换";

			// if remote.lastVersion == lastVersion == newestVersion
			// no change since trunk last notify Version
			newestContext = remote.deriveTree();
			lastContext = remote.deriveTree();
			newestContext.setFlag3(1);
			ASTService.generateEMFXmlFile(newestContext.getRoot(), newestPath);
		} else if (!remote.getLastVersion()
				.contentEquals(newestContext.getVersion())) {
			message = "t e x t " + session.getId()+": from="+remote.getFrom()+","
					+ remote.getFlag1()+remote.getFlag2()+ "3,3-way merge";
			// if edi.lastVersion != version,then 3-way-merge
			newestContext = three_way_merge(lastContext, newestContext, remote);
			newestContext.setFlag3(3);
			
			lastContext = remote.deriveTree();
		} else {
			System.out.println("=========================");
		}

		System.out.println(message);
		// emf file-> pf
		String oldpf = EMFService.EMFToPf(newestPath);
		if (oldpf == null) {
			System.out.println("EMFService.EMFToPf(" + newestPath + ") error!");
			return;
		}
		pf = oldpf;
		long time2 = new Date().getTime();
		newestContext.setT3(time2 - st2);
		newestContext.setSt2(st2);
		notifyClient(message, "didChange");
	}

	// send lastest pf when registered or merge
	public void notifyClient(String mes, String method) {
		JSONObject message = new JSONObject();
		message.put("jsonrpc", "2.0");
		message.put("id", id++);
		message.put("message", mes);
		message.put("method", "TextDocument/" + method);

		JSONObject textDocument = new JSONObject();
		textDocument.put("uri", uri);
		if (lastContext != null) {
			textDocument.put("lastVersion", lastContext.getVersion());
		}
		if (newestContext != null) {
			textDocument.put("version", newestContext.getVersion());
			textDocument.put("T0", newestContext.getT0());
			textDocument.put("T1", newestContext.getT1());
			textDocument.put("T2", newestContext.getT2());
			textDocument.put("T3", newestContext.getT3());
			textDocument.put("ST0", newestContext.getSt0());
			textDocument.put("ST1", newestContext.getSt1());
			textDocument.put("ST2", newestContext.getSt2());
			textDocument.put("ST3", newestContext.getSt3());
			textDocument.put("Flag1", newestContext.getFlag1());
			textDocument.put("Flag2", newestContext.getFlag2());
			textDocument.put("Flag3", newestContext.getFlag3());

			
		}
		if(subject!=null) {
			textDocument.put("nodes", subject.nodeNumber);
			textDocument.put("edges", subject.linkNumber);
			textDocument.put("ASTNodes", subject.ASTnodeNumber);
			if(pf == null){
				subject.setPfLen(0);
			}else{
				subject.setPfLen(pf.length());
			}

		}
		
		
		JSONObject params = new JSONObject();
		params.put("textDocument", textDocument);
		if (method.contentEquals("didChange")
				|| method.contentEquals("registered")) {
			params.put("text", pf);
		} else if (method.contentEquals("needDidOpen")) {
			params.put("needDidOpen", "needDidOpen");
		}
		message.put("params", params);

		synchronized (this) {
			try {
				session.getBasicRemote().sendText(JSON.toJSONString(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean getDataAndInitContext() {
		String branch = projectAddress;
		pf = fileService.getNotNullPf(getUserAdd(username)+branch+"/", version, branch);
		System.out.println("getDataAndInitContext:"+pf);
		if (toEMFFile(pf)) {
			iniContext();
			recordNodeNumber();
		}
		notifyClient("registered", "registered");
		return true;
	}

	public String getUserAdd(String username) {
		String userAdd;
		if (username == null || username == "")
			userAdd = rootAddress;
		else
			userAdd = userAddress + username + "/";
		return userAdd;
	}



}
