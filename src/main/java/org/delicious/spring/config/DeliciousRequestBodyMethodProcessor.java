package org.delicious.spring.config;

import org.delicious.spring.annotation.DeliciousRequestBody;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author huangcan
 * Date: 2025/5/6
 * Time: 19:52
 * 基于装饰器的
 */
public class DeliciousRequestBodyMethodProcessor implements HandlerMethodArgumentResolver {
    private RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;
    private final ApplicationContext applicationContext;

    public DeliciousRequestBodyMethodProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(DeliciousRequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        lazyPadding();
        Object o = requestResponseBodyMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        if (o instanceof Map) {
            ((LinkedHashMap<Object, Object>) o).put("DeliciousTime", String.valueOf(System.currentTimeMillis()));
        }
        return o;
    }

    private void lazyPadding() {
        if (Objects.isNull(requestResponseBodyMethodProcessor)) {
            applicationContext.getBean(RequestMappingHandlerAdapter.class).getArgumentResolvers().stream()
                    .filter(i -> i instanceof RequestResponseBodyMethodProcessor)
                    .findAny()
                    .map(i -> (RequestResponseBodyMethodProcessor) i)
                    .ifPresent(requestResponseBodyMethodProcessor -> this.requestResponseBodyMethodProcessor = requestResponseBodyMethodProcessor);
        }
    }
}
