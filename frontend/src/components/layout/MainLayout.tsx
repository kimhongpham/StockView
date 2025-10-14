import React from 'react';
import { Sidebar } from './Sidebar';
import { Header } from './Header';

interface MainLayoutProps {
  children: React.ReactNode;
  activePage: string;
  onPageChange: (page: string) => void;
  onLoginClick: () => void;
}

export const MainLayout: React.FC<MainLayoutProps> = ({
  children,
  activePage,
  onPageChange,
  onLoginClick,
}) => {
  return (
    <div className="container">
      <Sidebar activePage={activePage} onPageChange={onPageChange} />
      <Header onLoginClick={onLoginClick} />
      <main className="main-content">
        {children}
      </main>
    </div>
  );
};