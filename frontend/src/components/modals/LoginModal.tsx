import React, { useState } from "react";
import { useAuthStore } from "../../store/authStore";

interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const LoginModal: React.FC<LoginModalProps> = ({ isOpen, onClose }) => {
  const [formData, setFormData] = useState({
    usernameOrEmail: "",
    password: ""
  });
  const [loading, setLoading] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [id === "loginUsername" ? "usernameOrEmail" : "password"]: value
    }));
  };

  const handleConfirm = async () => {
    const { usernameOrEmail, password } = formData;
    
    if (!usernameOrEmail.trim() || !password.trim()) {
      alert("Vui lòng nhập username/email và mật khẩu!");
      return;
    }

    setLoading(true);

    try {
      const loginRes = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          usernameOrEmail: usernameOrEmail.trim(),
          password: password.trim(),
        }),
      });

      const loginData = await loginRes.json();

      if (!loginRes.ok || !loginData.token) {
        throw new Error(loginData.message || "Đăng nhập thất bại");
      }

      const token = loginData.token;
      localStorage.setItem("authToken", token);

      const meRes = await fetch("http://localhost:8080/api/users/me", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!meRes.ok) throw new Error("Không thể lấy thông tin người dùng");

      const userData = await meRes.json();
      const user = {
        id: userData.data.id,
        username: userData.data.username,
        name: userData.data.username,
        role: userData.data.role,
        email: userData.data.email,
        avatar: userData.data.avatarUrl || `https://i.pravatar.cc/150?u=${userData.data.email}`,
        token,
      };

      localStorage.setItem("user", JSON.stringify(user));
      useAuthStore.getState().login(user);

      alert("Đăng nhập thành công!");
      onClose();

      const redirectPath = user.role.toUpperCase() === "ADMIN" ? "/admin" : "/";
      window.location.href = redirectPath;
      
    } catch (err: any) {
      console.error("Login error:", err);
      alert(err.message || "Đăng nhập thất bại!");
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setFormData({ usernameOrEmail: "", password: "" });
    onClose();
  };

  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  const handleForgotPassword = (e: React.MouseEvent) => {
    e.preventDefault();
    alert("Tính năng quên mật khẩu đang được phát triển...");
  };

  return (
    <div className={`modal ${isOpen ? 'active' : ''}`}>
      <div className="modal-overlay" onClick={handleCancel}></div>
      <div className="modal-container">
        <div className="modal-header">
          <h2 className="modal-title">Đăng Nhập</h2>
          <button 
            className="close-modal" 
            onClick={handleCancel}
            aria-label="Đóng"
          >
            &times;
          </button>
        </div>

        <div className="modal-content">
          {/* Google Login */}
          <div className="google-login-section">
            <button
              className="btn btn-google"
              onClick={handleGoogleLogin}
              disabled={loading}
            >
              <GoogleIcon />
              Đăng nhập với Google
            </button>
          </div>

          <Divider />

          {/* Form login */}
          <div className="form-group">
            <label htmlFor="loginUsername">Username hoặc email</label>
            <input
              type="text"
              id="loginUsername"
              placeholder="Nhập username hoặc email"
              value={formData.usernameOrEmail}
              onChange={handleInputChange}
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="loginPassword">Mật khẩu</label>
            <input
              type="password"
              id="loginPassword"
              placeholder="Nhập mật khẩu"
              value={formData.password}
              onChange={handleInputChange}
              disabled={loading}
            />
          </div>

          <div className="form-actions">
            <button 
              className="btn btn-secondary" 
              onClick={handleCancel}
              disabled={loading}
            >
              Hủy
            </button>
            <button
              className="btn btn-primary"
              onClick={handleConfirm}
              disabled={loading}
            >
              {loading ? "Đang đăng nhập..." : "Đăng nhập"}
            </button>
          </div>

          <div className="forgot-password">
            <a href="#" onClick={handleForgotPassword}>
              Quên mật khẩu?
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

// Sub-components
const GoogleIcon: React.FC = () => (
  <svg
    width="18"
    height="18"
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 48 48"
    aria-hidden="true"
  >
    <path fill="#4285f4" d="M45.12 24.5c0-1.56-.14-3.06-.4-4.5H24v8.51h11.84c-.51 2.75-2.06 5.08-4.39 6.64v5.52h7.11c4.16-3.83 6.56-9.47 6.56-16.17z"/>
    <path fill="#34a853" d="M24 46c5.94 0 10.92-1.97 14.56-5.33l-7.11-5.52c-1.97 1.32-4.49 2.1-7.45 2.1-5.73 0-10.58-3.87-12.31-9.07H4.34v5.7C7.96 41.07 15.4 46 24 46z"/>
    <path fill="#fbbc05" d="M11.69 28.18C11.25 26.86 11 25.45 11 24s.25-2.86.69-4.18v-5.7H4.34C2.85 17.09 2 20.45 2 24c0 3.55.85 6.91 2.34 9.88l7.35-5.7z"/>
    <path fill="#ea4335" d="M24 10.75c3.23 0 6.13 1.11 8.41 3.29l6.31-6.31C34.92 4.18 29.94 2 24 2 15.4 2 7.96 6.93 4.34 14.12l7.35 5.7C13.42 14.62 18.27 10.75 24 10.75z"/>
  </svg>
);

const Divider: React.FC = () => (
  <div className="divider">
    <div className="divider-line" />
    <span>hoặc</span>
    <div className="divider-line" />
  </div>
);

export default LoginModal;
