package com.mindmirror.service;

import com.mindmirror.dto.request.LoginRequest;
import com.mindmirror.dto.request.RegisterRequest;
import com.mindmirror.dto.response.AuthResponse;
import com.mindmirror.dto.response.UserResponse;
import com.mindmirror.entity.User;
import com.mindmirror.entity.enums.Gender;
import com.mindmirror.entity.enums.NotificationType;
import com.mindmirror.entity.enums.Role;
import com.mindmirror.exception.ConflictException;
import com.mindmirror.repository.UserRepository;
import com.mindmirror.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserStatsService statsService;
    private final NotificationService notificationService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, UserStatsService statsService,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.statsService = statsService;
        this.notificationService = notificationService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new ConflictException("Username already taken");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName());
        user.setDateOfBirth(req.dateOfBirth());
        user.setGender(req.gender() == null ? Gender.UNSPECIFIED : req.gender());
        user.setRole(Role.STUDENT);
        user.setLocale(normaliseLocale(req.locale()));
        user.setAgeGroup(ageGroupFor(req.dateOfBirth()));
        user = userRepository.save(user);

        statsService.getOrCreate(user.getId());
        notificationService.create(user.getId(), NotificationType.DAILY_REMINDER,
                "Welcome to MindMirror!", "Добредојде во MindMirror!",
                "Log your first daily check-in to meet your avatar.",
                "Внеси ја првата дневна проверка за да го запознаеш твојот аватар.");

        return authResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.usernameOrEmail())
                .or(() -> userRepository.findByEmail(req.usernameOrEmail()))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return authResponse(user);
    }

    private AuthResponse authResponse(User user) {
        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, jwtService.getExpirationMs(), UserResponse.from(user));
    }

    private String normaliseLocale(String locale) {
        return "mk".equalsIgnoreCase(locale) ? "mk" : "en";
    }

    /** Map real age to the nearest HBSC age band (11/13/15). */
    public static int ageGroupFor(LocalDate dob) {
        if (dob == null) return 15;
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age <= 12) return 11;
        if (age <= 14) return 13;
        return 15;
    }
}
