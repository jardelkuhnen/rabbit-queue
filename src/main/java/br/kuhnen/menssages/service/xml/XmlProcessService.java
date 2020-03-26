package br.kuhnen.menssages.service.xml;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.interfaces.IEvent;
import br.kuhnen.menssages.interfaces.IProcessEvent;
import br.kuhnen.menssages.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class XmlProcessService implements IProcessEvent {

    private final RabbitService rabbitService;
    private final String QUEUE_NAME = "xml-messages";
    private final String ROUTING_KEY = "xml-messages-key";
    private final String EXCHANGE_NAME = "xml-messages-exchange";

    @Autowired
    public XmlProcessService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @PostConstruct
    public void registerConsumers() {
        String handlerName = this.getClass().getName();
        System.out.println("Registrando o evento " + handlerName);

        this.rabbitService.registerQueue(EventType.SEND_XML.name(), handlerName, this::processEvents, 2);
    }

    @Override
    public void processEvents(IEvent event) {
        System.out.println("Recebido evento " + event.getType());
        System.out.println(event.getMessage());


    }
}
