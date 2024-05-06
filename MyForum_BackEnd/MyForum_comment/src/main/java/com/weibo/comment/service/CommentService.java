package com.weibo.comment.service;

import com.weibo.comment.feign.UserClient;
import com.weibo.comment.mapper.CommentMapper;
import com.weibo.common.constants.HttpCodeEnum;
import com.weibo.common.constants.Result;
import com.weibo.common.pojo.comment.Comment;
import com.weibo.common.pojo.comment.CommentDTO;
import com.weibo.common.pojo.posting.Appendix;
import com.weibo.common.pojo.user.SimpleUserInfo;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserClient userClient;
    @Value("${minio.accesskey}")
    private String accesskey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.bucket}")
    private String bucketName;
    public Result getCommentCount(Integer postingId) {
        return Result.ok(HttpCodeEnum.SUCCESS, commentMapper.selectCommentSum(postingId));
    }

    public Result getCommentAll(Integer postingId, Integer userId, Integer page) {
        Integer pageSize = 8;
        Integer offset = (page - 1) * pageSize;
        List<Comment> comments = commentMapper.selectCommentAll(postingId, offset, pageSize);
        if(comments == null || comments.isEmpty()) return Result.error(HttpCodeEnum.NOPOSTING_ERROR);
        List<CommentDTO> results = new ArrayList<>();
        for (Comment comment: comments) {
            SimpleUserInfo user = (SimpleUserInfo)userClient.userGet(comment.getAuth()).getData();
            List<Appendix> files = commentMapper.selectCommentAppendix(comment.getId());
            List<String> likeList = commentMapper.selectCommentLikeSum(comment.getId());
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setComment(comment);
            commentDTO.setAuth(user);
            commentDTO.setFiles(files);
            commentDTO.setLikeSum(likeList.size());
            commentDTO.setIsLike(likeList.contains(userId));
            results.add(commentDTO);
        }
        return Result.ok(HttpCodeEnum.SUCCESS, results);
    }

    public Result addCommentBody(Integer postingId, Integer userId, String content,
                                 List<MultipartFile> files,
                                 List<String> types) {
        if (postingId == null || userId == null|| StringUtils.isEmpty(content))
            return Result.error(HttpCodeEnum.INFO_REQUIRE);
        Comment comment = new Comment();
        comment.setAuth(userId);
        comment.setPosting_id(postingId);
        comment.setContent(content);
        commentMapper.addCommentBody(comment);
        if(files == null || files.isEmpty())
            return Result.ok(HttpCodeEnum.SUCCESS);
        else {
            MinioClient minioClient = MinioClient.builder().credentials(accesskey, secretKey).endpoint(minioUrl).build();
            try {
                Integer i = 0;
                for (MultipartFile file:files) {
                    String fileName = file.getOriginalFilename();
                    String[] split = fileName.split("\\.");
                    String ext = split[split.length - 1];
                    String newFileName = UUID.randomUUID()+ "." + ext;

                    BufferedInputStream reader = new BufferedInputStream(file.getInputStream());
                    PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                            .object(newFileName)
                            .contentType(types.get(i))
                            .bucket(bucketName)
                            .stream(reader, reader.available(), -1)
                            .build();
                    minioClient.putObject(putObjectArgs);
                    commentMapper.addCommentAppendix(comment.getId(), minioUrl+"/"+bucketName+"/"+newFileName,types.get(i));
                    i++;
                }
            } catch (Exception e) {
                return Result.error(HttpCodeEnum.SERVER_ERROR);
            }
            return Result.ok(HttpCodeEnum.SUCCESS);
        }
    }


}
