from fastapi import APIRouter, HTTPException

from ..ml import store
from ..schemas import PredictRequest, PredictResponse

router = APIRouter(tags=["predictions"])


@router.post("/predict", response_model=PredictResponse, summary="Predict tomorrow's wellbeing")
def predict(req: PredictRequest) -> PredictResponse:
    """ML predictions for tomorrow's mood, burnout, stress, sleep quality and the
    recommended activity, with per-target feature importances."""
    try:
        return store.predict_next_day(req.history, req.lang)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc))
