package com.navin.ratelimit.cache;

import java.util.concurrent.TimeUnit;

import com.navin.ratelimit.annotations.RateLimit;
import com.navin.ratelimit.annotations.RateLimitKey;
import com.navin.ratelimit.annotations.RateLimits;
import com.navin.ratelimit.exceptions.RateLimitExceededException;

public class RateLimitSample {
    @RateLimits({
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=2)
    })
    public void hello() throws RateLimitExceededException {
        System.out.println("Hello World");
    }
    @RateLimits({
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=2)
    })
    public void hello(@RateLimitKey String key) {
        System.out.println(key);
    }
    @RateLimits({
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=2)
    })
    public void helloWorld(@RateLimitKey String key, int a, String b) {
        System.out.println(key);
    }
    @RateLimits({
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=2)
    })
    public void helloWorldWrongAnnotatedParam(String key, int a, @RateLimitKey String b) {
        System.out.println(key);
    }
    @RateLimits({
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=2)
    })
    public void helloWorldWrongAnnotationOnWrongType(@RateLimitKey int a,String key) {
        System.out.println(key);
    }
    @RateLimits({
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=2),
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=3),
        @RateLimit(duration=15, unit=TimeUnit.SECONDS, limit=3)
    })
    public void conflictingRateLimits(@RateLimitKey String key) throws RateLimitExceededException {
        System.out.println(key);
    }
}
