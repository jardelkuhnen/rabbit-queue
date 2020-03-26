package br.kuhnen.menssages.service.xml;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.event.MessageEvent;
import br.kuhnen.menssages.interfaces.IProcessEvent;
import br.kuhnen.menssages.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class PublishXmlService {

    private final RabbitService rabbitService;

    @Autowired
    public PublishXmlService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    public void sendXmlFile(MultipartFile file) {

        try {
            String message = new String(file.getBytes(), StandardCharsets.UTF_8);

            MessageEvent event = new MessageEvent(EventType.SEND_XML, message);

            this.rabbitService.handleMessage(event.getEventType().name(), event, IProcessEvent.class.getDeclaredMethods()[0].toString());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
