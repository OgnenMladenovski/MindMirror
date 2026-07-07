package com.mindmirror.config;

import com.mindmirror.client.AiServiceClient;
import com.mindmirror.dto.request.DailyLogRequest;
import com.mindmirror.entity.DailyLog;
import com.mindmirror.entity.User;
import com.mindmirror.entity.enums.Gender;
import com.mindmirror.entity.enums.Role;
import com.mindmirror.repository.DailyLogRepository;
import com.mindmirror.repository.UserRepository;
import com.mindmirror.service.DailyLogService;
import com.mindmirror.service.UserStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Seeds an admin and a demo student (with ~30 days of logs) on first startup. */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataSeeder.class);
    private static final int DEMO_DAYS = 30;

    private final MindMirrorProperties props;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserStatsService statsService;
    private final DailyLogService dailyLogService;
    private final DailyLogRepository dailyLogRepository;
    private final AiServiceClient aiClient;

    public DataSeeder(MindMirrorProperties props, UserRepository userRepository,
                      PasswordEncoder passwordEncoder, UserStatsService statsService,
                      DailyLogService dailyLogService, DailyLogRepository dailyLogRepository,
                      AiServiceClient aiClient) {
        this.props = props;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.statsService = statsService;
        this.dailyLogService = dailyLogService;
        this.dailyLogRepository = dailyLogRepository;
        this.aiClient = aiClient;
    }

    @Override
    public void run(String... args) {
        if (!props.isSeedDemo()) {
            return;
        }
        ensureUser("admin", "admin@mindmirror.app", "admin1234", Role.ADMIN, "MindMirror Admin");
        User demo = ensureUser("demo", "demo@mindmirror.app", "demo1234", Role.STUDENT, "Demo Student");

        if (dailyLogRepository.countByUserId(demo.getId()) == 0) {
            seedDemoLogs(demo);
        }
    }

    private User ensureUser(String username, String email, String rawPassword, Role role, String fullName) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            User u = new User();
            u.setUsername(username);
            u.setEmail(email);
            u.setPasswordHash(passwordEncoder.encode(rawPassword));
            u.setFullName(fullName);
            u.setRole(role);
            u.setGender(Gender.UNSPECIFIED);
            u.setAgeGroup(15);
            u.setLocale("en");
            u = userRepository.save(u);
            statsService.getOrCreate(u.getId());
            LOG.info("Seeded {} user '{}'", role, username);
            return u;
        });
    }

    private void seedDemoLogs(User demo) {
        boolean aiUp = aiClient.isHealthy();
        LOG.info("Seeding {} demo logs (AI service {})", DEMO_DAYS, aiUp ? "online" : "offline — logs only");
        LocalDate today = LocalDate.now();
        for (int i = 0; i < DEMO_DAYS; i++) {
            LocalDate date = today.minusDays(DEMO_DAYS - 1L - i);
            DailyLogRequest req = generate(i, date);
            if (aiUp) {
                dailyLogService.create(demo.getId(), req);
            } else {
                saveRaw(demo.getId(), req);
            }
        }
        LOG.info("Demo data ready. Login: demo / demo1234 (admin / admin1234).");
    }

    /** A realistic month: good start, an exam-stress dip mid-month, then recovery. */
    private DailyLogRequest generate(int i, LocalDate date) {
        double exam = Math.exp(-Math.pow((i - 17) / 4.0, 2)); // peak stress around day 17
        double n = Math.sin(i * 1.3) * 0.4;                    // gentle deterministic wobble

        double sleep = clamp(7.9 - 1.8 * exam + n, 4.5, 9.5);
        int stress = (int) Math.round(clamp(4 + 5 * exam + n, 1, 10));
        int mood = (int) Math.round(clamp(7 - 3 * exam + n, 1, 10));
        int activity = (int) Math.round(clamp(45 - 25 * exam + 10 * n, 0, 120));
        double water = clamp(1.8 - 0.4 * exam + 0.1 * n, 0.5, 3.0);
        double screen = clamp(4.5 + 3 * exam + n, 1, 12);
        double study = clamp(3 + 4 * exam + n, 0, 10);
        int social = (int) Math.round(clamp(60 - 30 * exam + 10 * n, 0, 150));
        int energy = (int) Math.round(clamp(7 - 3 * exam + n, 1, 10));
        int nutrition = (int) Math.round(clamp(7 - 2 * exam + n, 1, 10));

        return new DailyLogRequest(date, round1(sleep), stress, mood, emoji(mood), activity,
                round1(water), round1(screen), round1(study), social, energy, nutrition, null);
    }

    private void saveRaw(Long userId, DailyLogRequest r) {
        DailyLog l = new DailyLog();
        l.setUserId(userId);
        l.setLogDate(r.logDate());
        l.setSleepHours(r.sleepHours());
        l.setStressLevel(r.stressLevel());
        l.setMoodScore(r.moodScore());
        l.setMoodEmoji(r.moodEmoji());
        l.setPhysicalActivityMin(r.physicalActivityMin());
        l.setWaterIntake(r.waterIntake());
        l.setScreenTimeHours(r.screenTimeHours());
        l.setStudyHours(r.studyHours());
        l.setSocialTimeMin(r.socialTimeMin());
        l.setEnergyLevel(r.energyLevel());
        l.setNutritionQuality(r.nutritionQuality());
        dailyLogRepository.save(l);
        statsService.recordLog(userId, r.logDate());
    }

    private static String emoji(int mood) {
        if (mood >= 8) return "😄";
        if (mood >= 6) return "🙂";
        if (mood >= 4) return "😐";
        return "😔";
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
