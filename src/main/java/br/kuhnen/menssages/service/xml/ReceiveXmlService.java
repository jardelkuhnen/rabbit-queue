package br.kuhnen.menssages.service.xml;

import br.kuhnen.menssages.service.RabbitService;
import br.kuhnen.menssages.util.InfoXml;
import br.kuhnen.menssages.util.XmlExtractorUtil;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
public class ReceiveXmlService {

    private final RabbitService rabbitService;
    private final String QUEUE_NAME = "xml-messages";
    private final String ROUTING_KEY = "xml-messages-key";
    private final String EXCHANGE_NAME = "xml-messages-exchange";

    @Autowired
    public ReceiveXmlService(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    @PostConstruct
    public void listenMessages() {
        this.receiveXmls();
    }

    public void receiveXmls() {
        try {
            Channel channel = this.rabbitService.createChannel();

            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    InfoXml infoXml = XmlExtractorUtil.getInfoXml(body);

                    System.out.println("Mensagem recebida: " + infoXml.getChaveAcesso() + ". Horário: " + LocalDateTime.now());
                    System.out.println(infoXml.getXml());

                }
            };

            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}