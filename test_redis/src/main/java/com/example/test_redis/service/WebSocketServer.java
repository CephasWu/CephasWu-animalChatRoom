package com.example.test_redis.service;

import com.example.test_redis.model.ChatUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@ServerEndpoint("/{chatRoom}/{userName}")
public class WebSocketServer {

    private static UserService userService;

    @Autowired
    private void setUserService(UserService userService) {
        WebSocketServer.userService = userService;
    }

    private static int onlineCount;

    private Session session;

    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    private static StringBuffer log = new StringBuffer();

    private String room;


    // 當有新的 WebSocket 連線進來時，會呼叫這個方法
    @OnOpen
    public void onOpen(Session session, @PathParam("chatRoom") String room, @PathParam("userName") String userName) {
        userService.addFriend(session.getId(), userName);
        webSocketSet.add(this);     // 將新的 WebSocket 連線加入到 Set 中
        addOnlineCount();           // 在線數 +1
        this.room = room;
        try {
            this.session = session;
            System.out.println(log.append("窗口：").append(room).append("開始監聽---").append("有新連線加入！當前在線人數為")
                    .append(onlineCount));
            log.setLength(0);
            onMessage(log.append("System：").append(userName).append("加入聊天室").toString());
            log.setLength(0);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    // 當有 WebSocket 連線關閉時，會呼叫這個方法
    @OnClose
    public void onClose() {
        System.out.println(log.append("有一連線關閉！當前在線人數為：").append(getOnlineCount()));
        log.setLength(0);
        String userName = userService.getFriend(session.getId()).map(ChatUser::getName).get();
        onMessage(log.append("System：").append(userName).append("離開聊天室").toString());
        log.setLength(0);
        userService.deleteFriend(session.getId());
        webSocketSet.remove(this);  // 從 Set 中刪除
        subOnlineCount();           // 在線數 -1
    }

    // 當接收到客戶端訊息時，會呼叫這個方法
    @OnMessage
    public void onMessage(String message) {
        System.out.println(log.append("收到來自窗口：").append(room).append("來自客戶端的訊息：").append(message));
        log.setLength(0);
        try {
            for (WebSocketServer item : webSocketSet) {
                item.sendMessage(message);
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println("發生錯誤");
        error.printStackTrace();
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    //    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
//        return webSocketSet;
//    }
}
