import { Box, Typography, Button, Stack, IconButton, Menu, MenuItem } from '@mui/material';
import TranslateRoundedIcon from '@mui/icons-material/TranslateRounded';
import { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import GlassCard from '../components/GlassCard';
import AvatarView from '../components/AvatarView';

export default function Landing() {
  const { t, i18n } = useTranslation();
  const [anchor, setAnchor] = useState(null);
  const setLang = (l) => { i18n.changeLanguage(l); localStorage.setItem('mm_lang', l); setAnchor(null); };

  const features = [
    { icon: '🧑‍🚀', title: t('landing.f1title'), body: t('landing.f1body') },
    { icon: '🤖', title: t('landing.f2title'), body: t('landing.f2body') },
    { icon: '🇲🇰', title: t('landing.f3title'), body: t('landing.f3body') },
  ];

  return (
    <Box sx={{ minHeight: '100vh', px: { xs: 2, md: 8 }, py: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: { xs: 4, md: 8 } }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.2 }}>
          <Box sx={{ width: 40, height: 40, borderRadius: 2.4, background: 'linear-gradient(135deg,#7c6cf0,#31d0aa)', display: 'grid', placeItems: 'center', fontSize: 22 }}>🪞</Box>
          <Typography variant="h6" sx={{ fontWeight: 800 }}>{t('app.name')}</Typography>
        </Box>
        <Box>
          <IconButton onClick={(e) => setAnchor(e.currentTarget)}><TranslateRoundedIcon /></IconButton>
          <Menu anchorEl={anchor} open={!!anchor} onClose={() => setAnchor(null)}>
            <MenuItem onClick={() => setLang('en')}>🇬🇧 English</MenuItem>
            <MenuItem onClick={() => setLang('mk')}>🇲🇰 Македонски</MenuItem>
          </Menu>
          <Button component={RouterLink} to="/login" sx={{ ml: 1 }}>{t('landing.login')}</Button>
        </Box>
      </Box>

      <Box sx={{ display: 'grid', gap: 4, gridTemplateColumns: { xs: '1fr', md: '1.1fr 0.9fr' }, alignItems: 'center' }}>
        <Box component={motion.div} initial={{ opacity: 0, x: -24 }} animate={{ opacity: 1, x: 0 }} transition={{ duration: 0.5 }}>
          <Typography variant="h2" sx={{ fontSize: { xs: 40, md: 60 }, lineHeight: 1.05, mb: 2 }}>
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
          <AvatarView state="EXCELLENT" attributes={{ smile: 0.95, glow: true }} size={300} />
        </Box>
      </Box>

      <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: 'repeat(3,1fr)' }, mt: { xs: 5, md: 9 } }}>
        {features.map((f, i) => (
          <GlassCard key={i} delay={0.1 * i} sx={{ p: 3 }}>
            <Typography sx={{ fontSize: 34, mb: 1 }}>{f.icon}</Typography>
            <Typography variant="h6" sx={{ mb: 0.5 }}>{f.title}</Typography>
            <Typography variant="body2" color="text.secondary">{f.body}</Typography>
          </GlassCard>
        ))}
      </Box>
    </Box>
  );
}
