package com.weibo.common.pojo.user;

import lombok.Data;

@Data
public class SimpleUserInfo {
    private Integer id;
    private String avatar;
    private String nickname;
    private String introduce;
}
