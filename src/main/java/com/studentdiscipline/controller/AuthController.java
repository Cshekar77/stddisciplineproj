package com.studentdiscipline.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(Authentication authentication) {
        // If already logged in, redirect to their dashboard
        if (authentication != null && authentication.isAuthenticated()) {
            return redirectByRole(authentication);
        }
        return "auth/login";
    }

    // ✅ This is what SecurityConfig calls after successful login
    @GetMapping("/login/redirect")
    public String loginRedirect(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        return redirectByRole(authentication);
    }

    private String redirectByRole(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
            return "redirect:/teacher/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            return "redirect:/student/dashboard";
        }
        return "redirect:/login";
    }
}