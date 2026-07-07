"""Train the MindMirror predictive models on synthetic-but-realistic data.

We don't have real longitudinal student data, so we simulate plausible
relationships between today's behaviour and tomorrow's wellbeing. The learned
models still expose meaningful feature importances and can be swapped for models
trained on real data later without changing the serving code.

Run:  python -m app.ml.train
"""
from __future__ import annotations

import json
from datetime import datetime, timezone

import joblib
import numpy as np
from sklearn.ensemble import (
    GradientBoostingRegressor,
    RandomForestClassifier,
    RandomForestRegressor,
)

from ..config import settings
from .features import FEATURE_NAMES

MODEL_VERSION = "1.0.0"
_MODEL_PATH = settings.model_dir / "models.joblib"
_META_PATH = settings.model_dir / "metadata.json"


def _clamp(a, lo, hi):
    return np.clip(a, lo, hi)


def _simulate(n: int = 6000, seed: int = 42):
    rng = np.random.default_rng(seed)

    sleep = _clamp(rng.normal(7.2, 1.4, n), 3, 12)
    stress = _clamp(rng.normal(5.5, 2.2, n), 1, 10)
    screen = _clamp(rng.normal(5.0, 2.2, n), 0, 14)
    study = _clamp(rng.normal(3.5, 1.8, n), 0, 12)
    activity = _clamp(rng.normal(40, 30, n), 0, 180)
    water = _clamp(rng.normal(1.6, 0.7, n), 0, 5)
    mood_yesterday = _clamp(rng.normal(6.2, 1.8, n), 1, 10)
    burnout_yesterday = _clamp(rng.normal(45, 20, n), 0, 100)

    noise = lambda s: rng.normal(0, s, n)

    mood_tomorrow = _clamp(
        4.0
        + 0.55 * (sleep - 7)
        + 0.012 * activity
        - 0.40 * stress
        - 0.12 * screen
        + 0.35 * mood_yesterday
        - 0.015 * burnout_yesterday
        + noise(0.8),
        1, 10,
    )
    burnout_tomorrow = _clamp(
        10
        + 5.5 * stress
        + 2.5 * screen
        + 3.5 * np.maximum(0, study - 5)
        + 4.5 * np.maximum(0, 8 - sleep)
        - 0.10 * activity
        + 0.35 * burnout_yesterday
        + noise(6),
        0, 100,
    )
    stress_tomorrow = _clamp(
        1.2
        + 0.55 * stress
        + 0.12 * screen
        + 0.10 * np.maximum(0, study - 4)
        - 0.15 * (sleep - 7)
        + noise(1.0),
        1, 10,
    )
    sleep_quality_tomorrow = _clamp(
        100 - 10 * np.abs(sleep - 8.5) - 2.0 * stress - 1.5 * screen + noise(6),
        0, 100,
    )

    # Recommended activity = the most impactful nudge given today's inputs.
    deficits = np.stack([
        np.maximum(0, 7.5 - sleep) / 4,          # sleep_earlier
        np.maximum(0, 30 - activity) / 30,        # exercise
        np.maximum(0, stress - 6) / 4,            # meditate
        np.maximum(0, screen - 6) / 6,            # reduce_screen
        np.maximum(0, 1.5 - water) / 1.5,         # hydrate
    ], axis=1)
    labels_idx = deficits.argmax(axis=1)
    # If no notable deficit, recommend maintaining balance.
    labels_idx = np.where(deficits.max(axis=1) < 0.15, 5, labels_idx)
    label_names = np.array(
        ["sleep_earlier", "exercise", "meditate", "reduce_screen", "hydrate", "maintain"]
    )
    activity_label = label_names[labels_idx]

    X = np.stack(
        [sleep, stress, screen, study, activity, water, mood_yesterday, burnout_yesterday],
        axis=1,
    )
    return X, {
        "mood": mood_tomorrow,
        "burnout": burnout_tomorrow,
        "stress": stress_tomorrow,
        "sleep_quality": sleep_quality_tomorrow,
        "activity": activity_label,
    }


def train_and_save() -> dict:
    X, y = _simulate()
    split = int(0.85 * len(X))
    Xtr, Xte = X[:split], X[split:]

    models = {
        "mood": RandomForestRegressor(n_estimators=200, max_depth=12, random_state=1, n_jobs=-1),
        "burnout": GradientBoostingRegressor(n_estimators=250, max_depth=3, random_state=1),
        "stress": RandomForestRegressor(n_estimators=200, max_depth=12, random_state=1, n_jobs=-1),
        "sleep_quality": RandomForestRegressor(n_estimators=200, max_depth=12, random_state=1, n_jobs=-1),
        "activity": RandomForestClassifier(n_estimators=200, max_depth=12, random_state=1, n_jobs=-1),
    }

    metrics: dict[str, float] = {}
    importances: dict[str, dict[str, float]] = {}
    for name, model in models.items():
        ytr, yte = y[name][:split], y[name][split:]
        model.fit(Xtr, ytr)
        if name == "activity":
            metrics[name] = float(model.score(Xte, yte))  # accuracy
        else:
            metrics[name] = float(model.score(Xte, yte.astype(float)))  # R^2
        importances[name] = {
            f: round(float(imp), 4) for f, imp in zip(FEATURE_NAMES, model.feature_importances_)
        }

    settings.model_dir.mkdir(parents=True, exist_ok=True)
    bundle = {
        "models": models,
        "feature_names": FEATURE_NAMES,
        "version": MODEL_VERSION,
        "trained_at": datetime.now(timezone.utc).isoformat(),
    }
    joblib.dump(bundle, _MODEL_PATH)

    meta = {
        "version": MODEL_VERSION,
        "trained_at": bundle["trained_at"],
        "n_samples": int(len(X)),
        "metrics": metrics,
        "feature_importance": importances,
    }
    _META_PATH.write_text(json.dumps(meta, indent=2, ensure_ascii=False))
    print(f"[train] saved models v{MODEL_VERSION} -> {_MODEL_PATH}")
    print(f"[train] metrics: {json.dumps(metrics, indent=2)}")
    return meta


if __name__ == "__main__":
    train_and_save()
