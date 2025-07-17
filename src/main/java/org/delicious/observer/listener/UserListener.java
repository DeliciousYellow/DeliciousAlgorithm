package org.delicious.observer.listener;

import org.delicious.observer.event.Event;

import static java.util.Optional.ofNullable;

public class UserListener implements EventListener {

    private final String name;
    private Runnable customLogic;

    public UserListener(String name) {
        this.name = name;
    }

    public UserListener(String name,Runnable customLogic) {
        this.name = name;
        this.customLogic = customLogic;
    }

    @Override
    public void handleEvent(Event event) {
        String printContent = String.format(
                printFormat,
                this.getClass().getSimpleName() + name,
                event.getTimestamp(),
                event.getEventType(),
                event.getSource()
        );
        System.out.println(printContent);
        ofNullable(customLogic).ifPresent(Runnable::run);
        switch (event.getEventType()) {
            case STRING:
                System.out.println("执行STRING消息特定逻辑");
                break;
            case INTEGER:
                System.out.println("执行INTEGER消息特定逻辑");
                break;
            case UNKNOWN:
            default:
                System.out.println("未知的消息 抛弃");
        }
    }
}
