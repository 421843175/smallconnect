package com.jupiter.smallconnect.mvc.controller;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ServerEndPoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端，
 * 注解的值将被用于监听用户连接的终端访问URL地址，客户端可以通过这个URL连接到websocket服务器端
 */
@ServerEndpoint("/websocket")
@Component
public class WebSocketController {
    private static int onlineCount=0;
    private static CopyOnWriteArrayList<WebSocketController> webSocketSet=new CopyOnWriteArrayList<WebSocketController>();
    private Session session;
    //这里要搞成static 加载一次
    private static Session[] sessions=new Session[10];


    public static Session[] getSessions() {
        return sessions;
    }

    @OnOpen
    public void onOpen(Session session){

        this.session=session;
        webSocketSet.add(this);//加入set中
        sessions[onlineCount]=session;
        System.out.println(Arrays.toString(sessions));

        addOnlineCount();
        System.out.println("有新连接加入！当前在线人数为"+getOnlineCount());
    }

    @OnClose
    public void onClose(){
        webSocketSet.remove(this);

        // remove session from sessions array
        List<Session> sessionList = new ArrayList<>(Arrays.asList(sessions));
        sessionList.remove(session);

        sessions = sessionList.toArray(new Session[10]);

        subOnlineCount();
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message,Session session){
        int index=-1;

        System.out.println("来自客户端的消息："+message);

        for(int i=0;i<sessions.length;i++)
        {
            if(sessions[i].equals(session))
            {
                System.out.println("kinlia");
                index=i;
                break;
            }
        }
//        群发消息
        for (WebSocketController item:webSocketSet){
            try {
                //返回给前端的数据
                //TODO TIP 调用自己的sendMessage方法
                item.sendMessage("发送人"+(index+1)+":"+message);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    @OnError
    public void onError(Session session,Throwable throwable){
        System.out.println("发生错误！");
        throwable.printStackTrace();
    }
    //   下面是自定义的一些方法
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount(){
        return onlineCount;
    }
    public static synchronized void addOnlineCount(){
        WebSocketController.onlineCount++;
    }
    public static synchronized void subOnlineCount(){
        WebSocketController.onlineCount--;
    }


}
