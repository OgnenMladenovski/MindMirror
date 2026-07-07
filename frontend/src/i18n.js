import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './locales/en.json';
import mk from './locales/mk.json';

const stored = localStorage.getItem('mm_lang') || 'en';

i18n.use(initReactI18next).init({
  resources: { en: { translation: en }, mk: { translation: mk } },
  lng: stored,
  fallbackLng: 'en',
  interpolation: { escapeValue: false },
});

export default i18n;
