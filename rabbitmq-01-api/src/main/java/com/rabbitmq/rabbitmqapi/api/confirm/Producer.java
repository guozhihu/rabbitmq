package com.rabbitmq.rabbitmqapi.api.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/5 13:03
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
        
        // 4指定我们的消息投递模式：消息的确认模式
        channel.confirmSelect();
        
        String exchangeName = "test_confirm_exchange";
        String routingKey = "confirm.save";
        
        // 5发送一条消息
        String msg = "Hello RabbitMQ Send confirm message!";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        
        // 6添加一个确认监听
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("------ack!------");
            }
            
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.err.println("------no ack!------");
            }
        });
        
    }
}
