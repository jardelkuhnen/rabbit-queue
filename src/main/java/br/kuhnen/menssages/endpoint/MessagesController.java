package br.kuhnen.menssages.endpoint;

import br.kuhnen.menssages.service.message.PublishMessagesService;
import br.kuhnen.menssages.service.xml.PublishXmlService;
import br.kuhnen.menssages.service.message.ReceiveMessagesService;
import br.kuhnen.menssages.service.xml.ReceiveXmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/message")
public class MessagesController {

    private final PublishMessagesService publishMessagesService;
    private final ReceiveMessagesService receiveMessagesService;
    private final PublishXmlService publishXmlService;
    private final ReceiveXmlService receiveXmlService;

    @Autowired
    public MessagesController(PublishMessagesService publishMessagesService,
                              ReceiveMessagesService receiveMessagesService,
                              PublishXmlService publishXmlService,
                              ReceiveXmlService receiveXmlService) {
        this.publishMessagesService = publishMessagesService;
        this.receiveMessagesService = receiveMessagesService;
        this.publishXmlService = publishXmlService;
        this.receiveXmlService = receiveXmlService;
    }

    @PostMapping
    @RequestMapping("/user")
    public ResponseEntity enviarMensagem(@RequestBody String menssagem) {
        String retorno = this.publishMessagesService.enviarMensagem(menssagem);
        return ResponseEntity.ok(retorno);
    }

    @PostMapping
    @RequestMapping("/xml")
    public ResponseEntity postXml(@RequestParam("file") MultipartFile file) {

        this.publishXmlService.sendXml(file);
        System.out.println(file.getSize());

        return ResponseEntity.ok("");
    }

}
