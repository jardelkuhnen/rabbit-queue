package br.kuhnen.menssages.service;

import br.kuhnen.menssages.configuration.RabbitConfiguration;
import br.kuhnen.menssages.interfaces.ICallbackEvent;
import br.kuhnen.menssages.interfaces.IEvent;
import br.kuhnen.menssages.util.rabbit.EventConsumer;
import br.kuhnen.menssages.util.rabbit.EventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitService {

    @Autowired
    private RabbitConfiguration rabbitConfiguration;
    private final String EXCHANGE_TIPE = "topic";
    private final Boolean DURABLE = Boolean.TRUE;
    private final Boolean AUTO_DELETE = Boolean.TRUE;
    private final Boolean EXCLUSIVE = Boolean.FALSE;

    private Connection connection;

    public Connection getRabbitConnection() {

        try {

            if (this.connection != null) {
                return this.connection;
            }

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(rabbitConfiguration.getUser());
            factory.setPassword(rabbitConfiguration.getPassword());
            factory.setHost(rabbitConfiguration.getVhost());

            return this.connection = factory.newConnection();
        } catch (IOException e) {
            log.error("Error to create connection");
            e.printStackTrace();
        } catch (TimeoutException e) {
            log.error("Errot to create connection");
            e.printStackTrace();
        }

        return null;
    }


    public Channel createChannel() {
        try {
            return this.getRabbitConnection().createChannel();
        } catch (IOException e) {
            log.error("Error to create channel");
            e.printStackTrace();
        }

        return null;
    }

    public void declareExchange(String exchangeName) {

        Channel channel = null;
        try {
            channel = this.getRabbitConnection().createChannel();
            channel.exchangeDeclare(exchangeName, EXCHANGE_TIPE);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Boolean closeChannel(Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    public void registerQueue(String eventName, String handlerName, ICallbackEvent callback, Integer qtdConsumers) {

        Channel channel = null;

        try {
            channel = this.createChannel();

            String routingKey = eventName;
            String queueName = String.join(".", eventName, handlerName);

            this.declareExchange(eventName);
            channel.queueDeclare(queueName, DURABLE, EXCLUSIVE, AUTO_DELETE, new HashMap<>());

            channel.queueBind(queueName, eventName, routingKey);

            for (int i = 0; i < qtdConsumers; i++) {
                Consumer consumer = new EventConsumer(callback, handlerName, channel);

                channel.basicConsume(queueName, false, consumer);
            }

            channel = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(String eventName, IEvent event, String handlerName) {

        Channel channel = null;

        try {

            channel = this.createChannel();

            channel.exchangeDeclare(eventName, this.EXCHANGE_TIPE);

            ObjectMapper mapper = new ObjectMapper();
            String message = mapper.writeValueAsString(new EventPayload(event, handlerName));
            message = StringUtils.stripAccents(message);

            log.info("Message sended. " + "Time: " + LocalDateTime.now());

            channel.basicPublish(eventName, eventName, null, message.getBytes());


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.closeChannel(channel);
        }
    }
}
