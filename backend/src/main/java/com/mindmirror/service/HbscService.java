package com.mindmirror.service;

import com.mindmirror.dto.response.HbscComparisonResponse;
import com.mindmirror.dto.response.HbscComparisonResponse.Row;
import com.mindmirror.entity.DailyLog;
import com.mindmirror.entity.HbscReferenceData;
import com.mindmirror.entity.User;
import com.mindmirror.repository.DailyLogRepository;
import com.mindmirror.repository.HbscReferenceDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Compares a student's habits against HBSC North Macedonia reference values. */
@Service
public class HbscService {

    public static final String COUNTRY = "north-macedonia";

    private record IndicatorDef(String key, String labelEn, String labelMk) { }

    private static final List<IndicatorDef> INDICATORS = List.of(
            new IndicatorDef("sleep_hours", "Sleep", "Спиење"),
            new IndicatorDef("physical_activity_min", "Physical Activity", "Физичка активност"),
            new IndicatorDef("screen_time_hours", "Screen Time", "Време пред екран"),
            new IndicatorDef("life_satisfaction", "Life Satisfaction", "Задоволство од животот"),
            new IndicatorDef("stress_level", "Stress", "Стрес")
    );

    private final HbscReferenceDataRepository referenceRepository;
    private final DailyLogRepository dailyLogRepository;
    private final UserService userService;

    public HbscService(HbscReferenceDataRepository referenceRepository,
                       DailyLogRepository dailyLogRepository, UserService userService) {
        this.referenceRepository = referenceRepository;
        this.dailyLogRepository = dailyLogRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public HbscComparisonResponse compare(Long userId) {
        User user = userService.require(userId);
        LocalDate today = LocalDate.now();
        List<DailyLog> logs = dailyLogRepository
                .findByUserIdAndLogDateBetweenOrderByLogDate(userId, today.minusDays(30), today);

        Double sleep = avg(logs, DailyLog::getSleepHours);
        Double activity = avg(logs, l -> (double) l.getPhysicalActivityMin());
        Double screen = avg(logs, DailyLog::getScreenTimeHours);
        Double mood = avg(logs, l -> (double) l.getMoodScore());
        Double stress = avg(logs, l -> (double) l.getStressLevel());

        List<Row> rows = buildComparison(sleep, activity, screen, mood, stress, user.getAgeGroup());
        return new HbscComparisonResponse(COUNTRY, user.getAgeGroup(), rows);
    }

    /** Reusable comparison used both for a single user and for admin (global) averages. */
    @Transactional(readOnly = true)
    public List<Row> buildComparison(Double sleep, Double activity, Double screen,
                                     Double mood, Double stress, int ageGroup) {
        List<HbscReferenceData> refs = referenceRepository.findByCountry(COUNTRY);
        List<Row> rows = new ArrayList<>();
        addRow(rows, refs, "sleep_hours", ageGroup, sleep);
        addRow(rows, refs, "physical_activity_min", ageGroup, activity);
        addRow(rows, refs, "screen_time_hours", ageGroup, screen);
        addRow(rows, refs, "life_satisfaction", ageGroup, mood);
        addRow(rows, refs, "stress_level", ageGroup, stress);
        return rows;
    }

    @Transactional(readOnly = true)
    public List<HbscReferenceData> referenceData() {
        return referenceRepository.findByCountry(COUNTRY);
    }

    // --- helpers ------------------------------------------------------------

    private void addRow(List<Row> rows, List<HbscReferenceData> refs, String key,
                        int ageGroup, Double userValue) {
        IndicatorDef def = INDICATORS.stream().filter(i -> i.key().equals(key)).findFirst().orElse(null);
        Optional<HbscReferenceData> ref = lookup(refs, key, ageGroup);
        if (def == null || ref.isEmpty()) return;
        HbscReferenceData r = ref.get();
        Double diff = userValue == null ? null : round1(userValue - r.getValue());
        rows.add(new Row(key, def.labelEn(), def.labelMk(),
                userValue == null ? null : round1(userValue),
                round1(r.getValue()), diff, r.getUnit(), r.getYear(), r.getSource()));
    }

    private Optional<HbscReferenceData> lookup(List<HbscReferenceData> refs, String key, int ageGroup) {
        return refs.stream()
                .filter(r -> r.getIndicator().equals(key) && "BOTH".equals(r.getGender())
                        && r.getAgeGroup() != null && r.getAgeGroup() == ageGroup)
                .findFirst()
                .or(() -> refs.stream()
                        .filter(r -> r.getIndicator().equals(key) && "BOTH".equals(r.getGender()))
                        .findFirst())
                .or(() -> refs.stream().filter(r -> r.getIndicator().equals(key)).findFirst());
    }

    private Double avg(List<DailyLog> logs, java.util.function.ToDoubleFunction<DailyLog> f) {
        if (logs.isEmpty()) return null;
        return logs.stream().mapToDouble(f).average().orElse(Double.NaN);
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
