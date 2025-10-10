package dev.baskakov.eventnotificatorservice.repository;

import dev.baskakov.eventnotificatorservice.model.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<NotificationEntity> findByIdIn(List<Long> ids);

    @Modifying
    @Query("""
            UPDATE NotificationEntity ne
            SET ne.isRead = true, ne.readAt = CURRENT TIMESTAMP 
            WHERE ne.id IN :ids
            """)
    void makeAsRead(
            @Param("ids") List<Long> ids
    );
}
