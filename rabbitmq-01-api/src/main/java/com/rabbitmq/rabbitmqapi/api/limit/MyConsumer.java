package com.rabbitmq.rabbitmqapi.api.limit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * Author: zhihu
 * Description: 自定义消费者
 * Date: Create in 2019/4/5 15:15
 */
public class MyConsumer extends DefaultConsumer {
    
    private Channel channel;
    
    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public MyConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }
    
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.err.println("-----------consume message----------");
        System.err.println("consumerTag: " + consumerTag);
        System.err.println("envelope: " + envelope);
        System.err.println("properties: " + properties);
        System.err.println("body: " + new String(body));
        
        // multiple: 如果为false表示不支持批量消息推送到消费端
        // 手动ack
        channel.basicAck(envelope.getDeliveryTag(), false);
    }
}
