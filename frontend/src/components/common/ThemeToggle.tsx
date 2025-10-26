import React from 'react'
import { useUIStore } from '../../store/uiStore'

export const ThemeToggle: React.FC = () => {
  const { isDarkMode, toggleDarkMode } = useUIStore()

  const handleToggle = () => {
    toggleDarkMode()
    // Thêm/xóa class dark-mode trên body
    if (isDarkMode) {
      document.body.classList.remove('dark-mode')
    } else {
      document.body.classList.add('dark-mode')
    }
  }

  return (
    <button 
      className="theme-toggle" 
      onClick={handleToggle}
      aria-label={isDarkMode ? 'Switch to light mode' : 'Switch to dark mode'}
    >
      {isDarkMode ? '☀️' : '🌙'}
    </button>
  )
}