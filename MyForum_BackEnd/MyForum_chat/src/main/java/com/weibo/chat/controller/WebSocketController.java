package com.weibo.chat.controller;

import com.alibaba.fastjson.JSON;
import com.weibo.chat.feign.UserClient;
import com.weibo.chat.mapper.ChatMapper;
import com.weibo.common.constants.HttpCodeEnum;
import com.weibo.common.constants.Result;
import com.weibo.common.pojo.chat.Record;
import com.weibo.common.pojo.chat.RecordDTO;
import com.weibo.common.pojo.user.SimpleUserInfo;
import lombok.SneakyThrows;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/chat")
public class WebSocketController {
    /**
     * 连接会话池
     */
    private static ConcurrentHashMap<String, Session> SESSION_POOL = new ConcurrentHashMap<>();
    private static ChatMapper chatMapper;
    private static UserClient userClient;
    private ChatHandler chatHandler;
    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;
    private RedisTemplate redisTemplate;
    private static ApplicationContext applicationContext;
    private SimpleMessageListenerContainer container;
    private static String queueKey = "chatService";
    private static final String  exchangeName = "chatService";

    public static void setApplicationContext(ApplicationContext context){
        WebSocketController.applicationContext = context;
    }
    @OnOpen
    public void onOpen(Session session) throws IOException {
        // 判断客户端对象是否存在
        // userId
        if (SESSION_POOL.containsKey(session.getQueryString())) {
            CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "ID冲突，连接拒绝");
            session.getUserProperties().put("reason", closeReason);
            session.close();
            return;
        }
        rabbitAdmin = applicationContext.getBean(RabbitAdmin.class);
        redisTemplate =(RedisTemplate) applicationContext.getBean("redisTemplate");
        container = applicationContext.getBean(SimpleMessageListenerContainer.class);
        chatHandler = applicationContext.getBean(ChatHandler.class);
        Integer i = 1;
        while (!redisTemplate.opsForValue().setIfAbsent(queueKey+i, true)){
            i++;
        }
        queueKey = queueKey + i;
        Queue queue = new Queue(queueKey,true);
        rabbitAdmin.declareQueue(queue);
        container.addQueueNames(queueKey);
        //设置消息监听处理类
        container.setMessageListener(chatHandler);
        // 将客户端对象存储到会话池
        SESSION_POOL.put(session.getQueryString(), session);
        System.out.println("客户端（" + session.getQueryString() + "）：开启了连接");
    }

    @OnMessage
    public void onMessage(String msg, Session session) throws IOException {
        // 解析消息 ==>
        // sourceUserId=?&targetUserId=?&content=?
        // sourceUserId=?&targetRoomName=?&content=?
        rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.convertAndSend("chatService",null, msg);
        msgHandler(msg);
    }


    @OnClose
    public void onClose(Session session) {
        // 连接拒绝关闭会话
        Object reason = session.getUserProperties().get("reason");
        if (reason instanceof CloseReason) {
            CloseReason creason = (CloseReason) reason;
            if (creason.getCloseCode() == CloseReason.CloseCodes.CANNOT_ACCEPT) {
                System.out.println("拒绝客户（" + session.getQueryString() + "）：关闭连接");
                return;
            }
        }
        // 从会话池中移除会话
        SESSION_POOL.remove(session.getQueryString());
        System.out.println("客户端（" + session.getQueryString() + "）：关闭连接");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        SESSION_POOL.remove(session.getQueryString());
        System.out.println("客户端（" + session.getQueryString() + "）错误信息：" + throwable.getMessage());
    }
    public void msgHandler(String msg) throws IOException {
        String[] msgArr = msg.split("&", 3);
        String[] target;
        String[] source;
        if(msgArr.length > 1)
        {
            target = msgArr[1].split("=", 2);
            source = msgArr[0].split("=", 2);
            if(target.length > 1 && source.length > 1){
                chatMapper = WebSocketController.getChatMapper();
                userClient = WebSocketController.getUserClient();
                SESSION_POOL = WebSocketController.getSessionPool();
                RecordDTO recordDTO = new RecordDTO();
                Record record = new Record();
                record.setSourceUserId(Integer.valueOf(source[1]));
                record.setContent(msgArr[2].split("=", 2)[1]);
                SimpleUserInfo simpleUserInfo = (SimpleUserInfo)userClient.userGet(record.getSourceUserId()).getData();
                recordDTO.setAuth(simpleUserInfo);
                recordDTO.setRecord(record);
                if(target[0].equals("targetUserId")){
                    chatMapper.addSingleChat(source[1], target[1], msgArr[2].split("=", 2)[1]);
                    Session targetSession = SESSION_POOL.get(target[1]);
                    if (targetSession != null) {
                        try {
                            targetSession.getBasicRemote().sendText(JSON.toJSONString(recordDTO));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else if(target[0].equals("targetRoomName")){
                    chatMapper.addMultipleChat(source[1], target[1], msgArr[2].split("=", 2)[1]);
                    List<String> memberList = chatMapper.selectRoomMember(target[1]);
                    for (String member: memberList) {
                        Session targetSession = SESSION_POOL.get(member);
                        if (targetSession != null) {
                            try {
                                targetSession.getBasicRemote().sendText(JSON.toJSONString(recordDTO));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("消息格式错误");
        }
    }


    public static ConcurrentHashMap<String, Session> getSessionPool() {
        return SESSION_POOL;
    }

    public static  ChatMapper getChatMapper() {
        chatMapper = WebSocketController.applicationContext.getBean(ChatMapper.class);
        return chatMapper;
    }
    public static UserClient getUserClient() {
        userClient = applicationContext.getBean(UserClient.class);
        return userClient;
    }

}
