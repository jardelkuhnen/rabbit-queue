package br.kuhnen.menssages.service.xml;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.interfaces.IEvent;
import br.kuhnen.menssages.interfaces.IProcessEvent;
import br.kuhnen.menssages.service.RabbitService;
import br.kuhnen.menssages.util.InfoXml;
import br.kuhnen.menssages.util.XmlExtractorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ReceiveXmlService implements IProcessEvent {

    private final RabbitService rabbitService;

    @Autowired
    public ReceiveXmlService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @PostConstruct
    public void listenMessages() {
//        this.receiveXmls();
        String handlerName = this.getClass().getName();
        System.out.println("Registrando o evento " + handlerName);

        this.rabbitService.registerQueue(EventType.SEND_XML.name(), handlerName, this::processEvents, 2);
    }

    @Override
    public void processEvents(IEvent event) {
        System.out.println("Recebido evento " + event.getClass() + " para processamento");

        InfoXml infoXml = XmlExtractorUtil.getInfoXml(event.getMessage().getBytes());
        System.out.println(infoXml);
    }

}
