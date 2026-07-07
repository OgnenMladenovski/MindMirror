import { cloneElement, useLayoutEffect, useRef, useState } from 'react';
import {
  LineChart, Line, AreaChart, Area, BarChart, Bar,
  RadarChart, Radar, PolarGrid, PolarAngleAxis, PolarRadiusAxis,
  PieChart, Pie, Cell, XAxis, YAxis, Tooltip, CartesianGrid, Legend,
} from 'recharts';
import { Box, Typography, Tooltip as MuiTooltip, useTheme } from '@mui/material';
import GlassCard from './GlassCard';
import { chartColors, heatColor } from '../theme';
import { useLocalized } from '../hooks/useLocalized';

/**
 * Self-measuring responsive wrapper. Recharts' own ResponsiveContainer relies on
 * a ResizeObserver that doesn't emit dimensions in some headless browsers, so we
 * measure width ourselves and hand explicit pixel width/height to the chart.
 */
export function Responsive({ height = 260, children }) {
  const ref = useRef(null);
  const [w, setW] = useState(0);
  useLayoutEffect(() => {
    const el = ref.current;
    if (!el) return;
    const measure = () => setW(el.clientWidth);
    measure();
    let ro;
    if (typeof ResizeObserver !== 'undefined') { ro = new ResizeObserver(measure); ro.observe(el); }
    window.addEventListener('resize', measure);
    return () => { ro && ro.disconnect(); window.removeEventListener('resize', measure); };
  }, []);
  return (
    <div ref={ref} style={{ width: '100%', height }}>
      {w > 0 ? children(w, height) : null}
    </div>
  );
}

export function ChartCard({ title, children, height = 260, delay = 0 }) {
  return (
    <GlassCard delay={delay}>
      <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1.5 }}>{title}</Typography>
      <Responsive height={height}>{(w, h) => cloneElement(children, { width: w, height: h })}</Responsive>
    </GlassCard>
  );
}

const fmtDate = (d) => (d ? String(d).slice(5) : '');

function useAxis() {
  const theme = useTheme();
  return {
    stroke: theme.palette.text.secondary,
    grid: theme.palette.mode === 'dark' ? 'rgba(255,255,255,0.08)' : 'rgba(0,0,0,0.08)',
    tooltip: {
      contentStyle: {
        background: theme.palette.background.paper,
        border: `1px solid ${theme.palette.divider}`,
        borderRadius: 12,
        backdropFilter: 'blur(8px)',
      },
    },
  };
}

export function MoodLineChart({ data, width, height }) {
  const a = useAxis();
  return (
    <LineChart width={width} height={height} data={data} margin={{ top: 6, right: 16, left: -18, bottom: 0 }}>
      <CartesianGrid stroke={a.grid} vertical={false} />
      <XAxis dataKey="date" tickFormatter={fmtDate} stroke={a.stroke} fontSize={11} minTickGap={24} />
      <YAxis domain={[0, 10]} stroke={a.stroke} fontSize={11} />
      <Tooltip {...a.tooltip} labelFormatter={fmtDate} />
      <Line type="monotone" dataKey="value" stroke={chartColors[0]} strokeWidth={3} dot={false} />
    </LineChart>
  );
}

export function SleepAreaChart({ data, width, height }) {
  const a = useAxis();
  return (
    <AreaChart width={width} height={height} data={data} margin={{ top: 6, right: 16, left: -18, bottom: 0 }}>
      <defs>
        <linearGradient id="sleepFill" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor={chartColors[4]} stopOpacity={0.6} />
          <stop offset="100%" stopColor={chartColors[4]} stopOpacity={0.05} />
        </linearGradient>
      </defs>
      <CartesianGrid stroke={a.grid} vertical={false} />
      <XAxis dataKey="date" tickFormatter={fmtDate} stroke={a.stroke} fontSize={11} minTickGap={24} />
      <YAxis domain={[0, 12]} stroke={a.stroke} fontSize={11} />
      <Tooltip {...a.tooltip} labelFormatter={fmtDate} />
      <Area type="monotone" dataKey="value" stroke={chartColors[4]} strokeWidth={3} fill="url(#sleepFill)" />
    </AreaChart>
  );
}

export function ScreenBarChart({ data, width, height }) {
  const a = useAxis();
  return (
    <BarChart width={width} height={height} data={data} margin={{ top: 6, right: 16, left: -18, bottom: 0 }}>
      <CartesianGrid stroke={a.grid} vertical={false} />
      <XAxis dataKey="date" tickFormatter={fmtDate} stroke={a.stroke} fontSize={11} minTickGap={24} />
      <YAxis stroke={a.stroke} fontSize={11} />
      <Tooltip {...a.tooltip} labelFormatter={fmtDate} cursor={{ fill: a.grid }} />
      <Bar dataKey="value" fill={chartColors[3]} radius={[6, 6, 0, 0]} />
    </BarChart>
  );
}

