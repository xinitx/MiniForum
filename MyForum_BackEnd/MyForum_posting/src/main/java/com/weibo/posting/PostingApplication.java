package com.weibo.posting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PostingApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostingApplication.class,args);
    }
}
