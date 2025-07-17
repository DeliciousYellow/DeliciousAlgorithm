package org.delicious.observer.publisher;

import org.delicious.observer.event.Event;
import org.delicious.observer.listener.EventListener;

import java.util.*;

import static java.util.Optional.ofNullable;

public class EventPublisher {

    private final Map<Class<? extends Event>, List<EventListener>> listenerMap;

    public EventPublisher() {
        this.listenerMap = new HashMap<>();
    }

    public void addListener(EventListener listener, Class<? extends Event> eventClazz) {
        listenerMap.computeIfAbsent(eventClazz, k -> new ArrayList<>())
                .add(listener);
    }

    public void publish(Event event) {
        ofNullable(this.listenerMap.get(event.getClass()))
                .orElse(Collections.emptyList()).parallelStream()
                .forEach(eventListener -> eventListener.handleEvent(event));
    }

}