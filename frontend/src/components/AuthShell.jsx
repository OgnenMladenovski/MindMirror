import { Box, Typography, IconButton, Menu, MenuItem } from '@mui/material';
import TranslateRoundedIcon from '@mui/icons-material/TranslateRounded';
import { motion } from 'framer-motion';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import GlassCard from './GlassCard';
import AvatarView from './AvatarView';

export default function AuthShell({ children, maxWidth = 440, avatarSize = 92 }) {
  const { i18n } = useTranslation();
  const [anchor, setAnchor] = useState(null);
  const setLang = (l) => { i18n.changeLanguage(l); localStorage.setItem('mm_lang', l); setAnchor(null); };

  return (
    <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center', p: { xs: 2, sm: 3 }, position: 'relative' }}>
      <Box sx={{ position: 'absolute', top: 16, right: 16 }}>
        <IconButton onClick={(e) => setAnchor(e.currentTarget)}><TranslateRoundedIcon /></IconButton>
        <Menu anchorEl={anchor} open={!!anchor} onClose={() => setAnchor(null)}>
          <MenuItem onClick={() => setLang('en')}>🇬🇧 English</MenuItem>
          <MenuItem onClick={() => setLang('mk')}>🇲🇰 Македонски</MenuItem>
        </Menu>
      </Box>
      {/* height: auto so the card hugs its content and stays centered (GlassCard defaults to 100%). */}
      <GlassCard sx={{ width: '100%', maxWidth, height: 'auto', p: { xs: 3, sm: 4 } }}>
        <Box sx={{ display: 'grid', placeItems: 'center', mb: 0.5 }}>
          <motion.div initial={{ scale: 0.8, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} transition={{ duration: 0.4 }}>
            <AvatarView state="HAPPY" attributes={{ smile: 0.85, glow: true }} size={avatarSize} />
          </motion.div>
        </Box>
        {children}
      </GlassCard>
    </Box>
  );
}
