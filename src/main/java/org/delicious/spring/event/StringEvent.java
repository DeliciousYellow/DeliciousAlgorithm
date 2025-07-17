package org.delicious.spring.event;

import org.springframework.context.ApplicationEvent;

public class StringEvent extends ApplicationEvent {

    public StringEvent(Object source) {
        super(source);
    }

    @Override
    public String getSource() {
        return super.getSource().toString();
    }
}
