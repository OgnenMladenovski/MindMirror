from fastapi import APIRouter, Body, HTTPException

from ..core import avatar, scoring
from ..schemas import AvatarRequest, AvatarResponse

router = APIRouter(tags=["avatar"])


@router.api_route("/avatar-state", methods=["GET", "POST"], response_model=AvatarResponse,
                  summary="Digital-twin avatar state")
def avatar_state(req: AvatarRequest = Body(default=AvatarRequest())) -> AvatarResponse:
    """Derive the avatar's state/animation from scores (or from a raw entry)."""
    if req.scores is not None:
        return avatar.derive(req.scores, req.entry)
    entry = req.entry or (req.history[-1] if req.history else None)
    if entry is None:
        raise HTTPException(status_code=422, detail="Provide `scores`, `entry`, or `history`.")
    return avatar.derive(scoring.analyze(entry), entry)
