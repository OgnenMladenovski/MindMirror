package com.mindmirror.dto.response;

import java.util.List;

/** Compares the user's rolling averages against HBSC North Macedonia references. */
public record HbscComparisonResponse(
        String country,
        int ageGroup,
        List<Row> rows
) {
    public record Row(
            String indicator,
            String labelEn,
            String labelMk,
            Double userValue,
            double hbscValue,
            Double difference,
            String unit,
            int year,
            String source
    ) { }
}
