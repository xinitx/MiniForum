package com.weibo.common.pojo.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String salt;
    private String role;
    private String avatar;
    private String introduce;
    private LocalDateTime create_time;
}
