import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

const OAuth2RedirectHandler: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuthStore();

  useEffect(() => {
    const handleOAuthRedirect = () => {
      // Support both query (?a=b) and hash (#a=b) styles
      const searchParams = new URLSearchParams(
        window.location.search || window.location.hash.replace(/^#/, "")
      );

      // Common token param aliases used by various backends
      const token =
        searchParams.get("token") ||
        searchParams.get("access_token") ||
        searchParams.get("accessToken") ||
        searchParams.get("jwt") ||
        undefined;

      // Some backends may include serialized user info under different keys
      const rawUserParam =
        searchParams.get("user") ||
        searchParams.get("userInfo") ||
        searchParams.get("profile") ||
        undefined;

      if (token && rawUserParam) {
        try {
          // Decode user info từ URL
          const decodedUserJson = decodeURIComponent(rawUserParam);
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
      } else if (token && !rawUserParam) {
        // If only token is present, try to fetch current user from backend
        (async () => {
          try {
            localStorage.setItem("authToken", token);
            const meRes = await fetch("http://localhost:8080/api/users/me", {
              headers: { Authorization: `Bearer ${token}` },
            });

            if (!meRes.ok) {
              throw new Error("Không thể lấy thông tin người dùng");
            }

            const meData = await meRes.json();
            const data = meData.data || meData;

            const user = {
              id: data.id || data.sub,
              username: data.username ?? data.email?.split("@")[0] ?? "user",
              name: data.name || data.username || "User",
              role: (data.role || "Nhà đầu tư") as string,
              email: data.email,
              avatar:
                data.avatarUrl ||
                data.picture ||
                `https://i.pravatar.cc/150?u=${data.email || data.username}`,
              token,
            };

            localStorage.setItem("user", JSON.stringify(user));
            login(user);
            navigate("/dashboard", { replace: true });
          } catch (err) {
            console.error("OAuth2 fetch /me failed:", err);
            // Cleanup on failure
            localStorage.removeItem("authToken");
            localStorage.removeItem("user");
            navigate("/dashboard", { replace: true });
          }
        })();
      } else {
        console.error(
          "Missing token or user info in OAuth2 redirect. URL:",
          window.location.href
        );
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
