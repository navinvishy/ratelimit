package com.navin.ratelimit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
/**
 * Defines a rate limit annotation
 * @author Navin.Viswanath
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(RateLimits.class)
public @interface RateLimit {
    TimeUnit unit();
    int duration();
    int limit();
}
