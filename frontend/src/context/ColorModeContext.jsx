import { createContext, useContext, useMemo, useState } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { makeTheme } from '../theme';

const ColorModeContext = createContext({ mode: 'dark', toggle: () => {} });

export function ColorModeProvider({ children }) {
  const [mode, setMode] = useState(localStorage.getItem('mm_mode') || 'dark');

  const value = useMemo(
    () => ({
      mode,
      toggle: () =>
        setMode((m) => {
          const next = m === 'dark' ? 'light' : 'dark';
          localStorage.setItem('mm_mode', next);
          return next;
        }),
    }),
    [mode]
  );

  const theme = useMemo(() => makeTheme(mode), [mode]);

  return (
    <ColorModeContext.Provider value={value}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </ColorModeContext.Provider>
  );
}

export const useColorMode = () => useContext(ColorModeContext);
