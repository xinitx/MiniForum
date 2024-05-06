package com.weibo.chat;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.bootstrap.BootstrapApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import javax.servlet.annotation.WebServlet;
import java.util.concurrent.locks.StampedLock;

@WebServlet
public class Test {
    Test(int a){
        System.out.println(a);
    }
    public static void main(String[] args) {

        StampedLock stampedLock = new StampedLock();
        long s1  = stampedLock.readLock();


    }

}
