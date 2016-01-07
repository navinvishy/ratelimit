package com.navin.ratelimit.cache;

import com.navin.ratelimit.annotations.RateLimits;
import com.navin.ratelimit.exceptions.RateLimitExceededException;

public interface RateLimitCacheStrategy {
    
    RateLimitExceededStrategy getRateLimitExceededStrategy();
    
    void check(String key, RateLimits rateLimits) throws RateLimitExceededException, InterruptedException;
}
