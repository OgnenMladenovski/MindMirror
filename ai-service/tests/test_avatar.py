from app.core import avatar, scoring
from app.schemas import DailyEntry


def _entry(**over):
    base = dict(
        sleep_hours=8.5, stress_level=3, mood_score=8, physical_activity_min=45,
        water_intake=2.0, screen_time_hours=3.0, study_hours=3.0, social_time_min=90,
        energy_level=8, nutrition_quality=8,
    )
    base.update(over)
    return DailyEntry(**base)


def test_healthy_maps_to_positive_state():
    resp = avatar.derive_from_entry(_entry())
    assert resp.state in ("EXCELLENT", "HAPPY")
    assert resp.attributes["glow"] is True
    assert resp.caption_en and resp.caption_mk


def test_burnout_maps_to_burned_out():
    entry = _entry(sleep_hours=4.5, stress_level=10, energy_level=2, screen_time_hours=11,
                   study_hours=9, mood_score=2, physical_activity_min=0, social_time_min=0)
    resp = avatar.derive(scoring.analyze(entry), entry)
    assert resp.state in ("BURNED_OUT", "EXHAUSTED", "STRESSED")
    assert resp.attributes["dark_circles"] is True


def test_all_states_have_metadata():
    for meta in avatar._STATE_META.values():
        assert len(meta) == 3
