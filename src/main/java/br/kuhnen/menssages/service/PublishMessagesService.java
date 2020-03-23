package br.kuhnen.menssages.service;

import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@Service
public class PublishMessagesService {

    private final RabbitService rabbitService;
    private final String EXCHANGE_TIPE = "direct";
    private final String QUEUE_NAME = "user-messages";
    private final String ROUTING_KEY = "user-messages-key";
    private final String EXCHANGE_NAME = "user-messages-exchange";
    private final Charset UTF_8_CHAR_SET = Charset.forName("UTF-8");

    @Autowired
    public PublishMessagesService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    public String enviarMensagem(String mensagem) {

        Channel channel = null;

        try {

            channel = this.rabbitService.createChannel();

            this.rabbitService.declareExchange(EXCHANGE_NAME, EXCHANGE_TIPE);

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            System.out.println("Mensagem enviada. " + "Horário: " + LocalDateTime.now());

            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, mensagem.getBytes(UTF_8_CHAR_SET));

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

        return "";
    }

}
