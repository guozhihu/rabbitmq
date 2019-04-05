package com.rabbitmq.rabbitmqapi.api.ack;

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
        System.err.println("body: " + new String(body));
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (0 == (Integer) properties.getHeaders().get("num")) {
            // 手动设置为消费失败，让消息重新回到队列中，会被扔到队列的最尾端
            // multiple: 是否支持批量消费
            // requeue: 是否重新扔回队列
            channel.basicNack(envelope.getDeliveryTag(), false, true);
        } else {
            // multiple: 如果为false表示不支持批量消息推送到消费端
            // 手动ack，设置为消费成功
            channel.basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
