package com.rabbitmq.rabbitmqapi.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/3/24 22:41
 */
public class Procuder {
    public static void main(String[] args) throws Exception {
        // 1创建一个ConnectionFactory，并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("mini1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        
        // 2通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();
        
        // 3通过connection创建一个Channel
        Channel channel = connection.createChannel();
        
        // 4通过Channel发送数据
        for (int i = 0; i < 5; i++) {
            String msg = "Hello RabbitMQ!";
            // 1 exchange   2 routingKey
            channel.basicPublish("", "test001", null, msg.getBytes());
        }
        
        // 5记得要关闭相关的连接
        channel.close();
        connection.close();
    }
}
