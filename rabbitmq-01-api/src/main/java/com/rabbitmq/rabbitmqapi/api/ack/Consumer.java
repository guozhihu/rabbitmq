package com.rabbitmq.rabbitmqapi.api.ack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/5 17:31
 */
public class Consumer {
    
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
        
        String exchangeName = "test_ack_exchange";
        String queueName = "test_ack_queue";
        String routingKey = "ack.#";
        
        channel.exchangeDeclare(exchangeName, "topic", true, false, null);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);
        
        // 手工ack 必须要关闭自动ack autoAck = false
        channel.basicConsume(queueName, false, new MyConsumer(channel));
    }
}
