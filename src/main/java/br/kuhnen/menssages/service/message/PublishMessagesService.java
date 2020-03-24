package br.kuhnen.menssages.service.message;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.event.MessageEvent;
import br.kuhnen.menssages.service.RabbitService;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class PublishMessagesService {

    private final RabbitService rabbitService;
    private final String EXCHANGE_TIPE = "topic";
    private final String QUEUE_NAME = "user-messages";
    private final String ROUTING_KEY = "user-messages-key";
    private final String EXCHANGE_NAME = "user-messages-exchange";

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

            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, mensagem.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.rabbitService.closeChannel(channel);
        }

        return "";
    }

    public void enviarMsg(String menssagem) {
        MessageEvent event = new MessageEvent(EventType.SEND_USUARIO_MENSAGEM, menssagem);

        this.rabbitService.handleMessage(QUEUE_NAME, EXCHANGE_NAME, EXCHANGE_TIPE, ROUTING_KEY, event, "listenMessageEvents");
    }
}
