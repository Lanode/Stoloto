package com.stoloto.api.controller;

import com.stoloto.api.model.Participant;
import com.stoloto.api.service.ParticipantService;
import com.stoloto.api.service.SseEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    private final ParticipantService participantService;
    private final SseEventService sseEventService;

    @Autowired
    public ParticipantController(ParticipantService participantService, SseEventService sseEventService) {
        this.participantService = participantService;
        this.sseEventService = sseEventService;
    }

    @GetMapping
    public ResponseEntity<List<Participant>> getAllParticipants() {
        List<Participant> participants = participantService.getAllParticipants();
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable Long id) {
        return participantService.getParticipantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Participant> createParticipant(@RequestBody Participant participant) {
        Participant savedParticipant = participantService.createParticipant(participant);
        sseEventService.broadcastEvent("participant-created", savedParticipant);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedParticipant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participant> updateParticipant(@PathVariable Long id, @RequestBody Participant participant) {
        Participant updatedParticipant = participantService.updateParticipant(id, participant);
        sseEventService.broadcastEvent("participant-updated", updatedParticipant);
        return ResponseEntity.ok(updatedParticipant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        sseEventService.broadcastEvent("participant-deleted", id);
        return ResponseEntity.noContent().build();
    }

    // Balance management endpoints

    @PostMapping("/{id}/balance/add")
    public ResponseEntity<Participant> addBalance(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer amount = request.get("amount");
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Participant updatedParticipant = participantService.addBalance(id, amount);
            sseEventService.broadcastEvent("balance-added",
                Map.of("participantId", id, "amount", amount, "newBalance", updatedParticipant.getBalance()));
            return ResponseEntity.ok(updatedParticipant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/balance/subtract")
    public ResponseEntity<Participant> subtractBalance(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer amount = request.get("amount");
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Participant updatedParticipant = participantService.subtractBalance(id, amount);
            sseEventService.broadcastEvent("balance-subtracted",
                Map.of("participantId", id, "amount", amount, "newBalance", updatedParticipant.getBalance()));
            return ResponseEntity.ok(updatedParticipant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/balance/block")
    public ResponseEntity<Participant> blockBalance(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer amount = request.get("amount");
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Participant updatedParticipant = participantService.blockBalance(id, amount);
            sseEventService.broadcastEvent("balance-blocked",
                Map.of("participantId", id, "amount", amount,
                    "newBalance", updatedParticipant.getBalance(),
                    "blockedBalance", updatedParticipant.getBlockedBalance()));
            return ResponseEntity.ok(updatedParticipant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/balance/unblock")
    public ResponseEntity<Participant> unblockBalance(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer amount = request.get("amount");
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Participant updatedParticipant = participantService.unblockBalance(id, amount);
            sseEventService.broadcastEvent("balance-unblocked",
                Map.of("participantId", id, "amount", amount,
                    "newBalance", updatedParticipant.getBalance(),
                    "blockedBalance", updatedParticipant.getBlockedBalance()));
            return ResponseEntity.ok(updatedParticipant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Query endpoints

    @GetMapping("/with-min-balance/{minBalance}")
    public ResponseEntity<List<Participant>> getParticipantsWithMinBalance(@PathVariable Integer minBalance) {
        List<Participant> participants = participantService.getParticipantsWithMinBalance(minBalance);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/with-blocked-balance")
    public ResponseEntity<List<Participant>> getParticipantsWithBlockedBalance() {
        List<Participant> participants = participantService.getParticipantsWithBlockedBalance();
        return ResponseEntity.ok(participants);
    }
}