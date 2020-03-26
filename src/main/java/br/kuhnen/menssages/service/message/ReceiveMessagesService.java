package br.kuhnen.menssages.service.message;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.interfaces.IEvent;
import br.kuhnen.menssages.interfaces.IProcessEvent;
import br.kuhnen.menssages.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ReceiveMessagesService implements IProcessEvent {

    private final RabbitService rabbitService;

    @Autowired
    public ReceiveMessagesService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @PostConstruct
    public void listenMessages() {
//        this.receiveMessages();
        String handlerName = this.getClass().getName();
        System.out.println("Registrando o evento " + handlerName);

        this.rabbitService.registerQueue(EventType.SEND_USUARIO_MENSAGEM.name(), handlerName, this::processEvents, 2);
    }

    @Override
    public void processEvents(IEvent event) {
        System.out.println("Processando evento recebido. " + event.getType() + "Clazz: " + event.getClass());
        System.out.println(event.getMessage());
    }

}
