import React, { useState, useEffect } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  useLocation,
  useNavigate,
} from "react-router-dom";
import MainLayout from "./components/layout/MainLayout";
import DashboardPage from "./pages/DashboardPage";
import StockPage from "./pages/StockPage";
import StockDetailPage from "./pages/StockDetailPage";
import WatchlistPage from "./pages/WatchlistPage";
import WalletPage from "./pages/WalletPage";
import ProfilePage from "./pages/ProfilePage";
import AdminPage from "./pages/AdminPage";
import OAuth2RedirectHandler from "./pages/OAuth2RedirectHandler";
import LoginModal from "./components/modals/LoginModal";
import { useAuthStore } from "./store/authStore";
import "./styles/index.css";

// Component để xử lý active page và navigation
const AppContent: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [showLoginModal, setShowLoginModal] = useState(false);
  const { login, initializeAuth, logout } = useAuthStore();

  useEffect(() => {
    initializeAuth();
  }, []);

  // Map route paths to page IDs
  const getActivePage = () => {
    const path = location.pathname;
    if (path === "/dashboard") return "dashboard";
    if (path === "/stock") return "stock";
    if (path === "/favorit") return "favorit";
    if (path === "/wallet") return "wallet";
    if (path === "/profile") return "profile";
    return "dashboard";
  };

  const handlePageChange = (page: string) => {
    const routes: { [key: string]: string } = {
      dashboard: "/dashboard",
      stock: "/stock",
      favorit: "/favorit",
      wallet: "/wallet",
      profile: "/profile",
      admin: "/admin",
    };

    if (routes[page]) {
      navigate(routes[page]);
    }
  };

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

  // Xử lý khi user logout từ header
  const handleLogoutFromHeader = () => {
    logout();
    // Có thể redirect về dashboard nếu cần
    navigate("/dashboard");
  };

  return (
    <>
      <MainLayout
        activePage={getActivePage()}
        onPageChange={handlePageChange}
        onLoginClick={() => setShowLoginModal(true)}
        onLogout={handleLogoutFromHeader}
      >
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/stock" element={<StockPage />} />{" "}
          <Route path="/stock/:symbol" element={<StockDetailPage />} />{" "}
          <Route path="/favorit" element={<WatchlistPage />} />
          <Route path="/admin" element={<AdminPage />} />
          <Route path="/wallet" element={<WalletPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />
        </Routes>
      </MainLayout>

      <LoginModal
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
        onLogin={handleLogin}
      />
    </>
  );
};

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
