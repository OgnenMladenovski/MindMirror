package com.mindmirror.repository;

import com.mindmirror.entity.HbscReferenceData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HbscReferenceDataRepository extends JpaRepository<HbscReferenceData, Long> {
    List<HbscReferenceData> findByCountry(String country);
    List<HbscReferenceData> findByCountryAndIndicator(String country, String indicator);
    long countByCountry(String country);
}
