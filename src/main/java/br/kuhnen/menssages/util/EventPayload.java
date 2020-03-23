package br.kuhnen.menssages.util;

import br.kuhnen.menssages.interfaces.IEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class EventPayload {

    private String className;
    private String handlerName;
    private String event;

    public EventPayload(IEvent event, String handlerName) {
        this.className = event.getClass().getName();
        this.handlerName = handlerName;
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.event = mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Erro ao executar o parsing ", e);
        }
    }

}
