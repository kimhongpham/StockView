import React from 'react'
import { useUIStore } from '../../store/uiStore'
import { Sun, Moon } from 'lucide-react'

export const ThemeToggle: React.FC = () => {
  const { isDarkMode, toggleDarkMode } = useUIStore()

  const handleToggle = () => {
    toggleDarkMode()
  }

  return (
    <button
      onClick={handleToggle}
      className="
        flex items-center justify-center gap-2 p-2 rounded-full
        border border-gray-300 dark:border-gray-700
        hover:bg-gray-100 dark:hover:bg-gray-800
        transition-colors shadow-sm
      "
      aria-label={isDarkMode ? 'Switch to light mode' : 'Switch to dark mode'}
    >
      {isDarkMode ? (
        <>
          <Sun className="w-5 h-5 text-yellow-400" />
          <span className="text-sm font-medium hidden sm:inline">Light</span>
        </>
      ) : (
        <>
          <Moon className="w-5 h-5 text-blue-400" />
          <span className="text-sm font-medium hidden sm:inline">Dark</span>
        </>
      )}
    </button>
  )
}
