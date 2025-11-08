import React, { useState, useEffect } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  useLocation,
  useNavigate,
  useParams,
} from "react-router-dom";
import MainLayout from "../components/layout/MainLayout";
import DashboardPage from "../features/dashboard/DashboardPage";
import StockPage from "../features/stock/StockPage";
import StockDetailPage from "../features/stock/StockDetailPage";
import WatchlistPage from "../features/watchlist/WatchlistPage";
import WalletPage from "../features/user/WalletPage";
import ProfilePage from "../features/user/ProfilePage";
import AdminPage from "../features/admin/AdminPage";
import OAuth2RedirectHandler from "./OAuth2RedirectHandler";
import LoginModal from "../components/modals/LoginModal";
import { useAuthStore } from "../store/authStore";
import "../styles/index.css";

// Component để redirect từ /stocks/:symbol sang /stock/:symbol
const StocksRedirect: React.FC = () => {
  const { symbol } = useParams<{ symbol: string }>();
  return <Navigate to={`/stock/${symbol}`} replace />;
};

// Component để xử lý active page và navigation
const AppContent: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [showLoginModal, setShowLoginModal] = useState(false);
  const {initializeAuth, logout } = useAuthStore();

  useEffect(() => {
    initializeAuth();
  }, []);

  // Map route paths to page IDs
  const getActivePage = () => {
    const path = location.pathname;
    if (path.startsWith("/stock") || path.startsWith("/stocks")) {
      return "stock";
    }
    return path.replace("/", "") || "dashboard";
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
          
          {/* Routes chính - sử dụng /stock */}
          <Route path="/stock" element={<StockPage />} />
          <Route path="/stock/:symbol" element={<StockDetailPage />} />
          
          {/* Redirect routes từ /stocks sang /stock */}
          <Route path="/stocks" element={<Navigate to="/stock" replace />} />
          <Route path="/stocks/:symbol" element={<StocksRedirect />} />
          
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