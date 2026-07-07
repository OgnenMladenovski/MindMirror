package com.mindmirror.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "daily_logs",
        uniqueConstraints = @UniqueConstraint(name = "uk_daily_logs_user_date",
                columnNames = {"user_id", "log_date"}))
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "sleep_hours", nullable = false)
    private double sleepHours;

    @Column(name = "stress_level", nullable = false)
    private int stressLevel;

    @Column(name = "mood_score", nullable = false)
    private int moodScore;

    @Column(name = "mood_emoji", length = 8)
    private String moodEmoji;

    @Column(name = "physical_activity_min", nullable = false)
    private int physicalActivityMin;

    @Column(name = "water_intake", nullable = false)
    private double waterIntake;

    @Column(name = "screen_time_hours", nullable = false)
    private double screenTimeHours;

    @Column(name = "study_hours", nullable = false)
    private double studyHours;

    @Column(name = "social_time_min", nullable = false)
    private int socialTimeMin;

    @Column(name = "energy_level", nullable = false)
    private int energyLevel;

    @Column(name = "nutrition_quality", nullable = false)
    private int nutritionQuality;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }
    public int getStressLevel() { return stressLevel; }
    public void setStressLevel(int stressLevel) { this.stressLevel = stressLevel; }
    public int getMoodScore() { return moodScore; }
    public void setMoodScore(int moodScore) { this.moodScore = moodScore; }
    public String getMoodEmoji() { return moodEmoji; }
    public void setMoodEmoji(String moodEmoji) { this.moodEmoji = moodEmoji; }
    public int getPhysicalActivityMin() { return physicalActivityMin; }
    public void setPhysicalActivityMin(int physicalActivityMin) { this.physicalActivityMin = physicalActivityMin; }
    public double getWaterIntake() { return waterIntake; }
    public void setWaterIntake(double waterIntake) { this.waterIntake = waterIntake; }
    public double getScreenTimeHours() { return screenTimeHours; }
    public void setScreenTimeHours(double screenTimeHours) { this.screenTimeHours = screenTimeHours; }
    public double getStudyHours() { return studyHours; }
    public void setStudyHours(double studyHours) { this.studyHours = studyHours; }
    public int getSocialTimeMin() { return socialTimeMin; }
    public void setSocialTimeMin(int socialTimeMin) { this.socialTimeMin = socialTimeMin; }
    public int getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(int energyLevel) { this.energyLevel = energyLevel; }
    public int getNutritionQuality() { return nutritionQuality; }
    public void setNutritionQuality(int nutritionQuality) { this.nutritionQuality = nutritionQuality; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
