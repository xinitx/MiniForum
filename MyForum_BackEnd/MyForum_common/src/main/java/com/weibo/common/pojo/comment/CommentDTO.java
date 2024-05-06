package com.weibo.common.pojo.comment;

import com.weibo.common.pojo.posting.Appendix;
import com.weibo.common.pojo.user.SimpleUserInfo;
import lombok.Data;

import java.util.List;

@Data
public class CommentDTO {
    private Comment comment;
    private SimpleUserInfo auth;
    private List<Appendix> files;
    private Integer likeSum;
    private Boolean isLike;
}
