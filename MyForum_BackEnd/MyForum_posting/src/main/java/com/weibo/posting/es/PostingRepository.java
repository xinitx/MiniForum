package com.weibo.posting.es;

import com.weibo.common.pojo.posting.Posting;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PostingRepository extends ElasticsearchRepository<Posting, Integer> {
    List<Posting> findByContentLikeOrTitleLike(String searchCondition1, String searchCondition2);
}
