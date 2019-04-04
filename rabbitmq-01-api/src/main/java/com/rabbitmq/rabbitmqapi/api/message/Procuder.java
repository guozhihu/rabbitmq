package com.rabbitmq.rabbitmqapi.api.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/3/25 16:35
 */
public class Procuder {
    
    public static void main(String[] args) throws Exception {
        // 1创建一个ConnectionFactory, 并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("mini1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        
        // 2通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();
        
        // 3通过connection创建一个Channel
        Channel channel = connection.createChannel();
        
        Map<String, Object> headers = new HashMap<>();
        headers.put("my1", "111");
        headers.put("my2", "222");
        
        // 在这里设置如何去发送一个带有附加属性的消息
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
            .deliveryMode(2) // 设置发送的消息是否是持久化消息，1为非持久化消息，2为持久化消息，持久化消息是指那些没有被消费者消费的消息在服务器重启后是否还存在
            .contentEncoding("UTF-8") // 设置字符集编码
            .expiration("10000") // 设置过期时间，这里是10秒钟后过期
            .headers(headers) // 设置自定义属性
            .build();
        
        // 4通过Channel发送数据
        for (int i = 0; i < 5; i++) {
            String msg = "Hello RabbitMQ!";
            //1 exchange   2 routingKey
            channel.basicPublish("", "test001", properties, msg.getBytes());
        }
        
        // 5记得要关闭相关的连接
        channel.close();
        connection.close();
    }
}
