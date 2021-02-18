package com.example.consumer;

import com.netflix.loadbalancer.RetryRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import com.netflix.loadbalancer.IRule;

@SpringBootApplication(scanBasePackages = "com.example")
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    // 手动切换choose规则
    @Bean
    public IRule myRule() {
        //return new RoundRobinRule();
        //return new RandomRule();
        return new RetryRule();
    }
}
