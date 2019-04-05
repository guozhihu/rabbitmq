package com.rabbitmq.rabbitmqapi.api.returnlistener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/5 14:43
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
        
        String exchangeName = "test_return_exchange";
        String routingKey = "return.#";
        String queueName = "test_return_queue";
        
        channel.exchangeDeclare(exchangeName, "topic", true, false, null);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);
        
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        
        channel.basicConsume(queueName, true, queueingConsumer);
        
        while (true) {
            
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.err.println("消费者: " + msg);
        }
    }
}