export function WellnessRadarChart({ data, width, height }) {
  const a = useAxis();
  const pick = useLocalized();
  const rows = (data || []).map((d) => ({ ...d, name: pick(d, 'label') }));
  return (
    <RadarChart width={width} height={height} data={rows} outerRadius="72%">
      <PolarGrid stroke={a.grid} />
      <PolarAngleAxis dataKey="name" tick={{ fill: a.stroke, fontSize: 11 }} />
      <PolarRadiusAxis domain={[0, 100]} tick={false} axisLine={false} />
      <Radar dataKey="value" stroke={chartColors[0]} fill={chartColors[0]} fillOpacity={0.4} />
      <Tooltip {...a.tooltip} />
    </RadarChart>
  );
}

export function ActivityPieChart({ data, width, height }) {
  const a = useAxis();
  const pick = useLocalized();
  const rows = (data || []).map((d) => ({ ...d, name: pick(d, 'label') }));
  return (
    <PieChart width={width} height={height}>
      <Pie data={rows} dataKey="value" nameKey="name" innerRadius="45%" outerRadius="75%" paddingAngle={3}>
        {rows.map((_, i) => <Cell key={i} fill={chartColors[i % chartColors.length]} />)}
      </Pie>
      <Tooltip {...a.tooltip} />
    </PieChart>
  );
}

export function BurnoutDistChart({ dist, width, height }) {
  const a = useAxis();
  const colors = { Low: '#31d0aa', Medium: '#f7b955', High: '#f0616d' };
  const rows = Object.entries(dist || {}).map(([name, value]) => ({ name, value }));
  return (
    <PieChart width={width} height={height}>
      <Pie data={rows} dataKey="value" nameKey="name" innerRadius="45%" outerRadius="75%" paddingAngle={3} label>
        {rows.map((r, i) => <Cell key={i} fill={colors[r.name] || chartColors[i]} />)}
      </Pie>
      <Legend />
      <Tooltip {...a.tooltip} />
    </PieChart>
  );
}

export function FeatureImportanceChart({ importance, target = 'mood', width, height }) {
  const a = useAxis();
  const map = (importance && importance[target]) || {};
  const rows = Object.entries(map)
    .map(([feature, value]) => ({ feature: prettyFeature(feature), value: Math.round(value * 1000) / 1000 }))
    .sort((x, y) => y.value - x.value);
  return (
    <BarChart layout="vertical" width={width} height={height} data={rows} margin={{ top: 4, right: 16, left: 40, bottom: 0 }}>
      <CartesianGrid stroke={a.grid} horizontal={false} />
      <XAxis type="number" stroke={a.stroke} fontSize={11} />
      <YAxis type="category" dataKey="feature" stroke={a.stroke} fontSize={11} width={110} />
      <Tooltip {...a.tooltip} cursor={{ fill: a.grid }} />
      <Bar dataKey="value" fill={chartColors[5]} radius={[0, 6, 6, 0]} />
    </BarChart>
  );
}

export function ComparisonBarChart({ data, width, height }) {
  const a = useAxis();
  return (
    <BarChart width={width} height={height} data={data} margin={{ top: 8, right: 12, left: -18, bottom: 40 }}>
      <CartesianGrid stroke={a.grid} vertical={false} />
      <XAxis dataKey="name" stroke={a.stroke} fontSize={11} angle={-20} textAnchor="end" interval={0} height={60} />
      <YAxis stroke={a.stroke} fontSize={11} />
      <Tooltip {...a.tooltip} />
      <Legend />
      <Bar dataKey="You" fill="#7c6cf0" radius={[6, 6, 0, 0]} />
      <Bar dataKey="HBSC" fill="#31d0aa" radius={[6, 6, 0, 0]} />
    </BarChart>
  );
}

export function MoodHeatmap({ data, title }) {
  return (
    <GlassCard>
      <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1.5 }}>{title}</Typography>
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.8 }}>
        {(data || []).map((c) => (
          <MuiTooltip key={c.date} title={`${c.date} · ${c.value}`} arrow>
            <Box sx={{ width: 26, height: 26, borderRadius: 1.2, bgcolor: heatColor[c.color] || '#888',
              cursor: 'pointer', transition: 'transform .15s', '&:hover': { transform: 'scale(1.18)' } }} />
          </MuiTooltip>
        ))}
      </Box>
      <Box sx={{ display: 'flex', gap: 2, mt: 2, flexWrap: 'wrap' }}>
        {[['green', 'Excellent'], ['yellow', 'Normal'], ['orange', 'Poor'], ['red', 'Critical']].map(([c, l]) => (
          <Box key={c} sx={{ display: 'flex', alignItems: 'center', gap: 0.6 }}>
            <Box sx={{ width: 14, height: 14, borderRadius: 0.8, bgcolor: heatColor[c] }} />
            <Typography variant="caption" color="text.secondary">{l}</Typography>
          </Box>
        ))}
      </Box>
    </GlassCard>
  );
}

function prettyFeature(f) {
  return f.replace(/_/g, ' ').replace('min', 'minutes').replace('hours', 'hrs');
}
