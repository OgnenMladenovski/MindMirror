package com.mindmirror.entity;

import jakarta.persistence.*;

/** HBSC reference values (North Macedonia) used for the comparison feature. */
@Entity
@Table(name = "hbsc_reference_data")
public class HbscReferenceData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String country;

    /** e.g. sleep_hours, physical_activity_days, screen_time_hours, life_satisfaction, stress_high_pct. */
    @Column(nullable = false, length = 60)
    private String indicator;

    /** HBSC age band 11/13/15, or null for an all-ages figure. */
    @Column(name = "age_group")
    private Integer ageGroup;

    /** BOTH / BOYS / GIRLS. */
    @Column(nullable = false, length = 10)
    private String gender = "BOTH";

    @Column(nullable = false)
    private double value;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(nullable = false)
    private int year;

    @Column(length = 300)
    private String source;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getIndicator() { return indicator; }
    public void setIndicator(String indicator) { this.indicator = indicator; }
    public Integer getAgeGroup() { return ageGroup; }
    public void setAgeGroup(Integer ageGroup) { this.ageGroup = ageGroup; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
