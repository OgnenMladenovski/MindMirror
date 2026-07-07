# MindMirror — AI Digital Twin for Student Wellbeing

An HBSC-inspired platform where each student owns a **digital-twin avatar** that reflects
their physical and mental wellbeing. Students log daily habits; an AI microservice scores
them, predicts tomorrow, generates personalised (bilingual) recommendations, and drives the
avatar. Everything is available in **English and Macedonian (македонски)**.

> Status: **full stack working**. Spring Boot API, FastAPI AI microservice, ML models,
> PostgreSQL schema, HBSC North Macedonia data, Docker orchestration **and the React
> frontend** (dashboards, digital-twin avatar, MK/EN i18n) are complete and verified.

## Architecture

```
                 React (later)
                      │  REST + JWT
                      ▼
                Spring Boot API  ──REST──►  FastAPI AI service  ──►  ML models (joblib)
        auth · logs · dashboard             analyze · predict
        challenges · achievements           recommend · trends
        avatar · statistics · chat          avatar-state · chat
                      │
                      ▼
                 PostgreSQL 17
```

- **backend/** — Spring Boot 3, Spring Security (JWT), Spring Data JPA, Flyway, PostgreSQL, Swagger.
- **ai-service/** — FastAPI, scikit-learn (RandomForest/GradientBoosting), pandas, NumPy, joblib.
- **frontend/** — placeholder for the React 19 app (MUI, Recharts, Framer Motion) — next phase.
- **docs/** — architecture, API catalog, HBSC data provenance.

## Quick start

**1. Backend stack (Docker):**
```bash
cp .env.example .env
docker compose up --build          # Postgres + FastAPI AI + Spring Boot API
```
- Spring Boot API + Swagger UI → http://localhost:8080/swagger-ui.html
- FastAPI AI docs → http://localhost:8000/docs

**2. Frontend (React dev server):**
```bash
cd frontend
npm install
npm run dev                        # http://localhost:5173
```
Open **http://localhost:5173** and log in as **demo / demo1234** (admin: **admin / admin1234**).
A demo student is seeded with ~30 days of history so the dashboard, charts, predictions and
HBSC comparison have data immediately. The Vite dev server proxies `/api` to the backend on
:8080 (see `frontend/vite.config.js`), so no CORS setup is needed.

> If your machine already runs PostgreSQL on 5432, start the stack with a different host
> port: `POSTGRES_PORT=5544 docker compose up` (internal wiring is unaffected).

> **You do NOT need Python, Maven or a local JDK.** Docker runs PostgreSQL, the FastAPI AI
> service and the Spring Boot backend for you. The only tool you run directly is `npm`
> (for the frontend). If `npm run dev` says *"Port 5173 is already in use"*, another Vite
> instance is running — stop it first: `lsof -ti :5173 | xargs kill`.

## Run manually (advanced / optional — Docker is easier)

Only if you want to run a service outside Docker. Requires **JDK 21** and **Python 3.11/3.12**
(the host's JDK 25/26 and Python 3.14 are too new for Spring/Hibernate and scikit-learn).

1. **PostgreSQL** — a database `mindmirror` with role `mindmirror` on `localhost:5432`
   (this is exactly what the Docker `db` provides; a plain local Postgres won't have that role).
2. **AI service** — use whatever Python 3.11/3.12 you have (check with `python3 --version`):
   ```bash
   cd ai-service
   python3 -m venv .venv && source .venv/bin/activate   # must be 3.11 or 3.12
   pip install -r requirements-dev.txt
   python -m app.ml.train
   uvicorn app.main:app --port 8000
   ```
3. **Backend** (no Maven wrapper is committed — use a system `mvn` **on JDK 21**):
   ```bash
   cd backend
   JAVA_HOME=$(/usr/libexec/java_home -v 21) \
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mindmirror \
   MINDMIRROR_AI_BASE_URL=http://localhost:8000 \
   mvn spring-boot:run
   ```
   Other overrides: `SPRING_DATASOURCE_USERNAME/PASSWORD`, `MINDMIRROR_JWT_SECRET`,
   `MINDMIRROR_SEED_DEMO`.

## Try the API

```bash
# Log in as the seeded demo user
TOKEN=$(curl -s localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"usernameOrEmail":"demo","password":"demo1234"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')

curl -s localhost:8080/api/dashboard        -H "Authorization: Bearer $TOKEN"
curl -s localhost:8080/api/recommendations  -H "Authorization: Bearer $TOKEN"
curl -s localhost:8080/api/avatar           -H "Authorization: Bearer $TOKEN"
curl -s localhost:8080/api/hbsc/comparison  -H "Authorization: Bearer $TOKEN"
curl -s localhost:8080/api/dashboard/prediction -H "Authorization: Bearer $TOKEN"
```

## Documentation
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- [docs/API.md](docs/API.md)
- [docs/hbsc-data-sources.md](docs/hbsc-data-sources.md)

## Tests
```bash
cd backend && mvn test        # JUnit
cd ai-service && pytest        # pytest
```
