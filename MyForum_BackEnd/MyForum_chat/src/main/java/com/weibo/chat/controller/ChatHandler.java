package com.weibo.chat.controller;


import com.alibaba.fastjson.JSON;
import com.weibo.chat.feign.UserClient;
import com.weibo.chat.mapper.ChatMapper;
import com.weibo.common.pojo.chat.Record;
import com.weibo.common.pojo.chat.RecordDTO;
import com.weibo.common.pojo.user.SimpleUserInfo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class ChatHandler implements MessageListener {
    private static ConcurrentHashMap<String, Session> SESSION_POOL;
    private ChatMapper chatMapper;
    private UserClient userClient;
    @Override
    public void onMessage(Message message) {
        String msg = null;
        try {
            msg = new String(message.getBody(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
}
