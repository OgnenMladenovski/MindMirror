package com.mindmirror.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(name = "title_en", nullable = false, length = 120)
    private String titleEn;

    @Column(name = "title_mk", nullable = false, length = 120)
    private String titleMk;

    @Column(name = "description_en", length = 300)
    private String descriptionEn;

    @Column(name = "description_mk", length = 300)
    private String descriptionMk;

    @Column(length = 40)
    private String icon;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward = 50;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
    public String getTitleMk() { return titleMk; }
    public void setTitleMk(String titleMk) { this.titleMk = titleMk; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
    public String getDescriptionMk() { return descriptionMk; }
    public void setDescriptionMk(String descriptionMk) { this.descriptionMk = descriptionMk; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
}
