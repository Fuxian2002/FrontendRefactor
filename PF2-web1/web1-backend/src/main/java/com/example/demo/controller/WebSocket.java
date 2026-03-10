package com.example.demo.controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.example.demo.LSP.*;
import com.example.demo.bean.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
//import com.google.gson.Gson;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/webSocket")
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class WebSocket {
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	//concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();

//	private static CopyOnWriteArraySet<LSP> lspSet =
//			new CopyOnWriteArraySet<LSP>();

	//单例
	private static class SingletonHolder{
  	     private static WebSocket instance = new WebSocket();
       }
   	public static  WebSocket getInstance(){
   		return SingletonHolder.instance;
   	}
	//与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session){
		this.session = session;
		webSocketSet.add(this);     //加入set中
		addOnlineCount();           //在线数加1
		System.out.println("draw有新连接加入！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(){

		unregister(this.session);
		webSocketSet.remove(this);  //从set中删除
		subOnlineCount();           //在线数减1
		System.out.println("draw有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 * @param session 可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws Exception {

		System.out.println("draw来自客户端的消息:" + message);
		JSONObject json = JSONObject.parseObject(message);
		String method = (String) json.get("method");
//		String type = (String) json.get("type");
//		String title = (String) json.get("title");
//		String version = (String) json.get("version");
//		int id = (int) json.get("id");
		switch (method) {
			case "Diagram/TimeRecoder":
				System.out.print("\n########## DiagramLSP " + session.getId()
						+ " receive TimeRecoder Message ########## ");
				System.out.println(message);
				recordSynTime(json);
				break;
			case "Diagram/moveTimes":
				System.out.println("\n########## DiagramLSP " + session.getId() +
						" receive moveTimes Message ########## ");
			 	System.out.println(message);
				moveTimesRecoder(session, json);
				break;
			case "Diagram/register":
				System.out.println("\n########## DiagramLSP " + session.getId()
						+ " receive register Message ########## ");
//					lsp_logger.info(
//							"sid:" + session.hashCode() + session.getId() + ",lsp:"
//									+ message);
				register_new(session, json);
				break;
			case "Diagram/didOpen":
				System.out.println(
						"\n########## DiagramLSP " + session.getId() + " receive didOpen Message########## ");
				// System.out.println(sFrame);
				register(session, json);
//					lsp_logger.info(
//							"sid:" + session.hashCode() + session.getId() + ",lsp:"
//									+ message);
				break;
			case "Diagram/didClose":
				System.out.println(
						"\n########## DiagramLSP " + session.getId() + " receive didClose Message########## ");
//					 System.out.println(sFrame);
				unregister(session, json);
				break;
			case "Diagram/didChange":
				System.out.println("\n########## DiagramLSP " + session.getId()
						+ " receive didChange Message ########## ");
				System.out.println(message);
				change(session, message);
				break;
			// default:
			// System.out.println("type=" + method + "==");
		}
//		switch(type) {
//			case "register": register(session,title,version,id);break;
//			case "unregister":unregister(session,title,version);break;
//			case "change":
//			case "add":
//			case "delete":
//				change(message,title,version,session);
//				break;
//			default: System.out.println("type="+type+"==");
//		}

	}
	void recordSynTime(JSONObject json) {
		JSONObject params = (JSONObject) json.get("params");
		String uri = params.getString("uri");
		for (LSPSubject subject : LSPSubjects.getSubjectSet()) {
			if (uri.equals(subject.getUri())) {
				subject.recordSynTime(json);// 实验三用
				subject.synTimeRecoder(session, json);// userstudy用
			}
		}
	}
	void moveTimesRecoder(Session session, JSONObject json) {
		JSONObject params = (JSONObject) json.get("params");
		String uri = params.getString("uri");
		int moveTimes = params.getIntValue("moveTimes");
		for (LSPObserver o : LSPObservers.getObserverSet()) {
			if (o.getSession() == session) {
				if (o.getUri().contentEquals(uri)) {
					o.recordMoveTimes(moveTimes);
					return;
				}
			}
		}

	}
	void register(Session session, JSONObject json) throws Exception {
        System.out.println("============register==============");
		JSONObject params = (JSONObject) json.get("params");
		JSONObject diagram = (JSONObject) params.get("diagram");
		// System.out.println(json.toJSONString());
		// System.out.println(diagram.toJSONString());
		String username = (String) diagram.get("username");
		String uri = (String) diagram.get("uri");

		for (LSPObserver o : LSPObservers.getObserverSet()) {
			if (o.getSession() == session) {
				if (o.getUri().contentEquals(uri)) {
					o.notifyClient("registered", "registered");
					return;
				} else {
					LSPObservers.getObserverSet().remove(o);
				}
			}
		}
		// Project
		Project project = new Project();
		JSONObject pro = (JSONObject) diagram.get("project");
		String title = (String) pro.get("title");
		project.setTitle(title);
		// intentDiagram
		IntentDiagram intentDiagram = new IntentDiagram();
		JSONObject jintentDiagram = (JSONObject) pro.get("intentDiagram");
		if (jintentDiagram != null) {
			title = (String) jintentDiagram.get("title");
			String ssystem = jintentDiagram.getString("system");
			ESystem system = JSONObject.parseObject(ssystem, ESystem.class);
			String spdList = jintentDiagram.getString("externalEntityList");
			List<ExternalEntity> externalEntityList = (List<ExternalEntity>) JSONObject
					.parseArray(spdList, ExternalEntity.class);
			String sreqList = jintentDiagram.getString("intentList");
			List<Intent> intentList = (List<Intent>) JSONObject
					.parseArray(sreqList, Intent.class);
			String srefList = jintentDiagram.getString("referenceList");
			List<Reference> referenceList = (List<Reference>) JSONObject
					.parseArray(srefList, Reference.class);
			String sconList = jintentDiagram.getString("constraintList");
			List<Constraint> constraintList = (List<Constraint>) JSONObject
					.parseArray(sconList, Constraint.class);
			String sintList = jintentDiagram.getString("interfaceList");
			List<Interface> interfaceList = (List<Interface>) JSONObject
					.parseArray(sintList, Interface.class);

			intentDiagram.setTitle(title);
			intentDiagram.setSystem(system);
			intentDiagram.setExternalEntityList(externalEntityList);
			intentDiagram.setIntentList(intentList);
			intentDiagram.setInterfaceList(interfaceList);
			intentDiagram.setReferenceList(referenceList);
			intentDiagram.setConstraintList(constraintList);
			project.setIntentDiagram(intentDiagram);
		}

		// contextDiagram
		ContextDiagram contextDiagram = new ContextDiagram();
		JSONObject jcontextDiagram = (JSONObject) pro.get("contextDiagram");
		if (jcontextDiagram != null) {
			title = (String) jcontextDiagram.get("title");
			String smachine = jcontextDiagram.getString("machine");
			Machine machine = JSONObject.parseObject(smachine, Machine.class);
			String spdList = jcontextDiagram.getString("problemDomainList");
			List<ProblemDomain> problemDomainList = (List<ProblemDomain>) JSONObject
					.parseArray(spdList, ProblemDomain.class);
			List<ProblemDomain> pdList = new ArrayList<ProblemDomain>();
			for (ProblemDomain pd : problemDomainList) {
				pdList.add(pd);
			}
			String sintList = jcontextDiagram.getString("interfaceList");
			List<Interface> interfaceList = (List<Interface>) JSONObject
					.parseArray(sintList, Interface.class);
			System.out.println("interfaceList: "+ interfaceList.size());
			contextDiagram.setTitle(title);
			contextDiagram.setMachine(machine);
			contextDiagram.setProblemDomainList(problemDomainList);
			contextDiagram.setInterfaceList(interfaceList);
			project.setContextDiagram(contextDiagram);
		}

		// problemDiagram
		ProblemDiagram problemDiagram = new ProblemDiagram();
		JSONObject jproblemDiagram = (JSONObject) pro.get("problemDiagram");
		if (jproblemDiagram != null) {
			title = (String) jproblemDiagram.get("title");
			String sreqList = jproblemDiagram.getString("requirementList");
			List<Requirement> requirementList = (List<Requirement>) JSONObject
					.parseArray(sreqList, Requirement.class);
			String srefList = jproblemDiagram.getString("referenceList");
			List<Reference> referenceList = (List<Reference>) JSONObject
					.parseArray(srefList, Reference.class);
			String sconList = jproblemDiagram.getString("constraintList");
			List<Constraint> constraintList = (List<Constraint>) JSONObject
					.parseArray(sconList, Constraint.class);
			problemDiagram.setTitle(title);
			problemDiagram.setRequirementList(requirementList);
			problemDiagram.setReferenceList(referenceList);
			problemDiagram.setConstraintList(constraintList);
			problemDiagram.setContextDiagram(contextDiagram);
			project.setProblemDiagram(problemDiagram);
		}

		// String sProject = diagram.getString("pro");
		// 为每个编辑器创建一个observer对象
		LSPDiagramObserver observer = new LSPDiagramObserver(session, uri,
				project, username);
		LSPObservers.getObserverSet().add(observer);

		// 若存在subject则注册
		boolean isFind = false;
		synchronized (LSPSubjects.class) {
			for (LSPSubject subject : LSPSubjects.getSubjectSet()) {
				if (uri.equals(subject.getUri())) {
					subject.attach_old(observer);
					isFind = true;
				}
			}
			// 若不存在subject则新建subject并注册
			if (!isFind) {
				LSPSubject subject = new LSPSubject(uri, observer);
				LSPSubjects.getSubjectSet().add(subject);
			}
		}
        System.out.println("==========================");
	}
	void register_new(Session session, JSONObject json) throws Exception {
		 System.out.println("============register_new==============");
		JSONObject params = (JSONObject) json.get("params");
		JSONObject diagram = (JSONObject) params.get("diagram");
		String username = (String) diagram.get("username");
		String uri = (String) diagram.get("uri");
		String projectAddress = (String) diagram.get("projectAddress");
		String version = (String) diagram.get("version");
		for (LSPObserver o : LSPObservers.getObserverSet()) {
			if (o.getSession() == session) {
				if (o.getUri().contentEquals(uri)) {
					o.notifyClient("registered", "registered");
					return;
				} else {
					LSPObservers.getObserverSet().remove(o);
				}
			}
		}
		// String sProject = diagram.getString("pro");
		// 为每个编辑器创建一个observer对象
		LSPDiagramObserver observer = new LSPDiagramObserver(session, uri,
				projectAddress, version, username);
		LSPObservers.getObserverSet().add(observer);

		// 若存在subject则注册并获取最新版本
		boolean isFind = false;
		synchronized (LSPSubjects.class) {
			for (LSPSubject subject : LSPSubjects.getSubjectSet()) {
				if (uri.equals(subject.getUri())) {
					subject.attach(observer);
					isFind = true;
				}
			}
			// 若不存在subject则新建subject，并获取最新版本
			if (!isFind && projectAddress != null) {
				LSPSubject subject = new LSPSubject(uri, projectAddress,
						version, observer);
				LSPSubjects.getSubjectSet().add(subject);
			}
		}
		// System.out.println("==========================");
	}
	void unregister(Session session, JSONObject json) {
		unregister(session);
	}
	void unregister(Session session) {
		for (LSPObserver lsp : LSPObservers.getObserverSet()) {
			if (lsp.getSession() == session) {
				lsp.unregister();
			}
		}
	}
	// 修改observer
	void change(Session session, String message) {
		for (LSPObserver lsp : LSPObservers.getObserverSet()) {
			if (session.equals(lsp.getSession())) {
				lsp.change(message);
			}
		}
	}
//	void register(Session session,String title,String version,int id){
//		boolean flag =false;
//		for(LSP lsp: WebSocket.lspSet) {
//			if(title.equals(lsp.getTitel())&& version.contentEquals(lsp.getVersion())) {
//				System.out.println(" title.equals(lsp.getTitel()) ");
//				lsp.addSession(session);
//				try {
//					int len = lsp.getMessagesLen();
//					for(int i =id;i<len;i++) {
//						session.getBasicRemote().sendText(lsp.getMessage(i));
//					}
//					System.out.println("注册时发送最新版");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				flag = true;
//				break;
//			}else {
//				System.out.print(title);
//				System.out.println(" not equals ");
//				System.out.print(lsp.getTitel());
//			}
//		}
//		if(!flag) {
//			LSP lsp = new LSP(title,version,session);
//			WebSocket.lspSet.add(lsp);
//		}
//	}
//	/**
//	 * @param session
//	 * @param title
//	 * @param version
//	 */
//	void unregister(Session session,String title,String version){
//		for(LSP lsp: WebSocket.lspSet) {
////			System.out.println(title+version);
//			if(title.equals(lsp.getTitel()) && version.contentEquals(lsp.getVersion())) {
//				lsp.removeSession(session);
//				if(lsp.getSessionSet().isEmpty()) {
//					WebSocket.lspSet.remove(lsp);
//					System.out.println(lsp.getTitel()+"无连接,删除该元素DRAW");
//				}
//			}
//		}
//	}
//	void unregister(Session session){
//		for(LSP lsp: WebSocket.lspSet) {
//				lsp.removeSession(session);
//				if(lsp.getSessionSet().isEmpty()) {
//					WebSocket.lspSet.remove(lsp);
//					System.out.println(lsp.getTitel()+"无连接,删除该元素DRAW");
//				}
//		}
//	}
//	//不会给发起者发送修改信息
//	void change(String message,String title,String version,Session session){
//		System.out.println("change project" + "," + message + "," + title + "," + version);
//		System.out.println(WebSocket.lspSet.size());
//		for(LSP lsp: WebSocket.lspSet) {
//			System.out.println(title+version);
//			if(title.equals(lsp.getTitel()) && version.contentEquals(lsp.getVersion())) {
//				System.out.println("==========================");
//				//给信息编号
//				StringBuffer Str=new StringBuffer(message);
//				Pattern p = Pattern.compile("\"id\":\\d*");
//				Matcher m = p.matcher(Str);
//				if(m.find()){
//					System.out.println(m.group(0));
//					Str.replace(m.start(),m.end(),"\"id\":"+lsp.getId());
//					message = Str.toString();
//				}
//
//				for(Session sess: lsp.getSessionSet()) {
////					if(sess.equals(session)) {
////						System.out.println("回复消息");
////						JSONObject object = new JSONObject();
////					    //string
////					    object.put("type","result");
////					    object.put("id",lsp.getId());
////						String str = object.toString();
////						try {
////							sess.getBasicRemote().sendText(str);
////						} catch (IOException e) {
////							e.printStackTrace();
////						}
////					}else {
//						try {
//							System.out.println("send message: "+message);
//							sess.getBasicRemote().sendText(message);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
////					}
//
//				}
//				lsp.addMessage(message);
//			}
//			System.out.println("===========DRAW===============");
//		}
//
//	}

	/**
	 * 发生错误时调用
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error){
		System.out.println("发生错误");
		error.printStackTrace();
	}

//	/**
//	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
//	 * @param message
//	 * @throws IOException
//	 */
//	public void sendMessage(String message) throws IOException{
//		this.session.getBasicRemote().sendText(message);
//		//this.session.getAsyncRemote().sendText(message);
//	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		WebSocket.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocket.onlineCount--;
	}
}
