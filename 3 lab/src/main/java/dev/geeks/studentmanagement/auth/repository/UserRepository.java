package dev.geeks.studentmanagement.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.geeks.studentmanagement.auth.model.User;

// =============================================================================
// Компонент:   UserRepository (interface)
// Модуль:      dev.geeks.studentmanagement.auth.repository
// Назначение:  доступ к данным пользователей (Spring Data JPA).
// Методы:      findByUsername, existsByUsername, existsByEmail.
// Статус: реализовано.
// =============================================================================

// Repository для пользователей: поиск по username, проверка уникальности username/email.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
