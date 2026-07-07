from fastapi import APIRouter, Body

from ..core import trends_engine
from ..schemas import TrendsRequest, TrendsResponse

router = APIRouter(tags=["trends"])


# Spec lists GET /trends; we accept both GET and POST with a JSON body so the
# Spring backend can send history either way.
@router.api_route("/trends", methods=["GET", "POST"], response_model=TrendsResponse,
                  summary="Automatically generated trend insights")
def trends(req: TrendsRequest = Body(default=TrendsRequest())) -> TrendsResponse:
    return TrendsResponse(insights=trends_engine.generate(req.history))
