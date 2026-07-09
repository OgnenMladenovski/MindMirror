"""Trend-aware, bilingual recommendation engine.

Rules look at multiple days of history (not just the latest entry) and emit
recommendations in both English and Macedonian. New rules should append a
``Recommendation`` — keep messages short and actionable for students.
"""
from __future__ import annotations

from statistics import mean

from ..schemas import DailyEntry, Recommendation


def _avg(values: list[float]) -> float:
    return mean(values) if values else 0.0


def _slope(values: list[float]) -> float:
    """Simple first-vs-second-half delta; positive => increasing over the window."""
    if len(values) < 2:
        return 0.0
    half = len(values) // 2
    first = _avg(values[:half])
    second = _avg(values[half:])
    return second - first


def generate(history: list[DailyEntry]) -> list[Recommendation]:
    if not history:
        return [
            Recommendation(
                category="onboarding",
                severity="info",
                text_en="Log your first few days so MindMirror can spot patterns and personalise advice.",
                text_mk="Внеси ги првите неколку дена за да може MindMirror да ги препознае обрасците и да ти даде персонализиран совет.",
            )
        ]

    recs: list[Recommendation] = []
    window = history[-7:]
    latest = history[-1]

    sleep = [d.sleep_hours for d in window]
    stress = [d.stress_level for d in window]
    screen = [d.screen_time_hours for d in window]
    activity = [d.physical_activity_min for d in window]
    water = [d.water_intake for d in window]
    social = [d.social_time_min for d in window]

    # --- Sleep ---------------------------------------------------------------
    if len(sleep) >= 4 and _slope(sleep) <= -0.6:
        recs.append(Recommendation(
            category="sleep", severity="medium",
            text_en="Your sleep has been decreasing over the last few days. Try going to bed before 23:00 tonight.",
            text_mk="Твоето спиење се намалува во последните неколку дена. Обиди се да си легнеш пред 23:00 вечерва.",
        ))
    elif _avg(sleep) < 7:
        recs.append(Recommendation(
            category="sleep", severity="high",
            text_en=f"You are averaging {_avg(sleep):.1f}h of sleep. Aim for 8-9h — a consistent bedtime helps most.",
            text_mk=f"Во просек спиеш {_avg(sleep):.1f}ч. Целѝ на 8-9ч — редовно време за спиење помага најмногу.",
        ))

    # --- Screen time ---------------------------------------------------------
    if _avg(screen) > 6:
        recs.append(Recommendation(
            category="screen", severity="medium",
            text_en="Screen time is high this week. Reduce it by one hour and put the phone away after 21:00.",
            text_mk="Времето пред екран е високо оваа недела. Намали го за еден час и тргни го телефонот по 21:00.",
        ))

    # --- Physical activity ---------------------------------------------------
    if _avg(activity) < 30:
        recs.append(Recommendation(
            category="activity", severity="medium",
            text_en="Try to move more — a 30-minute walk outdoors can lift your mood and energy.",
            text_mk="Обиди се да се движиш повеќе — 30-минутна прошетка на отворено може да ти го подигне расположението и енергијата.",
        ))

    # --- Stress --------------------------------------------------------------
    if _avg(stress) >= 7:
        recs.append(Recommendation(
            category="stress", severity="high",
            text_en="Stress has been high. Take short breaks and try 10 minutes of breathing or meditation today.",
            text_mk="Стресот е висок. Прави кратки паузи и пробај 10 минути дишење или медитација денес.",
        ))
    elif _slope(stress) >= 1.0:
        recs.append(Recommendation(
            category="stress", severity="medium",
            text_en="Your stress is trending up. Plan one relaxing activity you enjoy this evening.",
            text_mk="Твојот стрес расте. Испланирај една опуштувачка активност што ти се допаѓа за вечерва.",
        ))

    # --- Hydration -----------------------------------------------------------
    if _avg(water) < 1.5:
        recs.append(Recommendation(
            category="hydration", severity="low",
            text_en="You are drinking little water. Aim for about 2 litres a day — keep a bottle nearby.",
            text_mk="Пиеш малку вода. Целѝ на околу 2 литри дневно — држи шише во близина.",
        ))

    # --- Social --------------------------------------------------------------
    if _avg(social) < 30:
        recs.append(Recommendation(
            category="social", severity="low",
            text_en="Spend a little more time with friends or family — real-world connection improves wellbeing.",
            text_mk="Помини малку повеќе време со пријатели или семејство — вистинската поврзаност го подобрува благосостојбата.",
        ))

    # --- Nutrition -----------------------------------------------------------
    if latest.nutrition_quality <= 4:
        recs.append(Recommendation(
            category="nutrition", severity="low",
            text_en="Add a fruit or vegetable to your next meal to boost nutrition quality.",
            text_mk="Додади овошје или зеленчук во следниот оброк за да го подобриш квалитетот на исхраната.",
        ))

    if not recs:
        recs.append(Recommendation(
            category="positive", severity="info",
            text_en="Great balance this week — keep up your healthy routine! Your avatar is thriving.",
            text_mk="Одличен баланс оваа недела, продолжи со здравата рутина! Твојот аватар е во одлично расположение.",
        ))

    return recs
