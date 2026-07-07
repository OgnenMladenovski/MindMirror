import { Box, Typography, Table, TableBody, TableCell, TableHead, TableRow, Chip } from '@mui/material';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import MetricCard from '../components/MetricCard';
import { AsyncBlock, ErrorState } from '../components/States';
import { ChartCard, BurnoutDistChart } from '../components/charts';
import { useAsync } from '../hooks/useAsync';
import { useLocalized } from '../hooks/useLocalized';
import { useAuth } from '../context/AuthContext';
import { statisticsApi } from '../api/endpoints';

export default function Admin() {
  const { t } = useTranslation();
  const pick = useLocalized();
  const { user } = useAuth();
  const { data, loading, error, reload } = useAsync(() => statisticsApi.overview());

  if (user?.role !== 'ADMIN') {
    return (
      <Box>
        <PageHeader title={t('admin.title')} />
        <GlassCard><Typography color="text.secondary">{t('admin.onlyAdmin')}</Typography></GlassCard>
      </Box>
    );
  }

  return (
    <Box>
      <PageHeader title={t('admin.title')} />
      <AsyncBlock loading={loading} error={error} onRetry={reload}>
        {data && (
          <Box sx={{ display: 'grid', gap: 2.5 }}>
            <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr 1fr', md: 'repeat(3,1fr)' } }}>
              <MetricCard card={{ key: 'x', label: t('admin.totalUsers'), value: data.totalUsers, unit: '' }} />
              <MetricCard card={{ key: 'y', label: t('admin.totalLogs'), value: data.totalLogs, unit: '' }} />
              <MetricCard card={{ key: 'overall_wellness', label: t('admin.avgWellness'), value: data.averageWellness, unit: '/100' }} />
            </Box>

            <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' } }}>
              <ChartCard title={t('admin.burnoutDist')} height={260}>
                <BurnoutDistChart dist={data.burnoutDistribution} />
              </ChartCard>
              <GlassCard>
                <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1.5 }}>{t('admin.commonChallenges')}</Typography>
                {data.mostCommonChallenges.length === 0 && <Typography variant="body2" color="text.secondary">—</Typography>}
                {data.mostCommonChallenges.map((c) => (
                  <Box key={c.type} sx={{ display: 'flex', justifyContent: 'space-between', py: 0.6 }}>
                    <Typography variant="body2">{c.type}</Typography>
                    <Chip size="small" label={c.completedCount} />
                  </Box>
                ))}
              </GlassCard>
            </Box>

            <GlassCard>
              <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1.5 }}>{t('admin.hbsc')}</Typography>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>{t('hbsc.indicator')}</TableCell>
                    <TableCell align="right">{t('common.you')} (avg)</TableCell>
                    <TableCell align="right">{t('common.hbsc')}</TableCell>
                    <TableCell align="right">{t('common.difference')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {data.hbscComparison.map((r) => (
                    <TableRow key={r.indicator}>
                      <TableCell>{pick(r, 'label')}</TableCell>
                      <TableCell align="right">{r.userValue ?? '—'} {r.unit}</TableCell>
                      <TableCell align="right">{r.hbscValue} {r.unit}</TableCell>
                      <TableCell align="right">{r.difference == null ? '—' : `${r.difference > 0 ? '+' : ''}${r.difference}`}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </GlassCard>
          </Box>
        )}
      </AsyncBlock>
    </Box>
  );
}
