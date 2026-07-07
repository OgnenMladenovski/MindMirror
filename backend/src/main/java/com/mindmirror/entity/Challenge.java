package com.mindmirror.entity;

import com.mindmirror.entity.enums.ChallengeStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "challenges",
        uniqueConstraints = @UniqueConstraint(name = "uk_challenge_user_date",
                columnNames = {"user_id", "challenge_date"}))
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "challenge_date", nullable = false)
    private LocalDate challengeDate;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(name = "title_en", nullable = false, length = 160)
    private String titleEn;

    @Column(name = "title_mk", nullable = false, length = 160)
    private String titleMk;

    @Column(name = "description_en", length = 400)
    private String descriptionEn;

    @Column(name = "description_mk", length = 400)
    private String descriptionMk;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward = 20;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChallengeStatus status = ChallengeStatus.ASSIGNED;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getChallengeDate() { return challengeDate; }
    public void setChallengeDate(LocalDate challengeDate) { this.challengeDate = challengeDate; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
    public String getTitleMk() { return titleMk; }
    public void setTitleMk(String titleMk) { this.titleMk = titleMk; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
    public String getDescriptionMk() { return descriptionMk; }
    public void setDescriptionMk(String descriptionMk) { this.descriptionMk = descriptionMk; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
