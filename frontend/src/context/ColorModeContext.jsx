import { createContext, useContext, useMemo } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { makeTheme } from '../theme';

const ColorModeContext = createContext({ mode: 'light' });

export function ColorModeProvider({ children }) {
  const theme = useMemo(() => makeTheme(), []);

  return (
    <ColorModeContext.Provider value={{ mode: 'light' }}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </ColorModeContext.Provider>
  );
}

export const useColorMode = () => useContext(ColorModeContext);
