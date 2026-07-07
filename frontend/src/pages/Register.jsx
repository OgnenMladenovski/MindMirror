import { useState } from 'react';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { Box, Typography, TextField, Button, Alert, Link, MenuItem } from '@mui/material';
import { useTranslation } from 'react-i18next';
import AuthShell from '../components/AuthShell';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const { t, i18n } = useTranslation();
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: '', email: '', password: '', fullName: '', dateOfBirth: '', gender: 'UNSPECIFIED',
  });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true); setError('');
    try {
      await register({ ...form, dateOfBirth: form.dateOfBirth || null, locale: i18n.language });
      navigate('/dashboard');
    } catch (err) {
      setError(err?.response?.data?.message || t('auth.failed'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <AuthShell>
      <Typography variant="h5" align="center">{t('auth.createAccount')}</Typography>
      <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
        {t('auth.registerSub')}
      </Typography>
      <Box component="form" onSubmit={submit} sx={{ display: 'grid', gap: 2 }}>
        {error && <Alert severity="error">{error}</Alert>}
        <TextField label={t('auth.fullName')} value={form.fullName} onChange={set('fullName')} fullWidth />
        <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: '1fr 1fr' }}>
          <TextField label={t('auth.username')} value={form.username} onChange={set('username')} required />
          <TextField label={t('auth.email')} type="email" value={form.email} onChange={set('email')} required />
        </Box>
        <TextField label={t('auth.password')} type="password" value={form.password} onChange={set('password')} required
          helperText="min. 8" />
        <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: '1fr 1fr' }}>
          <TextField label={t('auth.dob')} type="date" value={form.dateOfBirth} onChange={set('dateOfBirth')}
            InputLabelProps={{ shrink: true }} />
          <TextField label={t('auth.gender')} select value={form.gender} onChange={set('gender')}>
            {['MALE', 'FEMALE', 'OTHER', 'UNSPECIFIED'].map((g) => (
              <MenuItem key={g} value={g}>{t(`gender.${g}`)}</MenuItem>
            ))}
          </TextField>
        </Box>
        <Button type="submit" variant="contained" size="large" disabled={busy}>{t('auth.registerCta')}</Button>
        <Typography variant="body2" align="center" color="text.secondary">
          {t('auth.haveAccount')}{' '}
          <Link component={RouterLink} to="/login" fontWeight={700}>{t('auth.loginHere')}</Link>
        </Typography>
      </Box>
    </AuthShell>
  );
}
