import React from 'react';
import { useAuthStore } from '../../store/authStore';

interface SidebarProps {
  activePage: string;
  onPageChange: (page: string) => void;
  isLoggedIn: boolean;
  isActive: boolean;
}

export const Sidebar: React.FC<SidebarProps> = ({ 
  activePage, 
  onPageChange, 
  isLoggedIn,
  isActive 
}) => {
  const menuItems = [
    { id: 'dashboard', icon: 'fas fa-chart-line', label: 'Thị Trường', premium: false },
    { id: 'stock', icon: 'fas fa-chart-bar', label: 'Danh Mục Quan Sát', premium: false },
    { id: 'favorit', icon: 'fas fa-star', label: 'Đang Theo Dõi', premium: false },
    { id: 'wallet', icon: 'fas fa-wallet', label: 'Danh Mục Đầu Tư', premium: false },
    { id: 'profile', icon: 'fas fa-user', label: 'Hồ Sơ', premium: false },
  ];

  const handleItemClick = (pageId: string) => {
    onPageChange(pageId);
  };

  const filteredMenuItems = menuItems.filter(item => {
    if (item.premium && !isLoggedIn) return false;
    return true;
  });

  return (
    <nav className={`sidebar ${isActive ? 'active' : ''}`} aria-label="Main navigation">
      <div className="logo">FinanceView</div>
      <ul className="sidebar-menu">
        {filteredMenuItems.map((item) => (
          <li
            key={item.id}
            className={`${activePage === item.id ? 'active' : ''} ${item.premium ? 'premium-feature' : ''}`}
            data-page={item.id}
            onClick={() => handleItemClick(item.id)}
          >
            <i className={item.icon}></i> {item.label}
          </li>
        ))}
      </ul>
    </nav>
  );
};