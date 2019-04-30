package com.rabbitmq.rabbitmqspring.entity;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/29 17:55
 */
public class Order {
    
    private String id;
    private String name;
    private String content;
    
    public Order() {
    }
    
    public Order(String id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}
