package com.rabbitmq.rabbitmqapi.api.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/3/25 15:07
 */
public class Producer4DirectExchange {
    public static void main(String[] args) throws Exception {
        
        // 1创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("mini1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        
        // 2创建Connection
        Connection connection = connectionFactory.newConnection();
        // 3创建Channel
        Channel channel = connection.createChannel();
        // 4声明
        String exchangeName = "test_direct_exchange";
        String routingKey = "test.direct";
        
        // 5发送
        String msg = "Hello World RabbitMQ 4 Direct Exchange Message 111 ... ";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        
        channel.close();
        connection.close();
    }
}
