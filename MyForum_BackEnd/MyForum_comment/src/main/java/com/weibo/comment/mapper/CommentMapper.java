package com.weibo.comment.mapper;

import com.weibo.common.pojo.comment.Comment;
import com.weibo.common.pojo.posting.Appendix;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("select count(*) from comment where posting_id = #{postingId} ")
    Integer selectCommentSum(Integer postingId);
    @Select("select * from comment where posting_id = #{postingId} limit #{offset}, #{pageSize}")
    List<Comment> selectCommentAll(@Param("postingId") Integer postingId,@Param("offset") Integer offset,@Param("pageSize") Integer pageSize);

    @Select("select user_id from comment_like where comment_id = #{commentId} ")
    List<String> selectCommentLikeSum(Integer commentId);
    @Select("select * from comment_appendix where ownerId = #{id} ")
    List<Appendix> selectCommentAppendix(Integer id);

    @Insert("insert into comment (auth, posting_id, content) values (#{auth}, #{posting_id},#{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addCommentBody(Comment comment);
    @Insert("insert into comment_appendix(ownerId, url, type) values (#{id}, #{url},#{type})")
    void addCommentAppendix(@Param("id") Integer id, @Param("url") String s, @Param("type") String s1);
}
