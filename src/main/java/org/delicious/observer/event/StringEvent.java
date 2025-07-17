package org.delicious.observer.event;

public class StringEvent extends BaseEvent<String> {

    public StringEvent(String info) {
        super(info);
    }

    @Override
    public EventType getEventType() {
        return EventType.STRING;
    }
}
