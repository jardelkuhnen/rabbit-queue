package br.kuhnen.menssages.interfaces;

import br.kuhnen.menssages.enuns.EventType;


public interface IEvent {

    EventType getType();

    String getMessage();

}
