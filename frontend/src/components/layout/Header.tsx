import React from 'react'
import { useAuthStore } from '../../store/authStore'
import { ThemeToggle } from '../common/ThemeToggle'
import { Menu, Search, UserCircle2, LogOut, LogIn } from 'lucide-react'

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
      {/* Menu toggle (mobile) */}
      <button className="menu-toggle" onClick={onMenuToggle}>
        <Menu className="w-6 h-6" />
      </button>

      {/* Search bar */}
      <div className="search-bar">
        <Search className="w-5 h-5 text-gray-500" />
        <input type="text" placeholder="Tìm kiếm cổ phiếu..." />
      </div>

      {/* Controls */}
      <div className="header-controls">
        <ThemeToggle />

        <div className="user-profile">
          <div className="user-avatar">
            {user?.avatar ? (
              <img src={user.avatar} alt="Avatar" className="w-full h-full rounded-full object-cover" />
            ) : (
              <UserCircle2 className="w-8 h-8 text-primary" />
            )}
          </div>

          <div className="user-info">
            <div className="user-name">{user?.name || 'Khách'}</div>
            <div className="user-role">{user?.role || 'Vui lòng đăng nhập'}</div>
          </div>

          <button
            className={`auth-btn ${isLoggedIn ? 'btn-secondary' : 'btn-outline'}`}
            onClick={handleAuthClick}
          >
            {isLoggedIn ? (
              <>
                <LogOut className="w-4 h-4 mr-1" /> Đăng xuất
              </>
            ) : (
              <>
                <LogIn className="w-4 h-4 mr-1" /> Đăng nhập
              </>
            )}
          </button>
        </div>
      </div>
    </header>
  )
}
