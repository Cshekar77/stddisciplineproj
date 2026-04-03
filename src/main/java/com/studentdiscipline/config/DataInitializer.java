package com.studentdiscipline.config;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.User;
import com.studentdiscipline.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component  // ✅ ENABLED - will create default users
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("✅ DataInitializer is ENABLED - Checking for existing users...");
        
        // Create Admin if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("✅ Admin user created - Username: admin, Password: admin123");
        } else {
            System.out.println("ℹ️ Admin user already exists - skipping");
        }

        // Create Teacher if not exists
        if (userRepository.findByUsername("teacher").isEmpty()) {
            User teacher = new User();
            teacher.setUsername("teacher");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setFirstName("John");
            teacher.setLastName("Teacher");
            teacher.setRole(Role.TEACHER);
            teacher.setEnabled(true);
            userRepository.save(teacher);
            System.out.println("✅ Teacher user created - Username: teacher, Password: teacher123");
        } else {
            System.out.println("ℹ️ Teacher user already exists - skipping");
        }

        // Create Student if not exists
        if (userRepository.findByUsername("student").isEmpty()) {
            User student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setFirstName("Jane");
            student.setLastName("Student");
            student.setRole(Role.STUDENT);
            student.setEnabled(true);
            userRepository.save(student);
            System.out.println("✅ Student user created - Username: student, Password: student123");
        } else {
            System.out.println("ℹ️ Student user already exists - skipping");
        }
    }
}