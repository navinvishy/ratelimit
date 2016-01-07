package com.navin.ratelimit.cache;

import java.util.concurrent.TimeUnit;

import com.navin.ratelimit.annotations.RateLimit;
import com.navin.ratelimit.annotations.RateLimits;
import com.navin.ratelimit.exceptions.RateLimitExceededException;

import net.spy.memcached.MemcachedClient;

/**
 * A memcached-based store implementation. The blocking call uses an exponential backoff strategy
 * @author Navin.Viswanath
 *
 */
public class MemcachedCache implements RateLimitCacheStrategy {
    private RateLimitExceededStrategy rateLimitExceededStrategy;
    private MemcachedClient cacheClient;
    
    @Override
    public RateLimitExceededStrategy getRateLimitExceededStrategy() {
        return rateLimitExceededStrategy;
    }
    private void checkWithException(String key, RateLimits rateLimits) throws RateLimitExceededException {
        String cacheKey = null;
        for(RateLimit rateLimit : rateLimits.value()){
            cacheKey = key.concat("_").concat(new Integer(rateLimit.duration()).toString()).concat("_").concat(rateLimit.unit().toString());
            if(cacheClient.incr(cacheKey, 1, 1L, new Long(TimeUnit.SECONDS.convert(rateLimit.duration(), rateLimit.unit())).intValue()) > rateLimit.limit()){
                throw new RateLimitExceededException("Rate limit exceeded");
            }
        }
    }
    private void checkSync(String key, RateLimits rateLimits) throws InterruptedException{
        String cacheKey = null;
        for(RateLimit rateLimit : rateLimits.value()){
            int incr = 0;
            cacheKey = key.concat("_").concat(new Integer(rateLimit.duration()).toString()).concat("_").concat(rateLimit.unit().toString());
            //Loop until the count is less than the rate limit
            while(cacheClient.incr(cacheKey, 1, 1L, new Long(TimeUnit.SECONDS.convert(rateLimit.duration(), rateLimit.unit())).intValue()) > rateLimit.limit()){
                Thread.sleep((long)Math.pow(2, incr++)*100L);
            }
        }
    }
    @Override
    public void check(String key, RateLimits rateLimits) throws RateLimitExceededException, InterruptedException {
        if(rateLimitExceededStrategy.equals(RateLimitExceededStrategy.EXCEPTION)){
            checkWithException(key, rateLimits);
        }
        if(rateLimitExceededStrategy.equals(RateLimitExceededStrategy.SYNC)){
            checkSync(key, rateLimits);
        }
    }
    
    public MemcachedClient getCacheClient() {
        return cacheClient;
    }
    public void setCacheClient(MemcachedClient cacheClient) {
        this.cacheClient = cacheClient;
    }
    public void setRateLimitExceededStrategy(RateLimitExceededStrategy rateLimitExceededStrategy) {
        this.rateLimitExceededStrategy = rateLimitExceededStrategy;
    }
    
}
