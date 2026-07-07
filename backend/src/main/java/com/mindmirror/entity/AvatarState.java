package com.mindmirror.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "avatar_states")
public class AvatarState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(nullable = false, length = 20)
    private String state;

    @Column(nullable = false, length = 20)
    private String animation;

    @Column(name = "attributes_json", length = 1000)
    private String attributesJson;

    @Column(name = "caption_en", length = 200)
    private String captionEn;

    @Column(name = "caption_mk", length = 200)
    private String captionMk;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getAnimation() { return animation; }
    public void setAnimation(String animation) { this.animation = animation; }
    public String getAttributesJson() { return attributesJson; }
    public void setAttributesJson(String attributesJson) { this.attributesJson = attributesJson; }
    public String getCaptionEn() { return captionEn; }
    public void setCaptionEn(String captionEn) { this.captionEn = captionEn; }
    public String getCaptionMk() { return captionMk; }
    public void setCaptionMk(String captionMk) { this.captionMk = captionMk; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
