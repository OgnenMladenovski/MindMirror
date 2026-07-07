package com.mindmirror;

import com.mindmirror.service.AuthService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgeGroupTest {

    @Test
    void mapsRealAgeToNearestHbscBand() {
        assertEquals(15, AuthService.ageGroupFor(null));
        assertEquals(11, AuthService.ageGroupFor(LocalDate.now().minusYears(11)));
        assertEquals(13, AuthService.ageGroupFor(LocalDate.now().minusYears(14)));
        assertEquals(15, AuthService.ageGroupFor(LocalDate.now().minusYears(16)));
    }
}
