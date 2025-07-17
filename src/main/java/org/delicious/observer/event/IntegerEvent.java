package org.delicious.observer.event;

public class IntegerEvent extends BaseEvent<Integer> {

    public IntegerEvent(Integer info) {
        super(info);
    }

    @Override
    public EventType getEventType() {
        return EventType.INTEGER;
    }
}
