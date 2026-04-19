package com.stoloto.api.controller;

import com.stoloto.api.model.Event;
import com.stoloto.api.service.EventService;
import com.stoloto.api.service.SseEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final SseEventService sseEventService;

    @Autowired
    public EventController(EventService eventService, SseEventService sseEventService) {
        this.eventService = eventService;
        this.sseEventService = sseEventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event savedEvent = eventService.createEvent(event);
        // Broadcast new event via SSE
        sseEventService.broadcastEvent("new-event", savedEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Event updatedEvent = eventService.updateEvent(id, event);
        sseEventService.broadcastEvent("updated-event", updatedEvent);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        sseEventService.broadcastEvent("deleted-event", id);
        return ResponseEntity.noContent().build();
    }
}