import { create } from 'zustand'

interface UIState {
  isDarkMode: boolean
  toggleDarkMode: () => void
}

const hasWindow = typeof window !== 'undefined'
let initialDark = false
if (hasWindow) {
  try {
    initialDark = localStorage.getItem('isDarkMode') === 'true'
    // Ensure body has the correct class on load
    document.body.classList.toggle('dark-mode', initialDark)
  } catch (e) {
    // ignore localStorage errors
    initialDark = false
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
      } catch (e) {
        // ignore storage errors
      }
    }
    return { isDarkMode: next }
  }),
}))