import React from 'react'
import { useAuthStore } from '../../store/authStore'
import { ThemeToggle } from '../common/ThemeToggle'
import { Menu, Search, UserCircle2, LogOut, LogIn, Bell } from 'lucide-react'

interface HeaderProps {
  onLoginClick: () => void
  onMenuToggle: () => void
  onLogout?: () => void
}

export const Header: React.FC<HeaderProps> = ({ onLoginClick, onMenuToggle, onLogout }) => {
  const { user, isLoggedIn, logout } = useAuthStore()

  const handleAuthClick = () => {
    if (isLoggedIn) {
      if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
        logout()
        if (onLogout) onLogout()
      }
    } else {
      onLoginClick()
    }
  }

  return (
    <header className="header">
      {/* Left section - Menu toggle and Logo */}
      <div className="header-left">
        <button
          className="menu-toggle"
          onClick={(e) => {
            e.stopPropagation()
            onMenuToggle()
          }}
          aria-label="Toggle menu"
        >
          <Menu className="w-6 h-6" />
        </button>

        {/* Logo + Text */}
        <div className="header-logo flex items-center gap-2">
          <img
            src="/logo.png"
            alt="StockView Logo"
            className="h-8 w-8 object-contain"
          />
          <span className="text-xl font-semibold tracking-wide">StockView</span>
        </div>
      </div>

      {/* Center section - Search bar */}
      <div className="header-center">
        <div className="search-bar">
          <Search className="w-5 h-5 text-gray-500" />
          <input
            type="text"
            placeholder="Tìm kiếm cổ phiếu, mã chứng khoán..."
            className="search-input"
          />
        </div>
      </div>

      {/* Right section - Controls */}
      <div className="header-controls">
        <button className="icon-btn notification-btn" aria-label="Notifications">
          <Bell className="w-5 h-5" />
          <span className="notification-badge">3</span>
        </button>

        <ThemeToggle />

        <div className="user-profile">
          <div className="user-avatar">
            {user?.avatar ? (
              <img
                src={user.avatar}
                alt="Avatar"
                className="w-full h-full rounded-full object-cover"
              />
            ) : (
              <UserCircle2 className="w-8 h-8 text-primary" />
            )}
          </div>

          <div className="user-info">
            <div className="user-name">{user?.username || 'Khách'}</div>
            <div className="user-role">{user?.role || 'Vui lòng đăng nhập'}</div>
          </div>

          <button
            className={`auth-btn ${isLoggedIn ? 'btn-logout' : 'btn-login'}`}
            onClick={handleAuthClick}
          >
            {isLoggedIn ? (
              <>
                <LogOut className="w-4 h-4" />
                <span className="auth-text">Đăng xuất</span>
              </>
            ) : (
              <>
                <LogIn className="w-4 h-4" />
                <span className="auth-text">Đăng nhập</span>
              </>
            )}
          </button>
        </div>
      </div>
    </header>
  )
}
