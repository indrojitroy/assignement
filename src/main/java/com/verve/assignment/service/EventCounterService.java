package com.verve.assignment.service;

import com.verve.assignment.client.EndpointEventTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EventCounterService {

    private static final Logger LOG = LoggerFactory.getLogger(EventCounterService.class);
    private static final String REDIS_KEY = "store_unique_id_only";
    private static final String KAFKA_TOPIC = "log-push-topic"; // Define your Kafka topic here

    private final EndpointEventTrigger endpointEventTrigger;
    private final ReactiveRedisTemplate<String, Integer> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate; // KafkaTemplate for sending messages

    public EventCounterService(EndpointEventTrigger endpointEventTrigger,
                               ReactiveRedisTemplate<String, Integer> redisTemplate,
                               KafkaTemplate<String, String> kafkaTemplate) {
        this.endpointEventTrigger = endpointEventTrigger;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    private ReactiveSetOperations<String, Integer> redisSetOps() {
        return redisTemplate.opsForSet();
    }

    public Mono<String> processEvent(int id, String endpoint) {
        return redisSetOps().add(REDIS_KEY, id)
                .flatMap(added -> {
                    if (added > 0) {
                        return redisSetOps().size(REDIS_KEY)
                                .flatMap(count -> {
                                    LOG.info("Unique size {}", count);
                                    if (endpoint != null && !endpoint.isEmpty()) {
                                        endpointEventTrigger.trigger(count.intValue(), endpoint);
                                    }
                                    return Mono.just("ok");
                                });
                    }
                    return Mono.just("ok");
                })
                .onErrorReturn("failed");
    }

    @Scheduled(fixedRate = 60000)
    public void logUniqueRequests() {
        redisSetOps().size(REDIS_KEY)
                .flatMap(count -> {
                    LOG.info("Count of unique IDs in the last 1 minute is: {}", count);
                    String s = "Count of unique IDs in the last 1 minute is: "+count;
                    kafkaTemplate.send(KAFKA_TOPIC, s);
                    return redisSetOps().delete(REDIS_KEY);
                })
                .subscribe();
    }
}