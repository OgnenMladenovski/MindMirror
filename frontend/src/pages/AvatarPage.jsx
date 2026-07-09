import { Box, Typography, Chip, Stack, LinearProgress } from '@mui/material';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import AvatarView from '../components/AvatarView';
import { AsyncBlock } from '../components/States';
import { useAsync } from '../hooks/useAsync';
import { useLocalized } from '../hooks/useLocalized';
import { avatarApi } from '../api/endpoints';
import { avatarStateColor } from '../theme';

export default function AvatarPage() {
  const { t } = useTranslation();
  const pick = useLocalized();
  const { data, loading, error, reload } = useAsync(() =>
    Promise.all([avatarApi.current(), avatarApi.history()]).then(([current, history]) => ({ current, history }))
  );

  return (
    <Box>
      <PageHeader title={t('avatar.title')} subtitle={t('avatar.subtitle')} />
      <AsyncBlock loading={loading} error={error} onRetry={reload}>
        {data && (
          <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' } }}>
            <GlassCard sx={{ textAlign: 'center', py: 4 }}>
              <Box sx={{ display: 'flex', justifyContent: 'center' }}>
                <AvatarView state={data.current.state} attributes={parse(data.current.attributesJson)} size={260} />
              </Box>
              <Chip label={data.current.state.replace('_', ' ')} color="primary" sx={{ mt: 2 }} />
              <Typography variant="body1" color="text.secondary" sx={{ mt: 1.5 }}>{pick(data.current, 'caption')}</Typography>
            </GlassCard>

            <GlassCard>
              <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 2 }}>Attributes</Typography>
              <Stack spacing={2}>
                <Attr label={t('avatar.energy')} value={num(parse(data.current.attributesJson).energy)} />
                <Attr label={t('avatar.smile')} value={num(parse(data.current.attributesJson).smile)} />
                <Attr label={t('avatar.stress')} value={num(parse(data.current.attributesJson).stress)} invert />
              </Stack>

              <Typography variant="subtitle1" sx={{ fontWeight: 700, mt: 3, mb: 1.5 }}>{t('avatar.history')}</Typography>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                {data.history.slice(0, 14).map((h, i) => (
                  <Chip key={i} size="small" label={`${String(h.logDate).slice(5)} · ${h.state.replace('_', ' ')}`}
                    sx={{ bgcolor: (avatarStateColor[h.state] || '#888') + '33', border: `1px solid ${avatarStateColor[h.state] || '#888'}` }} />
                ))}
              </Box>
            </GlassCard>
          </Box>
        )}
      </AsyncBlock>
    </Box>
  );
}

function Attr({ label, value, invert }) {
  const pct = Math.round(value * 100);
  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
        <Typography variant="body2">{label}</Typography>
        <Typography variant="body2" sx={{ fontWeight: 700 }}>{pct}%</Typography>
      </Box>
      <LinearProgress variant="determinate" value={pct}
        color={invert ? (pct > 60 ? 'error' : 'warning') : 'success'} sx={{ height: 8, borderRadius: 4 }} />
    </Box>
  );
}

function parse(json) { try { return json ? JSON.parse(json) : {}; } catch { return {}; } }
function num(v) { return typeof v === 'number' ? v : 0; }
