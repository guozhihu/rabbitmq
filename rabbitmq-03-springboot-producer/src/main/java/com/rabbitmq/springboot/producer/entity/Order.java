package com.rabbitmq.springboot.producer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/30 17:22
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order implements Serializable {
    
    private String id;
    private String name;
}
