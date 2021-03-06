# RabbitMQ核心概念
## 生产者消费者模型构建
理解如下几个概念<br>
ConnectionFactory：获取连接工厂<br>
Connection：一个连接<br>
Channel：数据通信信道，可发送和接收消息<br>
Queue：具体的消息存储队列<br>
Producer & Consumer生产消费者<br>
在quickstart中编写了生产者消费者模型构建的代码<br>

## Virtual host-虚拟主机
虚拟地址，用于进行逻辑隔离，最上层的消息路由  
一个Virtual Host里面可以有若干个Exchange和Queue  
同一个Virtual Host里面不能有相同名称的Exchange或Queue  
## Exchange交换机属性详解
**相关代码参考exchange包**<br>
* Name  
交换机名称<br><br>
* Type  
交换机类型<br><br>
**Exchange在RabbitMQ消息中间件中的作用：**  
服务器发送消息不会直接发送到队列中（Queue），而是直接发送给交换机（Exchange），
然后根据确定的规则，RabbitMQ将会决定消息该投递到哪个队列。这些规则称为路由键
（routing key），队列通过路由键绑定到交换机上。消息发送到服务器端（broker），
消息也有自己的路由键（也可以是空），RabbitMQ也会将消息和消息指定发送的交换机
的绑定（binding，就是队列和交互机根据路由键映射的关系）的路由键进行匹配。如果
匹配的话，就会将消息投递到相应的队列。<br><br>
**Exchange的类型主要有四种，Direct Exchange、Topic Exchange、Fanout Exchange、
Headers Exchange，而常用的只有前三种。**<br><br>
**Direct Exchange**  
所有发送到Direct Exchange的消息都会被转发到RouteKey中指定的Queue。  
1.一般情况可以使用rabbitMQ自带的Exchange："" (该Exchange的名字为空字符串，下文称其为default Exchange)。<br>
2.这种模式下不需要将Exchange进行任何绑定(binding)操作<br>
3.消息传递时需要一个“RouteKey”，可以简单的理解为要发送到的队列名字。<br>
4.如果vhost中不存在RouteKey中指定的队列名，则该消息会被抛弃。<br><br>
**Topic Exchange**  
所有发送到Topic Exchange的消息被转发到所有关心RouteKey中指定Topic的Queue上  
1.这种模式较为复杂，简单来说，就是每个队列都有其关心的主题，所有的消息都带有一个“标题”(RouteKey)，Exchange会将消息转发到所有关注主题能与RouteKey模糊匹配的队列。<br>
2.这种模式需要RouteKey，也许要提前绑定Exchange与Queue。<br>
3.在进行绑定时，要提供一个该队列关心的主题，如“#.log.#”表示该队列关心所有涉及log的消息(一个RouteKey为”MQ.log.error”的消息会被转发到该队列)。<br>
4.“#”表示0个或若干个关键字，“*”表示一个关键字。如“log.*”能与“log.warn”匹配，无法与“log.warn.timeout”匹配；但是“log.#”能与上述两者匹配。<br>
5.同样，如果Exchange没有发现能够与RouteKey匹配的Queue，则会抛弃此消息。<br><br>
**Fanout Exchange**  
不处理路由键，只需要简单的将队列绑定到交换机上  
发送到交换机的消息都会被转发到与该交换机绑定的所有队列上  
Fanout交换机转发消息是最快的  
1.可以理解为路由表的模式<br>
2.这种模式不需要RouteKey<br>
3.这种模式需要提前将Exchange与Queue进行绑定，一个Exchange可以绑定多个Queue，一个Queue可以同多个Exchange进行绑定。<br>
4.如果接收到消息的Exchange没有与任何Queue绑定，则消息会被抛弃。<br><br>
**Headers Exchange**  
将消息中的headers与该Exchange相关联的所有Binging中的参数进行匹配，如果匹配上了，则发送到该Binding对应的Queue中。<br><br>  
* Durability  
是否需要持久化，true为持久化<br><br>
* 其他属性<br>
**Auto Delete**：当最后一个绑定到Exchange上的队列删除后，自动删除该Exchange  
**Internal**：当前Exchange是否用于RabbitMQ内部使用，默认为false  
**Arguments**：扩展参数，用于扩展AMQP协议自制定化使用  

## Binding-绑定
Exchange和Exchange、Queue之间的连接关系  
Binding中可以包含RoutingKey或者参数  

## Queue-消息队列
消息队列，实际存储消息数据  
Durability：是否持久化，Durable：是，Transient：否  
Auto delete：如选yes，代表当最后一个监听被移除之后，该Queue会自动被删除  

