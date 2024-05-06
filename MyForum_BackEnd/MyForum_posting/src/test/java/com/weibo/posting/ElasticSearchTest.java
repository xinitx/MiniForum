package com.weibo.posting;

import com.weibo.common.pojo.posting.Posting;
import com.weibo.common.utils.RedisUtils;
import com.weibo.posting.es.PostingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ElasticSearchTest {
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    PostingRepository postingRepository;
    @MockBean
    private RedisUtils redisUtils;
    @Test
    public void test(){
        List<Posting> postings = postingRepository.findByContentLikeOrTitleLike("7", "7");
        for (Posting p: postings) {
            System.out.println(p);
        }
    }
}
