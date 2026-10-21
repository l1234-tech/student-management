// предоставляет доступ к к пользователям , которыые хранятся в таблице app_users
// наследование от JpaRepository

package dev.geeks.studentmanagement.auth.repository;

import dev.geeks.studentmanagement.auth.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
