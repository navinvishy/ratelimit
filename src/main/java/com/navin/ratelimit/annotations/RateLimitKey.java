package com.navin.ratelimit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A key to group the limits by
 * @author Navin.Viswanath
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RateLimitKey {
    String value() default "default-rate-limit-key";
}
