import { useI18n } from "vue-i18n"

export const useLocaleHooks = () => {
  const { t } = useI18n()

  return {
    t
  }
}
