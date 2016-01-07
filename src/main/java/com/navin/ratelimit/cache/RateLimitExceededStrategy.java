package com.navin.ratelimit.cache;
/**
 * This enum denotes the different response types when rate limit is exceeded.
 * sync: the call will block until it can go through
 * async: the call is queued and executed at a later point in time
 * exception : an exception is thrown indicating rate limit being exceeded
 * @author Navin.Viswanath
 *
 */
public enum RateLimitExceededStrategy {
    SYNC,
    ASYNC,
    EXCEPTION
}
