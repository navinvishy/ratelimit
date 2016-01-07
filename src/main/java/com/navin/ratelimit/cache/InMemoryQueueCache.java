package com.navin.ratelimit.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;

import com.navin.ratelimit.annotations.RateLimit;
import com.navin.ratelimit.annotations.RateLimits;
import com.navin.ratelimit.exceptions.RateLimitExceededException;

/**
 * A queue-based in memory storage strategy for rate limiting
 * @author Navin.Viswanath
 *
 */
public class InMemoryQueueCache implements RateLimitCacheStrategy {
    
    private RateLimitExceededStrategy rateLimitExceededStrategy;
    private Map<String,Map<RateLimit,DelayQueue<RateLimitToken>>> cache = new HashMap<>();
    
    /**
     * Initialize the cache with delay queues for each rate limit containing tokens with delay zero. 
     * The number of tokens is the number of calls allowed by this rate limit.
     * @param key the key value for this list
     * @param rateLimits the rate limits
     */
    private void initialize(String key, RateLimits rateLimits){
        RateLimit[] limits = rateLimits.value();
        Map<RateLimit,DelayQueue<RateLimitToken>> queues = new HashMap<RateLimit,DelayQueue<RateLimitToken>>();
        for(int i=0;i<limits.length;i++){
            DelayQueue<RateLimitToken> queue = new DelayQueue<>();
            List<RateLimitToken> tokens = new ArrayList<>();
            for(int j=0;j<limits[i].limit();j++){
                tokens.add(new RateLimitToken(limits[i].unit(), 0));
            }
            queue.addAll(tokens);
            queues.put(limits[i], queue);
        }
        cache.put(key, queues);
    }
    
    /**
     * Retrieves a token from each rate limit queue, if one is available. If not, an exception is thrown.
     * @param key
     * @param rateLimits
     * @throws RateLimitExceededException
     */
    private void checkWithException(String key, RateLimits rateLimits) throws RateLimitExceededException {
        Map<RateLimit,DelayQueue<RateLimitToken>> queues = cache.get(key);
        for(Map.Entry<RateLimit, DelayQueue<RateLimitToken>> queueByRateLimit: queues.entrySet()){
            DelayQueue<RateLimitToken> queue = queueByRateLimit.getValue();
            RateLimit limit = queueByRateLimit.getKey();
            RateLimitToken token = queue.poll();
            if(token != null){
                queue.put(new RateLimitToken(limit.unit(), limit.duration()));
            } else {
                throw new RateLimitExceededException("Rate limit exceeded");
            }
        }
    }
    
    /**
     * Retrieves a token from each rate limit queue, waiting until one is available.
     * @param key
     * @param rateLimits
     * @throws InterruptedException
     */
    private void checkSync(String key, RateLimits rateLimits) throws InterruptedException {
        Map<RateLimit,DelayQueue<RateLimitToken>> queues = cache.get(key);
        for(Map.Entry<RateLimit, DelayQueue<RateLimitToken>> queueByRateLimit: queues.entrySet()){
            DelayQueue<RateLimitToken> queue = queueByRateLimit.getValue();
            RateLimit limit = queueByRateLimit.getKey();
            RateLimitToken token = queue.take();
            if(token != null){
                queue.put(new RateLimitToken(limit.unit(), limit.duration()));
            }
        }
    }
    /**
     * Retrieves a token from each rate limit queue. If one is not available, the call is queued and executed at a later point
     * @param key
     * @param rateLimits
     */
    public void checkAsync(String key, RateLimits rateLimits) {
        
    }
    /**
     * Check whether the rate limit has been exceeded
     * @param key
     * @param rateLimits
     * @throws RateLimitExceededException
     */
    @Override
    public void check(String key, RateLimits rateLimits) throws RateLimitExceededException, InterruptedException {
        if(cache.get(key) == null){
            initialize(key, rateLimits);
        }
        if(rateLimitExceededStrategy.equals(RateLimitExceededStrategy.EXCEPTION)){
            checkWithException(key, rateLimits);
        }
        if(rateLimitExceededStrategy.equals(RateLimitExceededStrategy.SYNC)){
            checkSync(key, rateLimits);
        }
        if(rateLimitExceededStrategy.equals(RateLimitExceededStrategy.ASYNC)){
            checkAsync(key, rateLimits);
        }
    }
    public RateLimitExceededStrategy getRateLimitExceededStrategy() {
        return rateLimitExceededStrategy;
    }
    public void setRateLimitExceededStrategy(RateLimitExceededStrategy rateLimitExceededStrategy) {
        this.rateLimitExceededStrategy = rateLimitExceededStrategy;
    }
    public Map<String, Map<RateLimit,DelayQueue<RateLimitToken>>> getCache() {
        return cache;
    }
    
}
