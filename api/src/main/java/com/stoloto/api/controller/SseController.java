package com.stoloto.api.controller;

import com.stoloto.api.service.SseEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
public class SseController {

    private final SseEventService sseEventService;

    @Autowired
    public SseController(SseEventService sseEventService) {
        this.sseEventService = sseEventService;
    }

    /**
     * Endpoint for clients to subscribe to SSE events
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        return sseEventService.subscribe();
    }

    /**
     * Endpoint to trigger a test event (for demonstration)
     */
    @GetMapping("/trigger")
    public String triggerEvent() {
        sseEventService.broadcastEvent("test", "This is a test event triggered manually");
        return "Event triggered and broadcast to all subscribers";
    }
}