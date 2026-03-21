// ================================================================
// FILE 3: NotificationRepository.java + DeviceTokenRepository
// ================================================================
package com.fintrix.modules.notification.repository;

import com.fintrix.modules.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, String> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(
            String userId, Pageable pageable);

    long countByUserIdAndIsReadFalse(String userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true " +
        "WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") String userId);
}