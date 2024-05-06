package com.weibo.comment.controller;

import com.weibo.comment.service.CommentService;
import com.weibo.common.constants.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class CommentControllerV1 {
    @Autowired
    CommentService commentService;
    @PostMapping("/add")
    public Result addCommentBody(@RequestParam("postingId") Integer postingId,
                                @RequestParam("userId")Integer userId,
                                @RequestParam("content")String content,
                                 @RequestParam("files")List<MultipartFile> files,
                                 @RequestParam("types")List<String> types){
        return commentService.addCommentBody(postingId, userId, content,files,types);
    }
    @GetMapping("/all/{postingId}")
    public Result getCommentAll(@PathVariable("postingId") Integer postingId,
                                @RequestParam("userId")Integer userId,
                                @RequestParam("page")Integer page){
        return commentService.getCommentAll(postingId, userId, page);
    }
    @GetMapping("/count/{postingId}")
    public Result getCommentCount(@PathVariable("postingId") Integer postingId){
        return commentService.getCommentCount(postingId);
    }
}
