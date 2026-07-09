# MindMirror - an AI Digital Twin for tracking Student Wellbeing

> HBSC-inspired platform for monitoring the physical and mental wellbeing of students, with reference data for Macedonia.

---

## Overview

A web application where each student owns a digital-twin avatar that reflects their physical and mental wellbeing. Students log their daily habits like: sleep, mood, stress, physical activity, screen time, water intake, study hours, social time, energy and nutrition. A FastAPI microservice scores each entry, predicts the next day with machine-learning models, and generates personalised recommendations. The avatar changes with the student's lifestyle: healthy habits make it thrive, while unhealthy ones make it tired and stressed. The application also compares each student's habits against HBSC reference data for Macedonia.

---

## Preview

- [YouTube](https://www.youtube.com/watch?v=iv1J9st93B0)

---

## Features

- **Daily Wellness Log** — Students can record: sleep, stress, mood, activity, water, screen time, study hours, social time, energy and nutrition each day.
- **AI Analysis** — Each entry is scored for: burnout, sleep, wellbeing, social balance, productivity and overall wellness, with an assigned risk level.
- **Recommendation Engine** — Trend-aware advice based on multiple days of history rather than a single entry.
- **Predictive Analytics** — Machine-learning models (RandomForest / GradientBoosting) predict: tomorrow's mood, burnout, stress, sleep quality and recommended activity, with feature importance.
- **Digital Twin Avatar** — An animated avatar reflecting the current state of the student: Excellent, Happy, Neutral, Stressed, Burned Out or Exhausted.
- **Dashboard** — Summary cards and charts: mood line, sleep area, screen-time bar, wellness radar, activity distribution and a wellness calendar heatmap.
- **Daily Challenges** — One generated challenge per day, completed for experience points.
- **Achievements** — Unlockable badges such as: 7 Healthy Days, Early Sleeper, Hydration Master, Stress Fighter, Fitness Hero and Mood Explorer.
- **HBSC Comparison** — Each student's rolling averages compared against HBSC Macedonia reference values.

---

## Tech Stack

| Layer | Technology                        |
|-------|-----------------------------------|
| Frontend | React 18                          |
| Backend | Spring Boot 3                     |
| Migrations | Flyway                            |
| Database | PostgreSQL                        |
| AI Service | FastAPI / Python                  |
| Machine Learning | scikit-learn, pandas, NumPy, joblib |
| Orchestration | Docker, Docker Compose            |

---

## Architecture

The project has 3 independently deployable components:
```
MindMirror
├── backend/          Spring Boot API (auth, logs, dashboard, challenges,
│                     achievements, avatar, statistics, HBSC)
├── ai-service/       FastAPI microservice (scoring, recommendations,
│                     predictions, trends, avatar state) + ML models
├── frontend/         React application (dashboards, digital-twin avatar,
│                     English / Macedonian interface)
└── docs/             Architecture, API catalog, HBSC data provenance
```

---

## Project Setup

**1. Clone the repository**
```bash
git clone https://github.com/OgnenMladenovski/MindMirror.git
cd MindMirror
```

**2. Start the backend stack**
```bash
docker compose up --build
```
- Spring Boot API and Swagger UI: http://localhost:8080/swagger-ui.html
- FastAPI service and documentation: http://localhost:8000/docs

**3. Start the frontend**
```bash
cd frontend
npm install
npm run dev
```

**4. Open in browser**
```
http://localhost:5173
```

---

## Test Login Credentials

| Username | Password | Role |
|----------|----------|------|
| `demo` | `demo1234` | Student |
| `admin` | `admin1234` | Admin |

---

## Flow

```
Register / Login
   ↓
Daily Check-In (log habits)
   ↓
AI Analysis (scores, recommendations, avatar, prediction)
   ↓
Dashboard (cards, charts, avatar)
   ↓
Challenges / Achievements / HBSC comparison
```

---

## Pages
| Page | Route | Description |
|------|-------|-------------|
| Landing | `/` | Public landing page |
| Login | `/login` | Login form |
| Register | `/register` | Registration form |
| Dashboard | `/dashboard` | Summary cards and charts |
| Daily Check-In | `/checkin` | Daily wellness log with live AI results |
| Analytics | `/analytics` | Trends and next-day predictions |
| Avatar | `/avatar` | Digital-twin avatar and attributes |
| Challenges | `/challenges` | Daily challenge and history |
| Achievements | `/achievements` | Achievement badges and progress |
| HBSC Compare | `/hbsc` | Comparison with HBSC Macedonia averages |
| Profile | `/profile` | User profile |
| Settings | `/settings` | Language and theme |
| Admin | `/admin` | Aggregated statistics (admin only) |

---

## Team

- **Ognen Mladenovski** - 233108
- **Hristina Gjorgjievska** - 233215
- **Evica Isaevska** - 233245
