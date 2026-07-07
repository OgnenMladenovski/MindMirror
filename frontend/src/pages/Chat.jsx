import { useState, useRef, useEffect } from 'react';
import { Box, Typography, TextField, IconButton, Chip, Stack, Avatar } from '@mui/material';
import SendRoundedIcon from '@mui/icons-material/SendRounded';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import PageHeader from '../components/PageHeader';
import GlassCard from '../components/GlassCard';
import { chatApi } from '../api/endpoints';

export default function Chat() {
  const { t, i18n } = useTranslation();
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [busy, setBusy] = useState(false);
  const endRef = useRef(null);

  useEffect(() => { endRef.current?.scrollIntoView({ behavior: 'smooth' }); }, [messages, busy]);

  const send = async (text) => {
    const message = (text ?? input).trim();
    if (!message || busy) return;
    setInput('');
    setMessages((m) => [...m, { role: 'user', text: message }]);
    setBusy(true);
    try {
      const res = await chatApi.send(message, i18n.language);
      setMessages((m) => [...m, { role: 'bot', text: res.reply }]);
    } catch {
      setMessages((m) => [...m, { role: 'bot', text: t('auth.failed') }]);
    } finally {
      setBusy(false);
    }
  };

  const suggestions = [t('chat.s1'), t('chat.s2'), t('chat.s3')];

  return (
    <Box>
      <PageHeader title={t('chat.title')} />
      <GlassCard sx={{ display: 'flex', flexDirection: 'column', height: '68vh', p: 0 }}>
        <Box sx={{ flex: 1, overflowY: 'auto', p: 2.5, display: 'flex', flexDirection: 'column', gap: 1.5 }}>
          {messages.length === 0 && (
            <Box sx={{ m: 'auto', textAlign: 'center' }}>
              <Typography sx={{ fontSize: 44 }}>💬</Typography>
              <Typography color="text.secondary" sx={{ mb: 2 }}>{t('chat.empty')}</Typography>
              <Stack direction="row" spacing={1} justifyContent="center" flexWrap="wrap" useFlexGap>
                {suggestions.map((s) => <Chip key={s} label={s} onClick={() => send(s)} variant="outlined" />)}
              </Stack>
            </Box>
          )}
          {messages.map((m, i) => (
            <Box key={i} component={motion.div} initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }}
              sx={{ display: 'flex', gap: 1, justifyContent: m.role === 'user' ? 'flex-end' : 'flex-start' }}>
              {m.role === 'bot' && <Avatar sx={{ width: 30, height: 30, background: 'linear-gradient(135deg,#7c6cf0,#31d0aa)' }}>🪞</Avatar>}
              <Box sx={{ maxWidth: '75%', px: 2, py: 1.2, borderRadius: 3,
                bgcolor: m.role === 'user' ? 'primary.main' : 'background.paper',
                color: m.role === 'user' ? '#fff' : 'text.primary',
                border: (theme) => m.role === 'bot' ? `1px solid ${theme.palette.divider}` : 'none' }}>
                <Typography variant="body2">{m.text}</Typography>
              </Box>
            </Box>
          ))}
          {busy && <Typography variant="caption" color="text.secondary">…</Typography>}
          <div ref={endRef} />
        </Box>
        <Box sx={{ p: 2, borderTop: (theme) => `1px solid ${theme.palette.divider}`, display: 'flex', gap: 1 }}>
          <TextField fullWidth size="small" placeholder={t('chat.placeholder')} value={input}
            onChange={(e) => setInput(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && send()} />
          <IconButton color="primary" onClick={() => send()} disabled={busy}><SendRoundedIcon /></IconButton>
        </Box>
      </GlassCard>
    </Box>
  );
}
