package com.rabbitmq.springboot.consumer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/30 17:38
 */
@Configuration
@ComponentScan({"com.rabbitmq.springboot.consumer.*"})
public class MainConfig {
}
