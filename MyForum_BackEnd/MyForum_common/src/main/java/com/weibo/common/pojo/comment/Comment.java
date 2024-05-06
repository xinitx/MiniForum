package com.weibo.common.pojo.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private String content;
    private Integer auth;
    private Integer posting_id;
    private LocalDateTime create_time;
}
