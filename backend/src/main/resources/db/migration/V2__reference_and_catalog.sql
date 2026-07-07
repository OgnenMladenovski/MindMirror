-- Achievement catalog (bilingual) -------------------------------------------
INSERT INTO achievements (code, title_en, title_mk, description_en, description_mk, icon, xp_reward) VALUES
 ('SEVEN_HEALTHY_DAYS', '7 Healthy Days', '7 здрави дена',
  'Reach an overall wellness of 70+ on seven days.', 'Постигни целокупна благосостојба од 70+ во седум дена.', '🌟', 100),
 ('EARLY_SLEEPER', 'Early Sleeper', 'Ран спијач',
  'Sleep 8+ hours on five days.', 'Спиј 8+ часа во пет дена.', '😴', 50),
 ('HYDRATION_MASTER', 'Hydration Master', 'Мајстор за хидратација',
  'Drink 2L+ of water on five days.', 'Испиј 2Л+ вода во пет дена.', '💧', 50),
 ('STRESS_FIGHTER', 'Stress Fighter', 'Борец против стрес',
  'Keep stress at 4 or below on five days.', 'Задржи стрес на 4 или помалку во пет дена.', '🧘', 50),
 ('FITNESS_HERO', 'Fitness Hero', 'Херој на фитнесот',
  'Do 60+ minutes of activity on five days.', 'Направи 60+ минути активност во пет дена.', '🏃', 75),
 ('MOOD_EXPLORER', 'Mood Explorer', 'Истражувач на расположението',
  'Log your mood for ten days.', 'Внеси го расположението десет дена.', '🎭', 50);

-- HBSC North Macedonia reference values ---------------------------------------
-- Approximate values based on the HBSC 2021/2022 international report / data
-- browser for North Macedonia. Verify/refresh exact figures at
-- https://data-browser.hbsc.org (see docs/hbsc-data-sources.md).
INSERT INTO hbsc_reference_data (country, indicator, age_group, gender, value, unit, year, source) VALUES
 ('north-macedonia', 'life_satisfaction', 11, 'BOTH', 8.3, '/10', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'life_satisfaction', 13, 'BOTH', 8.0, '/10', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'life_satisfaction', 15, 'BOTH', 7.7, '/10', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),

 ('north-macedonia', 'sleep_hours', 11, 'BOTH', 8.6, 'h', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'sleep_hours', 13, 'BOTH', 8.1, 'h', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'sleep_hours', 15, 'BOTH', 7.5, 'h', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),

 ('north-macedonia', 'physical_activity_min', 11, 'BOTH', 55, 'min/day', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'physical_activity_min', 13, 'BOTH', 50, 'min/day', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'physical_activity_min', 15, 'BOTH', 45, 'min/day', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),

 ('north-macedonia', 'screen_time_hours', 11, 'BOTH', 4.5, 'h', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'screen_time_hours', 13, 'BOTH', 5.5, 'h', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'screen_time_hours', 15, 'BOTH', 6.5, 'h', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),

 ('north-macedonia', 'stress_level', 11, 'BOTH', 4.5, '/10', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'stress_level', 13, 'BOTH', 5.2, '/10', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org'),
 ('north-macedonia', 'stress_level', 15, 'BOTH', 5.8, '/10', 2022, 'HBSC 2021/2022 (North Macedonia), approximate — verify at data-browser.hbsc.org');
