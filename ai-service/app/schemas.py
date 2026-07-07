"""Pydantic request/response models shared across routers.

All user-facing text produced by the service is returned **bilingually**
(`*_en` / `*_mk`) so the Spring Boot backend can persist both languages and the
frontend can switch locale without another round-trip.
"""
from __future__ import annotations

from datetime import date
from typing import Literal, Optional

from pydantic import BaseModel, Field

Lang = Literal["en", "mk"]


class DailyEntry(BaseModel):
    """One day of wellness self-report. Mirrors the `daily_logs` table."""

    log_date: Optional[date] = Field(default=None, description="ISO date of the entry")
    sleep_hours: float = Field(ge=0, le=24, examples=[7.5])
    stress_level: int = Field(ge=1, le=10, examples=[6])
    mood_score: int = Field(ge=1, le=10, examples=[7])
    physical_activity_min: int = Field(ge=0, le=1440, examples=[45])
    water_intake: float = Field(ge=0, le=10, description="litres", examples=[1.5])
    screen_time_hours: float = Field(ge=0, le=24, examples=[5.0])
    study_hours: float = Field(ge=0, le=24, examples=[3.0])
    social_time_min: int = Field(ge=0, le=1440, description="minutes with friends", examples=[60])
    energy_level: int = Field(ge=1, le=10, examples=[6])
    nutrition_quality: int = Field(ge=1, le=10, examples=[7])
    notes: Optional[str] = None


class Scores(BaseModel):
    """Wellness scores produced by /analyze (0-100, higher = better except burnout)."""

    burnout_index: float
    sleep_score: float
    wellbeing_score: float
    social_balance_score: float
    productivity_score: float
    overall_wellness_score: float
    risk_level: Literal["Low", "Medium", "High"]

    # Short spec-compatible aliases (see assignment example JSON).
    def compact_dict(self) -> dict:
        return {
            "burnout": round(self.burnout_index),
            "sleep": round(self.sleep_score),
            "social": round(self.social_balance_score),
            "wellness": round(self.overall_wellness_score),
            "risk": self.risk_level,
        }


class AnalyzeRequest(BaseModel):
    entry: DailyEntry
    history: list[DailyEntry] = Field(default_factory=list, description="previous days, oldest first")
    age_group: int = Field(default=15, description="HBSC age band 11/13/15")
    lang: Lang = "en"


class AnalyzeResponse(Scores):
    compact: dict


class LocalizedText(BaseModel):
    text_en: str
    text_mk: str


class Recommendation(LocalizedText):
    category: str
    severity: Literal["info", "low", "medium", "high"]


class RecommendRequest(BaseModel):
    history: list[DailyEntry] = Field(default_factory=list)
    lang: Lang = "en"


class RecommendResponse(BaseModel):
    recommendations: list[Recommendation]


class Insight(LocalizedText):
    kind: str


class TrendsRequest(BaseModel):
    history: list[DailyEntry] = Field(default_factory=list)
    lang: Lang = "en"


class TrendsResponse(BaseModel):
    insights: list[Insight]


class PredictRequest(BaseModel):
    history: list[DailyEntry] = Field(default_factory=list)
    lang: Lang = "en"


class Predictions(BaseModel):
    mood_tomorrow: float
    burnout_tomorrow: float
    stress_tomorrow: float
    sleep_quality_tomorrow: float
    recommended_activity: str
    recommended_activity_text: LocalizedText


class PredictResponse(BaseModel):
    predictions: Predictions
    model_version: str
    feature_importance: dict[str, dict[str, float]]


class AvatarRequest(BaseModel):
    scores: Optional[Scores] = None
    entry: Optional[DailyEntry] = None
    history: list[DailyEntry] = Field(default_factory=list)
    lang: Lang = "en"


class AvatarResponse(BaseModel):
    state: Literal["EXCELLENT", "HAPPY", "NEUTRAL", "STRESSED", "BURNED_OUT", "EXHAUSTED"]
    animation: str
    attributes: dict[str, float | bool]
    caption_en: str
    caption_mk: str


class ChatRequest(BaseModel):
    message: str
    history: list[DailyEntry] = Field(default_factory=list)
    scores: Optional[Scores] = None
    lang: Lang = "en"


class ChatResponse(BaseModel):
    reply: str
    reply_en: str
    reply_mk: str
    intent: str
    backend: str
