package org.delicious.spring.config;

import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author huangcan
 * Date: 2025/5/6
 * Time: 19:48
 */
@Component
public class DeliciousWebMvcConfigurer implements WebMvcConfigurer {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        DeliciousRequestBodyMethodProcessor deliciousRequestBodyMethodProcessor = new DeliciousRequestBodyMethodProcessor(applicationContext);
        resolvers.add(deliciousRequestBodyMethodProcessor);
    }
}
