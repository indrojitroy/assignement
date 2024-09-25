package com.verve.assignment.controller;


import com.verve.assignment.service.EventCounterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class EventCounterController {

    private final EventCounterService eventCounterService;

    public EventCounterController(EventCounterService eventCounterService) {
        this.eventCounterService = eventCounterService;
    }

    @GetMapping("/api/verve/accept")
    public Mono<String> getEvent(@RequestParam int id, @RequestParam(required = false) String endpoint) {
        return eventCounterService.processEvent(id, endpoint);
    }
}