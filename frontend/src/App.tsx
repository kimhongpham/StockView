// App.tsx
import React, { useState } from 'react';
import { MainLayout } from './components/layout/MainLayout';
import { DashboardPage } from './pages/DashboardPage';
import { StockPage } from './pages/StockPage';
import { WatchlistPage } from './pages/WatchlistPage';
import { WalletPage } from './pages/WalletPage';
import { LoginModal } from './components/modals/LoginModal';
import { useAuthStore } from './store/authStore';
import './App.css';

function App() {
  const [activePage, setActivePage] = useState('dashboard');
  const [showLoginModal, setShowLoginModal] = useState(false);
  const { login } = useAuthStore();

  const renderPage = () => {
    switch (activePage) {
      case 'dashboard':
        return <DashboardPage />;
      case 'stock':
        return <StockPage />;
      case 'favorit':
        return <WatchlistPage />;
      case 'wallet':
        return <WalletPage />;
      default:
        return <DashboardPage />;
    }
  };

  const handleLogin = (email: string, password: string) => {
    // Mock login - trong thực tế sẽ gọi API
    const mockUser = {
      name: "Kim Hồng Phạm",
      role: "Nhà đầu tư",
      avatar: "KHP",
      email: email,
    };
    login(mockUser);
    setShowLoginModal(false);
  };

  return (
    <>
      <MainLayout
        activePage={activePage}
        onPageChange={setActivePage}
        onLoginClick={() => setShowLoginModal(true)}
      >
        {renderPage()}
      </MainLayout>

      <LoginModal
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
        onLogin={handleLogin}
      />
    </>
  );
}

export default App;