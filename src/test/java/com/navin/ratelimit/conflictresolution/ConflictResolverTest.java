package com.navin.ratelimit.conflictresolution;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import org.junit.Test;

import com.navin.ratelimit.annotations.RateLimit;
import com.navin.ratelimit.annotations.RateLimits;

public class ConflictResolverTest {
    public RateLimits getSampleData1(){
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
                return 10;
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
                return 15;
            }
            
            @Override
            public int duration() {
                return 6;
            }
        };
        RateLimits rateLimits = new RateLimits() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return RateLimits.class;
            }
            
            @Override
            public RateLimit[] value() {
                return new RateLimit[]{r1,r2};
            }
        };
        return rateLimits;
    }
    public RateLimits getSampleData2(){
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
                return 10;
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
                return 8;
            }
            
            @Override
            public int duration() {
                return 6;
            }
        };
        RateLimits rateLimits = new RateLimits() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return RateLimits.class;
            }
            
            @Override
            public RateLimit[] value() {
                return new RateLimit[]{r1,r2};
            }
        };
        return rateLimits;
    }
    public RateLimits getSampleData3(){
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
                return 10;
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
                return 8;
            }
            
            @Override
            public int duration() {
                return 6;
            }
        };
        RateLimit r3 = new RateLimit() {
            
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
                return 12;
            }
            
            @Override
            public int duration() {
                return 5;
            }
        };
        RateLimits rateLimits = new RateLimits() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return RateLimits.class;
            }
            
            @Override
            public RateLimit[] value() {
                return new RateLimit[]{r1,r2,r3};
            }
        };
        return rateLimits;
    }
    @Test
    public void testNoConflict(){
        RateLimits rateLimits = this.getSampleData1();
        ConflictResolver resolver = new ConflictResolver(ConflictResolutionStrategy.OPTIMISTIC);
        RateLimits limits = resolver.resolveConflicts(rateLimits);
        assertEquals(2, limits.value().length);
    }
    @Test
    public void testConflictOptimistic(){
        RateLimits rateLimits = this.getSampleData2();
        ConflictResolver resolver = new ConflictResolver(ConflictResolutionStrategy.OPTIMISTIC);
        RateLimits resolvedLimits = resolver.resolveConflicts(rateLimits);
        RateLimit[] limits = resolvedLimits.value();
        assertEquals(1, limits.length);
        assertEquals(limits[0].limit(), 10);
    }
    @Test
    public void testConflictOptimisticMultiple(){
        RateLimits rateLimits = this.getSampleData3();
        ConflictResolver resolver = new ConflictResolver(ConflictResolutionStrategy.OPTIMISTIC);
        RateLimits resolvedLimits = resolver.resolveConflicts(rateLimits);
        RateLimit[] limits = resolvedLimits.value();
        assertEquals(1, limits.length);
        assertEquals(limits[0].limit(), 12);
    }
    @Test
    public void testNoConflictPessimistic(){
        RateLimits rateLimits = this.getSampleData1();
        ConflictResolver resolver = new ConflictResolver(ConflictResolutionStrategy.PESSIMISTIC);
        RateLimits resolvedLimits = resolver.resolveConflicts(rateLimits);
        RateLimit[] limits = resolvedLimits.value();
        assertEquals(2, limits.length);
    }
    @Test
    public void testConflictPessimistic(){
        RateLimits rateLimits = this.getSampleData2();
        ConflictResolver resolver = new ConflictResolver(ConflictResolutionStrategy.PESSIMISTIC);
        RateLimits resolvedLimits = resolver.resolveConflicts(rateLimits);
        RateLimit[] limits = resolvedLimits.value();
        assertEquals(1, limits.length);
        assertEquals(limits[0].limit(), 8);
    }
    @Test
    public void testConflictPessimisticMultiple(){
        RateLimits rateLimits = this.getSampleData3();
        ConflictResolver resolver = new ConflictResolver(ConflictResolutionStrategy.PESSIMISTIC);
        RateLimits resolvedLimits = resolver.resolveConflicts(rateLimits);
        RateLimit[] limits = resolvedLimits.value();
        assertEquals(1, limits.length);
        assertEquals(limits[0].limit(), 8);
    }
}
