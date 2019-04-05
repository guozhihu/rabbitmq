package com.rabbitmq.rabbitmqapi.api.returnlistener;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/5 14:43
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
        
        String exchange = "test_return_exchange";
        String routingKey = "return.save";
        String routingKeyError = "abc.save";
        
        String msg = "Hello RabbitMQ Return Message";
        
        // 监听一些不可路由的消息
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode,
                                     String replyText,
                                     String exchange,
                                     String routingKey,
                                     AMQP.BasicProperties properties,
                                     byte[] body) throws IOException {
                System.err.println("---------handle  return----------");
                System.err.println("replyCode: " + replyCode);
                System.err.println("replyText: " + replyText);
                System.err.println("exchange: " + exchange);
                System.err.println("routingKey: " + routingKey);
                System.err.println("properties: " + properties);
                System.err.println("body: " + new String(body));
            }
        });
        
        // mandatory：如果为true，则监听器会接收到路由不可达的消息，然后进行后续处理，如果为false，那么broker端自动删除该消息！
        channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());
        // channel.basicPublish(exchange, routingKeyError, false, null, msg.getBytes());
    }
}
