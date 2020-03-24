package br.kuhnen.menssages.interfaces;

@FunctionalInterface
public interface IProcessEvent {

    void processEvents(IEvent event);

}
