package br.kuhnen.menssages.service.message;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.event.MessageEvent;
import br.kuhnen.menssages.interfaces.IProcessEvent;
import br.kuhnen.menssages.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublishMessagesService {

    private final RabbitService rabbitService;

    @Autowired
    public PublishMessagesService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }


    public void sendMessage(String message) {
        MessageEvent event = new MessageEvent(EventType.SEND_USUARIO_MENSAGEM, message);

        this.rabbitService.handleMessage(event.getEventType().name(), event, IProcessEvent.class.getDeclaredMethods()[0].toString());
    }
}
