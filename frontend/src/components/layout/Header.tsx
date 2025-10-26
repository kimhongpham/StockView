import React from 'react';
import { useAuthStore } from '../../store/authStore';
import { ThemeToggle } from '../common/ThemeToggle';

interface HeaderProps {
  onLoginClick: () => void;
  onMenuToggle: () => void;
  onLogout?: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onLoginClick, onMenuToggle, onLogout }) => {
  const { user, isLoggedIn, logout } = useAuthStore();

  const handleAuthClick = () => {
    if (isLoggedIn) {
      if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
        logout();
        // Gọi callback logout nếu có
        if (onLogout) {
          onLogout();
        }
      }
    } else {
      onLoginClick();
    }
  };

  return (
    <header className="header">
      <button className="menu-toggle" onClick={onMenuToggle}>☰</button>
      <div className="search-bar">
        <i>🔍</i>
        <input type="text" placeholder="Tìm kiếm cổ phiếu..." />
      </div>
      <div className="header-controls">
        <ThemeToggle />
        <div className="user-profile">
          <div className="user-avatar" id="userAvatar">
            {user?.avatar || '?'}
          </div>
          <div className="user-info" id="userInfo">
            <div className="user-name">{user?.name || 'Khách'}</div>
            <div className="user-role">{user?.role || 'Vui lòng đăng nhập'}</div>
          </div>
          <button
            className={`btn ${isLoggedIn ? 'btn-secondary' : 'btn-outline'}`}
            id="loginBtn"
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