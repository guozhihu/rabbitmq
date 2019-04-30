package com.rabbitmq.springboot.producer.config;
    
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.Configuration;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/30 17:06
 */
@Configuration
@ComponentScan({"com.rabbitmq.springboot.producer.*"})
public class MainConfig {
}
