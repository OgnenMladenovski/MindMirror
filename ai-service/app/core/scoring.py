"""Deterministic wellness scoring.

Every score is on a 0-100 scale. Higher is better for all scores **except**
``burnout_index`` where higher means more burnout. Formulas are intentionally
transparent (weighted, clamped combinations of the daily inputs) so results are
explainable to students aged 11-18.
"""
from __future__ import annotations

from ..schemas import DailyEntry, Scores


def _clamp(value: float, low: float = 0.0, high: float = 100.0) -> float:
    return max(low, min(high, value))


def sleep_score(sleep_hours: float, age_group: int = 15) -> float:
    """Peak score around the recommended ~9h for adolescents; deficit hurts more
    than oversleeping."""
    ideal = 9.0 if age_group <= 13 else 8.5
    diff = sleep_hours - ideal
    if diff >= 0:  # oversleeping — mild penalty
        penalty = (diff ** 1.5) * 6
    else:  # sleeping too little — steeper penalty
        penalty = (abs(diff) ** 1.5) * 10
    return round(_clamp(100 - penalty), 1)


def burnout_index(entry: DailyEntry) -> float:
    """0 = no burnout signals, 100 = severe. Weighted risk factors."""
    stress = entry.stress_level / 10
    sleep_deficit = min(1.0, max(0.0, 8.0 - entry.sleep_hours) / 4.0)
    study_overload = min(1.0, max(0.0, entry.study_hours - 5.0) / 5.0)
    screen = min(1.0, max(0.0, entry.screen_time_hours - 4.0) / 6.0)
    low_energy = (10 - entry.energy_level) / 10
    low_activity = min(1.0, max(0.0, 30 - entry.physical_activity_min) / 30.0)
    low_social = min(1.0, max(0.0, 60 - entry.social_time_min) / 60.0)

    risk = (
        0.30 * stress
        + 0.20 * sleep_deficit
        + 0.15 * low_energy
        + 0.10 * study_overload
        + 0.10 * screen
        + 0.10 * low_activity
        + 0.05 * low_social
    )
    return round(_clamp(risk * 100), 1)


def wellbeing_score(entry: DailyEntry, age_group: int = 15) -> float:
    mood = entry.mood_score / 10
    energy = entry.energy_level / 10
    low_stress = 1 - entry.stress_level / 10
    nutrition = entry.nutrition_quality / 10
    sleep = sleep_score(entry.sleep_hours, age_group) / 100

    value = (
        0.30 * mood
        + 0.20 * energy
        + 0.20 * low_stress
        + 0.15 * nutrition
        + 0.15 * sleep
    )
    return round(_clamp(value * 100), 1)


def social_balance_score(entry: DailyEntry) -> float:
    """Rewards real-world social time; penalises excessive screen time."""
    social = min(1.0, entry.social_time_min / 90.0)
    base = social * 100
    screen_penalty = max(0.0, entry.screen_time_hours - 6.0) * 5
    return round(_clamp(base - screen_penalty), 1)


def productivity_score(entry: DailyEntry, age_group: int = 15) -> float:
    """Healthy study load (bell around ~4h) combined with energy and sleep."""
    study_component = _clamp(1 - abs(entry.study_hours - 4.0) / 6.0, 0, 1)
    energy = entry.energy_level / 10
    sleep = sleep_score(entry.sleep_hours, age_group) / 100
    value = 0.40 * study_component + 0.30 * energy + 0.30 * sleep
    return round(_clamp(value * 100), 1)


def _risk_level(burnout: float, overall: float) -> str:
    if burnout >= 66 or overall < 40:
        return "High"
    if burnout >= 40 or overall < 60:
        return "Medium"
    return "Low"


def analyze(entry: DailyEntry, age_group: int = 15) -> Scores:
    sleep = sleep_score(entry.sleep_hours, age_group)
    burnout = burnout_index(entry)
    wellbeing = wellbeing_score(entry, age_group)
    social = social_balance_score(entry)
    productivity = productivity_score(entry, age_group)

    overall = round(
        _clamp(
            0.30 * wellbeing
            + 0.20 * sleep
            + 0.20 * (100 - burnout)
            + 0.15 * social
            + 0.15 * productivity
        ),
        1,
    )

    return Scores(
        burnout_index=burnout,
        sleep_score=sleep,
        wellbeing_score=wellbeing,
        social_balance_score=social,
        productivity_score=productivity,
        overall_wellness_score=overall,
        risk_level=_risk_level(burnout, overall),
    )
