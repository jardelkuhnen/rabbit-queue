package br.kuhnen.menssages.service.message;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.event.MessageEvent;
import br.kuhnen.menssages.interfaces.IProcessEvent;
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
    private final String QUEUE_NAME = "user-messages";
    private final String ROUTING_KEY = "user-messages-key";
    private final String EXCHANGE_NAME = "user-messages-exchange";

    @Autowired
    public PublishMessagesService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }


    public void enviarMsg(String menssagem) {
        MessageEvent event = new MessageEvent(EventType.SEND_USUARIO_MENSAGEM, menssagem);

        this.rabbitService.handleMessage(event.getEventType().name(), event, IProcessEvent.class.getDeclaredMethods()[0].toString());
    }
}
