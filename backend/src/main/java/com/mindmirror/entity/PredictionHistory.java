package com.mindmirror.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "prediction_history")
public class PredictionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** The day the prediction was generated (based on data up to this date). */
    @Column(name = "prediction_date", nullable = false)
    private LocalDate predictionDate;

    /** The day the prediction is for (usually prediction_date + 1). */
    @Column(name = "for_date", nullable = false)
    private LocalDate forDate;

    @Column(name = "mood_tomorrow", nullable = false)
    private double moodTomorrow;

    @Column(name = "burnout_tomorrow", nullable = false)
    private double burnoutTomorrow;

    @Column(name = "stress_tomorrow", nullable = false)
    private double stressTomorrow;

    @Column(name = "sleep_quality_tomorrow", nullable = false)
    private double sleepQualityTomorrow;

    @Column(name = "recommended_activity", length = 40)
    private String recommendedActivity;

    @Column(name = "model_version", length = 20)
    private String modelVersion;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDate predictionDate) { this.predictionDate = predictionDate; }
    public LocalDate getForDate() { return forDate; }
    public void setForDate(LocalDate forDate) { this.forDate = forDate; }
    public double getMoodTomorrow() { return moodTomorrow; }
    public void setMoodTomorrow(double moodTomorrow) { this.moodTomorrow = moodTomorrow; }
    public double getBurnoutTomorrow() { return burnoutTomorrow; }
    public void setBurnoutTomorrow(double burnoutTomorrow) { this.burnoutTomorrow = burnoutTomorrow; }
    public double getStressTomorrow() { return stressTomorrow; }
    public void setStressTomorrow(double stressTomorrow) { this.stressTomorrow = stressTomorrow; }
    public double getSleepQualityTomorrow() { return sleepQualityTomorrow; }
    public void setSleepQualityTomorrow(double sleepQualityTomorrow) { this.sleepQualityTomorrow = sleepQualityTomorrow; }
    public String getRecommendedActivity() { return recommendedActivity; }
    public void setRecommendedActivity(String recommendedActivity) { this.recommendedActivity = recommendedActivity; }
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
