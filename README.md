<h2>An annotation-based Rate Limiter for Java Methods based on Spring AOP</h2>
<h3>Prerequisites</h3>
  - Java 1.8
  - Memcached
  
<h3>Usage</h3>
```
@RateLimits({
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=2),
        @RateLimit(duration=30, unit=TimeUnit.SECONDS, limit=5)
    })
    public void helloWorld(@RateLimitKey String key, int a, String b) {
        System.out.println(key);
    }
```  
The annotations indicate two separate rate limits on this method : 2 calls every 10 seconds and 5 calls every
30 seconds. The ```@RateLimitKey``` annotation is optional. It can be used to pass in an argument to change the granularity at
which the rate limits will be applied. For example, without the ```@RateLimitKey``` annotation, the method will be rate limited
to 2 calls every 10 seconds and 5 calls every 30 seconds globally. With the annotation, the rate limiting will apply based on
the value for the argument. For example, if you call the method with key="x" twice and key="y" once in a span of 5 seconds,
the rate limit is not exceeded though the method has been called 3 times within 10 seconds. The key="x" calls and the key="y"
call are each still within the rate limit. If you call the method with key="x" 3 times within 10 seconds, the rate limit will
be exceeded.

<h3>Features</h3>
- There are two pluggable implementations of the counters for each call. One is in-memory and the other is based on Memcached.
If you have a single JVM running, then the in-memory implementation is sufficient. If you have multiple JVMs in a distributed
setting, and you want to apply the rate limiting in a distributed fashion, then the Memcached-based implementation is probably
what you need.
- The rate limiter also has a conflict resolver to resolve conflicting rate limits. For example,
```
@RateLimits({
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=2),
        @RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=5)
    })
```  
are conflicting. The conflict resolver will resolve these conflicts by picking one and dropping the other. Which one is picked
can be controlled by a choice of OPTIMISTIC and PESSIMISTIC conflict resolution strategies. The OPTIMISTIC strategy will pick 
```@RateLimit(duration=10, unit=TimeUnit.SECONDS, limit=5)``` and the PESSIMISTIC strategy will pick the other one.
- There are two strategies for what to do when the rate limit is exceeded. One is the EXCEPTION strategy that simple throws an 
exception when the rate limit is exceeded. The other is a SYNC strategy that blocks until the interval expires and the method
can proceed to execute.

<h3>Building</h3>
This is a Maven project. To build the jar, download the code and :

``` $ mvn clean package```

To run the unit tests:

```$ mvn test```

The file **src/test/resources/appContextRateLimitTest.xml** is the Spring XML context used by the test cases and should provide
a starting point if you want to use this rate limiter in your project.

Ideas on improving this are welcome. So are other implementations of the counters besides the existing in-memory and 
Memcached-based implementations.
