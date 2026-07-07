"""Historical-pattern insights (bilingual).

Detects weekday patterns, correlations between behaviours and mood, and
stress build-ups. Insights are descriptive ("you usually sleep less on
Mondays") rather than prescriptive (that is the recommender's job).
"""
from __future__ import annotations

from statistics import mean, pstdev

from ..schemas import DailyEntry, Insight

_WEEKDAYS_EN = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
_WEEKDAYS_MK = ["понеделник", "вторник", "среда", "четврток", "петок", "сабота", "недела"]


def _corr(xs: list[float], ys: list[float]) -> float:
    if len(xs) < 3:
        return 0.0
    mx, my = mean(xs), mean(ys)
    num = sum((x - mx) * (y - my) for x, y in zip(xs, ys))
    dx = sum((x - mx) ** 2 for x in xs) ** 0.5
    dy = sum((y - my) ** 2 for y in ys) ** 0.5
    if dx == 0 or dy == 0:
        return 0.0
    return num / (dx * dy)


def generate(history: list[DailyEntry]) -> list[Insight]:
    insights: list[Insight] = []
    if len(history) < 5:
        insights.append(Insight(
            kind="info",
            text_en="Keep logging daily — after about a week MindMirror can reveal your personal patterns.",
            text_mk="Продолжи да внесуваш секој ден — по околу една недела MindMirror може да ги открие твоите лични обрасци.",
        ))
        return insights

    # --- Weekday sleep pattern ----------------------------------------------
    dated = [d for d in history if d.log_date is not None]
    if len(dated) >= 7:
        by_weekday: dict[int, list[float]] = {}
        for d in dated:
            by_weekday.setdefault(d.log_date.weekday(), []).append(d.sleep_hours)
        overall = mean(d.sleep_hours for d in dated)
        worst_day, worst_avg = None, overall
        for wd, vals in by_weekday.items():
            if len(vals) >= 2 and mean(vals) < worst_avg - 0.6:
                worst_day, worst_avg = wd, mean(vals)
        if worst_day is not None:
            insights.append(Insight(
                kind="weekday_sleep",
                text_en=f"You usually sleep less on {_WEEKDAYS_EN[worst_day]}s ({worst_avg:.1f}h vs {overall:.1f}h overall).",
                text_mk=f"Обично спиеш помалку во {_WEEKDAYS_MK[worst_day]} ({worst_avg:.1f}ч наспроти {overall:.1f}ч вкупно).",
            ))

    # --- Activity <-> mood correlation --------------------------------------
    activity = [d.physical_activity_min for d in history]
    mood = [d.mood_score for d in history]
    c_am = _corr(activity, mood)
    if c_am >= 0.35:
        insights.append(Insight(
            kind="activity_mood",
            text_en="Physical activity tends to improve your mood — the more you move, the better you feel.",
            text_mk="Физичката активност има тенденција да го подобри твоето расположение — колку повеќе се движиш, толку подобро се чувствуваш.",
        ))

    # --- Screen time <-> stress correlation ---------------------------------
    screen = [d.screen_time_hours for d in history]
    stress = [d.stress_level for d in history]
    if _corr(screen, stress) >= 0.35:
        insights.append(Insight(
            kind="screen_stress",
            text_en="Your stress tends to be higher on high-screen-time days.",
            text_mk="Твојот стрес има тенденција да е повисок во деновите со многу време пред екран.",
        ))

    # --- Stress build-up (proxy for exam periods) ---------------------------
    if len(stress) >= 6:
        recent = mean(stress[-3:])
        earlier = mean(stress[-6:-3])
        if recent - earlier >= 1.5 and pstdev(stress) > 0.5:
            insights.append(Insight(
                kind="stress_buildup",
                text_en="Your stress has been rising recently — this often happens around exams or deadlines.",
                text_mk="Твојот стрес неодамна расте — ова често се случува околу испити или рокови.",
            ))

    # --- Sleep <-> next-day energy ------------------------------------------
    if _corr([d.sleep_hours for d in history], [d.energy_level for d in history]) >= 0.4:
        insights.append(Insight(
            kind="sleep_energy",
            text_en="More sleep clearly boosts your energy the next day.",
            text_mk="Повеќе спиење јасно ти ја зголемува енергијата следниот ден.",
        ))

    if not insights:
        insights.append(Insight(
            kind="stable",
            text_en="Your habits have been stable and balanced recently — nice consistency!",
            text_mk="Твоите навики се стабилни и балансирани неодамна — одлична доследност!",
        ))

    return insights