## Message-消息
服务器和应用程序之间传送的数据  
本质上就是一段数据，由Properties和Payload(Body)组成  
常用属性：delivery mode、headers(自定义属性)  
其他属性：content_type、content_encoding、priority、correlation_id、reply_to、
expiration、message_id、timestamp、type、user_id、app_id、cluster_id  

# 深入RabbitMQ高级特性
## Confirm确认消息
消息的确认，是指生产者投递消息后，如果Broker收到消息，则会给我们生产者一个应答。<br>
生产者进行接收应答，用来确定这条消息是否正常的发送到Broker，这种方式也是消息的可靠性投递的核心保障！<br>
**如何确认Confirm确认消息**  
第一步：在channel上开启确认模式：channel.confirmSelect()<br>
第二步：在channel上添加监听：addConfirmListener，监听成功和失败的返回结果，根据具体的结果对消息进行重新发送、或记录日志等后续处理！<br>

## Return消息机制
1.Return Listener用于处理一些不可路由的消息！  
2.我们的消息生产者，通过指定一个Exchange和RoutingKey，把消息送达到某一个队列中去，然后我们的消费者监听队列，进行消费处理操作！  
3.但是在某些情况下，如果我们在发送消息的时候，当前的exchange不存在或者指定的路由key路由不到，这个时候如果我们需要监听这种不可达的消息，就要使用Return Listener！<br><br>
**在基础API中有一个关键的配置项**  
mandatory：如果为true，则监听器会接收到路由不可达的消息，然后进行后续处理，如果为false，那么broker端自动删除该消息！<br>

## 自定义消费者使用
我们一般就是在代码中编写while循环，进行consumer.nextDelivery方法进行获取下一条消息，然后进行消费处理！<br>
但是我们使用自定义的Consumer更加的方便，解耦性更加的强，也是在实际工作中最常用的使用方式！<br>

## 消费端限流
* **什么是消费端的限流？**<br>
假设一个场景，首先，我们RabbitMQ服务器有上万条未处理的消息，我们随便打开一个消费者客户端，会出现下面情况：
巨量的消息瞬间全部推送过来，但是我们单个客户端无法同时处理这么多数据！

* **限流策略：**<br>
RabbitMQ提供了一种qos（服务质量保证）功能，即在非自动确认消息的前提下，如果一定数目的消息（通过基于consumer或者channel设置Qos的值）未被确认前，不进行消费新的消息。<br>
void BasicQos(uint prefetchSize, ushort prefetchCount, bool global);<br>
prefetchSize: 消费的单条消息的大小限制，一般设置为0，表示不限制<br>
prefetchCount：会告诉RabbitMQ不要同时给一个消费者推送多于N个消息，即一旦有N个消息还没有ack，则该consumer将block掉，直到有消息ack<br>
global：true/false是否将上面设置应用于channel，简单点说，就是上面限制是channel级别的还是consumer级别<br>

## 消费端ACK与重回队列
* **消费端的手工ACK和NACK**<br>
消费端进行消费的时候，如果由于业务异常我们可以进行日志的记录，然后进行补偿！<br>
如果由于服务器宕机等严重问题，那我们就需要手工进行ACK保障消费端消费成功！<br>
* **消费端的重回队列**<br>
消费端重回队列是为了对没有处理成功的消息，把消息重新递给Broker!<br>
一般我们在实际应用中，都会关闭重回队列，也就是设置为false<br>

## 死信队列
**死信队列：DLX, Dead-Letter-Exchange**<br>

利用DLX，当消息在一个队列中变成死信（dead message）之后，它能被重新publish到另一个Exchange，这个Exchange就是DLX。<br>

**消息变成死信有以下几种情况：**<br>
1.消息被拒绝（basic.reject/basic.nack）并且requeue=false<br>
2.消息TTL过期<br>
3.队列达到最大长度<br>

DLX也是一个正常的Exchange，和一般的Exchange没有区别，它能在任何的队列上被指定，实际上就是设置某个队列的属性。<br>
当这个队列中有死信时，RabbitMQ就会自动的将这个消息重新发布到设置的Exchange上去，进而被路由到另一个队列。<br>
可以监听这个队列中消息做相应的处理，这个特性可以弥补RabbitMQ3.0以前支持的immediate参数的功能。<br>

**死信队列设置：**<br>
首先需要设置死信队列的exchange和queue，然后进行绑定（举例如下）：<br>
Exchange：dlx.exchange<br>
Queue：dlx.queue<br>
RoutingKey：#<br>
然后我们进行正常声明交换机、队列、绑定，只不过我们需要在队列加上一个参数即可：arguments.put("x-dead-letter-exchange", "dlx.exchange") ;<br>
这样消息在过期、requeue、队列在达到最大长度时，消息就可以直接路由到死信队列！<br>


