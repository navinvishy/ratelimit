package com.navin.ratelimit.cache;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * A token object to insert in the delay queue for the in memory queue based strategy
 * @author Navin.Viswanath
 *
 */
public class RateLimitToken implements Delayed {
    
    private TimeUnit timeUnit;
    private long delay;
    private final long createTime;
    
    public RateLimitToken(TimeUnit timeUnit, long delay) {
        this.timeUnit = timeUnit;
        this.delay = delay;
        this.createTime = System.currentTimeMillis();
    }
    @Override
    public int compareTo(Delayed o) {
        long thisTimeInMillis = this.getDelay(TimeUnit.MILLISECONDS);
        long otherTimeInMillis = o.getDelay(TimeUnit.MILLISECONDS);
        return new Long(thisTimeInMillis - otherTimeInMillis).intValue();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long timeSoFar = System.currentTimeMillis() - createTime;
        long timeRemaining = TimeUnit.MILLISECONDS.convert(getDelay(), this.getTimeUnit()) - timeSoFar;
        return unit.convert(timeRemaining, TimeUnit.MILLISECONDS);
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
    
}
