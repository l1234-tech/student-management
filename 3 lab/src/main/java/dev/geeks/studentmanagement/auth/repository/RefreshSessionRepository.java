package dev.geeks.studentmanagement.auth.repository;
import dev.geeks.studentmanagement.auth.model.RefreshSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface RefreshSessionRepository extends JpaRepository<RefreshSession,String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from RefreshSession s where s.id=:id")
    Optional<RefreshSession> findForRotation(@Param("id") String id);
}
