import { createTheme } from '@mui/material/styles';

// MindMirror summer theme — soft pastel yellows, oranges and light blues.
const brand = {
  primary: '#f6a24b',   // warm apricot
  secondary: '#6cc4e8', // sky blue
  accent: '#ffcf5c',    // pastel yellow
};

export const makeTheme = () =>
  createTheme({
    palette: {
      mode: 'light',
      primary: { main: brand.primary },
      secondary: { main: brand.secondary },
      success: { main: '#5ac2a0' },
      warning: { main: '#f6a24b' },
      error: { main: '#ef7a72' },
      background: { default: '#fff6e6', paper: 'rgba(255, 255, 255, 0.72)' },
      text: { primary: '#4a3b28', secondary: '#8a7757' },
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
              'radial-gradient(1200px 600px at 8% -10%, rgba(255,207,92,0.45), transparent 55%), ' +
              'radial-gradient(1000px 520px at 100% 0%, rgba(246,162,75,0.32), transparent 52%), ' +
              'radial-gradient(900px 500px at 50% 110%, rgba(108,196,232,0.28), transparent 55%), ' +
              'linear-gradient(160deg, #fff6e6, #fff0d4 55%, #eaf6fc)',
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            backgroundImage: 'none',
            backdropFilter: 'blur(16px)',
            border: '1px solid rgba(255,255,255,0.65)',
          },
        },
      },
      MuiCard: {
        styleOverrides: {
          root: {
            backdropFilter: 'blur(16px)',
            boxShadow: '0 12px 40px rgba(214,150,70,0.16)',
          },
        },
      },
      MuiButton: {
        styleOverrides: {
          root: { borderRadius: 14, paddingInline: 18 },
          containedPrimary: {
            backgroundImage: `linear-gradient(135deg, ${brand.primary}, #ffcf5c)`,
            color: '#4a3b28',
          },
        },
      },
    },
  });

export const brandColors = brand;

// Consistent palette for charts / avatar states — summer pastels.
export const chartColors = ['#f6a24b', '#ffcf5c', '#6cc4e8', '#ef9a5c', '#5ac2a0', '#f9b4c6'];

export const avatarStateColor = {
  EXCELLENT: '#5ac2a0',
  HAPPY: '#7fd0ae',
  NEUTRAL: '#ffcf5c',
  STRESSED: '#f6a24b',
  BURNED_OUT: '#ef7a72',
  EXHAUSTED: '#e08bc0',
};

export const heatColor = {
  green: '#5ac2a0',
  yellow: '#ffcf5c',
  orange: '#f6a24b',
  red: '#ef7a72',
};
