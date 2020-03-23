package br.kuhnen.menssages.service;

import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Service
public class ReceiveMessagesService {

    private final RabbitService rabbitService;
    private final String QUEUE_NAME = "user-messages";
    private final String ROUTING_KEY = "user-messages-key";
    private final String EXCHANGE_NAME = "user-messages-exchange";
    private final Charset UTF_8_CHAR_SET = Charset.forName("UTF-8");

    @Autowired
    public ReceiveMessagesService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    public void receiveMessages() {

        try {
            Channel channel = this.rabbitService.createChannel();

            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String mensagem = new String(body, UTF_8_CHAR_SET);
                    System.out.println("Mensagem recebida: " + mensagem + ". Horário: " + LocalDateTime.now());
                }
            };

            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
