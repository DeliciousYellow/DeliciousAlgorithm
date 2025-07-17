package org.delicious.observer.event;

public abstract class BaseEvent<T> implements Event {

    private final long timestamp;
    private final T data;

    public BaseEvent(T data) {
        this.timestamp = System.currentTimeMillis();
        this.data = data;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public T getSource() {
        return data;
    }

    @Override
    public EventType getEventType() {
        return EventType.UNKNOWN;
    }
}
