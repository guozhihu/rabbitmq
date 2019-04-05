package com.rabbitmq.rabbitmqapi.api.limit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/5 16:13
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
    
        String exchangeName = "test_qos_exchange";
        String queueName = "test_qos_queue";
        String routingKey = "qos.#";
    
        channel.exchangeDeclare(exchangeName, "topic", true, false, null);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);
    
        //1 限流方式  第一件事就是 autoAck设置为false
        // 假如有100条消息，则会一条一条的向消费端推送
        channel.basicQos(0, 1, false);
    
        channel.basicConsume(queueName, false, new MyConsumer(channel));
    }
}
