package com.weibo.common.pojo.user;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private SimpleUserInfo targetUser;
    private Boolean isLike = false;
    private Boolean isUnlike = false;
    private Integer likeCount;
    private List<SimpleUserInfo> likeList;
    private Integer unlikeCount;
    private List<SimpleUserInfo> unlikeList;
    private Integer fanCount;
    private List<SimpleUserInfo> fanList;
}
