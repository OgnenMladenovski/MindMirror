"""Feature definitions shared by training and inference."""
from __future__ import annotations

from ..schemas import DailyEntry, LocalizedText

# Order matters — models are trained on this exact column order.
FEATURE_NAMES = [
    "sleep_hours",
    "stress_level",
    "screen_time_hours",
    "study_hours",
    "physical_activity_min",
    "water_intake",
    "mood_yesterday",
    "burnout_yesterday",
]

# Recommended-activity classes (targets of the classifier) with bilingual labels.
ACTIVITY_LABELS: dict[str, LocalizedText] = {
    "sleep_earlier": LocalizedText(text_en="Sleep earlier tonight", text_mk="Легни порано вечерва"),
    "exercise": LocalizedText(text_en="Get 30 minutes of exercise", text_mk="Вежбај 30 минути"),
    "meditate": LocalizedText(text_en="Try 10 minutes of meditation", text_mk="Пробај 10 минути медитација"),
    "reduce_screen": LocalizedText(text_en="Cut down screen time", text_mk="Намали го времето пред екран"),
    "hydrate": LocalizedText(text_en="Drink more water", text_mk="Пиј повеќе вода"),
    "socialize": LocalizedText(text_en="Spend time with friends", text_mk="Помини време со пријатели"),
    "maintain": LocalizedText(text_en="Keep up your balanced routine", text_mk="Продолжи со балансираната рутина"),
}


def build_feature_row(today: DailyEntry, mood_yesterday: float, burnout_yesterday: float) -> list[float]:
    return [
        today.sleep_hours,
        today.stress_level,
        today.screen_time_hours,
        today.study_hours,
        today.physical_activity_min,
        today.water_intake,
        mood_yesterday,
        burnout_yesterday,
    ]
