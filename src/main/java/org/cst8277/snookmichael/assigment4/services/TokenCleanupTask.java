package org.cst8277.snookmichael.assigment4.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupTask {

    private final AuthService authService;

    public TokenCleanupTask(AuthService authService) {
        this.authService = authService;
    }

    @Scheduled(fixedRate = 60000)
    public void cleanUpExpiredTokens() {
        authService.invalidateExpiredTokens();
    }
}
