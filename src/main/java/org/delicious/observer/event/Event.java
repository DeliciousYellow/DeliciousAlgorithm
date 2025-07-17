package org.delicious.observer.event;

public interface Event {

    long getTimestamp();
    Object getSource();
    EventType getEventType();

}
