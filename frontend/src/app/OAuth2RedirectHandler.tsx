import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

const OAuth2RedirectHandler: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuthStore();

  useEffect(() => {
    const handleOAuthRedirect = () => {
      const urlParams = new URLSearchParams(window.location.search);
      const token = urlParams.get("token");
      const userJson = urlParams.get("user");

      if (token && userJson) {
        try {
          // Decode user info từ URL
          const decodedUserJson = decodeURIComponent(userJson);
          const userData = JSON.parse(decodedUserJson);

          // Lưu thông tin user vào store
          const user = {
            id: userData.id || userData.sub,
            username:
              userData.username ??
              userData.email?.split("@")[0] ??
              "google_user",
            name: userData.username || userData.name || "Google User",
            role: userData.role || "Nhà đầu tư",
            avatar:
              userData.avatarUrl ||
              userData.picture ||
              `https://i.pravatar.cc/150?u=${
                userData.email || userData.username
              }`,
            email: userData.email,
            token,
          };

          login(user);

          // Lưu token vào localStorage
          localStorage.setItem("authToken", token);
          localStorage.setItem("user", JSON.stringify(user));

          // Redirect về trang chủ
          navigate("/dashboard", { replace: true });
        } catch (error) {
          console.error("Error processing OAuth2 redirect:", error);
          navigate("/dashboard", { replace: true });
        }
      } else {
        console.error("Missing token or user info in OAuth2 redirect");
        navigate("/dashboard", { replace: true });
      }
    };

    handleOAuthRedirect();
  }, [navigate, login]);

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
        flexDirection: "column",
        gap: "20px",
      }}
    >
      <div>Đang xử lý đăng nhập...</div>
      <div className="loading-spinner"></div>
    </div>
  );
};

export default OAuth2RedirectHandler;
