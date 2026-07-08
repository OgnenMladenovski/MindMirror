# MindMirror Frontend (React + Vite)

React client for MindMirror: dashboards, the digital-twin avatar, daily check-in, analytics,
challenges, achievements and HBSC comparison — with an **English / Македонски** toggle.

## Stack
- React 18 + Vite + React Router
- Material UI (glassmorphism theme, dark/light mode)
- Recharts (line / area / bar / radar / pie + calendar heatmap)
- Framer Motion (animated avatar + page transitions)
- Axios + react-i18next

## Run
```bash
npm install
npm run dev        # http://localhost:5173
```
The dev server proxies `/api` → the backend on `:8080` (`vite.config.js`), so start the
backend stack first (`docker compose up` from the repo root). Log in as **demo / demo1234**.

To point at a different backend: `VITE_BACKEND=http://host:port npm run dev`.

## Build
```bash
npm run build      # outputs to dist/
```

## Structure
```
src/
├── api/            axios client + endpoint wrappers
├── context/        auth + color-mode providers
├── components/     Layout, GlassCard, MetricCard, AvatarView, charts, …
├── hooks/          useAsync, useLocalized (picks *_en / *_mk)
├── locales/        en.json, mk.json
├── pages/          Landing, Login, Dashboard, CheckIn, Avatar, Hbsc, Admin, …
└── theme.js        MUI glassmorphism theme
```

## Notes
- Bilingual: static UI strings live in `locales/*`; dynamic API text is returned as
  `*_en`/`*_mk` and selected by `useLocalized`.
- Charts use a small self-measuring `Responsive` wrapper (in `components/charts.jsx`)
  instead of Recharts' `ResponsiveContainer`, which doesn't emit dimensions in some
  headless browsers.
