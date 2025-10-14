import React from 'react';
import { useUIStore } from '../../store/uiStore';

export const ThemeToggle: React.FC = () => {
  const { darkMode, toggleDarkMode } = useUIStore();

  return (
    <button className="theme-toggle" id="themeToggle" onClick={toggleDarkMode}>
      {darkMode ? '☀️' : '🌙'}
    </button>
  );
};