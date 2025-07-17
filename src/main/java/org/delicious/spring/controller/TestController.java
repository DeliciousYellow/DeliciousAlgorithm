package org.delicious.spring.controller;

import jakarta.annotation.Resource;
import org.delicious.spring.annotation.DeliciousRequestBody;
import org.delicious.spring.event.IntegerEvent;
import org.delicious.spring.event.StringEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author huangcan
 * Date: 2025/5/6
 * Time: 19:15
 */
@RestController
public class TestController {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/returnOriginalUseDeliciousAnnotation")
    public Map<String, String> returnOriginalUseDeliciousAnnotation(@DeliciousRequestBody Map<String, String> json) {
        return json;
    }

    @PostMapping("/testPublish")
    public void testPublish() {
        applicationEventPublisher.publishEvent(new StringEvent("你好监听器"));
        applicationEventPublisher.publishEvent(new IntegerEvent(99999999));
    }

}
