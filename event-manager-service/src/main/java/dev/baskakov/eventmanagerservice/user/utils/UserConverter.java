package dev.baskakov.eventmanagerservice.user.utils;

import dev.baskakov.eventmanagerservice.user.User;
import dev.baskakov.eventmanagerservice.user.UserDto;
import dev.baskakov.eventmanagerservice.user.UserEntity;
import dev.baskakov.eventmanagerservice.user.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public User convertToDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getAge(),
                UserRole.valueOf(userEntity.getRole())
        );
    }

    public UserDto convertToDto(User user) {
        return new UserDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }
}
