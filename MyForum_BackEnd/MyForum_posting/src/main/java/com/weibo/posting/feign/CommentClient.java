package com.weibo.posting.feign;

import com.weibo.common.constants.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("commentService")
public interface CommentClient {
    @GetMapping("/count/{postingId}")
    public Result getCommentCount(@PathVariable("postingId") Integer postingId);
}
