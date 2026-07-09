import { Box, Typography, Button, Stack, IconButton, Menu, MenuItem, Chip } from '@mui/material';
import TranslateRoundedIcon from '@mui/icons-material/TranslateRounded';
import { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import GlassCard from '../components/GlassCard';
import AvatarView from '../components/AvatarView';
import { avatarStateColor } from '../theme';

const STATES = [
  ['EXCELLENT', { smile: 0.95, glow: true }],
  ['HAPPY', { smile: 0.8, glow: true }],
  ['NEUTRAL', { smile: 0.5 }],
  ['STRESSED', { smile: 0.3, stress: 0.7 }],
  ['BURNED_OUT', { smile: 0.2, stress: 0.9 }],
  ['EXHAUSTED', { smile: 0.35, dark_circles: true }],
];

const METRIC_ICONS = ['😴', '🙂', '😰', '🏃', '📱', '💧', '📚', '👥', '⚡', '🥗'];
const FEATURE_ICONS = ['🧑‍🚀', '📊', '🔮', '💡', '🏆', '🇲🇰'];

function Section({ children, sx }) {
  return (
    <Box
      component={motion.section}
      initial={{ opacity: 0, y: 28 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: '-70px' }}
      transition={{ duration: 0.5, ease: 'easeOut' }}
      sx={{ maxWidth: 1120, mx: 'auto', px: { xs: 2, md: 4 }, ...sx }}
    >
      {children}
    </Box>
  );
}

function Heading({ title, sub }) {
  return (
    <Box sx={{ textAlign: 'center', mb: 4 }}>
      <Typography variant="h4" sx={{ fontWeight: 800 }}>{title}</Typography>
      {sub && <Typography color="text.secondary" sx={{ mt: 1, maxWidth: 620, mx: 'auto' }}>{sub}</Typography>}
    </Box>
  );
}

export default function Landing() {
  const { t, i18n } = useTranslation();
  const [anchor, setAnchor] = useState(null);
  const setLang = (l) => { i18n.changeLanguage(l); localStorage.setItem('mm_lang', l); setAnchor(null); };

  const metrics = t('landing.metrics', { returnObjects: true }) || [];
  const features = [1, 2, 3, 4, 5, 6].map((n) => ({
    icon: FEATURE_ICONS[n - 1], title: t(`landing.f${n}title`), body: t(`landing.f${n}body`),
  }));
  const steps = [1, 2, 3, 4].map((n) => ({ title: t(`landing.s${n}t`), body: t(`landing.s${n}b`) }));

  return (
    <Box sx={{ pb: 8 }}>
      {/* Top bar */}
      <Box sx={{ maxWidth: 1120, mx: 'auto', px: { xs: 2, md: 4 }, py: 2.5,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.2 }}>
          <Box sx={{ width: 40, height: 40, borderRadius: 2.4, background: 'linear-gradient(135deg,#f6a24b,#6cc4e8)', display: 'grid', placeItems: 'center', fontSize: 22 }}>🪞</Box>
          <Typography variant="h6" sx={{ fontWeight: 800 }}>{t('app.name')}</Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <IconButton onClick={(e) => setAnchor(e.currentTarget)}><TranslateRoundedIcon /></IconButton>
          <Menu anchorEl={anchor} open={!!anchor} onClose={() => setAnchor(null)}>
            <MenuItem onClick={() => setLang('en')}>🇬🇧 English</MenuItem>
            <MenuItem onClick={() => setLang('mk')}>🇲🇰 Македонски</MenuItem>
          </Menu>
          <Button component={RouterLink} to="/login" sx={{ ml: 1 }}>{t('landing.login')}</Button>
        </Box>
      </Box>

      {/* Hero */}
      <Box sx={{ maxWidth: 1120, mx: 'auto', px: { xs: 2, md: 4 }, pt: { xs: 3, md: 6 }, pb: { xs: 6, md: 9 } }}>
        <Box sx={{ display: 'grid', gap: 4, gridTemplateColumns: { xs: '1fr', md: '1.05fr 0.95fr' }, alignItems: 'center' }}>
          <Box component={motion.div} initial={{ opacity: 0, x: -24 }} animate={{ opacity: 1, x: 0 }} transition={{ duration: 0.55 }}>
            <Typography variant="h2" sx={{ fontSize: { xs: 40, md: 60 }, lineHeight: 1.04, mb: 2 }}>
              {t('landing.hero')}
            </Typography>
            <Typography variant="h6" color="text.secondary" sx={{ fontWeight: 400, maxWidth: 560, mb: 4 }}>
              {t('landing.sub')}
            </Typography>
            <Stack direction="row" spacing={2}>
              <Button component={RouterLink} to="/register" variant="contained" size="large">{t('landing.getStarted')}</Button>
              <Button component={RouterLink} to="/login" variant="outlined" size="large">{t('landing.login')}</Button>
            </Stack>
          </Box>
          <Box sx={{ display: 'grid', placeItems: 'center' }}>
            <motion.div animate={{ y: [0, -12, 0] }} transition={{ duration: 5, repeat: Infinity, ease: 'easeInOut' }}>
              <AvatarView state="EXCELLENT" attributes={{ smile: 0.95, glow: true }} size={300} />
            </motion.div>
          </Box>
        </Box>
      </Box>

      {/* Metrics strip */}
      <Section sx={{ mb: { xs: 7, md: 10 } }}>
        <GlassCard sx={{ p: { xs: 3, md: 4 } }}>
          <Heading title={t('landing.metricsTitle')} sub={t('landing.metricsSub')} />
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1.2, justifyContent: 'center' }}>
            {metrics.map((m, i) => (
              <Chip key={m} label={`${METRIC_ICONS[i] || ''} ${m}`} variant="outlined"
                sx={{ fontWeight: 600, py: 2, fontSize: 14 }} />
            ))}
          </Box>
        </GlassCard>
      </Section>

      {/* Features */}
      <Section sx={{ mb: { xs: 7, md: 10 } }}>
        <Heading title={t('landing.featuresTitle')} />
        <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: 'repeat(3,1fr)' } }}>
          {features.map((f, i) => (
            <GlassCard key={i} delay={i * 0.06} sx={{ p: 3 }}>
              <Typography sx={{ fontSize: 34, mb: 1 }}>{f.icon}</Typography>
              <Typography variant="h6" sx={{ mb: 0.5 }}>{f.title}</Typography>
              <Typography variant="body2" color="text.secondary">{f.body}</Typography>
            </GlassCard>
          ))}
        </Box>
      </Section>

      {/* How it works */}
      <Section sx={{ mb: { xs: 7, md: 10 } }}>
        <Heading title={t('landing.howTitle')} />
        <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: 'repeat(4,1fr)' } }}>
          {steps.map((s, i) => (
            <GlassCard key={i} delay={i * 0.06} sx={{ p: 3 }}>
              <Box sx={{ width: 38, height: 38, borderRadius: '50%', mb: 1.5, display: 'grid', placeItems: 'center',
                fontWeight: 800, color: '#fff', background: 'linear-gradient(135deg,#f6a24b,#6cc4e8)' }}>{i + 1}</Box>
              <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 0.5 }}>{s.title}</Typography>
              <Typography variant="body2" color="text.secondary">{s.body}</Typography>
            </GlassCard>
          ))}
        </Box>
      </Section>

      {/* Avatar states showcase */}
      <Section sx={{ mb: { xs: 7, md: 10 } }}>
        <Heading title={t('landing.statesTitle')} sub={t('landing.statesSub')} />
        <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr 1fr', sm: 'repeat(3,1fr)', md: 'repeat(6,1fr)' } }}>
          {STATES.map(([state, attrs], i) => (
            <GlassCard key={state} delay={i * 0.05} sx={{ p: 2, textAlign: 'center' }}>
              <Box sx={{ display: 'flex', justifyContent: 'center' }}>
                <AvatarView state={state} attributes={attrs} size={104} />
              </Box>
              <Chip size="small" label={t(`landing.states.${state}`)}
                sx={{ mt: 1, bgcolor: (avatarStateColor[state] || '#888') + '22',
                  border: `1px solid ${avatarStateColor[state] || '#888'}`, fontWeight: 600 }} />
            </GlassCard>
          ))}
        </Box>
      </Section>

      {/* Final CTA */}
      <Section>
        <GlassCard sx={{ p: { xs: 4, md: 6 }, textAlign: 'center',
          background: 'linear-gradient(135deg, rgba(246,162,75,0.20), rgba(108,196,232,0.16))' }}>
          <Typography variant="h4" sx={{ fontWeight: 800, mb: 1.5 }}>{t('landing.ctaTitle')}</Typography>
          <Typography color="text.secondary" sx={{ mb: 3, maxWidth: 560, mx: 'auto' }}>{t('landing.ctaSub')}</Typography>
          <Stack direction="row" spacing={2} justifyContent="center">
            <Button component={RouterLink} to="/register" variant="contained" size="large">{t('landing.getStarted')}</Button>
            <Button component={RouterLink} to="/login" variant="outlined" size="large">{t('landing.login')}</Button>
          </Stack>
        </GlassCard>
      </Section>
    </Box>
  );
}
