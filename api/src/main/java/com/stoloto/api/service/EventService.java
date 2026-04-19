package com.stoloto.api.service;

import com.stoloto.api.model.Event;
import com.stoloto.api.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(eventDetails.getTitle());
                    event.setDescription(eventDetails.getDescription());
                    // createdAt should not be updated
                    return eventRepository.save(event);
                })
                .orElseGet(() -> {
                    eventDetails.setId(id);
                    return eventRepository.save(eventDetails);
                });
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}