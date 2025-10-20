import React, { useState, useEffect } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import MainLayout from "./components/layout/MainLayout";
import DashboardPage from "./pages/DashboardPage";
import StockPage from "./pages/StockPage";
import WatchlistPage from "./pages/WatchlistPage";
import WalletPage from "./pages/WalletPage";
import OAuth2RedirectHandler from "./pages/OAuth2RedirectHandler";
import LoginModal from "./components/modals/LoginModal";
import { useAuthStore } from "./store/authStore";
import "./App.css";

function App() {
  const [showLoginModal, setShowLoginModal] = useState(false);
  const { login, initializeAuth } = useAuthStore();

  useEffect(() => {
    initializeAuth();
  }, []);

  const handleLogin = (email: string, password: string) => {
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
    <Router>
      <MainLayout onLoginClick={() => setShowLoginModal(true)}>
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/stock" element={<StockPage />} />
          <Route path="/favorit" element={<WatchlistPage />} />
          <Route path="/wallet" element={<WalletPage />} />
          <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />
        </Routes>
      </MainLayout>

      <LoginModal
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
        onLogin={handleLogin}
      />
    </Router>
  );
}

export default App;
