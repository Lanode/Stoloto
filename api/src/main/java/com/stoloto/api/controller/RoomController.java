package com.stoloto.api.controller;

import com.stoloto.api.model.Room;
import com.stoloto.api.service.RoomService;
import com.stoloto.api.service.SseEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final SseEventService sseEventService;

    @Autowired
    public RoomController(RoomService roomService, SseEventService sseEventService) {
        this.roomService = roomService;
        this.sseEventService = sseEventService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomService.createRoom(room);
        sseEventService.broadcastEvent("room-created", savedRoom);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        Room updatedRoom = roomService.updateRoom(id, room);
        sseEventService.broadcastEvent("room-updated", updatedRoom);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        sseEventService.broadcastEvent("room-deleted", id);
        return ResponseEntity.noContent().build();
    }

    // Room management endpoints

    @PostMapping("/{roomId}/participants/{participantId}")
    public ResponseEntity<Room> addParticipantToRoom(
            @PathVariable Long roomId,
            @PathVariable Long participantId) {
        try {
            Room updatedRoom = roomService.addParticipantToRoom(roomId, participantId);
            sseEventService.broadcastEvent("participant-added",
                Map.of("roomId", roomId, "participantId", participantId));
            return ResponseEntity.ok(updatedRoom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{roomId}/participants/{participantId}")
    public ResponseEntity<Room> removeParticipantFromRoom(
            @PathVariable Long roomId,
            @PathVariable Long participantId) {
        try {
            Room updatedRoom = roomService.removeParticipantFromRoom(roomId, participantId);
            sseEventService.broadcastEvent("participant-removed",
                Map.of("roomId", roomId, "participantId", participantId));
            return ResponseEntity.ok(updatedRoom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/with-boost")
    public ResponseEntity<List<Room>> getRoomsWithBoost() {
        List<Room> rooms = roomService.getRoomsWithBoost();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Room>> getUpcomingRooms() {
        List<Room> rooms = roomService.getUpcomingRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/by-price/{maxPrice}")
    public ResponseEntity<List<Room>> getRoomsByMaxPrice(@PathVariable Integer maxPrice) {
        List<Room> rooms = roomService.getRoomsByMaxPrice(maxPrice);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/by-prize/{minPrize}")
    public ResponseEntity<List<Room>> getRoomsByMinPrize(@PathVariable Integer minPrize) {
        List<Room> rooms = roomService.getRoomsByMinPrize(minPrize);
        return ResponseEntity.ok(rooms);
    }
}