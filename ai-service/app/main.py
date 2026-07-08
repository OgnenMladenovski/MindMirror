"""MindMirror AI microservice — FastAPI app entrypoint.

Endpoints (per assignment spec):
  POST /analyze         wellness scores
  POST /predict         ML predictions for tomorrow
  POST /recommend       personalised recommendations
  GET|POST /trends      automatically generated insights
  GET|POST /avatar-state digital-twin avatar state
  GET  /health          liveness/readiness probe
"""
from __future__ import annotations

from fastapi import FastAPI

from .config import settings
from .ml import store
from .routers import analyze, avatar, predict, recommend, trends

app = FastAPI(
    title="MindMirror AI Service",
    version=settings.version,
    description="AI recommendation, prediction and analytics engine for MindMirror.",
)

for module in (analyze, predict, recommend, trends, avatar):
    app.include_router(module.router)


@app.get("/health", tags=["health"], summary="Health check")
def health() -> dict:
    return {"status": "ok", "service": settings.app_name, "version": settings.version}


@app.get("/model-info", tags=["predictions"], summary="Trained model metadata")
def model_info() -> dict:
    """Model version, training metrics and feature importances (for the UI's
    feature-importance visualisation)."""
    return store.metadata()
