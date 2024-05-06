package com.weibo.common.pojo.chat;

import com.weibo.common.pojo.user.SimpleUserInfo;
import lombok.Data;

@Data
public class RecordDTO {
    private Record record;
    private SimpleUserInfo auth;
}
