package com.weibo.chat.service;

import com.weibo.chat.feign.UserClient;
import com.weibo.chat.mapper.ChatMapper;
import com.weibo.common.constants.HttpCodeEnum;
import com.weibo.common.constants.Result;
import com.weibo.common.pojo.chat.Friend;
import com.weibo.common.pojo.chat.Record;
import com.weibo.common.pojo.chat.RecordDTO;
import com.weibo.common.pojo.user.SimpleUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    ChatMapper chatMapper;
    @Autowired
    private UserClient userClient;
    public Result getFriendList(Integer userId, Integer page) {
        Integer pageSize = 8;
        Integer offset = (page - 1) * pageSize;
        List<Friend> friends = chatMapper.selectFriends(userId, offset, pageSize);
        List<Friend> results = new ArrayList<>();
        Set<String> distinct = new HashSet<>();
        if (friends == null || friends.isEmpty()) return Result.ok(HttpCodeEnum.SUCCESS, friends);
        for (Friend friend: friends) {
            if(friend.getRoomName() == null && friend.getTargetUserId() != null && friend.getTargetUserId() != userId){
                friend.setNickname(((SimpleUserInfo)userClient.userGet(friend.getTargetUserId()).getData()).getNickname());
                String nickname = friend.getNickname();
                if(distinct.add(nickname)){
                    results.add(friend);
                }
            }
            if(friend.getRoomName() == null && friend.getSourceUserId() != null && friend.getSourceUserId() != userId){
                friend.setNickname(((SimpleUserInfo)userClient.userGet(friend.getSourceUserId()).getData()).getNickname());
                String nickname = friend.getNickname();
                if(distinct.add(nickname)){
                    results.add(friend);
                }
            }
        }
        distinct = new HashSet<>();
        for (Friend item: friends) {
            String roomName = item.getRoomName();
            if (roomName != null && distinct.add(roomName)){
                results.add(item);
            }
        }
        results = results.stream().sorted(Comparator.comparing(Friend::getCreate_time).reversed()).collect(Collectors.toList());
        return Result.ok(HttpCodeEnum.SUCCESS, results);
    }

    public Result getRecordList(Integer userId, Integer page, Integer targetUserId, String roomName) {
        Integer pageSize = 8;
        Integer offset = (page - 1) * pageSize;
        List<RecordDTO> results = new ArrayList<>();
        if(targetUserId != null){
            List<Record> records = chatMapper.selectRecordSingle(userId, targetUserId, offset, pageSize);
            for (Record record:records) {
                SimpleUserInfo simpleUserInfo = (SimpleUserInfo)userClient.userGet(record.getSourceUserId()).getData();
                RecordDTO recordDTO = new RecordDTO();
                recordDTO.setRecord(record);
                recordDTO.setAuth(simpleUserInfo);
                results.add(recordDTO);
            }
        }
        if(roomName != null){
            List<Record> records = chatMapper.selectRecordMultiple(roomName, offset, pageSize);
            for (Record record:records) {
                SimpleUserInfo simpleUserInfo = (SimpleUserInfo)userClient.userGet(record.getSourceUserId()).getData();
                RecordDTO recordDTO = new RecordDTO();
                recordDTO.setRecord(record);
                recordDTO.setAuth(simpleUserInfo);
                results.add(recordDTO);
            }
        }
        if(results.isEmpty() || results == null){
            return Result.error(HttpCodeEnum.NOPOSTING_ERROR);
        }
        return Result.ok(HttpCodeEnum.SUCCESS, results);
    }
}
