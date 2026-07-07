import { useState } from 'react';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { Box, Typography, TextField, Button, Alert, Link, Chip } from '@mui/material';
import { useTranslation } from 'react-i18next';
import AuthShell from '../components/AuthShell';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { t } = useTranslation();
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ usernameOrEmail: 'demo', password: 'demo1234' });
  const [error, setError] = useState(false);
  const [busy, setBusy] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true); setError(false);
    try {
      await login(form.usernameOrEmail, form.password);
      navigate('/dashboard');
    } catch {
      setError(true);
    } finally {
      setBusy(false);
    }
  };

  return (
    <AuthShell>
      <Typography variant="h5" align="center">{t('auth.welcomeBack')}</Typography>
      <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
        {t('auth.loginSub')}
      </Typography>
      <Box component="form" onSubmit={submit} sx={{ display: 'grid', gap: 2 }}>
        {error && <Alert severity="error">{t('auth.invalid')}</Alert>}
        <TextField label={t('auth.usernameOrEmail')} value={form.usernameOrEmail}
          onChange={(e) => setForm({ ...form, usernameOrEmail: e.target.value })} fullWidth autoFocus />
        <TextField label={t('auth.password')} type="password" value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })} fullWidth />
        <Button type="submit" variant="contained" size="large" disabled={busy}>
          {t('auth.loginCta')}
        </Button>
        <Box sx={{ display: 'flex', justifyContent: 'center' }}>
          <Chip size="small" variant="outlined" label={t('auth.demoHint')} />
        </Box>
        <Typography variant="body2" align="center" color="text.secondary">
          {t('auth.noAccount')}{' '}
          <Link component={RouterLink} to="/register" fontWeight={700}>{t('auth.registerHere')}</Link>
        </Typography>
      </Box>
    </AuthShell>
  );
}
