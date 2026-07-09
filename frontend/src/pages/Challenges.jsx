import { Box, Typography, Button, Chip, Stack } from '@mui/material';
import CheckCircleRoundedIcon from '@mui/icons-material/CheckCircleRounded';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import { AsyncBlock } from '../components/States';
import { useAsync } from '../hooks/useAsync';
import { useLocalized } from '../hooks/useLocalized';
import { challengesApi } from '../api/endpoints';

export default function Challenges() {
  const { t } = useTranslation();
  const pick = useLocalized();
  const { data, loading, error, reload, setData } = useAsync(() =>
    Promise.all([challengesApi.today(), challengesApi.list()]).then(([today, list]) => ({ today, list }))
  );

  const complete = async (id) => {
    await challengesApi.complete(id);
    reload();
  };

  return (
    <Box>
      <PageHeader title={t('challenges.title')} />
      <AsyncBlock loading={loading} error={error} onRetry={reload}>
        {data && (
          <Box sx={{ display: 'grid', gap: 2.5 }}>
            <GlassCard sx={{ background: 'linear-gradient(135deg, rgba(246,162,75,0.18), rgba(108,196,232,0.14))' }}>
              <Typography variant="overline" color="text.secondary">{t('challenges.today')}</Typography>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
                <Box>
                  <Typography variant="h5">{pick(data.today, 'title')}</Typography>
                  <Typography variant="body2" color="text.secondary">{pick(data.today, 'description')}</Typography>
                </Box>
                <Stack alignItems="flex-end" spacing={1}>
                  <Chip color="secondary" label={t('challenges.reward', { xp: data.today.xpReward })} />
                  {data.today.status === 'COMPLETED' ? (
                    <Chip icon={<CheckCircleRoundedIcon />} color="success" label={t('challenges.completed')} />
                  ) : (
                    <Button variant="contained" onClick={() => complete(data.today.id)}>{t('challenges.complete')}</Button>
                  )}
                </Stack>
              </Box>
            </GlassCard>

            <Typography variant="h6">{t('challenges.history')}</Typography>
            <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', lg: 'repeat(3,1fr)' } }}>
              {data.list.map((c) => (
                <GlassCard key={c.id}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{pick(c, 'title')}</Typography>
                    <Chip size="small" label={`+${c.xpReward}`} />
                  </Box>
                  <Typography variant="caption" color="text.secondary">{String(c.challengeDate)}</Typography>
                  <Box sx={{ mt: 1 }}>
                    {c.status === 'COMPLETED'
                      ? <Chip size="small" color="success" label={t('challenges.completed')} />
                      : <Button size="small" variant="outlined" onClick={() => complete(c.id)}>{t('challenges.complete')}</Button>}
                  </Box>
                </GlassCard>
              ))}
            </Box>
          </Box>
        )}
      </AsyncBlock>
    </Box>
  );
}
