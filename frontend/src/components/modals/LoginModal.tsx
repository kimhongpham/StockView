import React, { useState } from "react";
import { useAuthStore } from "../../store/authStore";

interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
}
const LoginModal: React.FC<LoginModalProps> = ({ isOpen, onClose }) => {
  const [usernameOrEmail, setUsernameOrEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleConfirm = async () => {
    if (!usernameOrEmail.trim() || !password.trim()) {
      alert("Vui lòng nhập username/email và mật khẩu!");
      return;
    }

    setLoading(true);

    try {
      // 1️⃣ Login
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          usernameOrEmail: usernameOrEmail.trim(),
          password: password.trim(),
        }),
      });

      const loginData = await res.json();

      if (!res.ok || !loginData.token)
        throw new Error(loginData.message || "Đăng nhập thất bại");

      const token = loginData.token;
      localStorage.setItem("authToken", token);

      // 2️⃣ Lấy thông tin người dùng
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
        avatar:
          userData.data.avatarUrl ||
          `https://i.pravatar.cc/150?u=${userData.data.email}`,
        token,
      };

      localStorage.setItem("user", JSON.stringify(user));
      useAuthStore.getState().login(user);

      alert("Đăng nhập thành công!");
      onClose();

      // 3️⃣ Redirect theo role
      if (user.role.toUpperCase() === "ADMIN") {
        window.location.href = "/admin";
      } else {
        window.location.href = "/";
      }
    } catch (err: any) {
      console.error(err);
      alert(err.message || "Đăng nhập thất bại!");
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setUsernameOrEmail("");
    setPassword("");
    onClose();
  };

  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  if (!isOpen) return null;

  return (
    <div className="modal active" id="loginModal">
      <div className="modal-content">
        <div className="modal-header">
          <h2 className="modal-title">Đăng Nhập</h2>
          <button className="close-modal" onClick={handleCancel}>
            &times;
          </button>
        </div>

        {/* Google Login */}
        <div
          className="google-login-section"
          style={{ marginBottom: "20px", textAlign: "center" }}
        >
          <button
            className="btn btn-google"
            onClick={handleGoogleLogin}
            style={{
              width: "100%",
              padding: "12px",
              backgroundColor: "#4285f4",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
              fontSize: "16px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: "10px",
            }}
          >
            {/* SVG Google */}
            <svg
              width="18"
              height="18"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 48 48"
            >
              <path
                fill="#4285f4"
                d="M45.12 24.5c0-1.56-.14-3.06-.4-4.5H24v8.51h11.84c-.51 2.75-2.06 5.08-4.39 6.64v5.52h7.11c4.16-3.83 6.56-9.47 6.56-16.17z"
              />
              <path
                fill="#34a853"
                d="M24 46c5.94 0 10.92-1.97 14.56-5.33l-7.11-5.52c-1.97 1.32-4.49 2.1-7.45 2.1-5.73 0-10.58-3.87-12.31-9.07H4.34v5.7C7.96 41.07 15.4 46 24 46z"
              />
              <path
                fill="#fbbc05"
                d="M11.69 28.18C11.25 26.86 11 25.45 11 24s.25-2.86.69-4.18v-5.7H4.34C2.85 17.09 2 20.45 2 24c0 3.55.85 6.91 2.34 9.88l7.35-5.7z"
              />
              <path
                fill="#ea4335"
                d="M24 10.75c3.23 0 6.13 1.11 8.41 3.29l6.31-6.31C34.92 4.18 29.94 2 24 2 15.4 2 7.96 6.93 4.34 14.12l7.35 5.7C13.42 14.62 18.27 10.75 24 10.75z"
              />
            </svg>
            Đăng nhập với Google
          </button>
        </div>

        <div
          className="divider"
          style={{
            display: "flex",
            alignItems: "center",
            margin: "20px 0",
            color: "#666",
          }}
        >
          <div style={{ flex: 1, height: "1px", backgroundColor: "#ddd" }} />
          <span style={{ padding: "0 15px" }}>hoặc</span>
          <div style={{ flex: 1, height: "1px", backgroundColor: "#ddd" }} />
        </div>

        {/* Form login */}
        <div className="form-group">
          <label htmlFor="loginUsername">Username hoặc email</label>
          <input
            type="text"
            id="loginUsername"
            placeholder="Username hoặc email"
            value={usernameOrEmail}
            onChange={(e) => setUsernameOrEmail(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label htmlFor="loginPassword">Mật khẩu</label>
          <input
            type="password"
            id="loginPassword"
            placeholder="Nhập mật khẩu"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        <div className="form-actions">
          <button className="btn btn-secondary" onClick={handleCancel}>
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

        <div style={{ textAlign: "center", marginTop: "15px" }}>
          <a
            href="#"
            style={{ color: "var(--primary-color)", textDecoration: "none" }}
            onClick={(e) => {
              e.preventDefault();
              alert("Tính năng quên mật khẩu đang được phát triển...");
            }}
          >
            Quên mật khẩu?
          </a>
        </div>
      </div>
    </div>
  );
};

export default LoginModal;
