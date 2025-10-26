import React, { useState, useEffect } from 'react';
import { Sidebar } from './Sidebar';
import { Header } from './Header';
import { useAuthStore } from '../../store/authStore';

interface MainLayoutProps {
  children: React.ReactNode;
  activePage: string;
  onPageChange: (page: string) => void;
  onLoginClick: () => void;
  onLogout?: () => void;
}

export const MainLayout: React.FC<MainLayoutProps> = ({
  children,
  activePage,
  onPageChange,
  onLoginClick,
  onLogout,
}) => {
  const [sidebarActive, setSidebarActive] = useState(false);
  const { isLoggedIn } = useAuthStore();

  // Close sidebar when clicking outside on mobile
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const sidebar = document.querySelector('.sidebar');
      const menuToggle = document.querySelector('.menu-toggle');
      
      if (window.innerWidth <= 1024 && 
          sidebar && 
          !sidebar.contains(event.target as Node) && 
          menuToggle && 
          !menuToggle.contains(event.target as Node) && 
          sidebarActive) {
        setSidebarActive(false);
      }
    };

    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, [sidebarActive]);

  const handleMenuToggle = () => {
    setSidebarActive(!sidebarActive);
  };

  const handlePageChange = (page: string) => {
    onPageChange(page);
    if (window.innerWidth <= 1024) {
      setSidebarActive(false);
    }
  };

  return (
    <div className="container">
      <Sidebar 
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
      <main className="main-content">
        {children}
      </main>
    </div>
  );
};

export default MainLayout;