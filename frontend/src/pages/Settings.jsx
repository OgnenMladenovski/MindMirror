import { useState } from 'react';
import { Box, Typography, ToggleButton, ToggleButtonGroup, Switch, Stack, Snackbar } from '@mui/material';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import { useColorMode } from '../context/ColorModeContext';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/endpoints';

export default function Settings() {
  const { t, i18n } = useTranslation();
  const { mode, toggle } = useColorMode();
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

        <GlassCard>
          <Typography variant="subtitle1" sx={{ fontWeight: 700, mb: 2 }}>{t('settings.appearance')}</Typography>
          <Stack direction="row" alignItems="center" justifyContent="space-between">
            <Typography>{t('settings.darkMode')}</Typography>
            <Switch checked={mode === 'dark'} onChange={toggle} />
          </Stack>
        </GlassCard>
      </Box>
      <Snackbar open={snack} autoHideDuration={2000} onClose={() => setSnack(false)} message={t('settings.saved')} />
    </Box>
  );
}
