package br.kuhnen.menssages.service;

import br.kuhnen.menssages.configuration.RabbitConfiguration;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitService {

    @Autowired
    private RabbitConfiguration rabbitConfiguration;
    private Connection connection;

    private final Logger log = LoggerFactory.getLogger(RabbitService.class);

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
}
