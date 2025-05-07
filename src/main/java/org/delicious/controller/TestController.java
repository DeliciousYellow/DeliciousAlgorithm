package org.delicious.controller;

import org.delicious.spring.annotation.DeliciousRequestBody;
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

    @PostMapping("/returnOriginalUseDeliciousAnnotation")
    public Map<String, String> returnOriginalUseDeliciousAnnotation(@DeliciousRequestBody Map<String, String> json) {
        return json;
    }

}
