package br.kuhnen.menssages.endpoint;

import br.kuhnen.menssages.service.PublishMessagesService;
import br.kuhnen.menssages.service.ReceiveMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/message")
public class MessagesController {

    private final PublishMessagesService publishMessagesService;
    private final ReceiveMessagesService receiveMessagesService;

    @PostConstruct
    public void startReceiveMessages() {
        this.receiveMessagesService.receiveMessages();
    }

    @Autowired
    public MessagesController(PublishMessagesService publishMessagesService,
                              ReceiveMessagesService receiveMessagesService) {
        this.publishMessagesService = publishMessagesService;
        this.receiveMessagesService = receiveMessagesService;
    }

    @PostMapping
    public ResponseEntity enviarMensagem(@RequestBody String menssagem) {
        String retorno = this.publishMessagesService.enviarMensagem(menssagem);
        return ResponseEntity.ok(retorno);
    }

}
