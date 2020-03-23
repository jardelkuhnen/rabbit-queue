package br.kuhnen.menssages.interfaces;

@FunctionalInterface
public interface ICallbackEvent {

    void handle(IEvent event) throws Exception;

}
