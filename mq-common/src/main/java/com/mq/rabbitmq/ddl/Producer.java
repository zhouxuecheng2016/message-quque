package com.mq.rabbitmq.ddl;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
/**
 * Created by hadoop on 2017/12/17.
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException { ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.81");
        factory.setPort(5672);
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("exchangeA", "direct");
        channel.exchangeDeclare("exchangeB", "direct");

        String message = "hello world!" + System.currentTimeMillis();
        // 设置延时属性
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        // 持久性 non-persistent (1) or persistent (2)
        AMQP.BasicProperties properties = builder.expiration("3000").deliveryMode(2).build();
        // routingKey =delay_queue 进行转发
        channel.basicPublish("exchangeA", "", properties, message.getBytes());
        System.out.println("sent message: " + message + ",date:" + System.currentTimeMillis());
        // 关闭频道和连接
        channel.close();
        connection.close();
    }

}
