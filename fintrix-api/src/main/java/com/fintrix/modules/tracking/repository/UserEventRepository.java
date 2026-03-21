// ================================================================
// FILE 3: TrackingRepository.java + AffiliateRepository.java
// ================================================================
package com.fintrix.modules.tracking.repository;

import com.fintrix.modules.tracking.domain.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserEventRepository
        extends JpaRepository<UserEvent, String> {

    @Query("SELECT e.eventType, COUNT(e) FROM UserEvent e " +
           "WHERE e.userId = :userId " +
           "GROUP BY e.eventType")
    List<Object[]> countByEventTypeForUser(@Param("userId") String userId);

    @Query("SELECT e.entityId, COUNT(e) as views FROM UserEvent e " +
           "WHERE e.eventType = 'CARD_VIEW' " +
           "AND e.createdAt > :since " +
           "GROUP BY e.entityId ORDER BY views DESC")
    List<Object[]> topViewedCards(@Param("since") LocalDateTime since);

    
}