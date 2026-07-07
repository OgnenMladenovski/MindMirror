"""AI chat assistant.

Default backend is a fully offline, rule-based engine that grounds its answers
in the student's own history and scores. The ``ChatBackend`` protocol keeps the
seam clean so an ``OpenAIChatBackend`` can be enabled later via ``CHAT_BACKEND``.
"""
from __future__ import annotations

from statistics import mean
from typing import Protocol

from ..config import settings
from ..schemas import ChatRequest, ChatResponse, Scores
from . import scoring


def _pick(lang: str, en: str, mk: str) -> str:
    return mk if lang == "mk" else en


def _detect_intent(message: str) -> str:
    m = message.lower()
    if any(w in m for w in ["burnout", "burned", "исцрп", "изгор"]):
        return "burnout"
    if any(w in m for w in ["sleep", "tired", "спиењ", "спијам", "уморен"]):
        return "sleep"
    if any(w in m for w in ["stress", "anxious", "стрес", "напнат"]):
        return "stress"
    if any(w in m for w in ["improve", "better", "advice", "подобр", "совет"]):
        return "improve"
    if any(w in m for w in ["mood", "sad", "happy", "расположен", "тажен", "среќен"]):
        return "mood"
    return "general"


class ChatBackend(Protocol):
    name: str

    def answer(self, req: ChatRequest) -> ChatResponse: ...


class RuleBasedChatBackend:
    name = "rule_based"

    def answer(self, req: ChatRequest) -> ChatResponse:
        intent = _detect_intent(req.message)
        history = req.history
        scores = req.scores or (scoring.analyze(history[-1]) if history else None)

        en, mk = self._respond(intent, history, scores)
        return ChatResponse(
            reply=_pick(req.lang, en, mk),
            reply_en=en,
            reply_mk=mk,
            intent=intent,
            backend=self.name,
        )

    def _respond(self, intent, history, scores: Scores | None):
        if scores is None:
            return (
                "I don't have your data yet. Add a daily check-in and I can give personalised answers.",
                "Сè уште ги немам твоите податоци. Внеси дневна проверка за да ти дадам персонализирани одговори.",
            )

        avg_sleep = mean(d.sleep_hours for d in history) if history else 0
        avg_stress = mean(d.stress_level for d in history) if history else 0
        avg_screen = mean(d.screen_time_hours for d in history) if history else 0

        if intent == "burnout":
            return (
                f"Your burnout index is {scores.burnout_index:.0f}/100 ({scores.risk_level} risk). "
                f"The biggest drivers are stress (avg {avg_stress:.0f}/10) and sleep (avg {avg_sleep:.1f}h). "
                "Protecting sleep and taking short breaks will bring it down.",
                f"Твојот индекс на исцрпеност е {scores.burnout_index:.0f}/100 ({scores.risk_level} ризик). "
                f"Главни причини се стресот (просек {avg_stress:.0f}/10) и спиењето (просек {avg_sleep:.1f}ч). "
                "Заштитата на спиењето и кратките паузи ќе го намалат.",
            )
        if intent == "sleep":
            return (
                f"You're averaging {avg_sleep:.1f}h of sleep and your sleep score is {scores.sleep_score:.0f}/100. "
                "Aim for 8-9h with a consistent bedtime before 23:00.",
                f"Во просек спиеш {avg_sleep:.1f}ч, а твојот резултат за спиење е {scores.sleep_score:.0f}/100. "
                "Целѝ на 8-9ч со редовно легнување пред 23:00.",
            )
        if intent == "stress":
            return (
                f"Your recent stress averages {avg_stress:.0f}/10. Short breathing breaks, movement and less "
                "screen time in the evening usually help most.",
                f"Твојот неодамнешен стрес е во просек {avg_stress:.0f}/10. Кратки вежби за дишење, движење и "
                "помалку време пред екран навечер обично помагаат најмногу.",
            )
        if intent == "mood":
            return (
                f"Your wellbeing score is {scores.wellbeing_score:.0f}/100. Activity and good sleep are the "
                "strongest levers for a better mood tomorrow.",
                f"Твојот резултат за благосостојба е {scores.wellbeing_score:.0f}/100. Активноста и доброто спиење "
                "се најсилните лостови за подобро расположение утре.",
            )
        if intent == "improve":
            focus_en, focus_mk = self._weakest(scores, avg_screen)
            return (
                f"Your overall wellness is {scores.overall_wellness_score:.0f}/100. The area with most room to "
                f"improve is {focus_en}.",
                f"Твојата целокупна благосостојба е {scores.overall_wellness_score:.0f}/100. Областа со најмногу "
                f"простор за подобрување е {focus_mk}.",
            )
        return (
            f"Overall wellness {scores.overall_wellness_score:.0f}/100, burnout {scores.burnout_index:.0f}/100, "
            f"sleep {scores.sleep_score:.0f}/100. Ask me about your sleep, stress, mood or burnout.",
            f"Целокупна благосостојба {scores.overall_wellness_score:.0f}/100, исцрпеност {scores.burnout_index:.0f}/100, "
            f"спиење {scores.sleep_score:.0f}/100. Прашај ме за твоето спиење, стрес, расположение или исцрпеност.",
        )

    def _weakest(self, scores: Scores, avg_screen: float):
        candidates = {
            ("sleep", "спиењето"): scores.sleep_score,
            ("social connection", "социјалната поврзаност"): scores.social_balance_score,
            ("productivity", "продуктивноста"): scores.productivity_score,
            ("general wellbeing", "општата благосостојба"): scores.wellbeing_score,
        }
        (en, mk), _ = min(candidates.items(), key=lambda kv: kv[1])
        return en, mk


class OpenAIChatBackend:
    """Placeholder adapter — wired only when CHAT_BACKEND=openai and a key is set.

    Falls back to the rule-based engine if the OpenAI client/key is unavailable,
    so the service never hard-fails on chat.
    """

    name = "openai"

    def __init__(self) -> None:
        self._fallback = RuleBasedChatBackend()

    def answer(self, req: ChatRequest) -> ChatResponse:
        # Intentionally not shipping the OpenAI call yet (no key by default).
        # The seam is here; implement the API call and return its result.
        resp = self._fallback.answer(req)
        resp.backend = "openai_fallback_rule_based"
        return resp


def get_backend() -> ChatBackend:
    if settings.chat_backend == "openai" and settings.openai_api_key:
        return OpenAIChatBackend()
    return RuleBasedChatBackend()
