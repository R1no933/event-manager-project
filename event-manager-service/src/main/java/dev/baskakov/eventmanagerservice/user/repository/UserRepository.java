package dev.baskakov.eventmanagerservice.user.repository;

import dev.baskakov.eventmanagerservice.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String login);

}
