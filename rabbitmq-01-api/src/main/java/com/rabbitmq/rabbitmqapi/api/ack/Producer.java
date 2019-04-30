package com.rabbitmq.rabbitmqapi.api.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/5 17:32
 */
public class Producer {
    
    public static void main(String[] args) throws Exception {
        // 1创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("mini1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        
        // 2获取Connection
        Connection connection = connectionFactory.newConnection();
        
        // 3通过Connection创建一个新的Channel
        Channel channel = connection.createChannel();
        
        String exchange = "test_ack_exchange";
        String routingKey = "ack.save";
        
        for (int i = 0; i < 5; i++) {
            
            Map<String, Object> headers = new HashMap<String, Object>();
            headers.put("num", i);
            
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2) // 设置发送的消息是否是持久化消息，1为非持久化消息，2为持久化消息，持久化消息是指那些没有被消费者消费的消息在服务器重启后是否还存在
                .contentEncoding("UTF-8")
                .headers(headers) // 设置自定义属性
                .build();
            String msg = "Hello RabbitMQ ACK Message " + i;
            channel.basicPublish(exchange, routingKey, true, properties, msg.getBytes());
        }
    }
}
