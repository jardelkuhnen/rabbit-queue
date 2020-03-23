package br.kuhnen.menssages.service;

import br.kuhnen.menssages.configuration.RabbitConfiguration;
import br.kuhnen.menssages.event.MessageEvent;
import br.kuhnen.menssages.interfaces.ICallbackEvent;
import br.kuhnen.menssages.util.EventConsumer;
import br.kuhnen.menssages.util.EventPayload;
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
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class RabbitService {

    @Autowired
    private RabbitConfiguration rabbitConfiguration;

    private Connection connection;
    private final Charset UTF_8_CHAR_SET = Charset.forName("UTF-8");

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
            log.error("Erro ao criar connection");
            e.printStackTrace();
        } catch (TimeoutException e) {
            log.error("Erro ao criar connection");
            e.printStackTrace();
        }

        return null;
    }


    public Channel createChannel() {
        try {
            return this.getRabbitConnection().createChannel();
        } catch (IOException e) {
            log.error("Erro ao criar channel");
            e.printStackTrace();
        }

        return null;
    }

    public void declareExchange(String exchangeName, String exchangeTipe) {

        Channel channel = null;
        try {
            channel = this.getRabbitConnection().createChannel();
            channel.exchangeDeclare(exchangeName, exchangeTipe);

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

    public void registerQueue(String handlerName, ICallbackEvent callback, Integer quantidadeConsumers, String queueName, String exchangeName, String routingKey) {

        Channel channel = null;

        try {
            channel = this.createChannel();

            this.declareExchange(exchangeName, "topic");
            channel.queueDeclare(queueName, false, false, false, null);

            channel.queueBind(queueName, exchangeName, routingKey);

            for (int i = 0; i < quantidadeConsumers; i++) {
                Consumer consumer = new EventConsumer(callback, handlerName, channel);

                channel.basicConsume(queueName, true, consumer);
            }

            channel = null;

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void handleMessage(String queueName, String exchangeName, String exchangeTipe, String routingKey, MessageEvent event, String handlerName) {

        Channel channel = null;

        try {

            channel = this.createChannel();

            channel.exchangeDeclare(exchangeName, exchangeTipe);

//            channel.queueDeclare(queueName, false, false, false, null);

            ObjectMapper mapper = new ObjectMapper();
            String message = mapper.writeValueAsString(new EventPayload(event, handlerName));
            message = StringUtils.stripAccents(message);
            channel.basicPublish(exchangeName, routingKey, null, event.getMessage().getBytes(UTF_8_CHAR_SET));

            System.out.println("Mensagem enviada. " + "Horário: " + LocalDateTime.now());


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.closeChannel(channel);
        }


    }
}
