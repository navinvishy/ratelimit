package com.navin.ratelimit;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.navin.ratelimit.cache.MemcachedCache;
import com.navin.ratelimit.cache.RateLimitExceededStrategy;
import com.navin.ratelimit.cache.RateLimitSample;
import com.navin.ratelimit.exceptions.RateLimitExceededException;

import org.junit.Assert;
import org.junit.Before;

public class RateLimitPointCutTest {
    private ApplicationContext context;
    @Before
    public void setUp(){
        context = new ClassPathXmlApplicationContext("appContextRateLimitTest.xml");
    }
    @Test
    public void testPointCut(){
        boolean exception = false;
        RateLimitSample sample = (RateLimitSample) context.getBean("sample");
        try {
            sample.hello();
            sample.hello();
            sample.hello();
        } catch(Exception e){
            Assert.assertEquals(e.getClass(), RateLimitExceededException.class);
            exception = true;
        }
        Assert.assertTrue(exception);
    }
    @Test
    public void testPointCutConflictingRateLimits(){
        boolean exception = false;
        RateLimitSample sample = (RateLimitSample) context.getBean("sample");
        try {
            sample.conflictingRateLimits("hello");
            Thread.sleep(5000);
            sample.conflictingRateLimits("hello");
            Thread.sleep(5000);
            sample.conflictingRateLimits("hello");
            sample.conflictingRateLimits("hello");
        } catch(Exception e){
            Assert.assertEquals(e.getClass(), RateLimitExceededException.class);
            exception = true;
        }
        Assert.assertTrue(exception);
    }
    @Test
    public void testPointCutSync(){
        MemcachedCache strategy = (MemcachedCache) context.getBean("memcachedCache");
        strategy.setRateLimitExceededStrategy(RateLimitExceededStrategy.SYNC);
        RateLimitSample sample = (RateLimitSample) context.getBean("sample");
        sample.hello("test");
        sample.hello("test");
        sample.hello("test");
    }
    @Test
    public void testPointCutSyncMultipleParams(){
        MemcachedCache strategy = (MemcachedCache) context.getBean("memcachedCache");
        strategy.setRateLimitExceededStrategy(RateLimitExceededStrategy.SYNC);
        RateLimitSample sample = (RateLimitSample) context.getBean("sample");
        sample.helloWorld("test", 1, "test");
        sample.helloWorld("test", 1, "test");
        sample.helloWorld("test", 1, "test");
    }
    @Test
    public void testPointCutSyncAnnotationOnWrongParam(){
        MemcachedCache strategy = (MemcachedCache) context.getBean("memcachedCache");
        strategy.setRateLimitExceededStrategy(RateLimitExceededStrategy.SYNC);
        RateLimitSample sample = (RateLimitSample) context.getBean("sample");
        sample.helloWorldWrongAnnotatedParam("test", 1, "test");
        sample.helloWorldWrongAnnotatedParam("test", 1, "test");
        sample.helloWorldWrongAnnotatedParam("test", 1, "test");
    }
    @Test(expected = ClassCastException.class)
    public void testPointCutSyncAnnotatedParamWrongType(){
        MemcachedCache strategy = (MemcachedCache) context.getBean("memcachedCache");
        strategy.setRateLimitExceededStrategy(RateLimitExceededStrategy.SYNC);
        RateLimitSample sample = (RateLimitSample) context.getBean("sample");
        sample.helloWorldWrongAnnotationOnWrongType(1,"test");
        sample.helloWorldWrongAnnotationOnWrongType(1,"test");
    }
}
