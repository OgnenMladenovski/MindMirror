from fastapi import APIRouter

from ..core import scoring
from ..schemas import AnalyzeRequest, AnalyzeResponse

router = APIRouter(tags=["analysis"])


@router.post("/analyze", response_model=AnalyzeResponse, summary="Compute wellness scores")
def analyze(req: AnalyzeRequest) -> AnalyzeResponse:
    """Return burnout, sleep, wellbeing, social, productivity and overall scores
    plus a risk level for the submitted day."""
    scores = scoring.analyze(req.entry, req.age_group)
    return AnalyzeResponse(**scores.model_dump(), compact=scores.compact_dict())
