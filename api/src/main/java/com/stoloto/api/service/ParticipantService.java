package com.stoloto.api.service;

import com.stoloto.api.model.Participant;
import com.stoloto.api.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public Optional<Participant> getParticipantById(Long id) {
        return participantRepository.findById(id);
    }

    public Participant createParticipant(Participant participant) {
        // Validate participant data
        if (participant.getBalance() < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        if (participant.getBlockedBalance() < 0) {
            throw new IllegalArgumentException("Blocked balance cannot be negative");
        }
        return participantRepository.save(participant);
    }

    public Participant updateParticipant(Long id, Participant participantDetails) {
        return participantRepository.findById(id)
                .map(participant -> {
                    participant.setBalance(participantDetails.getBalance());
                    participant.setBlockedBalance(participantDetails.getBlockedBalance());
                    return participantRepository.save(participant);
                })
                .orElseGet(() -> {
                    participantDetails.setId(id);
                    return participantRepository.save(participantDetails);
                });
    }

    public void deleteParticipant(Long id) {
        participantRepository.deleteById(id);
    }

    @Transactional
    public Participant addBalance(Long participantId, Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        participant.addBalance(amount);
        return participantRepository.save(participant);
    }

    @Transactional
    public Participant subtractBalance(Long participantId, Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        participant.subtractBalance(amount);
        return participantRepository.save(participant);
    }

    @Transactional
    public Participant blockBalance(Long participantId, Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        participant.blockBalance(amount);
        return participantRepository.save(participant);
    }

    @Transactional
    public Participant unblockBalance(Long participantId, Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        participant.unblockBalance(amount);
        return participantRepository.save(participant);
    }

    public List<Participant> getParticipantsWithMinBalance(Integer minBalance) {
        return participantRepository.findByBalanceGreaterThanEqual(minBalance);
    }

    public List<Participant> getParticipantsWithBlockedBalance() {
        return participantRepository.findByBlockedBalanceGreaterThan(0);
    }

    // Helper method for RoomService
    public Participant saveParticipant(Participant participant) {
        return participantRepository.save(participant);
    }
}