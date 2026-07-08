# MindMirror Architecture

## Components
| Component | Tech | Responsibility |
| --- | --- | --- |
| **backend** | Spring Boot 3, Spring Security (JWT), Spring Data JPA, Flyway, PostgreSQL | Users/auth, daily logs, dashboard, challenges, achievements, avatar, statistics, HBSC comparison, notifications |
| **ai-service** | FastAPI, scikit-learn, pandas, NumPy, joblib | Scoring, recommendations, trend insights, ML predictions, avatar state |
| **db** | PostgreSQL 17 | Persistence |
| **frontend** | React 19 (planned) | UI (dashboards, avatar, i18n MK/EN) |

## Request flow: a daily check-in
```
POST /api/logs  (JWT)
   └─ DailyLogService.create
        ├─ persist DailyLog (upsert by user+date)
        ├─ AI POST /analyze     → persist WellnessScore
        ├─ AI POST /avatar-state→ persist AvatarState
        ├─ AI POST /recommend   → replace Recommendations
        ├─ AI POST /predict     → append PredictionHistory
        ├─ UserStats: streak + XP
        ├─ ChallengeService: ensure today's challenge
        └─ AchievementService: evaluate unlocks
   └─ returns log + scores + avatar + recommendations + prediction
```
Each AI call is best-effort: if the AI service is down the log is still saved and the
affected section is simply omitted (see `AiServiceClient` + per-section try/catch).

## Layers (backend)
`controller → service → repository → entity`, with `client` (AI), `security` (JWT),
`config`, `dto` (records) and `exception` (global `@RestControllerAdvice`).
Child tables reference `user_id` as plain columns (no lazy JPA associations) to keep the
data access simple and predictable.

## Bilingual strategy
- **Static UI strings** → handled by the React app's i18n (next phase).
- **Dynamic AI/text content** (recommendations, insights, challenges, achievements,
  notifications, avatar captions) → stored/returned in **both** `*_en` and `*_mk`
  so locale can switch with no extra round-trip.

## AI / ML
- Deterministic scoring formulas (`app/core/scoring.py`) — transparent and explainable.
- Models (`app/ml/`) trained on synthetic-but-realistic data: RandomForest + GradientBoosting
  for tomorrow's mood/burnout/stress/sleep-quality and a RandomForest classifier for the
  recommended activity. Persisted via joblib; feature importances exposed at `/model-info`.

## Security
Stateless JWT (HMAC-SHA256). `JwtAuthFilter` authenticates each request; `/api/auth/**`
and Swagger are public, `/api/statistics/**` requires `ROLE_ADMIN`.

## Configuration & orchestration
`docker-compose.yml` wires PostgreSQL + ai-service + backend with healthchecks; the backend
waits for both. Containers pin **JDK 21** and **Python 3.12** for compatibility.
