package com.weibo.posting.controller;

import com.weibo.common.constants.Result;
import com.weibo.common.pojo.posting.Posting;
import com.weibo.common.pojo.posting.PostingDTO;
import com.weibo.posting.service.PostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class PostingControllerV1 {
    @Autowired
    PostingService postingService;
    @PostMapping("/add/appendix")
    public Result postingAddAppendix(@RequestParam("multipartFiles") List<MultipartFile> files,
                                   @RequestParam("type") List<String> fileType,
                                     @RequestParam("postingId") Integer postingId){
        return postingService.postingAddAppendix(files, fileType, postingId);
    }
    @PostMapping("/add")
    public Result postingAddContent(@RequestBody PostingDTO postingdto){
        return postingService.postingAddContent(postingdto);
    }
    @GetMapping("/all")
    public Result postingGetAll(@RequestParam("userId") Integer userId,
                                @RequestParam("page") Integer pageNum,
                                @RequestParam(value = "searchCondition", required = false) String searchCondition) {
        if(searchCondition == null)
        return postingService.postingGetAll(userId, pageNum);
        else return postingService.postingGetAllByCondition(userId, pageNum, searchCondition);
    }
    @GetMapping("/like")
    public Result postingGetLike(@RequestParam("userId") Integer userId) {
        return postingService.postingGetLike(userId);
    }
    @GetMapping("/user")
    public Result postingByUser(@RequestParam("userId") Integer userId,
                                 @RequestParam("page") Integer pageNum) {
        return postingService.postingGetUser(userId, pageNum);
    }
    @GetMapping("/id")
    public Result postingGetById(@RequestParam("postingId") Integer postingId) {
        return postingService.postingGetById(postingId);
    }
}
