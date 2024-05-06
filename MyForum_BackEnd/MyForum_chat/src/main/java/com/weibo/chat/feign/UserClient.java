package com.weibo.chat.feign;

import com.weibo.common.constants.Result;
import com.weibo.common.pojo.user.SimpleUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("userService")
public interface UserClient {
    public int a = 1;
    @GetMapping("/likeList/{userId}")
    public Result userGetLikeList(@PathVariable("userId") Integer userId);
    @GetMapping("/unlikeList/{userId}")
    public Result userGetUnlikeList(@PathVariable("userId") Integer userId);
    @GetMapping("/getUser/{userId}")
    public Result<SimpleUserInfo> userGet(@PathVariable("userId") Integer userId);
}