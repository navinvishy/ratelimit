package com.navin.ratelimit.conflictresolution;
/**
 * When a set of rate limits are in conflict, for eg, 5 calls/sec and 10 calls/sec, this strategy decides which ones to pick.
 * In this example, the optimistic strategy will pick 10 calls/sec and the pessimistic strategy will pick 5 calls/sec
 * @author Navin.Viswanath
 *
 */
public enum ConflictResolutionStrategy {
    OPTIMISTIC,
    PESSIMISTIC
}
