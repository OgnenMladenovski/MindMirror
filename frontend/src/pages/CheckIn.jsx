import { useState } from 'react';
import {
  Box, Typography, Slider, Button, Chip, Stack, Alert, Divider,
} from '@mui/material';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import AvatarView from '../components/AvatarView';
import { useLocalized } from '../hooks/useLocalized';
import { logsApi } from '../api/endpoints';

const moodEmoji = (m) => (m >= 8 ? '😄' : m >= 6 ? '🙂' : m >= 4 ? '😐' : '😔');

function SliderField({ label, value, onChange, min, max, step, unit, format }) {
  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
        <Typography variant="body2" sx={{ fontWeight: 600 }}>{label}</Typography>
        <Typography variant="body2" color="primary" sx={{ fontWeight: 700 }}>
          {format ? format(value) : value}{unit ? ` ${unit}` : ''}
        </Typography>
      </Box>
      <Slider value={value} onChange={(_, v) => onChange(v)} min={min} max={max} step={step} />
    </Box>
  );
}

const INITIAL = {
  sleepHours: 8, stressLevel: 5, moodScore: 7, physicalActivityMin: 45, waterIntake: 1.8,
  screenTimeHours: 4, studyHours: 3, socialTimeMin: 60, energyLevel: 7, nutritionQuality: 7, notes: '',
};

export default function CheckIn() {
  const { t } = useTranslation();
  const pick = useLocalized();
  const [form, setForm] = useState(INITIAL);
  const [result, setResult] = useState(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(false);
  const set = (k) => (v) => setForm((f) => ({ ...f, [k]: v }));

  const submit = async () => {
    setBusy(true); setError(false);
    try {
      const data = await logsApi.create({ ...form, moodEmoji: moodEmoji(form.moodScore) });
      setResult(data);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch {
      setError(true);
    } finally {
      setBusy(false);
    }
  };

  return (
    <Box>
      <PageHeader title={t('checkin.title')} subtitle={t('checkin.subtitle')} />

      {result && <Results result={result} pick={pick} t={t} />}

      <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' } }}>
        <GlassCard>
          <Stack spacing={2.5}>
            {error && <Alert severity="error">{t('auth.failed')}</Alert>}
            <SliderField label={t('checkin.sleep')} value={form.sleepHours} onChange={set('sleepHours')} min={0} max={12} step={0.5} unit="h" />
            <SliderField label={t('checkin.mood')} value={form.moodScore} onChange={set('moodScore')} min={1} max={10} step={1} />
            <SliderField label={t('checkin.stress')} value={form.stressLevel} onChange={set('stressLevel')} min={1} max={10} step={1} />
            <SliderField label={t('checkin.energy')} value={form.energyLevel} onChange={set('energyLevel')} min={1} max={10} step={1} />
            <SliderField label={t('checkin.nutrition')} value={form.nutritionQuality} onChange={set('nutritionQuality')} min={1} max={10} step={1} />
          </Stack>
        </GlassCard>
        <GlassCard>
          <Stack spacing={2.5}>
            <SliderField label={t('checkin.activity')} value={form.physicalActivityMin} onChange={set('physicalActivityMin')} min={0} max={180} step={5} unit="min" />
            <SliderField label={t('checkin.screen')} value={form.screenTimeHours} onChange={set('screenTimeHours')} min={0} max={14} step={0.5} unit="h" />
            <SliderField label={t('checkin.study')} value={form.studyHours} onChange={set('studyHours')} min={0} max={12} step={0.5} unit="h" />
            <SliderField label={t('checkin.social')} value={form.socialTimeMin} onChange={set('socialTimeMin')} min={0} max={180} step={5} unit="min" />
            <SliderField label={t('checkin.water')} value={form.waterIntake} onChange={set('waterIntake')} min={0} max={4} step={0.1} unit="L" format={(v) => v.toFixed(1)} />
          </Stack>
        </GlassCard>
      </Box>

      <Button variant="contained" size="large" sx={{ mt: 2.5 }} onClick={submit} disabled={busy}>
        {busy ? t('checkin.saving') : t('checkin.submit')}
      </Button>
    </Box>
  );
}

function Results({ result, pick, t }) {
  const s = result.scores;
  const scoreChips = s ? [
    ['overall', s.overallWellnessScore], ['sleep', s.sleepScore], ['burnout', s.burnoutIndex],
    ['wellbeing', s.wellbeingScore], ['social', s.socialBalanceScore], ['productivity', s.productivityScore],
  ] : [];

  return (
    <Box component={motion.div} initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}
      sx={{ mb: 3 }}>
      <Alert severity="success" sx={{ mb: 2 }}>{t('checkin.success')}</Alert>
      <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: '1fr 2fr' } }}>
        {result.avatar && (
          <GlassCard sx={{ textAlign: 'center' }}>
            <Box sx={{ display: 'flex', justifyContent: 'center' }}>
              <AvatarView state={result.avatar.state} attributes={parse(result.avatar.attributesJson)} size={170} />
            </Box>
            <Chip label={result.avatar.state.replace('_', ' ')} color="primary" size="small" sx={{ mt: 1 }} />
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>{pick(result.avatar, 'caption')}</Typography>
          </GlassCard>
        )}
        <GlassCard>
          <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 1.5 }}>{t('checkin.yourScores')}</Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {scoreChips.map(([k, v]) => (
              <Chip key={k} label={`${t(`scores.${k}`)}: ${Math.round(v)}`} variant="outlined" />
            ))}
            {s && <Chip label={t(`risk.${s.riskLevel}`)} color={s.riskLevel === 'High' ? 'error' : s.riskLevel === 'Medium' ? 'warning' : 'success'} />}
          </Box>
          {result.prediction && (
            <>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1 }}>{t('checkin.prediction')}</Typography>
              <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                <Chip size="small" label={`${t('analytics.moodTomorrow')}: ${result.prediction.moodTomorrow?.toFixed(1)}/10`} />
                <Chip size="small" label={`${t('analytics.burnoutTomorrow')}: ${Math.round(result.prediction.burnoutTomorrow)}`} />
                <Chip size="small" color="secondary" label={pick(result.prediction, 'recommendedActivity')} />
              </Stack>
            </>
          )}
          {result.recommendations?.length > 0 && (
            <>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1 }}>{t('checkin.advice')}</Typography>
              <Stack spacing={1}>
                {result.recommendations.map((r) => <Typography key={r.id} variant="body2">• {pick(r, 'text')}</Typography>)}
              </Stack>
            </>
          )}
        </GlassCard>
      </Box>
    </Box>
  );

  function pick2(p) {
    const mk = document.documentElement.lang === 'mk';
    return p.recommendedActivityMk || p.recommendedActivityEn || '';
  }
}

function parse(json) { try { return json ? JSON.parse(json) : {}; } catch { return {}; } }
