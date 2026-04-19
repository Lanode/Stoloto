package com.stoloto.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seats_count", nullable = false)
    private Integer seatsCount;

    @Column(name = "entry_price", nullable = false)
    private Integer entryPrice;

    @Column(name = "prize_fund", nullable = false)
    private Integer prizeFund;

    @Column(name = "has_boost", nullable = false)
    private Boolean hasBoost = false;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @ManyToMany
    @JoinTable(
        name = "room_participants",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Set<Participant> participants = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (hasBoost == null) {
            hasBoost = false;
        }
    }
}