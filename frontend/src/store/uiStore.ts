import { create } from 'zustand'

interface UIState {
  isDarkMode: boolean
  toggleDarkMode: () => void
}

const hasWindow = typeof window !== 'undefined'
let initialDark = false
if (hasWindow) {
  try {
    const stored = localStorage.getItem('isDarkMode')
    if (stored !== null) {
      // Use stored preference if available (manual preference)
      initialDark = stored === 'true'
      document.body.classList.toggle('dark-mode', initialDark)
      document.body.setAttribute('data-theme', initialDark ? 'dark' : 'light')
    } else {
      // If no stored preference, use system preference
      // Don't set dark-mode class or data-theme, let CSS media query handle it
      initialDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      // Remove any existing classes/attributes to allow system preference
      document.body.classList.remove('dark-mode')
      document.body.removeAttribute('data-theme')
    }
  } catch (e) {
    // ignore localStorage errors, fallback to system preference
    try {
      initialDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      document.body.classList.remove('dark-mode')
      document.body.removeAttribute('data-theme')
    } catch (e2) {
      initialDark = false
      document.body.classList.remove('dark-mode')
      document.body.removeAttribute('data-theme')
    }
  }
}

export const useUIStore = create<UIState>((set) => ({
  isDarkMode: initialDark,
  toggleDarkMode: () => set((state) => {
    const next = !state.isDarkMode
    if (hasWindow) {
      try {
        localStorage.setItem('isDarkMode', String(next))
        document.body.classList.toggle('dark-mode', next)
        // Mark as manual preference
        document.body.setAttribute('data-theme', next ? 'dark' : 'light')
      } catch (e) {
        // ignore storage errors
      }
    }
    return { isDarkMode: next }
  }),
}))