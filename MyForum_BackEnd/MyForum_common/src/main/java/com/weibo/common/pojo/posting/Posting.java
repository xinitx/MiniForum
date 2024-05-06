package com.weibo.common.pojo.posting;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
public class Posting {
    private Integer id;
    private String title;
    private String content;
    private Integer record;
    private Integer auth;
    private LocalDateTime create_time;
}
