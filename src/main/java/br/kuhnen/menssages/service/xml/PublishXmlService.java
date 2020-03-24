package br.kuhnen.menssages.service.xml;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.event.MessageEvent;
import br.kuhnen.menssages.service.RabbitService;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PublishXmlService {

    private final RabbitService rabbitService;
    private final String EXCHANGE_TIPE = "topic";
    private final String QUEUE_NAME = "xml-messages";
    private final String ROUTING_KEY = "xml-messages-key";
    private final String EXCHANGE_NAME = "xml-messages-exchange";

    @Autowired
    public PublishXmlService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    public void sendXml(MultipartFile file) {

        Channel channel = null;

        try {
            channel = this.rabbitService.createChannel();

            this.rabbitService.declareExchange(EXCHANGE_NAME, EXCHANGE_TIPE);

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            System.out.println("Mensagem enviada: " + file.getName());

            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, file.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.rabbitService.closeChannel(channel);
        }


    }

    public void enviarXml(MultipartFile file) {

        try {
            String menssagem = new String(file.getBytes(), StandardCharsets.UTF_8);

            MessageEvent event = new MessageEvent(EventType.SEND_XML, menssagem);

            this.rabbitService.handleMessage(QUEUE_NAME, EXCHANGE_NAME, EXCHANGE_TIPE, ROUTING_KEY, event, "listenMessageEvents");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
