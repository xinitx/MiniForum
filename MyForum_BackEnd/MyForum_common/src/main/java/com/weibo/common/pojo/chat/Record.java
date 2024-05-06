package com.weibo.common.pojo.chat;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Record {
    private Integer sourceUserId;
    private String content;
    private LocalDateTime create_time;
}
