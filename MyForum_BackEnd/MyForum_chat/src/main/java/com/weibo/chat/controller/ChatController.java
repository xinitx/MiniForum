package com.weibo.chat.controller;

import com.weibo.chat.service.ChatService;
import com.weibo.common.constants.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;
    @GetMapping("/friend")
    public Result getFriendList(@RequestParam("userId") Integer userId,
                                @RequestParam("page") Integer page){
        return chatService.getFriendList(userId, page);
    }
    @GetMapping("/record")
    public Result getRecordList(@RequestParam("userId") Integer userId,
                                @RequestParam("page") Integer page,
                                @RequestParam(value = "targetUserId", required = false) Integer targetUserId,
                                @RequestParam(value = "roomName", required = false) String roomName){
        return chatService.getRecordList(userId, page, targetUserId, roomName);
    }
}
