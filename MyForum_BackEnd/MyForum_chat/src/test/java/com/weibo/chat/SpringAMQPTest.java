package com.weibo.chat;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringAMQPTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    void testSendMsg(){
        String queueName = "test.queue";
        String msg = "test";
        rabbitTemplate.convertAndSend(queueName, msg);
    }
    @Test
    @RabbitListener(queues="test.queue")
    public void listensimpleQueue(String msg){
        System.out.println("消费者收到了simple.queue的消息："+msg);
    }
}

