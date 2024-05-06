package com.weibo.chat.mapper;

import com.weibo.common.pojo.chat.Friend;
import com.weibo.common.pojo.chat.Record;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMapper {
    @Select("SELECT sourceUserId, roomName, NULL AS targetUserId, create_time FROM multichat WHERE sourceUserId = #{userId} UNION SELECT sourceUserId, NULL AS roomName , targetUserId, create_time FROM singlechat WHERE sourceUserId = #{userId} or targetUserId=#{userId} GROUP BY sourceUserId, targetUserId ORDER BY create_time DESC limit #{offset}, #{pageSize}")
    List<Friend> selectFriends(@Param("userId") Integer userId,@Param("offset") Integer offset,@Param("pageSize") Integer pageSize);
    @Select("select sourceUserId, content, create_time from singlechat where sourceUserId = #{userId} or sourceUserId = #{targetUserId} order by create_time desc limit #{offset}, #{pageSize}")
    List<Record> selectRecordSingle(@Param("userId") Integer userId,@Param("targetUserId") Integer targetUserId,@Param("offset") Integer offset,@Param("pageSize") Integer pageSize);
    @Select("select sourceUserId, content, create_time from multichat where roomName = #{roomName} order by create_time desc limit #{offset}, #{pageSize}")
    List<Record> selectRecordMultiple(@Param("roomName") String roomName, @Param("offset") Integer offset,@Param("pageSize") Integer pageSize);

    @Insert("insert into singlechat (sourceUserId, targetUserId, content) values (#{sourceUserId}, #{targetUserId}, #{content})")
    Integer addSingleChat(@Param("sourceUserId") String sourceUserId,@Param("targetUserId") String targetUserId,@Param("content") String s2);

    @Select("select member from chatroom_member join chatroom on chatroom.id = chatroom_member.room_id where chatroom.roomName = #{roomName}")
    List<String> selectRoomMember(String roomName);

    @Insert("insert into multichat (sourceUserId,roomName, content) values (#{sourceUserId}, #{roomName}, #{content})")
    Integer addMultipleChat(@Param("sourceUserId") String sourceUserId,@Param("roomName") String roomName,@Param("content") String s2);
}
