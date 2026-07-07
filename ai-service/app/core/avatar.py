"""Maps wellness scores to the digital-twin avatar state.

The avatar is the emotional heart of MindMirror: healthy habits make it glow,
unhealthy ones make it tired and stressed. Output drives frontend animations.
"""
from __future__ import annotations

from ..schemas import AvatarResponse, DailyEntry, Scores
from . import scoring

_STATE_META = {
    "EXCELLENT": ("glow", "You're thriving — keep it up!", "Цветаш — само така продолжи!"),
    "HAPPY": ("smiling", "Feeling good and balanced.", "Се чувствуваш добро и избалансирано."),
    "NEUTRAL": ("breathing", "Doing okay — small tweaks could help.", "Одиш добро — мали промени би помогнале."),
    "STRESSED": ("sad", "A bit stressed — take a breather.", "Малку си под стрес — здивни."),
    "BURNED_OUT": ("crying", "Signs of burnout — please slow down.", "Знаци на исцрпеност — забави малку."),
    "EXHAUSTED": ("sleeping", "Very low energy — rest is a priority.", "Многу ниска енергија — одморот е приоритет."),
}


def derive(scores: Scores, entry: DailyEntry | None = None) -> AvatarResponse:
    overall = scores.overall_wellness_score
    burnout = scores.burnout_index

    if burnout >= 75:
        state = "BURNED_OUT"
    elif entry is not None and entry.energy_level <= 3 and entry.sleep_hours < 6:
        state = "EXHAUSTED"
    elif overall >= 82 and burnout < 30:
        state = "EXCELLENT"
    elif overall >= 68:
        state = "HAPPY"
    elif overall >= 50:
        state = "NEUTRAL"
    else:
        state = "STRESSED"

    animation, cap_en, cap_mk = _STATE_META[state]

    attributes: dict[str, float | bool] = {
        "energy": round(scores.wellbeing_score / 100, 2),
        "glow": state in ("EXCELLENT", "HAPPY"),
        "dark_circles": bool(entry is not None and entry.sleep_hours < 6.5),
        "smile": round(scores.wellbeing_score / 100, 2),
        "stress": round(scores.burnout_index / 100, 2),
    }

    return AvatarResponse(
        state=state,
        animation=animation,
        attributes=attributes,
        caption_en=cap_en,
        caption_mk=cap_mk,
    )


def derive_from_entry(entry: DailyEntry, age_group: int = 15) -> AvatarResponse:
    return derive(scoring.analyze(entry, age_group), entry)
