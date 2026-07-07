import { useTranslation } from 'react-i18next';

/**
 * Returns a picker for bilingual API objects. The backend sends `*_en` / `*_mk`
 * (serialised as camelCase `textEn`/`textMk`, `titleEn`/`titleMk`, etc.); this
 * chooses the field matching the current UI language.
 */
export function useLocalized() {
  const { i18n } = useTranslation();
  const mk = i18n.language === 'mk';

  return (obj, base) => {
    if (!obj) return '';
    const suffix = mk ? 'Mk' : 'En';
    return obj[base + suffix] ?? obj[base + 'En'] ?? '';
  };
}
