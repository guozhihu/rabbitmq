package com.rabbitmq.rabbitmqspring.adapter;

import com.rabbitmq.rabbitmqspring.entity.Order;
import com.rabbitmq.rabbitmqspring.entity.Packaged;

import java.io.File;
import java.util.Map;

/**
 * Author: zhihu
 * Description: Delegate委托对象：实际真实的委托对象，用于处理消息
 * Date: Create in 2019/4/29 17:16
 */
public class MessageDelegate {
    
    // 默认的监听方法名称，处理的数据是二进制数据
    public void handleMessage(byte[] messageBody) {
        System.err.println("默认方法，消息内容：" + new String(messageBody));
    }
    
    // 自定义监听方法名称，处理的数据同样是二进制数据
    public void consumeMessage(byte[] messageBody) {
        System.err.println("字节数组方法，消息内容：" + new String(messageBody));
    }
    
    // 自定义监听方法名称，处理的数据是文本数据
    public void consumeMessage(String messageBody) {
        System.err.println("字符串方法，消息内容：" + messageBody);
    }
    
    // 自定义监听方法名称，处理的数据是文本数据
    public void method1(String messageBody) {
        System.err.println("method1 收到消息内容:" + new String(messageBody));
    }
    
    // 自定义监听方法名称，处理的数据是文本数据
    public void method2(String messageBody) {
        System.err.println("method2 收到消息内容:" + new String(messageBody));
    }
    
    // 自定义监听方法名称，在这里我用于处理json数据
    public void consumeMessage(Map messageBody) {
        System.err.println("map方法, 消息内容:" + messageBody);
    }
    
    // 自定义监听方法名称，处理的数据是java对象数据
    public void consumeMessage(Order order) {
        System.err.println("order对象, 消息内容, id: " + order.getId() +
            ", name: " + order.getName() +
            ", content: " + order.getContent());
    }
    
    // 自定义监听方法名称，处理的数据是java对象数据
    public void consumeMessage(Packaged pack) {
        System.err.println("package对象, 消息内容, id: " + pack.getId() +
            ", name: " + pack.getName() +
            ", content: " + pack.getDescription());
    }
    
    // 自定义监听方法名称，处理的数据是png、pdf等文件
    public void consumeMessage(File file) {
        System.err.println("文件对象 方法, 消息内容:" + file.getName());
    }
    
}
