import { Box, Typography, Table, TableBody, TableCell, TableHead, TableRow, Chip } from '@mui/material';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import { AsyncBlock } from '../components/States';
import { ChartCard, ComparisonBarChart } from '../components/charts';
import { useAsync } from '../hooks/useAsync';
import { useLocalized } from '../hooks/useLocalized';
import { hbscApi } from '../api/endpoints';

export default function Hbsc() {
  const { t } = useTranslation();
  const pick = useLocalized();
  const { data, loading, error, reload } = useAsync(() => hbscApi.comparison());

  return (
    <Box>
      <PageHeader title={t('hbsc.title')} subtitle={data ? t('hbsc.subtitle', { age: data.ageGroup }) : ''} />
      <AsyncBlock loading={loading} error={error} onRetry={reload}>
        {data && (
          <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' } }}>
            <ChartCard title={`${t('common.you')} · ${t('common.hbsc')}`} height={320}>
              <ComparisonBarChart data={data.rows.map((r) => ({ name: pick(r, 'label'), You: r.userValue, HBSC: r.hbscValue }))} />
            </ChartCard>

            <GlassCard>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>{t('hbsc.indicator')}</TableCell>
                    <TableCell align="right">{t('common.you')}</TableCell>
                    <TableCell align="right">{t('common.hbsc')}</TableCell>
                    <TableCell align="right">{t('common.difference')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {data.rows.map((r) => (
                    <TableRow key={r.indicator}>
                      <TableCell>{pick(r, 'label')}</TableCell>
                      <TableCell align="right">{r.userValue ?? '—'} {r.unit}</TableCell>
                      <TableCell align="right">{r.hbscValue} {r.unit}</TableCell>
                      <TableCell align="right">
                        {r.difference == null ? '—' : (
                          <Chip size="small" label={`${r.difference > 0 ? '+' : ''}${r.difference}`}
                            color={r.difference === 0 ? 'default' : r.difference > 0 ? 'success' : 'warning'} />
                        )}
                      </TableCell>
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
