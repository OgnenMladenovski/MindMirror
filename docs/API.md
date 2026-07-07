# MindMirror API Catalog

Full interactive docs (with schemas + auth): **http://localhost:8080/swagger-ui.html**
(OpenAPI JSON at `/v3/api-docs`). AI service docs: **http://localhost:8000/docs**.

All `/api/**` routes except `/api/auth/**` and `/api/hbsc/reference` require a
`Authorization: Bearer <JWT>` header.

## Spring Boot (`/api`)
| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/auth/register` | Register, returns JWT |
| POST | `/api/auth/login` | Log in, returns JWT |
| GET | `/api/users/me` | Current profile |
| PUT | `/api/users/me` | Update profile / settings (locale mk/en) |
| POST | `/api/logs` | Daily check-in → runs AI analysis, recommendations, avatar, prediction |
| GET | `/api/logs` | List logs (newest first) |
| GET | `/api/logs/{date}` | One day's log + stored analysis |
| GET | `/api/dashboard` | Cards + all chart series + XP/level/streak |
| GET | `/api/dashboard/trends` | Auto-generated trend insights (bilingual) |
| GET | `/api/dashboard/prediction` | Tomorrow's ML prediction + feature importance |
| GET | `/api/recommendations?limit=` | Latest recommendations |
| GET | `/api/avatar` | Current avatar state |
| GET | `/api/avatar/history` | Avatar history |
| GET | `/api/challenges/today` | Today's challenge (generated if needed) |
| GET | `/api/challenges` | Challenge history |
| POST | `/api/challenges/{id}/complete` | Complete a challenge (+XP) |
| GET | `/api/achievements` | Catalog + unlock progress |
| GET | `/api/hbsc/comparison` | You vs HBSC North Macedonia |
| GET | `/api/hbsc/reference` | Raw HBSC reference values (public) |
| GET | `/api/statistics` | Admin analytics (ROLE_ADMIN) |
| GET | `/api/notifications` | List notifications |
| GET | `/api/notifications/unread-count` | Unread count |
| POST | `/api/notifications/{id}/read` | Mark read |
| POST | `/api/chat` | Ask the AI wellbeing assistant |

## FastAPI AI service
| Method | Path | Description |
| --- | --- | --- |
| POST | `/analyze` | Wellness scores (spec-shaped `compact` included) |
| POST | `/predict` | Tomorrow's mood/burnout/stress/sleep + recommended activity |
| POST | `/recommend` | Trend-aware recommendations |
| GET/POST | `/trends` | Automatically generated insights |
| GET/POST | `/avatar-state` | Avatar state from scores/entry |
| POST | `/chat` | Rule-based assistant |
| GET | `/health` | Health probe |
| GET | `/model-info` | Model version, metrics, feature importances |

## Example: `POST /analyze` response (compact block)
```json
{ "burnout": 54, "sleep": 65, "social": 49, "wellness": 53, "risk": "Medium" }
```
