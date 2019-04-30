package com.rabbitmq.rabbitmqspring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.rabbitmqspring.adapter.MessageDelegate;
import com.rabbitmq.rabbitmqspring.convert.ImageMessageConverter;
import com.rabbitmq.rabbitmqspring.convert.PDFMessageConverter;
import com.rabbitmq.rabbitmqspring.convert.TextMessageConvert;
import com.rabbitmq.rabbitmqspring.entity.Order;
import com.rabbitmq.rabbitmqspring.entity.Packaged;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Author: zhihu
 * Description:
 * Date: Create in 2019/4/6 18:11
 */
@Configuration
@ComponentScan({"com.rabbitmq.rabbitmqspring.*"})
public class RabbitMQConfig {
    
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("mini1:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
    
    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }
    
    @Bean
    public Queue queue001() {
        return new Queue("queue001", true); //队列持久
    }
    
    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }
    
    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }
    
    @Bean
    public Queue queue002() {
        return new Queue("queue002", true); //队列持久
    }
    
    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }
    
    @Bean
    public Queue queue003() {
        return new Queue("queue003", true); //队列持久
    }
    
    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }
    
    @Bean
    public Queue queue_image() {
        return new Queue("image_queue", true); //队列持久
    }
    
    @Bean
    public Queue queue_pdf() {
        return new Queue("pdf_queue", true); //队列持久
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }
    
    @Bean
    public MessageDelegate messageDelegate() {
        return new MessageDelegate();
    }
    
    @Bean
    public TextMessageConvert textMessageConvert() {
        return new TextMessageConvert();
    }
    
    @Bean
    public Jackson2JsonMessageConverter jsonConvert() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public Jackson2JsonMessageConverter javaConvert() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        // 可以进行java对象的映射关系
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        javaTypeMapper.addTrustedPackages("com.rabbitmq.rabbitmqspring.entity"); // 配置信任指定package，SpringBoot 1.x不需要该设置，SpringBoot 2.x需要添加该设置
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        return jackson2JsonMessageConverter;
    }
    
    @Bean
    public Jackson2JsonMessageConverter manyJavaConvert() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

        Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
        idClassMapping.put("order", Order.class);
        idClassMapping.put("packaged", Packaged.class);

        javaTypeMapper.setIdClassMapping(idClassMapping);

        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        return jackson2JsonMessageConverter;
    }
    
    @Bean
    public ImageMessageConverter imageMessageConverter() {
        return new ImageMessageConverter();
    }
    
    @Bean
    public PDFMessageConverter pdfMessageConverter() {
        return new PDFMessageConverter();
    }
    
    /**
     * 简单消息监听容器（SimpleMessageListenerContainer）
     */
    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        // 设置监听的消息队列
        container.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
        // 设置当前消费者数量
        container.setConcurrentConsumers(1);
        // 设置最大消费者数量
        container.setMaxConcurrentConsumers(5);
        // 设置为不重回队列
        container.setDefaultRequeueRejected(false);
        // 设置自动确认模式
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setExposeListenerChannel(true);
        // 设置消费者标签生成策略
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });

//        container.setMessageListener(new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                String msg = new String(message.getBody());
//                System.err.println("------消费者：" + msg);
//            }
//        });
        
        /**
         1 适配器方式。默认是有自己的方法名字的：handleMessage
         // 可以自己指定一个方法的名字: consumeMessage
         // 也可以添加一个转换器: 从字节数组转换为String
         */
//        MessageListenerAdapter adapter = new MessageListenerAdapter(messageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage"); // 设置监听方法名称
//        adapter.setMessageConverter(textMessageConvert()); // 设置消息转换器
//        container.setMessageListener(adapter);
        
        /**
         * 2 适配器方式: 我们的队列名称 和 方法名称 也可以进行一一的匹配
         */
//        MessageListenerAdapter adapter = new MessageListenerAdapter(messageDelegate());
//        adapter.setMessageConverter(textMessageConvert());
//        Map<String, String> queueOrTagToMethodName = new HashMap<>();
//        queueOrTagToMethodName.put("queue001", "method1"); // 用method1方法名监听queue001队列
//        queueOrTagToMethodName.put("queue002", "method2"); // 用method2方法名监听queue002队列
//        adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
//        container.setMessageListener(adapter);
        
        // 1.1 支持json格式的转换器
//        MessageListenerAdapter adapter = new MessageListenerAdapter(messageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        adapter.setMessageConverter(jsonConvert());
//        container.setMessageListener(adapter);
        
        // 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        adapter.setMessageConverter(javaConvert());
//        container.setMessageListener(adapter);
        
        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        adapter.setMessageConverter(manyJavaConvert());
//        container.setMessageListener(adapter);
        
        // 1.4 ext convert
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");

        // 全局的转换器
        ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();
        convert.addDelegate("text", textMessageConvert());
        convert.addDelegate("html/text", textMessageConvert());
        convert.addDelegate("xml/text", textMessageConvert());
        convert.addDelegate("text/plain", textMessageConvert());

        convert.addDelegate("json", jsonConvert());
        convert.addDelegate("application/json", jsonConvert());

        convert.addDelegate("image/png", imageMessageConverter());
        convert.addDelegate("image", imageMessageConverter());

        convert.addDelegate("application/pdf", pdfMessageConverter());

        adapter.setMessageConverter(convert);
        container.setMessageListener(adapter);
        
        return container;
    }
}
