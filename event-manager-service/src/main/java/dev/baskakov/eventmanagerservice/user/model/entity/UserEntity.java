package dev.baskakov.eventmanagerservice.user.model.entity;

import dev.baskakov.eventmanagerservice.user.model.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String login;
    private String password;
    private Integer age;
    private String role;

    public UserEntity() {
    }

    public UserEntity(
            Long id,
            String login,
            String password,
            Integer age
    ) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.age = age;
        this.role = UserRole.USER.name();
    }

    public UserEntity(
            Long id,
            String login,
            String password,
            Integer age,
            String role
    ) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.age = age;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
