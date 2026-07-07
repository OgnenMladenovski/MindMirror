"""Model loading + inference. Trains lazily on first use if no bundle exists."""
from __future__ import annotations

import json
import threading

import joblib

from ..config import settings
from ..core import scoring
from ..schemas import DailyEntry, PredictResponse, Predictions
from . import train
from .features import ACTIVITY_LABELS, build_feature_row

_MODEL_PATH = settings.model_dir / "models.joblib"
_META_PATH = settings.model_dir / "metadata.json"

_lock = threading.Lock()
_bundle: dict | None = None
_meta: dict | None = None


def _load() -> tuple[dict, dict]:
    global _bundle, _meta
    if _bundle is not None and _meta is not None:
        return _bundle, _meta
    with _lock:
        if _bundle is None or _meta is None:
            if not _MODEL_PATH.exists():
                train.train_and_save()
            _bundle = joblib.load(_MODEL_PATH)
            _meta = json.loads(_META_PATH.read_text()) if _META_PATH.exists() else {}
    return _bundle, _meta


def predict_next_day(history: list[DailyEntry], lang: str = "en") -> PredictResponse:
    if not history:
        raise ValueError("history must contain at least one entry")

    bundle, meta = _load()
    models = bundle["models"]

    today = history[-1]
    yesterday = history[-2] if len(history) >= 2 else today
    mood_yesterday = yesterday.mood_score
    burnout_yesterday = scoring.burnout_index(yesterday)

    row = [build_feature_row(today, mood_yesterday, burnout_yesterday)]

    mood = float(models["mood"].predict(row)[0])
    burnout = float(models["burnout"].predict(row)[0])
    stress = float(models["stress"].predict(row)[0])
    sleep_quality = float(models["sleep_quality"].predict(row)[0])
    activity_key = str(models["activity"].predict(row)[0])
    activity_text = ACTIVITY_LABELS.get(activity_key, ACTIVITY_LABELS["maintain"])

    predictions = Predictions(
        mood_tomorrow=round(max(1.0, min(10.0, mood)), 1),
        burnout_tomorrow=round(max(0.0, min(100.0, burnout)), 1),
        stress_tomorrow=round(max(1.0, min(10.0, stress)), 1),
        sleep_quality_tomorrow=round(max(0.0, min(100.0, sleep_quality)), 1),
        recommended_activity=activity_key,
        recommended_activity_text=activity_text,
    )

    return PredictResponse(
        predictions=predictions,
        model_version=bundle.get("version", "unknown"),
        feature_importance=meta.get("feature_importance", {}),
    )


def metadata() -> dict:
    _, meta = _load()
    return meta
