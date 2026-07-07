from fastapi import APIRouter

from ..core import chatbot
from ..schemas import ChatRequest, ChatResponse

router = APIRouter(tags=["chat"])


@router.post("/chat", response_model=ChatResponse, summary="AI wellbeing chat assistant")
def chat(req: ChatRequest) -> ChatResponse:
    """Answer a student's question grounded in their own history and scores."""
    return chatbot.get_backend().answer(req)
