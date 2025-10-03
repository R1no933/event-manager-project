package dev.baskakov.eventmanagerservice.events.registration.repository;

import dev.baskakov.eventmanagerservice.events.event.model.entity.EventEntity;
import dev.baskakov.eventmanagerservice.events.registration.model.entity.EventRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    @Query("""
            SELECT ere FROM EventRegistrationEntity ere
            WHERE ere.userId = :userId
            AND ere.event.id = :eventId
            """)
    Optional<EventRegistrationEntity> findExistsRegistration(
            @Param("userId") Long userId,
            @Param("eventId") Long eventId
    );


    @Query("SELECT ere.event FROM EventRegistrationEntity ere WHERE ere.userId = :userId")
    List<EventEntity> findRegisteredEventsForUser(
            @Param("userId") Long userId
    );
}
