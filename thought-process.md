1. Why webflux not webmvc

There are some blocking task mainly interaction with Redis and 
also the post call we don't want to block the thread and webmvc
will not able to handle 10k req/sec means 1 req/0.1msec

2. Using Redis as multiple instance can talk to redis and since 
redis are very fast if we have key to fetch values and also
redis support reactive programming. I chose Redis for this project because of its faster response times.

3. We can use kafka for streaming the data ..also websocket kind of..
While Kafka is another great option for handling high-throughput systems.
