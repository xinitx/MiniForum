package com.weibo.user.mapper;

import com.weibo.common.pojo.user.SimpleUserInfo;
import com.weibo.common.pojo.user.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);
    @Select("select * from user where nickname = #{nickname}")
    User selectByNickname(String nickname);
    @Insert("insert into user (username, password, nickname,salt) values (#{username}, #{password}, #{nickname},#{salt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addUser(User user);
    @Update("update user set avatar = #{url} where id = #{userId}")
    void updateAvatar(@Param("userId") Integer userId,@Param("url") String url);
    @Select("select target_id from user_like where source_id = #{userId}")
    List<String> selectLikeList(Integer userId);
    @Select("select target_id from user_unlike where source_id = #{userId}")
    List<String> selectUnlikeList(Integer userId);
    @Select("select source_id from user_like where target_id = #{targetUserId}")
    List<String> selectFanList(Integer targetUserId);
    @Select("select id, avatar, nickname,introduce from user where id = #{userId}")
    SimpleUserInfo selectUserSimpleInfoByUserId(Integer userId);
    @Select("select * from user where id = #{userId}")
    User selectUserByUserId(Integer userId);
    @Select("select id, avatar, nickname, introduce from user where id in (#{likeIdString})")
    List<SimpleUserInfo> selectSimpleUserInfoByIdList(String likeIdString);
    @Update("update user set username = #{username}, password = #{passwordNew}  where id = #{userId}")
    void updataAccount(@Param("username") String username, @Param("passwordNew") String passwordNew,@Param("userId") Integer userId);

    @Update("update user set nickname = #{nickname}  where id = #{userId}")
    void updataNickname(@Param("nickname") String nickname, @Param("userId") Integer userId);
    @Update("update user set introduce = #{introduce}  where id = #{userId}")
    void updataIntroduce(@Param("introduce") String introduce, @Param("userId") Integer userId);

    @Select("select id from user_like where source_id = #{sourceUserId} and target_id = #{targetUserId}")
    String selectLike(@Param("sourceUserId") Integer sourceUserId,@Param("targetUserId") Integer targetUserId);

    @Insert("insert into user_like (source_id, target_id) values (#{sourceUserId}, #{targetUserId})")
    void addLike(@Param("sourceUserId") Integer sourceUserId,@Param("targetUserId") Integer targetUserId);
    @Select("select id from user_unlike where source_id = #{sourceUserId} and target_id = #{targetUserId}")
    String selectUnlike(@Param("sourceUserId") Integer sourceUserId,@Param("targetUserId") Integer targetUserId);
    @Insert("insert into user_unlike (source_id, target_id) values (#{sourceUserId}, #{targetUserId})")
    void addUnlike(@Param("sourceUserId") Integer sourceUserId,@Param("targetUserId") Integer targetUserId);
    @Delete("delete from user_unlike where source_id = #{sourceUserId} and target_id = #{targetUserId}")
    void deleteUnlike(@Param("sourceUserId") Integer sourceUserId,@Param("targetUserId") Integer targetUserId);
    @Delete("delete from user_like where source_id = #{sourceUserId} and target_id = #{targetUserId}")
    void deleteLike(@Param("sourceUserId") Integer sourceUserId,@Param("targetUserId") Integer targetUserId);
}
