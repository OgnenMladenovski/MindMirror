import { Box, Typography, LinearProgress } from '@mui/material';
import ArrowUpwardRoundedIcon from '@mui/icons-material/ArrowUpwardRounded';
import ArrowDownwardRoundedIcon from '@mui/icons-material/ArrowDownwardRounded';
import GlassCard from './GlassCard';
import { brandColors } from '../theme';

const ACCENTS = {
  overall_wellness: '#f6a24b',
  sleep: '#6cc4e8',
  burnout: '#ef7a72',
  social: '#6cc4e8',
  mood: '#f9b4c6',
  stress: '#f7b955',
  activity: '#f6c26b',
};

export default function MetricCard({ card, delay = 0 }) {
  const accent = ACCENTS[card.key] || brandColors.primary;
  const showBar = card.unit === '/100';
  const delta = card.delta;
  const good = card.key === 'burnout' || card.key === 'stress' ? delta < 0 : delta > 0;

  return (
    <GlassCard delay={delay} sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 600 }}>
          {card.label}
        </Typography>
        {delta != null && delta !== 0 && (
          <Box
            sx={{
              display: 'flex', alignItems: 'center', gap: 0.3,
              color: good ? 'success.main' : 'error.main', fontSize: 13, fontWeight: 700,
            }}
          >
            {delta > 0 ? <ArrowUpwardRoundedIcon fontSize="inherit" /> : <ArrowDownwardRoundedIcon fontSize="inherit" />}
            {Math.abs(delta)}
          </Box>
        )}
      </Box>
      <Box sx={{ display: 'flex', alignItems: 'baseline', gap: 0.5 }}>
        <Typography variant="h4" sx={{ color: accent }}>{card.value}</Typography>
        <Typography variant="body2" color="text.secondary">{card.unit}</Typography>
      </Box>
      {showBar && (
        <LinearProgress
          variant="determinate"
          value={Math.min(100, Math.max(0, card.value))}
          sx={{ height: 8, borderRadius: 6, '& .MuiLinearProgress-bar': { backgroundColor: accent } }}
        />
      )}
    </GlassCard>
  );
}
