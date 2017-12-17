package com.mq.rabbitmq.ddl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Created by hadoop on 2017/12/17.
 */
public class Recv {

    public static void main(String[] args) throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.81");
        factory.setPort(5672);
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("exchangeA", "direct");
        channel.exchangeDeclare("exchangeB", "direct");

        Map<String, Object> queueArgs = new HashMap<String, Object>();
        queueArgs.put("x-dead-letter-exchange", "exchangeB");
        channel.queueDeclare("queueA", true, false, false, queueArgs);
        channel.queueDeclare("queueB", true, false, false, null);

        // 绑定路由
        channel.queueBind("queueA", "exchangeA", "");
        channel.queueBind("queueB", "exchangeB", "");
        System.out.println("ready to receive message");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                properties.getHeaders();
                System.out.println("received message:" + message + ",date:" + System.currentTimeMillis());
            }
        };
        channel.basicConsume("queueB", true, consumer);
    }


}
