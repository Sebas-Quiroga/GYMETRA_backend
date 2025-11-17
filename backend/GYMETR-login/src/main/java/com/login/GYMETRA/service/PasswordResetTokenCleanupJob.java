package com.login.GYMETRA.service;


import com.login.GYMETRA.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetTokenCleanupJob {

    private final PasswordResetTokenRepository tokenRepository;

    // Corre cada 15 minutos (mm ss hh día mes díaSemana)
    // Formato: segundo minuto hora día-del-mes mes día-de-la-semana
    @Scheduled(cron = "0 */15 * * * *")
    public void purgeExpiredTokens() {
        int deleted = tokenRepository.deleteAllExpiredSince(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Limpieza de tokens: {} tokens expirados eliminados", deleted);
        }
    }
}
