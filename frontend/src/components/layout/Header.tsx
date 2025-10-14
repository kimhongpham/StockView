import React from 'react';
import { useAuthStore } from '../../store/authStore';
import { useUIStore } from '../../store/uiStore';
import { ThemeToggle } from '../common/ThemeToggle';

interface HeaderProps {
  onLoginClick: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onLoginClick }) => {
  const { user, isLoggedIn, logout } = useAuthStore();
  const { toggleSidebar } = useUIStore();

  const handleAuthClick = () => {
    if (isLoggedIn) {
      if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
        logout();
      }
    } else {
      onLoginClick();
    }
  };

  return (
    <header className="header">
      <button className="menu-toggle" onClick={toggleSidebar}>☰</button>
      <div className="search-bar">
        <i>🔍</i>
        <input type="text" placeholder="Tìm kiếm cổ phiếu..." />
      </div>
      <div className="header-controls">
        <ThemeToggle />
        <div className="user-profile">
          <div className="user-avatar">{user?.avatar || '?'}</div>
          <div className="user-info">
            <div className="user-name">{user?.name || 'Khách'}</div>
            <div className="user-role">{user?.role || 'Vui lòng đăng nhập'}</div>
          </div>
          <button
            className={`btn ${isLoggedIn ? 'btn-secondary' : 'btn-outline'}`}
            onClick={handleAuthClick}
            style={{ marginLeft: '10px', padding: '5px 15px' }}
          >
            {isLoggedIn ? 'Đăng xuất' : 'Đăng nhập'}
          </button>
        </div>
      </div>
    </header>
  );
};