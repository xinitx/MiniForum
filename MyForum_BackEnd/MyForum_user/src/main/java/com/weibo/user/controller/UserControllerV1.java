package com.weibo.user.controller;

import com.weibo.common.constants.Result;
import com.weibo.common.pojo.user.User;
import com.weibo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserControllerV1 {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Result userLogin(@RequestBody User user){
        return userService.userLogin(user);
    }
    @PostMapping("/register")
    public Result userRegister(@RequestBody User user){
        return userService.userRegister(user);
    }
    @PostMapping("/modify/avatar/{userId}")
    public Result userModifyAvatar(@RequestParam("file") MultipartFile avatar,
                                   @RequestParam("type") String avatarType,
                                   @PathVariable("userId") Integer userId
                                   ){
        return userService.userModifyAvatar(avatar, avatarType, userId);
    }
    @PostMapping("/modify/account/{userId}")
    public Result userModifyAvatar(@RequestParam("username") String username,
                                   @RequestParam("passwordOld") String passwordOld,
                                   @RequestParam("password") String passwordNew,
                                   @PathVariable("userId") Integer userId
                                   ){
        return userService.userModifyAccount(username, passwordOld,passwordNew, userId);
    }
    @PostMapping("/modify/info/{userId}")
    public Result userModifyAvatar(@RequestParam(value = "nickname", required = false) String nickname,
                                   @RequestParam(value = "introduce",required = false) String introduce,
                                   @PathVariable("userId") Integer userId
                                   ){
        return userService.userModifyInfo(nickname, introduce, userId);
    }
    @PostMapping("/like")
    public Result userLike(@RequestParam("sourceUserId") Integer sourceUserId,
                                   @RequestParam("targetUserId") Integer targetUserId){
        return userService.userLike(sourceUserId, targetUserId);
    }
    @PostMapping("/unlike")
    public Result userUnlike(@RequestParam("sourceUserId") Integer sourceUserId,
                           @RequestParam("targetUserId") Integer targetUserId){
        return userService.userUnlike(sourceUserId, targetUserId);
    }


    @GetMapping("/visit")
    public Result userVisit(@RequestParam("sourceUserId") Integer sourceUserId,
                            @RequestParam("targetUserId") Integer targetUserId){
        return userService.userVisit(sourceUserId, targetUserId);
    }

    @GetMapping("/likeList/{userId}")
    public Result userGetLikeList(@PathVariable("userId") Integer userId){
        return userService.userGetLikeList(userId);
    }
    @GetMapping("/unlikeList/{userId}")
    public Result userGetUnlikeList(@PathVariable("userId") Integer userId){
        return userService.userGetUnlikeList(userId);
    }
    @GetMapping("/getUser/{userId}")
    public Result userGet(@PathVariable("userId") Integer userId){
        return userService.userGet(userId);
    }



}
