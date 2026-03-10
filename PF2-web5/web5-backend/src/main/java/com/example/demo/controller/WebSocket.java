package com.example.demo.controller;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.LSP;
import com.alibaba.fastjson.JSONObject;
//import com.google.gson.Gson;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/webSocket")
@RestController
public class WebSocket {
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	//concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();

	private static CopyOnWriteArraySet<LSP> lspSet = 
			new CopyOnWriteArraySet<LSP>();

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
		System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(){
		
		unregister(this.session);
		webSocketSet.remove(this);  //从set中删除
		subOnlineCount();           //在线数减1
		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 * @param session 可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("来自客户端的消息:" + message);
		JSONObject json = JSONObject.parseObject(message); 
		String type = (String) json.get("type");
		String title = (String) json.get("title");	
		String version = (String) json.get("version");	
		int id = (int) json.get("id");

		switch(type) {
		case "register": register(session,title,version,id);break;
		case "unregister":unregister(session,title,version);break;
		case "change":
		case "add":
		case "delete":			
			change(message,title,version,session);
			break;
		default: System.out.println("type="+type+"==");
		}

	}
	void register(Session session,String title,String version,int id){
		boolean flag =false;
		for(LSP lsp: WebSocket.lspSet) {
			if(title.equals(lsp.getTitel())&& version.contentEquals(lsp.getVersion())) {
				System.out.println(" title.equals(lsp.getTitel()) ");
				lsp.addSession(session);
				try {
					int len = lsp.getMessagesLen();
					for(int i =id;i<len;i++) {
						session.getBasicRemote().sendText(lsp.getMessage(i));
					}						
					System.out.println("注册时发送最新版");
				} catch (IOException e) {
					e.printStackTrace();
				}
				flag = true;
				break;
			}else {
				System.out.print(title);
				System.out.println(" not equals ");
				System.out.print(lsp.getTitel());
			}
		}
		if(!flag) {
			LSP lsp = new LSP(title,version,session);
			WebSocket.lspSet.add(lsp);
		}
	}
	/**
	 * @param session
	 * @param title
	 * @param version
	 */
	void unregister(Session session,String title,String version){
		for(LSP lsp: WebSocket.lspSet) {
//			System.out.println(title+version);
			if(title.equals(lsp.getTitel()) && version.contentEquals(lsp.getVersion())) {
				lsp.removeSession(session);
				if(lsp.getSessionSet().isEmpty()) {
					WebSocket.lspSet.remove(lsp);
					System.out.println(lsp.getTitel()+"无连接,删除该元素");
				}
			}
		}
	}
	void unregister(Session session){
		for(LSP lsp: WebSocket.lspSet) {			
				lsp.removeSession(session);	
				if(lsp.getSessionSet().isEmpty()) {
					WebSocket.lspSet.remove(lsp);
					System.out.println(lsp.getTitel()+"无连接,删除该元素");
				}
		}
	}


	//不会给发起者发送修改信息
	void change(String message,String title,String version,Session session){
		System.out.println("change project" + "," + message + "," + title + "," + version);
		System.out.println(WebSocket.lspSet.size());
		for(LSP lsp: WebSocket.lspSet) {
			System.out.println(title+version);
			if(title.equals(lsp.getTitel()) && version.contentEquals(lsp.getVersion())) {
				System.out.println("==========================");
				//给信息编号
				StringBuffer Str=new StringBuffer(message);  
				Pattern p = Pattern.compile("\"id\":\\d*");
				Matcher m = p.matcher(Str);
				if(m.find()){
					System.out.println(m.group(0));					
					Str.replace(m.start(),m.end(),"\"id\":"+lsp.getId());
					message = Str.toString();
				}
				
				for(Session sess: lsp.getSessionSet()) {
//					if(sess.equals(session)) {
//						System.out.println("回复消息");	
//						JSONObject object = new JSONObject();
//					    //string
//					    object.put("type","result");
//					    object.put("id",lsp.getId());			
//						String str = object.toString();
//						try {
//							sess.getBasicRemote().sendText(str);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}else {
						try {
							System.out.println("send message: "+message);
							sess.getBasicRemote().sendText(message);
						} catch (IOException e) {
							e.printStackTrace();
						}
//					}						
					
				}
				lsp.addMessage(message);					
			}
			System.out.println("==========================");
		}
		
	}

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

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException{
		this.session.getBasicRemote().sendText(message);
		//this.session.getAsyncRemote().sendText(message);
	}

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
