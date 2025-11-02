import React, { useState, useEffect, useRef } from 'react'
import { Sidebar } from './Sidebar'
import { Header } from './Header'
import { useAuthStore } from '../../store/authStore'
import { useUIStore } from '../../store/uiStore'

interface MainLayoutProps {
  children: React.ReactNode
  activePage: string
  onPageChange: (page: string) => void
  onLoginClick: () => void
  onLogout?: () => void
}

export const MainLayout: React.FC<MainLayoutProps> = ({
  children,
  activePage,
  onPageChange,
  onLoginClick,
  onLogout,
}) => {
  const [sidebarActive, setSidebarActive] = useState(false)
  const { isLoggedIn } = useAuthStore()
  const { isDarkMode } = useUIStore()
  const sidebarRef = useRef<HTMLDivElement>(null)

  // Close sidebar when clicking outside (mobile)
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        window.innerWidth <= 1024 &&
        sidebarRef.current &&
        !sidebarRef.current.contains(event.target as Node) &&
        sidebarActive
      ) {
        setSidebarActive(false)
      }
    }

    document.addEventListener('click', handleClickOutside)
    return () => document.removeEventListener('click', handleClickOutside)
  }, [sidebarActive])

  // Sync dark mode with body
  useEffect(() => {
    document.body.classList.toggle('dark-mode', isDarkMode)
  }, [isDarkMode])

  const handleMenuToggle = () => setSidebarActive(!sidebarActive)

  const handlePageChange = (page: string) => {
    onPageChange(page)
    if (window.innerWidth <= 1024) setSidebarActive(false)
  }

  return (
    <div className={`main-layout ${isDarkMode ? 'dark' : ''}`}>
      <Sidebar
        ref={sidebarRef}
        activePage={activePage}
        onPageChange={handlePageChange}
        isLoggedIn={isLoggedIn}
        isActive={sidebarActive}
      />
      <Header
        onLoginClick={onLoginClick}
        onMenuToggle={handleMenuToggle}
        onLogout={onLogout}
      />
      <main className="main-content">{children}</main>
    </div>
  )
}

export default MainLayout
