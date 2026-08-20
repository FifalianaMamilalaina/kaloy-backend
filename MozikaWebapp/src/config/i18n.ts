/* Import lang Files */
import english from '@/lang/english'
import french from '@/lang/french'

import { createI18n } from 'vue-i18n'
const i18n = createI18n({
  legacy: false,
  locale: 'en',
  fallbackLocale: 'en',
  messages: {
    en: english.messages,
    fr: french.messages,
  },
  datetimeFormats: {
    en: english.dateTimeFormats,
    fr: french.dateTimeFormats,
  },
  numberFormats: {
    en: english.numberFormats,
    fr: french.numberFormats,
  },
})
export default i18n
