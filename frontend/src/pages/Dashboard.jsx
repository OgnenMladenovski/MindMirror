import { Box, Typography, Chip, Button, Stack } from '@mui/material';
import LocalFireDepartmentRoundedIcon from '@mui/icons-material/LocalFireDepartmentRounded';
import BoltRoundedIcon from '@mui/icons-material/BoltRounded';
import MilitaryTechRoundedIcon from '@mui/icons-material/MilitaryTechRounded';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import MetricCard from '../components/MetricCard';
import AvatarView from '../components/AvatarView';
import { AsyncBlock } from '../components/States';
import {
  ChartCard, MoodLineChart, SleepAreaChart, ScreenBarChart, WellnessRadarChart,
  ActivityPieChart, MoodHeatmap,
} from '../components/charts';
import { useAsync } from '../hooks/useAsync';
import { useLocalized } from '../hooks/useLocalized';
import { useAuth } from '../context/AuthContext';
import { dashboardApi, avatarApi, recommendationsApi, challengesApi } from '../api/endpoints';

export default function Dashboard() {
  const { t } = useTranslation();
  const pick = useLocalized();
  const { user } = useAuth();

  const { data, loading, error, reload } = useAsync(() =>
    Promise.all([
      dashboardApi.get(),
      avatarApi.current(),
      recommendationsApi.latest(1),
      challengesApi.today().catch(() => null),
    ]).then(([dash, avatar, recs, challenge]) => ({ dash, avatar, recs, challenge }))
  );

  return (
    <Box>
      <PageHeader
        title={t('dashboard.greeting', { name: user?.fullName?.split(' ')[0] || user?.username })}
        subtitle={t('dashboard.subtitle')}
        action={<Button component={RouterLink} to="/checkin" variant="contained">{t('nav.checkin')}</Button>}
      />
      <AsyncBlock loading={loading} error={error} onRetry={reload}>
        {data && <Content data={data} pick={pick} t={t} />}
      </AsyncBlock>
    </Box>
  );
}

function Content({ data, pick, t }) {
  const { dash, avatar, recs, challenge } = data;
  const cards = (dash.cards || []).map((c) => ({ ...c, label: pick(c, 'label') }));

  return (
    <Box sx={{ display: 'grid', gap: 2.5 }}>
      {/* metric cards */}
      <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr 1fr', sm: 'repeat(3,1fr)', lg: 'repeat(4,1fr)' } }}>
        {cards.map((c, i) => <MetricCard key={c.key} card={c} delay={i * 0.05} />)}
        <GlassCard delay={cards.length * 0.05} sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', gap: 1 }}>
          <Stack direction="row" spacing={1} alignItems="center"><MilitaryTechRoundedIcon color="primary" />
            <Typography variant="body2" color="text.secondary">{t('common.level')} {dash.level}</Typography></Stack>
          <Stack direction="row" spacing={1} alignItems="center"><BoltRoundedIcon sx={{ color: '#f7b955' }} />
            <Typography variant="body2" color="text.secondary">{dash.totalXp} {t('common.xp')}</Typography></Stack>
          <Stack direction="row" spacing={1} alignItems="center"><LocalFireDepartmentRoundedIcon sx={{ color: '#f9b4c6' }} />
            <Typography variant="body2" color="text.secondary">{dash.currentStreak} {t('common.days')}</Typography></Stack>
        </GlassCard>
      </Box>

      {/* charts + sidebar */}
      <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', lg: '2fr 1fr' } }}>
        <Box sx={{ display: 'grid', gap: 2.5 }}>
          <ChartCard title={t('dashboard.mood30')} height={220}><MoodLineChart data={dash.moodTrend} /></ChartCard>
          <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' } }}>
            <ChartCard title={t('dashboard.sleepTrend')} height={220}><SleepAreaChart data={dash.sleepTrend} /></ChartCard>
            <ChartCard title={t('dashboard.screenTime')} height={220}><ScreenBarChart data={dash.screenTime} /></ChartCard>
            <ChartCard title={t('dashboard.wellnessDim')} height={240}><WellnessRadarChart data={dash.wellnessRadar} /></ChartCard>
            <ChartCard title={t('dashboard.activityDist')} height={240}><ActivityPieChart data={dash.activityDistribution} /></ChartCard>
          </Box>
        </Box>

        <Box sx={{ display: 'grid', gap: 2.5, alignContent: 'start' }}>
          <GlassCard sx={{ textAlign: 'center' }}>
            <Box sx={{ display: 'flex', justifyContent: 'center' }}>
              <AvatarView state={avatar.state} attributes={parseAttrs(avatar.attributesJson)} size={180} />
            </Box>
            <Chip label={avatar.state.replace('_', ' ')} color="primary" size="small" sx={{ mt: 1 }} />
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>{pick(avatar, 'caption')}</Typography>
          </GlassCard>

          {challenge && (
            <GlassCard>
              <Typography variant="overline" color="text.secondary">{t('challenges.today')}</Typography>
              <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{pick(challenge, 'title')}</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>{pick(challenge, 'description')}</Typography>
              <Chip size="small" label={t('challenges.reward', { xp: challenge.xpReward })} color="secondary" />
            </GlassCard>
          )}

          <GlassCard>
            <Typography variant="overline" color="text.secondary">{t('checkin.advice')}</Typography>
            <Stack spacing={1.2} sx={{ mt: 0.5 }}>
              {recs.map((r) => (
                <Typography key={r.id} variant="body2">• {pick(r, 'text')}</Typography>
              ))}
            </Stack>
          </GlassCard>
        </Box>
      </Box>

      <MoodHeatmap data={dash.moodHeatmap} title={t('dashboard.moodCalendar')} />
    </Box>
  );
}

function parseAttrs(json) {
  try { return json ? JSON.parse(json) : {}; } catch { return {}; }
}
