import { Box, Typography, LinearProgress, Chip } from '@mui/material';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import { AsyncBlock } from '../components/States';
import { useAsync } from '../hooks/useAsync';
import { useLocalized } from '../hooks/useLocalized';
import { achievementsApi } from '../api/endpoints';

export default function Achievements() {
  const { t } = useTranslation();
  const pick = useLocalized();
  const { data, loading, error, reload } = useAsync(() => achievementsApi.list());

  return (
    <Box>
      <PageHeader title={t('achievements.title')} />
      <AsyncBlock loading={loading} error={error} onRetry={reload}>
        {data && (
          <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', lg: 'repeat(3,1fr)' } }}>
            {data.map((a, i) => (
              <GlassCard key={a.id} delay={i * 0.05}
                sx={{ opacity: a.unlocked ? 1 : 0.6, filter: a.unlocked ? 'none' : 'grayscale(0.6)' }}>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', mb: 1 }}>
                  <Box sx={{ fontSize: 40, filter: a.unlocked ? 'none' : 'grayscale(1)' }}>{a.icon || '🏅'}</Box>
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{pick(a, 'title')}</Typography>
                    <Typography variant="caption" color="text.secondary">{pick(a, 'description')}</Typography>
                  </Box>
                  {a.unlocked && <Chip size="small" color="success" label={t('achievements.unlocked')} />}
                </Box>
                <LinearProgress variant="determinate" value={a.progress}
                  sx={{ height: 8, borderRadius: 4, mt: 1 }} color={a.unlocked ? 'success' : 'primary'} />
                <Typography variant="caption" color="text.secondary">{a.progress}% · +{a.xpReward} XP</Typography>
              </GlassCard>
            ))}
          </Box>
        )}
      </AsyncBlock>
    </Box>
  );
}
