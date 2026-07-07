# Deploying MindMirror (frontend on Vercel, backend on Render)

Vercel can only host the **static React frontend**. The Spring Boot API, FastAPI AI service
and PostgreSQL must run on a Docker-capable host — this guide uses **Render** (free tier).

```
Frontend (React)  →  Vercel        ── VITE_API_URL ──►  Backend (Spring Boot)  →  Render
                                                         AI service (FastAPI)   →  Render
                                                         PostgreSQL             →  Render
```

Two config files are already in the repo: `render.yaml` (Render blueprint) and
`frontend/vercel.json` (SPA rewrite). **Commit and push them yourself**, then:

---

## 1. Deploy the backend stack on Render

1. Create a free account at **https://render.com** and connect your GitHub.
2. **New → Blueprint** → pick the **MindMirror** repo. Render reads `render.yaml` and
   proposes 3 resources: `mindmirror-db`, `mindmirror-ai`, `mindmirror-backend`.
3. Click **Apply**. The database + AI service build first; the backend builds too but will
   need two values (next step). First build takes ~5–10 min (Maven + scikit-learn).

## 2. Wire the two "paste-in" values on the backend

Once **mindmirror-ai** is live, copy its URL (e.g. `https://mindmirror-ai.onrender.com`).
Then open **mindmirror-backend → Environment** and set:

| Key | Value |
| --- | --- |
| `MINDMIRROR_AI_BASE_URL` | the AI service URL, e.g. `https://mindmirror-ai.onrender.com` |
| `MINDMIRROR_CORS_ORIGINS` | your Vercel URL, e.g. `https://mind-mirror.vercel.app` (no trailing slash) |

Save → the backend redeploys. Verify it's up:
`https://mindmirror-backend.onrender.com/swagger-ui.html` and
`https://mindmirror-backend.onrender.com/api/hbsc/reference` (should return JSON).

## 3. Point the Vercel frontend at the backend

In **Vercel → your project → Settings → Environment Variables** add (Production):

| Key | Value |
| --- | --- |
| `VITE_API_URL` | `https://mindmirror-backend.onrender.com` (no trailing slash, no `/api`) |

Then **Deployments → ⋯ → Redeploy** (uncheck "use existing build cache"). Also confirm
**Settings → Root Directory = `frontend`**.

## 4. Test

Open your Vercel URL → log in with **demo / demo1234**. 🎉

---

## Notes & troubleshooting
- **Cold starts:** Render free web services sleep after ~15 min idle and take ~50s to wake.
  The first request after a nap is slow — that's normal, not a failure.
- **Still "Invalid credentials" on Vercel?** It means the browser can't reach the backend.
  Check: `VITE_API_URL` is the exact Render URL (no trailing slash) and you redeployed;
  `MINDMIRROR_CORS_ORIGINS` is your exact Vercel origin; the backend is awake (open its
  `/api/hbsc/reference` once to wake it).
- **CORS error in the browser console:** `MINDMIRROR_CORS_ORIGINS` must match the Vercel
  origin exactly (scheme + host, no path, no trailing slash).
- **Demo data:** seeded on the backend's first boot against the empty DB. If the AI service
  wasn't reachable yet at that moment, the demo user still exists (login works) but its
  history has no AI scores until a check-in is submitted. To reseed fully, delete the Render
  database and re-apply the blueprint.
- **Free Postgres** expires ~90 days after creation (Render free tier).
- If Render rejects `runtime: docker` in the blueprint, change it to `env: docker`.

> Reminder: this repo's owner commits/pushes manually — the assistant does not run git for you.
