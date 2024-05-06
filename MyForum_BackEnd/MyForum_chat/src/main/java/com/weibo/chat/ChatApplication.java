package com.weibo.chat;


import com.weibo.chat.controller.WebSocketController;
import org.apache.commons.configuration.beanutils.DefaultBeanFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.Scanner;

@EnableWebSocket
@EnableFeignClients
@SpringBootApplication
public class ChatApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ChatApplication.class, args);
        WebSocketController.setApplicationContext(context);

        Scanner scanner = new Scanner(System.in);

    }
    @Bean
    public ServerEndpointExporter serverEndpoint(){
        return new ServerEndpointExporter();
    }
    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        return container;
    }
}
