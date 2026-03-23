package com.studentdiscipline.config;

import com.studentdiscipline.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppWarmup {

    @Autowired
    private UserRepository userRepository;

    // Runs once when app finishes starting up
    // Pre-warms the DB connection pool so the first login isn't slow
    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        try {
            // Touch the DB so HikariCP opens its connection pool immediately
            userRepository.count();
            System.out.println("✅ App warmup complete — DB connection ready");
        } catch (Exception e) {
            System.out.println("⚠️ Warmup failed (non-critical): " + e.getMessage());
        }
    }
}