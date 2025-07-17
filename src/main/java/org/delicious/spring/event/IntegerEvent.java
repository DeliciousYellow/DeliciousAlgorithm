package org.delicious.spring.event;

import org.springframework.context.ApplicationEvent;

public class IntegerEvent extends ApplicationEvent {

    public IntegerEvent(Integer source) {
        super(source);
    }

    @Override
    public Integer getSource() {
        return (Integer) super.getSource();
    }
}
