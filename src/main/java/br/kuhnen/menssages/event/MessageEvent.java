package br.kuhnen.menssages.event;

import br.kuhnen.menssages.enuns.EventType;
import br.kuhnen.menssages.interfaces.IEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent implements IEvent {

    private EventType eventType;
    private String message;

    @Override
    public EventType getType() {
        return eventType;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
