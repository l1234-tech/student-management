package dev.geeks.studentmanagement.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.geeks.studentmanagement.auth.model.User;

// Repository для пользователей: поиск по username, проверка уникальности username/email.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
