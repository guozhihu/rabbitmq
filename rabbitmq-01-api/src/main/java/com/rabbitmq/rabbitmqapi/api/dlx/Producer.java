package com.rabbitmq.rabbitmqapi.api.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/5 18:47
 */
public class Producer {
    
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
        
        String exchange = "test_dlx_exchange";
        String routingKey = "dlx.save";
        
        String msg = "Hello RabbitMQ DLX Message";
        
        for (int i = 0; i < 1; i++) {
            // 设置消息的过期时间为10秒，10秒后消息没有被消费，会被发送到死信队列上
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2) // 设置发送的消息是否是持久化消息，1为非持久化消息，2为持久化消息，持久化消息是指那些没有被消费者消费的消息在服务器重启后是否还存在
                .contentEncoding("UTF-8") // 设置字符集编码
                .expiration("10000") // 设置过期时间，这里是10秒钟后过期
                .build();
            channel.basicPublish(exchange, routingKey, true, properties, msg.getBytes());
        }
    }
}
