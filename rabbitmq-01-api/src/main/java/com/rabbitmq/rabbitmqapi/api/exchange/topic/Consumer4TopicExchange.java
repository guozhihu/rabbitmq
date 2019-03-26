package com.rabbitmq.rabbitmqapi.api.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/3/25 15:33
 */
public class Consumer4TopicExchange {
    
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        
        connectionFactory.setHost("mini1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
    
        connectionFactory.setAutomaticRecoveryEnabled(true); // 设置允许自动重连
        connectionFactory.setNetworkRecoveryInterval(3000); // 设置每3秒钟重连一次
        Connection connection = connectionFactory.newConnection();
        
        Channel channel = connection.createChannel();
        // 声明
        String exchangeName = "test_topic_exchange";
        String exchangeType = "topic";
        String queueName = "test_topic_queue";
        //String routingKey = "user.*";
        String routingKey = "user.*";
        // 1声明交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        // 2声明队列
        channel.queueDeclare(queueName, false, false, false, null);
        // 3建立交换机和队列的绑定关系:
        channel.queueBind(queueName, exchangeName, routingKey);
        
        // durable 是否持久化消息
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, consumer);
        //循环获取消息
        while (true) {
            //获取消息，如果没有消息，这一步将会一直阻塞
            Delivery delivery = consumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("收到消息：" + msg);
        }
    }
}
