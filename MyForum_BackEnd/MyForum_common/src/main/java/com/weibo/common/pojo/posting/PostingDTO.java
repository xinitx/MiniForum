package com.weibo.common.pojo.posting;

import com.weibo.common.pojo.user.SimpleUserInfo;
import com.weibo.common.pojo.user.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostingDTO {
    private Posting posting;
    private SimpleUserInfo auth;
    private Integer commentSum;
    private Integer likeSum;
    private Integer forwardSum;
    private List<Appendix> files;
    private List<Tag> tags;
}
