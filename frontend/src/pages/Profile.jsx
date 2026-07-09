import { useState } from 'react';
import { Box, Typography, TextField, Button, MenuItem, Alert, Stack, Avatar } from '@mui/material';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/endpoints';

export default function Profile() {
  const { t } = useTranslation();
  const { user, setUser } = useAuth();
  const [form, setForm] = useState({
    fullName: user?.fullName || '', gender: user?.gender || 'UNSPECIFIED',
    dateOfBirth: user?.dateOfBirth || '',
  });
  const [saved, setSaved] = useState(false);
  const [busy, setBusy] = useState(false);
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const save = async () => {
    setBusy(true); setSaved(false);
    try {
      const updated = await authApi.updateMe({ ...form, dateOfBirth: form.dateOfBirth || null });
      setUser(updated); setSaved(true);
    } finally { setBusy(false); }
  };

  return (
    <Box>
      <PageHeader title={t('profile.title')} />
      <Box sx={{ display: 'grid', gap: 2.5, gridTemplateColumns: { xs: '1fr', md: '1fr 2fr' } }}>
        <GlassCard sx={{ textAlign: 'center', py: 4 }}>
          <Avatar sx={{ width: 96, height: 96, mx: 'auto', fontSize: 40, background: 'linear-gradient(135deg,#f6a24b,#f9b4c6)' }}>
            {(user?.fullName || user?.username || '?').charAt(0).toUpperCase()}
          </Avatar>
          <Typography variant="h6" sx={{ mt: 2 }}>{user?.fullName || user?.username}</Typography>
          <Typography variant="body2" color="text.secondary">@{user?.username}</Typography>
          <Typography variant="body2" color="text.secondary">{user?.email}</Typography>
          <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
            {t('profile.ageGroup')}: {user?.ageGroup} · {user?.role}
          </Typography>
        </GlassCard>

        <GlassCard>
          <Stack spacing={2.5}>
            {saved && <Alert severity="success">{t('settings.saved')}</Alert>}
            <TextField label={t('profile.fullName')} value={form.fullName} onChange={set('fullName')} fullWidth />
            <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: '1fr 1fr' }}>
              <TextField label={t('auth.dob')} type="date" value={form.dateOfBirth || ''} onChange={set('dateOfBirth')}
                InputLabelProps={{ shrink: true }} />
              <TextField label={t('auth.gender')} select value={form.gender} onChange={set('gender')}>
                {['MALE', 'FEMALE', 'OTHER', 'UNSPECIFIED'].map((g) => <MenuItem key={g} value={g}>{t(`gender.${g}`)}</MenuItem>)}
              </TextField>
            </Box>
            <Box><Button variant="contained" onClick={save} disabled={busy}>{t('common.save')}</Button></Box>
          </Stack>
        </GlassCard>
      </Box>
    </Box>
  );
}
