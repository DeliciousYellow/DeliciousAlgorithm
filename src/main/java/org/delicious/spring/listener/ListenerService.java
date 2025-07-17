package org.delicious.spring.listener;

import org.delicious.spring.event.IntegerEvent;
import org.delicious.spring.event.StringEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ListenerService {
    @EventListener
    public void onStringEvent1(StringEvent event) {
        System.out.println("1号StringListener监听到 " + event.getSource());
    }

    @EventListener
    public void onStringEvent2(StringEvent event) {
        System.out.println("2号StringListener监听到 " + event.getSource());
    }

    @EventListener
    public void onIntegerEvent(IntegerEvent event) {
        System.out.println("监听到 " + event.getSource());
    }
}
