package br.kuhnen.menssages.service.xml;

import br.kuhnen.menssages.service.RabbitService;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

@Service
public class PublishXmlService {

    private final RabbitService rabbitService;
    private final String EXCHANGE_TIPE = "direct";
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
}
