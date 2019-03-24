package com.rabbitmq.rabbitmqapi.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/3/24 22:23
 */
public class Consumer {
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
        
        // 4声明（创建）一个队列
        String queueName = "test001";
        channel.queueDeclare(queueName, true, false, false, null);
        
        // 5创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        
        // 6设置Channel
        channel.basicConsume(queueName, true, queueingConsumer);
        
        while(true) {
            // 7获取消息
            Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.err.println("消费端：" + msg);
        }
    }
}
