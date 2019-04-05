package com.rabbitmq.rabbitmqapi.api.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/5 13:03
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
        
        String exchangeName = "test_confirm_exchange";
        String routingKey = "confirm.#";
        String queueName = "test_confirm_queue";
        
        // 4声明交换机和队列 然后进行绑定设置, 最后指定路由Key
        channel.exchangeDeclare(exchangeName, "topic", true);
        channel.queueDeclareNoWait(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);
        
        // 5创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, queueingConsumer);
        
        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            
            System.err.println("消费端：" + msg);
        }
    }
}
