from datetime import date, timedelta

from app.core import recommender, trends_engine
from app.schemas import DailyEntry


def _series(sleeps, **fixed):
    base = dict(
        stress_level=5, mood_score=6, physical_activity_min=40, water_intake=2.0,
        screen_time_hours=4.0, study_hours=3.0, social_time_min=60, energy_level=6,
        nutrition_quality=7,
    )
    base.update(fixed)
    start = date(2026, 6, 1)
    return [DailyEntry(log_date=start + timedelta(days=i), sleep_hours=s, **base)
            for i, s in enumerate(sleeps)]


def test_empty_history_returns_onboarding():
    recs = recommender.generate([])
    assert recs and recs[0].category == "onboarding"


def test_declining_sleep_flagged_bilingually():
    recs = recommender.generate(_series([8, 8, 7.5, 7, 6.5, 6, 5.5]))
    sleep_recs = [r for r in recs if r.category == "sleep"]
    assert sleep_recs
    assert sleep_recs[0].text_en and sleep_recs[0].text_mk
    assert sleep_recs[0].text_en != sleep_recs[0].text_mk


def test_high_screen_time_flagged():
    recs = recommender.generate(_series([8] * 7, screen_time_hours=8.0))
    assert any(r.category == "screen" for r in recs)


def test_trends_need_enough_data():
    insights = trends_engine.generate(_series([8, 7]))
    assert insights[0].kind == "info"
