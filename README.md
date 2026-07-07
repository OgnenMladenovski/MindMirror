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
docker compose up --build         
```
- Spring Boot API + Swagger UI → http://localhost:8080/swagger-ui.html
- FastAPI AI docs → http://localhost:8000/docs

**2. Frontend (React dev server):**
```bash
cd frontend
npm install
npm run dev                       
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