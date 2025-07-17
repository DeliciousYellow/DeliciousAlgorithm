package org.delicious.spring.listener;

import org.delicious.spring.event.IntegerEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class DeliciousListener implements ApplicationListener<IntegerEvent> {

    @Override
    public boolean supportsAsyncExecution() {
        return false;
    }

    @Override
    public void onApplicationEvent(IntegerEvent event) {
        Integer source = event.getSource();
        System.out.println("自定义监听器监听到事件" + source);
    }
}
