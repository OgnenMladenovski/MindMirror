# HBSC Data Sources — North Macedonia

MindMirror compares each student's habits against **HBSC (Health Behaviour in
School-aged Children)** reference values for **North Macedonia**.

## Provenance
- **Data browser:** https://data-browser.hbsc.org/country-data-landing/countries-data/?country=north-macedonia
- **International report:** *A focus on adolescent mental health and well-being* —
  HBSC 2021/2022 survey (WHO Regional Office for Europe).

The data browser is a JavaScript single-page app, so figures cannot be scraped
programmatically. The values shipped with MindMirror are **approximate**, drawn from
the published HBSC 2021/2022 material for North Macedonia (e.g. North Macedonia reports
among the highest adolescent mental well-being and life satisfaction; the international
average life satisfaction is 7.5/10). They are structured so exact figures drop in easily.

## Indicators used in the comparison
| indicator | unit | meaning | age bands |
| --- | --- | --- | --- |
| `life_satisfaction` | /10 | Cantril ladder life satisfaction | 11 / 13 / 15 |
| `sleep_hours` | h | average sleep on school days | 11 / 13 / 15 |
| `physical_activity_min` | min/day | approx. daily moderate-to-vigorous activity | 11 / 13 / 15 |
| `screen_time_hours` | h | average daily screen time | 11 / 13 / 15 |
| `stress_level` | /10 | self-reported pressure/stress proxy | 11 / 13 / 15 |

## How to refresh with exact values
1. Open the data browser link above, select **North Macedonia**, then a measure
   (e.g. *Life satisfaction*), and read the value for each age group (11/13/15).
2. Update the numbers in **`backend/src/main/resources/db/migration/V2__reference_and_catalog.sql`**
   (the database source of truth) and, for readability, the mirror file
   **`backend/src/main/resources/db/data/hbsc_north_macedonia.json`**.
3. Because Flyway migrations are immutable once applied, either reset the database
   (`docker compose down -v`) so `V2` re-runs, or add a new migration
   `V3__update_hbsc_values.sql` with `UPDATE hbsc_reference_data ...` statements.

The comparison logic lives in
[`HbscService`](../backend/src/main/java/com/mindmirror/service/HbscService.java) and is
exposed at `GET /api/hbsc/comparison` (per user) and `GET /api/hbsc/reference` (raw).
