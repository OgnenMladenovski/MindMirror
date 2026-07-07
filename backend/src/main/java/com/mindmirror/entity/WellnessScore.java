package com.mindmirror.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "wellness_scores",
        uniqueConstraints = @UniqueConstraint(name = "uk_wellness_daily_log",
                columnNames = {"daily_log_id"}))
public class WellnessScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "daily_log_id", nullable = false)
    private Long dailyLogId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "burnout_index", nullable = false)
    private double burnoutIndex;

    @Column(name = "sleep_score", nullable = false)
    private double sleepScore;

    @Column(name = "wellbeing_score", nullable = false)
    private double wellbeingScore;

    @Column(name = "social_balance_score", nullable = false)
    private double socialBalanceScore;

    @Column(name = "productivity_score", nullable = false)
    private double productivityScore;

    @Column(name = "overall_wellness_score", nullable = false)
    private double overallWellnessScore;

    @Column(name = "risk_level", nullable = false, length = 10)
    private String riskLevel;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDailyLogId() { return dailyLogId; }
    public void setDailyLogId(Long dailyLogId) { this.dailyLogId = dailyLogId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
    public double getBurnoutIndex() { return burnoutIndex; }
    public void setBurnoutIndex(double burnoutIndex) { this.burnoutIndex = burnoutIndex; }
    public double getSleepScore() { return sleepScore; }
    public void setSleepScore(double sleepScore) { this.sleepScore = sleepScore; }
    public double getWellbeingScore() { return wellbeingScore; }
    public void setWellbeingScore(double wellbeingScore) { this.wellbeingScore = wellbeingScore; }
    public double getSocialBalanceScore() { return socialBalanceScore; }
    public void setSocialBalanceScore(double socialBalanceScore) { this.socialBalanceScore = socialBalanceScore; }
    public double getProductivityScore() { return productivityScore; }
    public void setProductivityScore(double productivityScore) { this.productivityScore = productivityScore; }
    public double getOverallWellnessScore() { return overallWellnessScore; }
    public void setOverallWellnessScore(double overallWellnessScore) { this.overallWellnessScore = overallWellnessScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
