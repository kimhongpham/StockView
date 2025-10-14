import React from 'react';
import { useAuthStore } from '../../store/authStore';
import { useUIStore } from '../../store/uiStore';

interface SidebarProps {
  activePage: string;
  onPageChange: (page: string) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ activePage, onPageChange }) => {
  const { isLoggedIn } = useAuthStore();
  const { sidebarOpen, setSidebarOpen } = useUIStore();

  const menuItems = [
    { id: 'dashboard', icon: 'fas fa-chart-line', label: 'Bảng Điều Khiển' },
    { id: 'stock', icon: 'fas fa-chart-bar', label: 'Cổ Phiếu' },
    { id: 'favorit', icon: 'fas fa-star', label: 'Danh Mục Yêu Thích', premium: true },
    { id: 'wallet', icon: 'fas fa-wallet', label: 'Ví', premium: true },
    { id: 'profile', icon: 'fas fa-user', label: 'Hồ Sơ', premium: true },
  ];

  const handleItemClick = (pageId: string) => {
    onPageChange(pageId);
    if (window.innerWidth <= 1024) {
      setSidebarOpen(false);
    }
  };

  return (
    <nav className={`sidebar ${sidebarOpen ? 'active' : ''}`} aria-label="Main navigation">
      <div className="logo">StockView</div>
      <ul className="sidebar-menu">
        {menuItems.map((item) => {
          if (item.premium && !isLoggedIn) return null;
          
          return (
            <li
              key={item.id}
              className={activePage === item.id ? 'active' : ''}
              onClick={() => handleItemClick(item.id)}
            >
              <i className={item.icon}></i> {item.label}
            </li>
          );
        })}
      </ul>
    </nav>
  );
};