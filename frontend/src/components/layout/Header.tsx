import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
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
  const [searchValue, setSearchValue] = useState('')
  const navigate = useNavigate()

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

  const handleSearch = () => {
    const symbol = searchValue.trim().toUpperCase()
    if (symbol) {
      navigate(`/stock/${symbol}`)
      setSearchValue('')
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

        <div className="header-logo flex items-center gap-2">
          <img src="/logo.png" alt="StockView Logo" className="h-8 w-8 object-contain" />
          <span>StockView</span>
        </div>
      </div>

      {/* Center section - Search bar */}
      <div className="header-center">
        <div className="search-bar flex items-center">
          <Search className="w-4 h-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search..."
            className="search-input"
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') handleSearch()
            }}
          />
        </div>
      </div>

      {/* Right section - Controls */}
      <div className="header-controls flex items-center gap-4">
        <button className="icon-btn notification-btn relative" aria-label="Notifications">
          <Bell className="w-5 h-5" />
          <span className="notification-badge">3</span>
        </button>

        <ThemeToggle />

        <div className="user-profile flex items-center gap-2">
          <div className="user-avatar w-8 h-8">
            {user?.avatar ? (
              <img src={user.avatar} alt="Avatar" className="w-full h-full rounded-full object-cover" />
            ) : (
              <UserCircle2 className="w-8 h-8" />
            )}
          </div>

          <div className="user-info flex flex-col text-sm">
            <div className="user-name">{user?.username || 'Guest'}</div>
            <div className="user-role">{user?.role || 'Sign in'}</div>
          </div>

          <button
            className={`auth-btn ${isLoggedIn ? 'btn-logout' : 'btn-login'} flex items-center gap-1`}
            onClick={handleAuthClick}
          >
            {isLoggedIn ? (
              <>
                <LogOut className="w-4 h-4" />
                <span className="auth-text">Sign out</span>
              </>
            ) : (
              <>
                <LogIn className="w-4 h-4" />
                <span className="auth-text">Sign in</span>
              </>
            )}
          </button>
        </div>
      </div>
    </header>
  )
}
