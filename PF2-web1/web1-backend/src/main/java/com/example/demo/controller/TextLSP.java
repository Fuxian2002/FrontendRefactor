package com.example.demo.controller;

import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.LSP.LSPObserver;
import com.example.demo.LSP.LSPObservers;
import com.example.demo.LSP.LSPSubject;
import com.example.demo.LSP.LSPSubjects;
import com.example.demo.LSP.LSPTextObserver;

//import lombok.extern.log4j.Log4j2;
//@Log4j2(topic = "log")

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 *                 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/TextLSP")
@RestController
public class TextLSP {
	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;
	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	private static CopyOnWriteArraySet<TextLSP> webSocketSet = new CopyOnWriteArraySet<TextLSP>();

	// private final static Logger lsp_logger = LoggerFactory
	// .getLogger("lsp");
	private final static Logger lsp_logger = LogManager
			.getLogger("lsp");

	private final static Logger log = LogManager.getLogger("log");
	// 单例
	private static class SingletonHolder {
		private static TextLSP instance = new TextLSP();
	}

	public static TextLSP getInstance() {
		return SingletonHolder.instance;
	}

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 *
	 * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		webSocketSet.add(this); // 加入set中
		addOnlineCount(); // 在线数加1
		 System.out.println("TextLSP有新连接加入！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		unregister(this.session);
		webSocketSet.remove(this); // 从set中删除
		subOnlineCount(); // 在线数减1
		 System.out.println("TextLSP 有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message 客户端发送过来的消息
	 * @param session 可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		// System.out.println("\n$$$$$$$$$$$$$$ TextLSP" + session.getId() + "
		// receive
		// Message $$$$$$$$$$$$$$$$");
		// System.out.println(message);
		JSONObject json = JSONObject.parseObject(message);
		String method = (String) json.get("method");
		switch (method) {
		case "TextDocument/TimeRecoder":
			 System.out.print("\n$$$$$$$$$$$$$$ TextLSP " + session.getId()
			 + " receive TimeRecoder Message $$$$$$$$$$$$$$$$ ");
			 System.out.println(message);
			recordSynTime(json);
			break;
		case "TextDocument/didOpen":
			 System.out.println("\n$$$$$$$$$$$$$$ TextLSP" + session.getId()
			 + " receive didOpen Message $$$$$$$$$$$$$$$$");
			// System.out.println(sFrame);
			lsp_logger.info(
					"sid:" + session.hashCode() + session.getId() + ",lsp:"
							+ message);
			register(session, json);
			break;
		case "TextDocument/register":
			 System.out.println(
			 "\n$$$$$$$$$$$$$$ TextLSP" + session.getId() + " receive register Message $$$$$$$$$$$$$$$$");
			// System.out.println(sFrame);
			lsp_logger.info(
					"sid:" + session.hashCode() + session.getId() + ",lsp:"
							+ message);
			register_new(session, json);
			break;
		case "TextDocument/didSave":
			 System.out.println("\n$$$$$$$$$$$$$$ TextLSP" + session.getId()
			 + " receive didSave Message $$$$$$$$$$$$$$$$");
			// System.out.println(sFrame);
			change(session, message);
			break;
		case "TextDocument/didClose":
			 System.out.println("\n$$$$$$$$$$$$$$ TextLSP" + session.getId()
			 + " receive didClose Message $$$$$$$$$$$$$$$$");
			 System.out.println(message);
			unregister(session);
		default:
			log.error("type=" + method + "==========");
		}
	}

	public boolean recordSynTime(JSONObject json) {
		JSONObject params = (JSONObject) json.get("params");
		String uri = params.getString("uri");
		for (LSPSubject subject : LSPSubjects.getSubjectSet()) {
			if (uri.equals(subject.getUri())) {
				subject.recordSynTime(json);
				subject.synTimeRecoder(session, json);
			}
		}
		return true;
	}

	void register_new(Session session, JSONObject json) {
		JSONObject params = (JSONObject) json.get("params");
		JSONObject textDocument = (JSONObject) params.get("textDocument");
		String username = (String) textDocument.get("username");
		String uri = (String) textDocument.get("uri");
		String projectAddress = (String) textDocument.get("projectAddress");
		String projectVersion = (String) textDocument.get("projectVersion");
		for (LSPObserver o : LSPObservers.getObserverSet()) {
			if (o.getSession() == session) {
				// registered
				if (o.getUri().contentEquals(uri)) {
					if (o.getNewestContext() == null
							&& o.getSubject().getNewestContext() != null) {
						o.setTreeAddGetLastestData(
								o.getSubject().getNewestContext().deriveTree());
					}
					o.notifyClient("registered", "registered");
					return;
				} else {
					LSPObservers.getObserverSet().remove(o);
				}
			}
		}

		// 为每个编辑器创建一个observer对象
		LSPTextObserver observer = new LSPTextObserver(session, uri,
				projectAddress, projectVersion, username);
		LSPObservers.getObserverSet().add(observer);

		// 若存在subject则注册
		boolean isFind = false;
		synchronized (LSPSubjects.class) {
			for (LSPSubject subject : LSPSubjects.getSubjectSet()) {
				if (uri.equals(subject.getUri())) {
					try {
						subject.attach(observer);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					// System.out.println(LSPSubjects.getSubjectSet());
					isFind = true;
				}
			}

			// 若不存在subject则新建subject并注册
			if (!isFind) {
				LSPSubject subject = null;
				try {
					subject = new LSPSubject(uri, observer);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				LSPSubjects.getSubjectSet().add(subject);

				observer.notifyClient("needDidOpen", "needDidOpen");
			} else {

				// send registered message
				observer.notifyClient("registered", "registered");
			}
		}
	}

	void register(Session session, JSONObject json) {
		JSONObject params = (JSONObject) json.get("params");
		JSONObject textDocument = (JSONObject) params.get("textDocument");
		String uri = (String) textDocument.get("uri");
		String username = (String) textDocument.get("username");
		String pf = (String) textDocument.get("text");
		for (LSPObserver o : LSPObservers.getObserverSet()) {
			if (o.getSession() == session) {
				// registered
				if (o.getUri().contentEquals(uri)) {
					o.notifyClient("registered", "registered");
					return;
				} else {
					LSPObservers.getObserverSet().remove(o);
				}
			}
		}

		// 为每个编辑器创建一个observer对象
		LSPTextObserver observer = new LSPTextObserver(session, uri, pf,
				username);
		LSPObservers.getObserverSet().add(observer);

		// 若存在subject则注册
		boolean isFind = false;
		synchronized (LSPSubjects.class) {
			for (LSPSubject subject : LSPSubjects.getSubjectSet()) {
				if (uri.equals(subject.getUri())) {
					try {
						subject.attach_old(observer);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					// System.out.println(LSPSubjects.getSubjectSet());
					isFind = true;
				}
			}

			// 若不存在subject则新建subject并注册
			if (!isFind) {
				LSPSubject subject = null;
				try {
					subject = new LSPSubject(uri, observer);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				LSPSubjects.getSubjectSet().add(subject);
			}
		}
	}

//	/**
//	 * @param session
//	 * @param title
//	 * @param version
//	 */
	@Deprecated
	void unregister(Session session, JSONObject json) {
		JSONObject params = (JSONObject) json.get("params");
		JSONObject diagram = (JSONObject) params.get("diagram");
		String uri = (String) diagram.get("uri");
		for (LSPSubject lsp : LSPSubjects.getSubjectSet()) {
			if (uri.equals(lsp.getUri())) {
				lsp.detach(session);
			}
		}
	}

	void unregister(Session session) {
		for (LSPObserver lspObserver : LSPObservers.getObserverSet()) {
			if (lspObserver.getSession() == session) {
				lspObserver.unregister();
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

	/**
	 * 发生错误时调用
	 *
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		log.error("发生错误" + error);

		error.printStackTrace();
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		TextLSP.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		TextLSP.onlineCount--;
	}
}