from fastapi import APIRouter

from ..core import recommender
from ..schemas import RecommendRequest, RecommendResponse

router = APIRouter(tags=["recommendations"])


@router.post("/recommend", response_model=RecommendResponse, summary="Personalised recommendations")
def recommend(req: RecommendRequest) -> RecommendResponse:
    """Trend-aware recommendations (bilingual) based on multiple days of history."""
    return RecommendResponse(recommendations=recommender.generate(req.history))
