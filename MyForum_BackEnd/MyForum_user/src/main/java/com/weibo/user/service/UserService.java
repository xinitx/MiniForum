package com.weibo.user.service;

import com.weibo.common.constants.HttpCodeEnum;
import com.weibo.common.pojo.user.SimpleUserInfo;
import com.weibo.common.pojo.user.UserDTO;
import com.weibo.common.utils.JwtUtils;
import com.weibo.common.constants.Result;
import com.weibo.user.mapper.UserMapper;
import com.weibo.common.pojo.user.User;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Value("${minio.accesskey}")
    private String accesskey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.bucket}")
    private String bucketName;
    public Result userLogin(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username == null){
            return Result.error(HttpCodeEnum.USERNAME_ERROR);
        }
        User buffer = userMapper.selectByUsername(username);
        if(buffer != null){
            String salt = buffer.getSalt();
            String targetPassword = buffer.getPassword();
            password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if(Objects.equals(password, targetPassword)){
                String token = JwtUtils.getToken(buffer.getId(),buffer.getRole());
                Date tokenExpired = JwtUtils.getClaimsBody(token).getExpiration();
                HashMap<String,Object> map = new HashMap<>();
                map.put("token",token);
                map.put("user", buffer);
                map.put("tokenExpired", tokenExpired);
                return Result.ok(HttpCodeEnum.SUCCESS, map);
            }
        }else{
            return Result.error(HttpCodeEnum.USERNAME_ERROR);
        }
        return Result.error(HttpCodeEnum.PASSWORD_ERROR);
    }

    public Result userRegister(User user) {
        if(user.getUsername() == null){
            return Result.error(HttpCodeEnum.USERNAME_ERROR);
        }
        if(user.getNickname() == null){
            return Result.error(HttpCodeEnum.NICKNAME_ERROR);
        }
        User verifyUsername = userMapper.selectByUsername(user.getUsername());
        User verifyNickname = userMapper.selectByNickname(user.getNickname());
        if(verifyUsername != null){
            return Result.error(HttpCodeEnum.USERNAME_ERROR);
        }else if(verifyNickname != null){
            return Result.error(HttpCodeEnum.NICKNAME_ERROR);
        }else {
            String salt = "ustc";
            user.setSalt(salt);
            user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
            user.setRole("normal");
            /*
            user.setAvatar("http:///127.0.0.1:9000/weibo-avatar/default.jpg");
            user.setIntroduce("还没有自我介绍");*/
            userMapper.addUser(user);
            user = userMapper.selectByUsername(user.getUsername());
            String token = JwtUtils.getToken(user.getId(),user.getRole());
            Date tokenExpired = JwtUtils.getClaimsBody(token).getExpiration();
            HashMap<String,Object> map = new HashMap<>();
            map.put("token",token);
            map.put("user", user);
            map.put("tokenExpired", tokenExpired);
            return Result.ok(HttpCodeEnum.SUCCESS, map);
        }
    }

    public Result userModifyAvatar(MultipartFile avatar, String avatarType, Integer userId) {
        if(avatar == null) return Result.error(HttpCodeEnum.INFO_REQUIRE);
        MinioClient minioClient = MinioClient.builder().credentials(accesskey, secretKey).endpoint(minioUrl).build();
        try {
            String fileName = avatar.getOriginalFilename();
            String[] split = fileName.split("\\.");
            String ext = split[split.length - 1];
            String newFileName = UUID.randomUUID()+ "." + ext;

            BufferedInputStream reader = new BufferedInputStream(avatar.getInputStream());
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(newFileName)
                    .contentType(avatarType)
                    .bucket(bucketName)
                    .stream(reader, reader.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            userMapper.updateAvatar(userId, minioUrl+"/"+bucketName+"/"+newFileName);
        } catch (Exception e) {
            return Result.error(HttpCodeEnum.SERVER_ERROR);
        }
        return Result.ok(HttpCodeEnum.SUCCESS);
    }

    public Result userGetLikeList(Integer userId) {
        return Result.ok(HttpCodeEnum.SUCCESS,userMapper.selectLikeList(userId));
    }

    public Result userGetUnlikeList(Integer userId) {
        return Result.ok(HttpCodeEnum.SUCCESS,userMapper.selectUnlikeList(userId));
    }

    public Result userGet(Integer userId) {
        return Result.ok(HttpCodeEnum.SUCCESS,userMapper.selectUserSimpleInfoByUserId(userId));
    }

    public Result userVisit(Integer sourceUserId, Integer targetUserId) {
        SimpleUserInfo targetUser = userMapper.selectUserSimpleInfoByUserId(targetUserId);
        List<String> likeIdList = userMapper.selectLikeList(targetUserId);
        String likeIdString = likeIdList.stream().collect(Collectors.joining(",","",""));
        List<SimpleUserInfo> likeList = userMapper.selectSimpleUserInfoByIdList(likeIdString);

        List<String> unlikeIdList = userMapper.selectUnlikeList(targetUserId);
        String unlikeIdString = unlikeIdList.stream().collect(Collectors.joining(",","",""));
        List<SimpleUserInfo> unlikeList = userMapper.selectSimpleUserInfoByIdList(unlikeIdString);

        List<String> fanIdList = userMapper.selectFanList(targetUserId);
        String fanIdString = fanIdList.stream().collect(Collectors.joining(",","",""));
        List<SimpleUserInfo> fanList = userMapper.selectSimpleUserInfoByIdList(fanIdString);
        UserDTO results = new UserDTO();
        results.setTargetUser(targetUser);
        results.setLikeCount(likeIdList.size());
        results.setLikeList(likeList);
        results.setUnlikeCount(unlikeIdList.size());
        results.setUnlikeList(unlikeList);
        results.setFanCount(fanIdList.size());
        results.setFanList(fanList);
        if (sourceUserId.equals(targetUserId)) return Result.ok(HttpCodeEnum.SUCCESS,results);

        String verifyUnlike = userMapper.selectUnlike(sourceUserId, targetUserId);
        if(!StringUtils.isEmpty(verifyUnlike)) results.setIsUnlike(true);
        String verifylike = userMapper.selectLike(sourceUserId, targetUserId);
        if(!StringUtils.isEmpty(verifylike)) results.setIsLike(true);
        return Result.ok(HttpCodeEnum.SUCCESS,results);
    }

    public Result userModifyAccount(String username, String passwordOld, String passwordNew, Integer userId) {
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(passwordOld) || StringUtils.isEmpty(passwordNew) || userId == null)
            return Result.error(HttpCodeEnum.INFO_REQUIRE);
        User user = userMapper.selectUserByUserId(userId);
        passwordOld = DigestUtils.md5DigestAsHex((passwordOld + user.getSalt()).getBytes());
        if(!user.getPassword().equals(passwordOld))
            return Result.error(HttpCodeEnum.PASSWORD_ERROR);
        passwordNew = DigestUtils.md5DigestAsHex((passwordNew + user.getSalt()).getBytes());
        userMapper.updataAccount(username, passwordNew, userId);
        return Result.ok(HttpCodeEnum.SUCCESS);
    }
    public Result userModifyInfo(String nickname, String introduce, Integer userId) {
        if(StringUtils.isEmpty(nickname) && StringUtils.isEmpty(introduce))
            return Result.error(HttpCodeEnum.INFO_REQUIRE);
        if(!StringUtils.isEmpty(nickname))
            userMapper.updataNickname(nickname, userId);
        if(!StringUtils.isEmpty(introduce))
            userMapper.updataIntroduce(introduce, userId);
        return Result.ok(HttpCodeEnum.SUCCESS);
    }

    public Result userLike(Integer sourceUserId, Integer targetUserId) {
        String likeRecord = userMapper.selectLike(sourceUserId, targetUserId);
        if(StringUtils.isEmpty(likeRecord)) {
            userMapper.addLike(sourceUserId, targetUserId);
            String unlikeRecord = userMapper.selectUnlike(sourceUserId, targetUserId);
            if(!StringUtils.isEmpty(unlikeRecord)){
                userMapper.deleteUnlike(sourceUserId, targetUserId);
            }
            return Result.ok(HttpCodeEnum.SUCCESS);
        }
        return Result.error(HttpCodeEnum.REPEAT_ERROR);
    }

    public Result userUnlike(Integer sourceUserId, Integer targetUserId) {
        String unlikeRecord = userMapper.selectUnlike(sourceUserId, targetUserId);
        if(StringUtils.isEmpty(unlikeRecord)) {
            userMapper.addUnlike(sourceUserId, targetUserId);
            String likeRecord = userMapper.selectLike(sourceUserId, targetUserId);
            if(!StringUtils.isEmpty(likeRecord)){
                userMapper.deleteLike(sourceUserId, targetUserId);
            }
            return Result.ok(HttpCodeEnum.SUCCESS);
        }
        return Result.error(HttpCodeEnum.REPEAT_ERROR);
    }
}
