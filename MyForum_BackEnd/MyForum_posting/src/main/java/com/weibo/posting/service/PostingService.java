package com.weibo.posting.service;

import com.alibaba.fastjson.JSON;
import com.weibo.common.constants.HttpCodeEnum;
import com.weibo.common.constants.Result;
import com.weibo.common.pojo.posting.Appendix;
import com.weibo.common.pojo.posting.PostingDTO;
import com.weibo.common.pojo.posting.Tag;
import com.weibo.common.pojo.user.SimpleUserInfo;
import com.weibo.common.pojo.user.User;
import com.weibo.posting.common.RedisUtils;
import com.weibo.posting.es.PostingRepository;
import com.weibo.posting.feign.CommentClient;
import com.weibo.posting.feign.UserClient;
import com.weibo.posting.mapper.PostingMapper;
import com.weibo.common.pojo.posting.Posting;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostingService {
    @Autowired
    private PostingMapper postingMapper;
    @Autowired
    private UserClient userClient;
    @Autowired
    private CommentClient commentClient;
    @Value("${minio.accesskey}")
    private String accesskey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.bucket}")
    private String bucketName;
    @Resource
    private RedisUtils redisUtils;
    @Autowired
    private PostingRepository postingRepository;
    public Result postingGetAll(Integer userId, Integer pageNum) {
        Integer pageSize = 8;
        Integer offset = (pageNum - 1) * pageSize;

        String unlikeString = (String) redisUtils.get("user_unlikeString" + userId);
        if(unlikeString == null){
            List<String> unlikeList = (List<String>) userClient.userGetUnlikeList(userId).getData();
            unlikeString = unlikeList.stream().collect(Collectors.joining(",","",""));
            redisUtils.set("user_unlikeString" + userId, unlikeString);
        }
        System.out.println("unlikeString:"+unlikeString);
        List<Posting> postings = postingMapper.selectPostingByPage(offset,pageSize,unlikeString);
        return PostingToDTO(postings);
    }

    private Result PostingToDTO(List<Posting> postings) {
        if(postings == null || postings.isEmpty()) return Result.error(HttpCodeEnum.NOPOSTING_ERROR);
        List<PostingDTO> results = new ArrayList<>();
        for (Posting posting: postings) {
            Integer targetUserId = posting.getAuth();
            SimpleUserInfo auth = (SimpleUserInfo) redisUtils.get("user_SimpleUserInfo"+targetUserId);
            if(auth == null){
                auth = (SimpleUserInfo) userClient.userGet(targetUserId).getData();
                redisUtils.set("user_SimpleUserInfo"+targetUserId,auth);
            }
            Integer postingId = posting.getId();
            Integer commentSum = (Integer) redisUtils.get("posting_commentSum"+postingId);
            if(commentSum == null){
                commentSum = (Integer) (commentClient.getCommentCount(postingId).getData());
                redisUtils.set("posting_commentSum"+postingId, commentSum);
            }
            Integer likeSum = (Integer) redisUtils.get("posting_likeSum"+postingId);
            if(likeSum == null){
                likeSum = postingMapper.selectLikeSum(postingId);
                redisUtils.set("posting_likeSum"+postingId, likeSum);
            }
            Integer forwardSum = (Integer) redisUtils.get("posting_forwardSum"+postingId);
            if(forwardSum == null){
                forwardSum = postingMapper.selectForwardSum(postingId);
                redisUtils.set("posting_forwardSum"+postingId, forwardSum);
            }
            List<Tag> tags = (List<Tag>) redisUtils.get("posting_tags"+postingId);
            if(tags == null){
                tags = postingMapper.selectTags(postingId);
                redisUtils.set("posting_tags"+postingId, tags);
            }
            List<Appendix> files = (List<Appendix>) redisUtils.get("posting_files"+postingId);
            if(files == null){
                files = postingMapper.selectPostingAppendix(postingId);
                redisUtils.set("posting_files"+postingId, files);
            }
            PostingDTO dto = new PostingDTO();
            dto.setFiles(files);
            dto.setPosting(posting);
            dto.setAuth(auth);
            dto.setCommentSum(commentSum);
            dto.setLikeSum(likeSum);
            dto.setForwardSum(forwardSum);
            dto.setTags(tags);
            results.add(dto);
        }
        return Result.ok(HttpCodeEnum.SUCCESS, results);
    }

    public Result postingGetAllByCondition(Integer userId, Integer pageNum, String searchCondition) {
        Integer pageSize = 8;
        Integer offset = (pageNum - 1) * pageSize;

        String unlikeString = (String) redisUtils.get("user_unlikeString" + userId);
        if(unlikeString == null){
            List<String> unlikeList = (List<String>) userClient.userGetUnlikeList(userId).getData();
            unlikeString = unlikeList.stream().collect(Collectors.joining(",","",""));
            redisUtils.set("user_unlikeString" + userId, unlikeString);
        }

        //List<Posting> postings = postingMapper.selectPostingByCondition(offset,pageSize,unlikeString,searchCondition);
        List<Posting> postings = postingRepository.findByContentLikeOrTitleLike(searchCondition, searchCondition);
        return PostingToDTO(postings);
    }

    public Result postingGetLike(Integer userId) {
        String likeUserString = (String) redisUtils.get("user_likeUserString" + userId);
        if(likeUserString == null){
            List<String> likeUserList = (List<String>)userClient.userGetLikeList(userId).getData();
            likeUserString = likeUserList.stream().collect(Collectors.joining(",","",""));
            redisUtils.set("user_likeUserString" + userId, likeUserString);
        }

        List<Posting> postings = postingMapper.selectPostingNoRecordLike(likeUserString, userId);
        List<PostingDTO> results = new ArrayList<>();
        if(postings.size() > 5){
            Collections.sort(postings, Comparator.comparingInt(Posting::getRecord));
            postings = postings.subList(0,5);
        }
        for (Posting posting: postings) {
            Integer targetUserId = posting.getAuth();
            SimpleUserInfo auth = (SimpleUserInfo) redisUtils.get("user_SimpleUserInfo"+targetUserId);
            if(auth == null){
                auth = (SimpleUserInfo) userClient.userGet(targetUserId).getData();
                redisUtils.set("user_SimpleUserInfo"+targetUserId,auth);
            }
            PostingDTO dto = new PostingDTO();
            dto.setPosting(posting);
            dto.setAuth(auth);
            results.add(dto);
        }
        return Result.ok(HttpCodeEnum.SUCCESS, results);
    }
    public Result postingGetUser(Integer userId, Integer pageNum) {
        Integer pageSize = 8;
        Integer offset = (pageNum - 1) * pageSize;
        List<Posting> postings = postingMapper.selectPostingByUserId(offset,pageSize, userId);
        return PostingToDTO(postings);
    }

    public Result postingAddContent(PostingDTO postingdto) {
        Posting posting = postingdto.getPosting();
        postingMapper.addPosting(posting);
        Integer postingId =  posting.getId();
        List<Tag> tags = postingdto.getTags();
        for (Tag tag: tags) {
            postingMapper.addPostingTag(postingId, tag.getContent());
        }
        return Result.ok(HttpCodeEnum.SUCCESS,postingId);
    }

    public Result postingAddAppendix(List<MultipartFile> files, List<String> fileType, Integer postingId) {
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
                    .contentType(fileType.get(i))
                    .bucket(bucketName)
                    .stream(reader, reader.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            postingMapper.addAppendix(postingId, minioUrl+"/"+bucketName+"/"+newFileName,fileType.get(i));
            i++;
            }
        } catch (Exception e) {
            return Result.error(HttpCodeEnum.SERVER_ERROR);
        }
        return Result.ok(HttpCodeEnum.SUCCESS);
    }

    public Result postingGetById(Integer postingId) {
        Posting posting = postingMapper.selectPostingByPostingId(postingId);
        List<Posting> postings = new ArrayList<>();
        postings.add(posting);
        return PostingToDTO(postings);
    }
}
