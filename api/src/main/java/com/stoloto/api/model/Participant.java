package com.stoloto.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer balance = 0;

    @Column(name = "blocked_balance", nullable = false)
    private Integer blockedBalance = 0;

    @ManyToMany(mappedBy = "participants")
    private Set<Room> rooms = new HashSet<>();

    // Helper methods for managing balance
    public void addBalance(Integer amount) {
        this.balance += amount;
    }

    public void subtractBalance(Integer amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
        } else {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    public void blockBalance(Integer amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            this.blockedBalance += amount;
        } else {
            throw new IllegalArgumentException("Insufficient balance to block");
        }
    }

    public void unblockBalance(Integer amount) {
        if (this.blockedBalance >= amount) {
            this.blockedBalance -= amount;
            this.balance += amount;
        } else {
            throw new IllegalArgumentException("Insufficient blocked balance to unblock");
        }
    }
}