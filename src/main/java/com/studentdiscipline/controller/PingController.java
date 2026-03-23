package com.studentdiscipline.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PingController {

    // ✅ Lightweight keep-alive endpoint
    // Point your cron-job.org monitor to: https://your-app.cleverapps.io/ping
    // This is faster than /login because it skips Thymeleaf rendering + security checks
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of(
            "status", "ok",
            "app", "student-discipline-system"
        ));
    }
}