package org.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("aa.yml")
public class test {
    @Value("${aa}")
    public static String ddd;
    public static void main(String[] args) {

        SpringApplication.run(test.class, "command");
        System.out.println(ddd);
    }
}
