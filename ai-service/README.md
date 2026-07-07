# MindMirror AI Service (FastAPI)

AI microservice for scoring, recommendations, trend insights, ML predictions and a
rule-based wellbeing chat assistant. All user-facing text is returned in **English and
Macedonian**.

## Endpoints
| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/analyze` | Wellness scores (burnout, sleep, wellbeing, social, productivity, overall, risk) |
| POST | `/predict` | ML predictions for tomorrow + feature importance |
| POST | `/recommend` | Trend-aware personalised recommendations |
| GET/POST | `/trends` | Automatically generated insights |
| GET/POST | `/avatar-state` | Digital-twin avatar state |
| POST | `/chat` | Rule-based assistant grounded in the user's history |
| GET | `/health` | Health probe |
| GET | `/model-info` | Model version, metrics, feature importances |

Interactive docs: `http://localhost:8000/docs`

## Run with Docker (recommended)
Built as part of the root `docker compose up`. Models are trained during the image build.

## Run manually (Python 3.12 recommended)
> The host's Python 3.14 may lack scikit-learn wheels — use a 3.12 venv.
```bash
cd ai-service
python3.12 -m venv .venv && source .venv/bin/activate
pip install -r requirements-dev.txt
python -m app.ml.train            # train + persist models to model_store/
uvicorn app.main:app --reload --port 8000
pytest                            # run the test suite
```

## Chat backend
Default `CHAT_BACKEND=rule_based` (offline). Set `CHAT_BACKEND=openai` and `OPENAI_API_KEY`
to use the pluggable OpenAI adapter seam (`app/core/chatbot.py`).
