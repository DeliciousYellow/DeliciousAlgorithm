package org.delicious.observer;

import org.delicious.observer.event.IntegerEvent;
import org.delicious.observer.event.StringEvent;
import org.delicious.observer.listener.UserListener;
import org.delicious.observer.publisher.EventPublisher;
import org.delicious.util.SleepUtil;

public class Main {

    public static void main(String[] args) {
        UserListener aListener = new UserListener("A");
        UserListener bListener = new UserListener("B", () -> {
            SleepUtil.sleep(5000);
            System.out.println("睡了5秒");
        });
        UserListener cListener = new UserListener("C");
        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.addListener(aListener, IntegerEvent.class);
        eventPublisher.addListener(bListener, StringEvent.class);
        eventPublisher.addListener(cListener, StringEvent.class);

        new Thread(()-> eventPublisher.publish(new StringEvent("日出了"))).start();
        new Thread(()-> eventPublisher.publish(new StringEvent("日落了"))).start();
        new Thread(()-> eventPublisher.publish(new IntegerEvent(99999))).start();
    }

}
