import { useState } from 'react';
import { Box, Typography, ToggleButton, ToggleButtonGroup, Snackbar } from '@mui/material';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/endpoints';

export default function Settings() {
  const { t, i18n } = useTranslation();
  const { user, setUser } = useAuth();
  const [snack, setSnack] = useState(false);

  const changeLang = async (lng) => {
    if (!lng) return;
    i18n.changeLanguage(lng);
    localStorage.setItem('mm_lang', lng);
    setSnack(true);
    try {
      const updated = await authApi.updateMe({ locale: lng });
      setUser(updated);
    } catch { /* non-blocking */ }
  };

  return (
    <Box>
      <PageHeader title={t('settings.title')} />
      <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' } }}>
        <GlassCard>
          <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 2 }}>{t('settings.language')}</Typography>
          <ToggleButtonGroup exclusive value={i18n.language} onChange={(_, v) => changeLang(v)} color="primary">
            <ToggleButton value="en">🇬🇧 {t('settings.english')}</ToggleButton>
            <ToggleButton value="mk">🇲🇰 {t('settings.macedonian')}</ToggleButton>
          </ToggleButtonGroup>
        </GlassCard>
      </Box>
      <Snackbar open={snack} autoHideDuration={2000} onClose={() => setSnack(false)} message={t('settings.saved')} />
    </Box>
  );
}
