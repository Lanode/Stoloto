package com.stoloto.api.repository;

import com.stoloto.api.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findByBalanceGreaterThanEqual(Integer minBalance);

    List<Participant> findByBlockedBalanceGreaterThan(Integer minBlockedBalance);

    List<Participant> findByBalanceLessThan(Integer maxBalance);
}