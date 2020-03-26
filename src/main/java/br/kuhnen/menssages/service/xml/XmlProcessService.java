package br.kuhnen.menssages.service.xml;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.interfaces.IEvent;
import br.kuhnen.menssages.interfaces.IProcessEvent;
import br.kuhnen.menssages.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class XmlProcessService implements IProcessEvent {

    private final RabbitService rabbitService;
    private final Integer QTD_CONSUMERS = 2;

    @Autowired
    public XmlProcessService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @PostConstruct
    public void registerConsumers() {
        String handlerName = this.getClass().getName();
        log.info("Registering the event: " + handlerName);

        this.rabbitService.registerQueue(EventType.SEND_XML.name(), handlerName, this::processEvents, QTD_CONSUMERS);
    }

    @Override
    public void processEvents(IEvent event) {
        log.info("Received event: " + event.getType());
        log.debug(event.getMessage());
    }
}
