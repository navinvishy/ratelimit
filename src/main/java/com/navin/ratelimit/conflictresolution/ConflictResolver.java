package com.navin.ratelimit.conflictresolution;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.navin.ratelimit.annotations.RateLimit;
import com.navin.ratelimit.annotations.RateLimits;

/**
 * Given a set of rate limits, resolves conflicts by removing elements from the set based on a strategy
 * @author Navin.Viswanath
 *
 */
public class ConflictResolver {
    
    private static Log log = LogFactory.getLog(ConflictResolver.class);

    private ConflictResolutionStrategy conflictResolutionStrategy;
    
    public ConflictResolver(ConflictResolutionStrategy conflictResolutionStrategy){
        this.conflictResolutionStrategy = conflictResolutionStrategy;
    }
    /**
     * Resolve rate limit conflicts
     * @return
     */
    public RateLimits resolveConflicts(RateLimits rateLimits){
        RateLimit[] limits = rateLimits.value();
        log.debug("Input limits are:");
        for(int i=0;i<limits.length;i++){
            log.debug(limits[i].limit() + " calls in " + limits[i].duration() + " " + limits[i].unit());
        }
        Arrays.sort(limits, new Comparator<RateLimit>() {

            @Override
            public int compare(RateLimit r1, RateLimit r2) {
                long r1DurationInMillis = r1.unit().toMillis(r1.duration());
                long r2DurationInMillis = r2.unit().toMillis(r2.duration());
                return new Long(r1DurationInMillis - r2DurationInMillis).intValue();
            }
            
        });
        int minLimit = limits[0].limit();
        int minLimitIndex = 0;
        int index = 1;
        if(conflictResolutionStrategy.equals(ConflictResolutionStrategy.OPTIMISTIC)){
            while(index < limits.length) {
                if(limits[index].limit() < minLimit){
                    log.debug("Removing rate limit of " + limits[index].limit() + " calls per " + limits[index].duration() + " " + limits[index].unit());
                    limits = (RateLimit[])ArrayUtils.remove(limits, index);
                    if(index > 0) index--;
                } else {
                    long currentDurationInMillis = limits[index].unit().toMillis(limits[index].duration());
                    long minDurationInMillis = limits[minLimitIndex].unit().toMillis(limits[minLimitIndex].duration());
                    if(currentDurationInMillis == minDurationInMillis){
                        log.debug("Removing rate limit of " + limits[minLimitIndex].limit() + " calls per " + limits[minLimitIndex].duration() + " " + limits[minLimitIndex].unit());
                        limits = (RateLimit[]) ArrayUtils.remove(limits, minLimitIndex);
                        if(index > 0) index--;
                        minLimit = limits[index].limit();
                        minLimitIndex = index;
                    }
                }
                index++;
            }
        }
        index = 1;
        if(conflictResolutionStrategy.equals(ConflictResolutionStrategy.PESSIMISTIC)){
            while(index < limits.length) {
                if(limits[index].limit() < minLimit){
                    log.debug("Removing rate limit of " + limits[minLimitIndex].limit() + " calls per " + limits[minLimitIndex].duration() + " " + limits[minLimitIndex].unit());
                    limits = (RateLimit[])ArrayUtils.remove(limits, minLimitIndex);
                    if(index > 0) index--;
                    minLimit = limits[index].limit();
                    minLimitIndex = index;
                } else {
                    long currentDurationInMillis = limits[index].unit().toMillis(limits[index].duration());
                    long minDurationInMillis = limits[minLimitIndex].unit().toMillis(limits[minLimitIndex].duration());
                    if(currentDurationInMillis == minDurationInMillis){
                        log.debug("Removing rate limit of " + limits[index].limit() + " calls per " + limits[index].duration() + " " + limits[index].unit());
                        limits = (RateLimit[]) ArrayUtils.remove(limits, index);
                        if(index > 0) index--;
                    }
                }
                index++;
            }
        }
        final RateLimit[] resolvedLimits = limits;
        RateLimits resolved =  new RateLimits() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return RateLimits.class;
            }
            
            @Override
            public RateLimit[] value() {
                return resolvedLimits;
            }
        };
        return resolved;
    }
    public ConflictResolutionStrategy getConflictResolutionStrategy() {
        return conflictResolutionStrategy;
    }
    public void setConflictResolutionStrategy(ConflictResolutionStrategy conflictResolutionStrategy) {
        this.conflictResolutionStrategy = conflictResolutionStrategy;
    }
}
