package com.studentdiscipline.config;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.User;
import com.studentdiscipline.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User(
                "admin",
                passwordEncoder.encode("admin123"),
                "Admin",
                "User",
                Role.ADMIN
            );
            userRepository.save(admin);
            System.out.println("✅ Default admin created: admin / admin123");
        }

        if (userRepository.findByUsername("teacher1") == null) {
            User teacher = new User(
                "teacher1",
                passwordEncoder.encode("teacher123"),
                "Teacher",
                "One",
                Role.TEACHER
            );
            userRepository.save(teacher);
            System.out.println("✅ Default teacher created: teacher1 / teacher123");
        }

        if (userRepository.findByUsername("student1") == null) {
            User student = new User(
                "student1",
                passwordEncoder.encode("student123"),
                "Student",
                "One",
                Role.STUDENT
            );
            userRepository.save(student);
            System.out.println("✅ Default student created: student1 / student123");
        }
    }
}