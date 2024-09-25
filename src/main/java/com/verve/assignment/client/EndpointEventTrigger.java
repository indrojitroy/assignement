package com.verve.assignment.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static org.yaml.snakeyaml.nodes.Tag.STR;

@Component
public class EndpointEventTrigger {

    private static final Logger LOG = LoggerFactory.getLogger(EndpointEventTrigger.class);
    private final WebClient webClient;

    public EndpointEventTrigger() {
        this.webClient = WebClient.builder().build();
    }

    public void trigger(int count, String endpoint) {
        String requestPayload =  String.format("{\"count\": %d}", count);

        webClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestPayload))
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> LOG.info("Response status from endpoint {}: {}", endpoint, response.getStatusCode()))
                .doOnError(error -> LOG.error("Error sending POST request to {}: {}", endpoint, error.getMessage()))
                .subscribe();  // Ensure that we subscribe to execute the POST request
    }
}
