from app.core import scoring
from app.schemas import DailyEntry


def _entry(**over):
    base = dict(
        sleep_hours=8.5, stress_level=3, mood_score=8, physical_activity_min=45,
        water_intake=2.0, screen_time_hours=3.0, study_hours=3.0, social_time_min=90,
        energy_level=8, nutrition_quality=8,
    )
    base.update(over)
    return DailyEntry(**base)


def test_scores_bounded_0_100():
    s = scoring.analyze(_entry())
    for v in [s.burnout_index, s.sleep_score, s.wellbeing_score,
              s.social_balance_score, s.productivity_score, s.overall_wellness_score]:
        assert 0 <= v <= 100


def test_healthy_day_low_risk():
    s = scoring.analyze(_entry())
    assert s.risk_level == "Low"
    assert s.overall_wellness_score > 70
    assert s.burnout_index < 40


def test_unhealthy_day_high_risk():
    s = scoring.analyze(_entry(
        sleep_hours=4.5, stress_level=10, mood_score=2, physical_activity_min=0,
        screen_time_hours=11, study_hours=9, social_time_min=0, energy_level=1,
        nutrition_quality=2, water_intake=0.5,
    ))
    assert s.risk_level == "High"
    assert s.burnout_index > 65


def test_sleep_score_peaks_near_ideal():
    assert scoring.sleep_score(8.5) > scoring.sleep_score(5.5)
    assert scoring.sleep_score(8.5) > scoring.sleep_score(11.5)


def test_compact_shape_matches_spec():
    s = scoring.analyze(_entry())
    assert set(s.compact.keys()) == {"burnout", "sleep", "social", "wellness", "risk"}
