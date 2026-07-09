import { useState } from 'react';
import { Outlet, useNavigate, useLocation, Link } from 'react-router-dom';
import {
  AppBar, Toolbar, Box, Drawer, List, ListItemButton, ListItemIcon, ListItemText,
  IconButton, Typography, Avatar, Menu, MenuItem, Divider, Tooltip, useMediaQuery,
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import DashboardRoundedIcon from '@mui/icons-material/DashboardRounded';
import AddCircleRoundedIcon from '@mui/icons-material/AddCircleRounded';
import InsightsRoundedIcon from '@mui/icons-material/InsightsRounded';
import FaceRetouchingNaturalRoundedIcon from '@mui/icons-material/FaceRetouchingNaturalRounded';
import FlagRoundedIcon from '@mui/icons-material/FlagRounded';
import EmojiEventsRoundedIcon from '@mui/icons-material/EmojiEventsRounded';
import PublicRoundedIcon from '@mui/icons-material/PublicRounded';
import PersonRoundedIcon from '@mui/icons-material/PersonRounded';
import SettingsRoundedIcon from '@mui/icons-material/SettingsRounded';
import AdminPanelSettingsRoundedIcon from '@mui/icons-material/AdminPanelSettingsRounded';
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded';
import TranslateRoundedIcon from '@mui/icons-material/TranslateRounded';
import MenuRoundedIcon from '@mui/icons-material/MenuRounded';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';

const WIDTH = 258;

const NAV = [
  { to: '/dashboard', key: 'dashboard', icon: <DashboardRoundedIcon /> },
  { to: '/checkin', key: 'checkin', icon: <AddCircleRoundedIcon /> },
  { to: '/analytics', key: 'analytics', icon: <InsightsRoundedIcon /> },
  { to: '/avatar', key: 'avatar', icon: <FaceRetouchingNaturalRoundedIcon /> },
  { to: '/challenges', key: 'challenges', icon: <FlagRoundedIcon /> },
  { to: '/achievements', key: 'achievements', icon: <EmojiEventsRoundedIcon /> },
  { to: '/hbsc', key: 'hbsc', icon: <PublicRoundedIcon /> },
];

export default function Layout() {
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up('md'));
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchor, setAnchor] = useState(null);
  const [langAnchor, setLangAnchor] = useState(null);
  const { user, logout } = useAuth();
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const setLang = (lng) => { i18n.changeLanguage(lng); localStorage.setItem('mm_lang', lng); setLangAnchor(null); };

  const navItems = [...NAV];
  const footerItems = [
    { to: '/profile', key: 'profile', icon: <PersonRoundedIcon /> },
    { to: '/settings', key: 'settings', icon: <SettingsRoundedIcon /> },
    ...(user?.role === 'ADMIN' ? [{ to: '/admin', key: 'admin', icon: <AdminPanelSettingsRoundedIcon /> }] : []),
  ];

  const drawer = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column', p: 1.5 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.2, px: 1, py: 2 }}>
        <Box sx={{ width: 38, height: 38, borderRadius: 2.4, background: 'linear-gradient(135deg,#f6a24b,#6cc4e8)', display: 'grid', placeItems: 'center', fontSize: 20 }}>🪞</Box>
        <Box>
          <Typography sx={{ fontWeight: 800, lineHeight: 1 }}>{t('app.name')}</Typography>
          <Typography variant="caption" color="text.secondary">{t('app.tagline')}</Typography>
        </Box>
      </Box>
      <List sx={{ flex: 1 }}>
        {navItems.map((n) => (
          <NavRow key={n.to} n={n} active={pathname === n.to} label={t(`nav.${n.key}`)}
            onClick={() => { navigate(n.to); setMobileOpen(false); }} />
        ))}
      </List>
      <Divider sx={{ my: 1 }} />
      <List>
        {footerItems.map((n) => (
          <NavRow key={n.to} n={n} active={pathname === n.to} label={t(`nav.${n.key}`)}
            onClick={() => { navigate(n.to); setMobileOpen(false); }} />
        ))}
      </List>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <AppBar position="fixed" elevation={0} color="transparent"
        sx={{ backdropFilter: 'blur(12px)', borderBottom: `1px solid ${theme.palette.divider}`,
          width: { md: `calc(100% - ${WIDTH}px)` }, ml: { md: `${WIDTH}px` } }}>
        <Toolbar sx={{ gap: 1 }}>
          {!isDesktop && (
            <IconButton onClick={() => setMobileOpen(true)} edge="start"><MenuRoundedIcon /></IconButton>
          )}
          <Box sx={{ flex: 1 }} />
          <Tooltip title="Language">
            <IconButton onClick={(e) => setLangAnchor(e.currentTarget)}><TranslateRoundedIcon /></IconButton>
          </Tooltip>
          <Menu anchorEl={langAnchor} open={!!langAnchor} onClose={() => setLangAnchor(null)}>
            <MenuItem selected={i18n.language === 'en'} onClick={() => setLang('en')}>🇬🇧 English</MenuItem>
            <MenuItem selected={i18n.language === 'mk'} onClick={() => setLang('mk')}>🇲🇰 Македонски</MenuItem>
          </Menu>
          <IconButton onClick={(e) => setAnchor(e.currentTarget)}>
            <Avatar sx={{ width: 34, height: 34, background: 'linear-gradient(135deg,#f6a24b,#ffcf5c)', fontSize: 15 }}>
              {(user?.fullName || user?.username || '?').charAt(0).toUpperCase()}
            </Avatar>
          </IconButton>
          <Menu anchorEl={anchor} open={!!anchor} onClose={() => setAnchor(null)}>
            <MenuItem disabled>{user?.username}</MenuItem>
            <Divider />
            <MenuItem component={Link} to="/profile" onClick={() => setAnchor(null)}>{t('nav.profile')}</MenuItem>
            <MenuItem onClick={() => { logout(); navigate('/login'); }}>
              <ListItemIcon><LogoutRoundedIcon fontSize="small" /></ListItemIcon>{t('nav.logout')}
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      <Box component="nav" sx={{ width: { md: WIDTH }, flexShrink: { md: 0 } }}>
        {isDesktop ? (
          <Drawer variant="permanent" open PaperProps={{ sx: { width: WIDTH, borderRight: `1px solid ${theme.palette.divider}` } }}>
            {drawer}
          </Drawer>
        ) : (
          <Drawer variant="temporary" open={mobileOpen} onClose={() => setMobileOpen(false)}
            ModalProps={{ keepMounted: true }} PaperProps={{ sx: { width: WIDTH } }}>
            {drawer}
          </Drawer>
        )}
      </Box>

      <Box component="main" sx={{ flexGrow: 1, width: { md: `calc(100% - ${WIDTH}px)` }, p: { xs: 2, sm: 3, md: 4 } }}>
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
}

function NavRow({ n, active, label, onClick }) {
  return (
    <ListItemButton selected={active} onClick={onClick}
      sx={{ borderRadius: 3, mb: 0.5,
        '&.Mui-selected': { background: 'linear-gradient(135deg, rgba(246,162,75,0.22), rgba(108,196,232,0.18))' } }}>
      <ListItemIcon sx={{ minWidth: 40, color: active ? 'primary.main' : 'text.secondary' }}>{n.icon}</ListItemIcon>
      <ListItemText primary={label} primaryTypographyProps={{ fontWeight: active ? 700 : 500 }} />
    </ListItemButton>
  );
}
