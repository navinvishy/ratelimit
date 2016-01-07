package com.navin.ratelimit.exceptions;
/**
 * Exception thrown when rate limit is exceeded
 * @author Navin.Viswanath
 *
 */
public class RateLimitExceededException extends Exception {
    public RateLimitExceededException(String message){
        super(message);
    }
}
