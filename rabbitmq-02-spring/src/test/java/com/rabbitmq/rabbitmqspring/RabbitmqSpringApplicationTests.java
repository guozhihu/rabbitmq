package com.rabbitmq.rabbitmqspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.rabbitmqspring.entity.Order;
import com.rabbitmq.rabbitmqspring.entity.Packaged;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqSpringApplicationTests {
    
    @Test
    public void contextLoads() {
    }
    
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    /**
     * RabbitAdmin类的测试
     *
     * @throws Exception
     */
    @Test
    public void testRabbitAdmin() throws Exception {
        // 声明direct类型的Exchange
        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
        // 声明topic类型的Exchange
        rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
        // 声明fanout类型的Exchange
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));
        
        // 声明队列
        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));
        
        // 绑定方式一
        rabbitAdmin.declareBinding(new Binding("test.direct.queue",
            Binding.DestinationType.QUEUE,
            "test.direct", "direct", new HashMap<>()));
        
        // 绑定方式二
        rabbitAdmin.declareBinding(
            BindingBuilder
                .bind(new Queue("test.topic.queue", false))        //直接创建队列
                .to(new TopicExchange("test.topic", false, false))    //直接创建交换机 建立关联关系
                .with("user.#"));    //指定路由Key
        
        
        rabbitAdmin.declareBinding(
            BindingBuilder
                .bind(new Queue("test.fanout.queue", false))
                .to(new FanoutExchange("test.fanout", false, false)));
        
        //清空队列数据
        rabbitAdmin.purgeQueue("test.topic.queue", false);
    }
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Test
    public void testSendMessage() throws Exception {
        // 1.创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "信息描述");
        messageProperties.getHeaders().put("type", "自定义消息类型");
        Message message = new Message("Hello RabbitMQ".getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException { // 数据发送完后可继续处理
                System.err.println("------添加额外的设置------");
                message.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述"); // 会覆盖上面的desc键对应的值
                message.getMessageProperties().getHeaders().put("attr", "额外新加的属性");
                return message;
            }
        });
    }
    
    @Test
    public void testSendMessage2() throws Exception {
        // 1.创建消息
        MessageProperties messageProperties = new MessageProperties();
        // 设置发送的消息为文本类型
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq消息1234".getBytes(), messageProperties);
        
        rabbitTemplate.send("topic001", "spring.abc", message);
        
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send!");
        rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send!");
    }
    
    /**
     * 测试发送文本消息
     * @throws Exception
     */
    @Test
    public void testSendMessage4Text() throws Exception {
        //1 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq 消息1234".getBytes(), messageProperties);
        
        rabbitTemplate.send("topic001", "spring.abc", message);
        rabbitTemplate.send("topic002", "rabbit.abc", message);
    }
    
    /**
     * 测试发送json数据消息
     *
     * @throws Exception
     */
    @Test
    public void testSendJsonMessage() throws Exception {
        
        Order order = new Order();
        order.setId("001");
        order.setName("消息订单");
        order.setContent("描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);
        
        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);
        
        rabbitTemplate.send("topic001", "spring.order", message);
    }
    
    /**
     * 测试发送java对象消息
     *
     * @throws Exception
     */
    @Test
    public void testSendJavaMessage() throws Exception {
        
        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);
        
        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", "com.rabbitmq.rabbitmqspring.entity.Order");
        Message message = new Message(json.getBytes(), messageProperties);
        
        rabbitTemplate.send("topic001", "spring.order", message);
    }
    
    // 测试发送多个不同类型的java对象，这里同时发送了两个java对象，一个是Order对象，一个是Packaged对象
    @Test
    public void testSendMappingMessage() throws Exception {
        
        ObjectMapper mapper = new ObjectMapper();
        
        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述信息");
        
        String json1 = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json1);
        
        MessageProperties messageProperties1 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties1.setContentType("application/json");
        messageProperties1.getHeaders().put("__TypeId__", "order");
        Message message1 = new Message(json1.getBytes(), messageProperties1);
        rabbitTemplate.send("topic001", "spring.order", message1);
        
        Packaged pack = new Packaged();
        pack.setId("002");
        pack.setName("包裹消息");
        pack.setDescription("包裹描述信息");
        
        String json2 = mapper.writeValueAsString(pack);
        System.err.println("pack 4 json: " + json2);
        
        MessageProperties messageProperties2 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties2.setContentType("application/json");
        messageProperties2.getHeaders().put("__TypeId__", "packaged");
        Message message2 = new Message(json2.getBytes(), messageProperties2);
        rabbitTemplate.send("topic001", "spring.pack", message2);
    }
    
    // 测试发送png文件
    @Test
    public void testSendExtConverterPNGMessage() throws Exception {
        byte[] body = Files.readAllBytes(Paths.get("d:/images", "Capture001.png"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("image/png");
        messageProperties.getHeaders().put("extName", "png");
        Message message = new Message(body, messageProperties);
        rabbitTemplate.send("", "image_queue", message);
    }
    
    // 测试发送pdf文件
    @Test
    public void testSendExtConverterPDFMessage() throws Exception {
        byte[] body = Files.readAllBytes(Paths.get("d:/images", "深入浅出MySQL_数据库开发、优化与管理维护_第2版.pdf"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/pdf");
        Message message = new Message(body, messageProperties);
        rabbitTemplate.send("", "pdf_queue", message);
    }
}
