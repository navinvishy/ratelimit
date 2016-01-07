package com.navin.ratelimit.pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import com.navin.ratelimit.annotations.RateLimitKey;
import com.navin.ratelimit.annotations.RateLimits;
import com.navin.ratelimit.cache.RateLimitCacheStrategy;
import com.navin.ratelimit.conflictresolution.ConflictResolver;
import com.navin.ratelimit.exceptions.RateLimitExceededException;


/**
 * Defines point cut and advice for rate limiting
 * @author Navin.Viswanath
 *
 */
@Aspect
public class RateLimitPointCut {
    
    private Log log = LogFactory.getLog(RateLimitPointCut.class);
    
    private RateLimitCacheStrategy rateLimitCacheStrategy;
    
    Map<String,RateLimits> resolvedRateLimitCache = new HashMap<>();
    
    ConflictResolver conflictResolver;
    
    private String getCacheKey(Method method, String rateLimitKey){
        String cacheKey = null;
        String methodName = method.getName();
        String className = method.getDeclaringClass().getName();
        cacheKey = className.concat("_").concat(methodName);
        if(rateLimitKey != null){
            cacheKey = cacheKey.concat("_").concat(rateLimitKey);
        }
        return cacheKey;
    }
    @Before("@annotation(rateLimits)")
    public void checkRateLimits(JoinPoint jp, RateLimits rateLimits) throws RateLimitExceededException, InterruptedException{
        Object[] args = jp.getArgs();
        String rateLimitKey = null;
        MethodSignature signature = (MethodSignature) jp.getSignature();
        if(args.length > 0){
            Annotation[] firstArgAnnotations = signature.getMethod().getParameterAnnotations()[0];
            if(firstArgAnnotations.length > 0){
                for(int i=0;i<firstArgAnnotations.length;i++){
                    Annotation annotation = firstArgAnnotations[i];
                    if(annotation.annotationType().equals(RateLimitKey.class)){
                        rateLimitKey = (String) args[0];
                    }
                }
            }
        }
        String cacheKey = getCacheKey(signature.getMethod(), rateLimitKey);
        log.info("Checking rate limits with key = " + cacheKey);
        RateLimits resolvedRateLimits = rateLimits;
        if(conflictResolver != null){
            resolvedRateLimits = resolvedRateLimitCache.get(cacheKey);
            if(resolvedRateLimits == null){
                resolvedRateLimits = conflictResolver.resolveConflicts(rateLimits);
                resolvedRateLimitCache.put(cacheKey, resolvedRateLimits);
            }
        }
        rateLimitCacheStrategy.check(cacheKey, resolvedRateLimits);
    }

    public RateLimitCacheStrategy getRateLimitCacheStrategy() {
        return rateLimitCacheStrategy;
    }

    public void setRateLimitCacheStrategy(RateLimitCacheStrategy rateLimitCacheStrategy) {
        this.rateLimitCacheStrategy = rateLimitCacheStrategy;
    }
    public ConflictResolver getConflictResolver() {
        return conflictResolver;
    }
    public void setConflictResolver(ConflictResolver conflictResolver) {
        this.conflictResolver = conflictResolver;
    }
    
}
