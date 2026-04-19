package com.stoloto.api.service;

import com.stoloto.api.model.Participant;
import com.stoloto.api.model.Room;
import com.stoloto.api.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipantService participantService;

    @Autowired
    public RoomService(RoomRepository roomRepository, ParticipantService participantService) {
        this.roomRepository = roomRepository;
        this.participantService = participantService;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
        // Validate room data
        if (room.getSeatsCount() <= 0) {
            throw new IllegalArgumentException("Seats count must be positive");
        }
        if (room.getEntryPrice() < 0) {
            throw new IllegalArgumentException("Entry price cannot be negative");
        }
        if (room.getPrizeFund() < 0) {
            throw new IllegalArgumentException("Prize fund cannot be negative");
        }
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room roomDetails) {
        return roomRepository.findById(id)
                .map(room -> {
                    room.setSeatsCount(roomDetails.getSeatsCount());
                    room.setEntryPrice(roomDetails.getEntryPrice());
                    room.setPrizeFund(roomDetails.getPrizeFund());
                    room.setHasBoost(roomDetails.getHasBoost());
                    room.setStartTime(roomDetails.getStartTime());
                    return roomRepository.save(room);
                })
                .orElseGet(() -> {
                    roomDetails.setId(id);
                    return roomRepository.save(roomDetails);
                });
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Transactional
    public Room addParticipantToRoom(Long roomId, Long participantId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        Participant participant = participantService.getParticipantById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        // Check if room has available seats
        if (room.getParticipants().size() >= room.getSeatsCount()) {
            throw new IllegalArgumentException("Room is full");
        }

        // Check if participant has enough balance for entry
        if (participant.getBalance() < room.getEntryPrice()) {
            throw new IllegalArgumentException("Participant has insufficient balance");
        }

        // Block participant's balance
        participant.blockBalance(room.getEntryPrice());

        // Add participant to room
        room.getParticipants().add(participant);
        participant.getRooms().add(room);

        participantService.saveParticipant(participant);
        return roomRepository.save(room);
    }

    @Transactional
    public Room removeParticipantFromRoom(Long roomId, Long participantId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        Participant participant = participantService.getParticipantById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        // Remove participant from room
        room.getParticipants().remove(participant);
        participant.getRooms().remove(room);

        // Unblock participant's balance (if before start time)
        // In real scenario, you might have additional logic for refunds
        participant.unblockBalance(room.getEntryPrice());

        participantService.saveParticipant(participant);
        return roomRepository.save(room);
    }

    public List<Room> getRoomsWithBoost() {
        return roomRepository.findByHasBoost(true);
    }

    public List<Room> getUpcomingRooms() {
        return roomRepository.findByStartTimeAfter(java.time.LocalDateTime.now());
    }

    public List<Room> getRoomsByMaxPrice(Integer maxPrice) {
        return roomRepository.findByEntryPriceLessThanEqual(maxPrice);
    }

    public List<Room> getRoomsByMinPrize(Integer minPrize) {
        return roomRepository.findByPrizeFundGreaterThanEqual(minPrize);
    }
}