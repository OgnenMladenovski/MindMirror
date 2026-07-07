import { createTheme } from '@mui/material/styles';

// MindMirror glassmorphism theme — soft gradients, rounded translucent cards.
const brand = {
  primary: '#7c6cf0',   // periwinkle
  secondary: '#31d0aa', // mint
  accent: '#ff8fab',    // blossom
};

export const makeTheme = (mode) =>
  createTheme({
    palette: {
      mode,
      primary: { main: brand.primary },
      secondary: { main: brand.secondary },
      success: { main: '#31d0aa' },
      warning: { main: '#f7b955' },
      error: { main: '#f0616d' },
      ...(mode === 'dark'
        ? {
            background: { default: '#0d0f1a', paper: 'rgba(26, 29, 46, 0.72)' },
            text: { primary: '#eef0fb', secondary: '#a5abc9' },
          }
        : {
            background: { default: '#eef1fb', paper: 'rgba(255, 255, 255, 0.72)' },
            text: { primary: '#1c2033', secondary: '#5a6180' },
          }),
    },
    shape: { borderRadius: 18 },
    typography: {
      fontFamily: '"Plus Jakarta Sans", system-ui, -apple-system, sans-serif',
      h1: { fontWeight: 800, letterSpacing: '-0.02em' },
      h2: { fontWeight: 800, letterSpacing: '-0.02em' },
      h3: { fontWeight: 700, letterSpacing: '-0.01em' },
      h4: { fontWeight: 700 },
      h5: { fontWeight: 700 },
      h6: { fontWeight: 700 },
      button: { fontWeight: 600, textTransform: 'none' },
    },
    components: {
      MuiCssBaseline: {
        styleOverrides: {
          body: {
            minHeight: '100vh',
            backgroundAttachment: 'fixed',
            backgroundImage:
              mode === 'dark'
                ? 'radial-gradient(1200px 600px at 10% -10%, rgba(124,108,240,0.28), transparent 55%), radial-gradient(1000px 500px at 100% 0%, rgba(49,208,170,0.20), transparent 50%), linear-gradient(160deg, #0b0d18, #10132a 60%, #0b0d18)'
                : 'radial-gradient(1200px 600px at 10% -10%, rgba(124,108,240,0.25), transparent 55%), radial-gradient(1000px 500px at 100% 0%, rgba(49,208,170,0.22), transparent 50%), linear-gradient(160deg, #eef1fb, #f6f2ff 60%, #eaf7f3)',
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            backgroundImage: 'none',
            backdropFilter: 'blur(16px)',
            border: mode === 'dark'
              ? '1px solid rgba(255,255,255,0.08)'
              : '1px solid rgba(255,255,255,0.6)',
          },
        },
      },
      MuiCard: {
        styleOverrides: {
          root: {
            backdropFilter: 'blur(16px)',
            boxShadow: mode === 'dark'
              ? '0 12px 40px rgba(0,0,0,0.45)'
              : '0 12px 40px rgba(90,97,128,0.15)',
          },
        },
      },
      MuiButton: {
        styleOverrides: {
          root: { borderRadius: 14, paddingInline: 18 },
          containedPrimary: {
            backgroundImage: `linear-gradient(135deg, ${brand.primary}, #9d7bf5)`,
          },
        },
      },
    },
  });

export const brandColors = brand;

// Consistent palette for charts / avatar states.
export const chartColors = ['#7c6cf0', '#31d0aa', '#ff8fab', '#f7b955', '#5aa9ff', '#b088ff'];

export const avatarStateColor = {
  EXCELLENT: '#31d0aa',
  HAPPY: '#5ad1a8',
  NEUTRAL: '#f7b955',
  STRESSED: '#ff9f6b',
  BURNED_OUT: '#f0616d',
  EXHAUSTED: '#b06bd8',
};

export const heatColor = {
  green: '#31d0aa',
  yellow: '#f7b955',
  orange: '#ff9f6b',
  red: '#f0616d',
};
