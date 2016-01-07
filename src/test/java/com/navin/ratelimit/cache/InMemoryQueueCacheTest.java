package com.navin.ratelimit.cache;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.navin.ratelimit.annotations.RateLimit;
import com.navin.ratelimit.annotations.RateLimits;
import com.navin.ratelimit.exceptions.RateLimitExceededException;

public class InMemoryQueueCacheTest {
    private InMemoryQueueCache inMemoryQueueCache;
    
    public RateLimits getRateLimits(){
            return new RateLimits() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return RateLimits.class;
            }
            
            @Override
            public RateLimit[] value() {
                RateLimit r1 = new RateLimit() {
                    
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return RateLimit.class;
                    }
                    
                    @Override
                    public TimeUnit unit() {
                        return TimeUnit.SECONDS;
                    }
                    
                    @Override
                    public int limit() {
                        return 2;
                    }
                    
                    @Override
                    public int duration() {
                        return 5;
                    }
                };
                return new RateLimit[]{r1};
            }
        };
    }
    public RateLimits getRateLimitsMultiple(){
        return new RateLimits() {
        
        @Override
        public Class<? extends Annotation> annotationType() {
            return RateLimits.class;
        }
        
        @Override
        public RateLimit[] value() {
            RateLimit r1 = new RateLimit() {
                
                @Override
                public Class<? extends Annotation> annotationType() {
                    return RateLimit.class;
                }
                
                @Override
                public TimeUnit unit() {
                    return TimeUnit.SECONDS;
                }
                
                @Override
                public int limit() {
                    return 2;
                }
                
                @Override
                public int duration() {
                    return 5;
                }
            };
            RateLimit r2 = new RateLimit() {
                
                @Override
                public Class<? extends Annotation> annotationType() {
                    return RateLimit.class;
                }
                
                @Override
                public TimeUnit unit() {
                    return TimeUnit.SECONDS;
                }
                
                @Override
                public int limit() {
                    return 3;
                }
                
                @Override
                public int duration() {
                    return 6;
                }
            };
            return new RateLimit[]{r1,r2};
        }
    };
}
    @Before
    public void setUp(){
        inMemoryQueueCache = new InMemoryQueueCache();
    }
    @Test
    public void testCheckExceptionStrategy(){
        boolean exceptionThrown = false;
        inMemoryQueueCache.setRateLimitExceededStrategy(RateLimitExceededStrategy.EXCEPTION);
        String key = "test";
        RateLimits rateLimits = this.getRateLimits();
        try {
            inMemoryQueueCache.check(key, rateLimits);
            inMemoryQueueCache.check(key, rateLimits);
            inMemoryQueueCache.check(key, rateLimits);
            inMemoryQueueCache.check(key, rateLimits);
        } catch (RateLimitExceededException | InterruptedException e) {
            exceptionThrown = true;
        }
        assertEquals(true, exceptionThrown);
    }
    @Test
    public void testCheckExceptionStrategyMultiple(){
        boolean exceptionThrown = false;
        inMemoryQueueCache.setRateLimitExceededStrategy(RateLimitExceededStrategy.EXCEPTION);
        String key = "test";
        RateLimits rateLimits = this.getRateLimitsMultiple();
        try {
            inMemoryQueueCache.check(key, rateLimits);
            inMemoryQueueCache.check(key, rateLimits);
            inMemoryQueueCache.check(key, rateLimits);
        } catch (RateLimitExceededException | InterruptedException e) {
            exceptionThrown = true;
        }
        assertEquals(true, exceptionThrown);
    }
    @Test
    public void testCheckSyncStrategy(){
        boolean exceptionThrown = false;
        inMemoryQueueCache.setRateLimitExceededStrategy(RateLimitExceededStrategy.SYNC);
        String key = "test";
        RateLimits rateLimits = this.getRateLimits();
        try {
            inMemoryQueueCache.check(key, rateLimits);
            inMemoryQueueCache.check(key, rateLimits);
            inMemoryQueueCache.check(key, rateLimits);
        } catch (RateLimitExceededException | InterruptedException e) {
            exceptionThrown = true;
        }
        assertEquals(false, exceptionThrown);
    }
}
