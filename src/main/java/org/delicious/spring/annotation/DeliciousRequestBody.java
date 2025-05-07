package org.delicious.spring.annotation;

import java.lang.annotation.*;

/**
 * @author huangcan
 * Date: 2025/5/6
 * Time: 19:54
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeliciousRequestBody {
}
