import { Box, CircularProgress, Typography, Button } from '@mui/material';
import { useTranslation } from 'react-i18next';

export function Loading({ minHeight = 240 }) {
  return (
    <Box sx={{ display: 'grid', placeItems: 'center', minHeight }}>
      <CircularProgress />
    </Box>
  );
}

export function ErrorState({ error, onRetry }) {
  const { t } = useTranslation();
  const status = error?.response?.status;
  return (
    <Box sx={{ display: 'grid', placeItems: 'center', gap: 1.5, minHeight: 220, textAlign: 'center' }}>
      <Typography color="text.secondary">
        {status ? `${t('auth.failed')} (${status})` : t('auth.failed')}
      </Typography>
      {onRetry && <Button variant="outlined" onClick={onRetry}>{t('common.retry')}</Button>}
    </Box>
  );
}

/** Wraps async content: shows spinner / error / children. */
export function AsyncBlock({ loading, error, onRetry, children }) {
  if (loading) return <Loading />;
  if (error) return <ErrorState error={error} onRetry={onRetry} />;
  return children;
}
