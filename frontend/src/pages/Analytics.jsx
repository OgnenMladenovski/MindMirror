import { Box, Typography, Stack, Chip } from '@mui/material';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import { AsyncBlock } from '../components/States';
import { ChartCard, FeatureImportanceChart } from '../components/charts';
import { useAsync } from '../hooks/useAsync';
import { useLocalized } from '../hooks/useLocalized';
import { dashboardApi } from '../api/endpoints';

export default function Analytics() {
  const { t } = useTranslation();
  const pick = useLocalized();
  const { data, loading, error, reload } = useAsync(() =>
    Promise.all([dashboardApi.trends(), dashboardApi.prediction().catch(() => null)])
      .then(([trends, prediction]) => ({ trends, prediction }))
  );

  return (
    <Box>
      <PageHeader title={t('analytics.title')} />
      <AsyncBlock loading={loading} error={error} onRetry={reload}>
        {data && (
          <Box sx={{ display: 'grid', gap: 2.5 }}>
            {data.prediction && (
              <GlassCard>
                <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 2 }}>{t('analytics.prediction')}</Typography>
                <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr 1fr', md: 'repeat(5,1fr)' } }}>
                  <Stat label={t('analytics.moodTomorrow')} value={`${data.prediction.moodTomorrow?.toFixed(1)}/10`} color="#f9b4c6" />
                  <Stat label={t('analytics.burnoutTomorrow')} value={Math.round(data.prediction.burnoutTomorrow)} color="#ef7a72" />
                  <Stat label={t('analytics.stressTomorrow')} value={`${data.prediction.stressTomorrow?.toFixed(1)}/10`} color="#f7b955" />
                  <Stat label={t('analytics.sleepTomorrow')} value={Math.round(data.prediction.sleepQualityTomorrow)} color="#6cc4e8" />
                  <Stat label={t('analytics.recommendedActivity')} value={pick(data.prediction, 'recommendedActivity')} color="#6cc4e8" small />
                </Box>
                <Typography variant="caption" color="text.secondary" sx={{ mt: 1.5, display: 'block' }}>
                  {t('analytics.model')}: {data.prediction.modelVersion}
                </Typography>
              </GlassCard>
            )}

            <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' } }}>
              <GlassCard>
                <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1.5 }}>{t('analytics.trends')}</Typography>
                <Stack spacing={1.5}>
                  {data.trends.map((i, idx) => (
                    <Box key={idx} sx={{ display: 'flex', gap: 1.2, alignItems: 'flex-start' }}>
                      <Chip size="small" label={i.kind} variant="outlined" sx={{ mt: 0.2 }} />
                      <Typography variant="body2">{pick(i, 'text')}</Typography>
                    </Box>
                  ))}
                </Stack>
              </GlassCard>
              {data.prediction && (
                <ChartCard title={t('analytics.featureImportance')} height={300}>
                  <FeatureImportanceChart importance={data.prediction.featureImportance} target="mood" />
                </ChartCard>
              )}
            </Box>
          </Box>
        )}
      </AsyncBlock>
    </Box>
  );
}

function Stat({ label, value, color, small }) {
  return (
    <Box>
      <Typography variant={small ? 'subtitle1' : 'h4'} sx={{ color, fontWeight: 800, lineHeight: 1.1 }}>{value}</Typography>
      <Typography variant="caption" color="text.secondary">{label}</Typography>
    </Box>
  );
}
