package com.weibo.posting.mapper;

import com.weibo.common.pojo.posting.Appendix;
import com.weibo.common.pojo.posting.Posting;
import com.weibo.common.pojo.posting.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostingMapper {

    @Select("select * from posting where auth not in (#{unlikeString}) limit #{offset}, #{pageSize}")

    List<Posting> selectPostingByPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize,@Param("unlikeString") String unlikeString);
    @Select("select * from posting where content  like CONCAT('%', #{searchCondition}, '%') or title like CONCAT('%', #{searchCondition}, '%') and auth not in (#{unlikeString}) limit #{offset}, #{pageSize}")

    List<Posting> selectPostingByCondition(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize,@Param("unlikeString") String unlikeString,@Param("searchCondition") String searchCondition);
    @Select("select * from posting where auth = #{userId} limit #{offset}, #{pageSize}")

    List<Posting> selectPostingByUserId(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize,@Param("userId") Integer userId);

    @Select("select * from posting where posting.auth in (#{likeUserString}) and posting.id not in(" +
            "select posting_id from posting_record where user_id = #{userId})")
    List<Posting> selectPostingNoRecordLike(@Param("likeUserString") String likeUserString,@Param("userId") Integer userId);
    @Select("select count(*) from posting_like where posting_id = #{id}")
    Integer selectLikeSum(Integer id);
    @Select("select count(*) from posting_forward where posting_id = #{id}")
    Integer selectForwardSum(Integer id);
    @Select("select id, content from posting_tag where posting_id = #{id}")
    List<Tag> selectTags(Integer id);
    @Insert("insert into posting(title, content, auth) values (#{title}, #{content},#{auth} )")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addPosting(Posting posting);

    @Insert("insert into posting_tag(posting_id, content) values (#{postingId}, #{content})")
    void addPostingTag(@Param("postingId") Integer postingId,@Param("content") String content);
    @Insert("insert into posting_appendix(posting_id,url, type) values (#{postingId}, #{fileUrl},#{fileType})")
    void addAppendix(@Param("postingId") Integer postingId,@Param("fileUrl") String fileUrl,@Param("fileType") String fileType);

    @Select("select * from posting where id = #{postingId}")
    Posting selectPostingByPostingId(Integer postingId);
    @Select("select * from posting_appendix where ownerId = #{postingId}")

    List<Appendix> selectPostingAppendix(Integer id);
}
