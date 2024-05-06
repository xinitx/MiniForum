package com.weibo.common.pojo.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Friend {
    private String roomName;
    private Integer targetUserId;
    private Integer sourceUserId;
    private LocalDateTime create_time;
    private String nickname;
}
