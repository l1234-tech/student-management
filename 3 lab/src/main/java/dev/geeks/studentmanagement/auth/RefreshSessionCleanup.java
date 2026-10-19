package dev.geeks.studentmanagement.auth;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Removes expired refresh-session records after they can no longer be used. */
@Component
@EnableScheduling
public class RefreshSessionCleanup {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshSessionCleanup.class);
    private final AuthService authService;

    public RefreshSessionCleanup(AuthService authService) {
        this.authService = authService;
    }

    @Scheduled(cron = "${app.auth.refresh-cleanup-cron:0 0 3 * * *}")
    public void cleanupExpiredSessions() {
        int removed = authService.cleanupExpiredSessions(Instant.now());
        if (removed > 0) {
            LOGGER.info("Removed {} expired refresh sessions", removed);
        }
    }
}
