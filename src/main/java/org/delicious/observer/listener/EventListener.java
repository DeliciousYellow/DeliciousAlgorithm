package org.delicious.observer.listener;

import org.delicious.observer.event.Event;

public interface EventListener {

    String printFormat = "%s收到来自%s时间的%s消息 内容为 %s";

    void handleEvent(Event event);

}
