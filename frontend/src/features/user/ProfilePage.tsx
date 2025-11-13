import React, { useEffect, useState } from "react";
import "../../styles/pages/ProfilePage.css";
import { useAuthStore } from "../../store/authStore";

const ProfilePage: React.FC = () => {
  const { user: authUser, setUser } = useAuthStore();
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);

  // ✅ Lấy thông tin người dùng khi mount
  useEffect(() => {
    if (!authUser?.token) return;

    fetch("http://localhost:8080/api/users/me", {
      headers: {
        Authorization: `Bearer ${authUser.token}`,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.data) {
          setDisplayName(data.data.username || "");
          setEmail(data.data.email || "");
        }
      })
      .catch(console.error);
  }, [authUser]);

  // Cập nhật thông tin người dùng
  const handleSave = async () => {
    console.log("authUser hiện tại:", authUser);
    if (!authUser?.id || !authUser?.token) {
      alert("Không tìm thấy thông tin người dùng!");
      return;
    }

    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/api/users/me", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${authUser.token}`,
        },
        body: JSON.stringify({
          username: displayName,
          email: email,
          firstName: null,
          lastName: null,
          timezone: "UTC",
          avatarUrl: null,
        }),
      });

      const result = await res.json();
      if (!res.ok) throw new Error(result.message || "Cập nhật thất bại");

      // Lấy lại thông tin mới
      const meRes = await fetch("http://localhost:8080/api/users/me", {
        headers: { Authorization: `Bearer ${authUser.token}` },
      });
      const meData = await meRes.json();

      if (meData.data) {
        setUser({ ...authUser, ...meData.data });
      }

      alert("Cập nhật thông tin thành công!");
      setIsEditing(false);
    } catch (err: any) {
      console.error(err);
      alert(err.message || "Cập nhật thất bại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="profile-page">
      <div className="profile-container">
        <div className="profile-header">
          <h1 className="profile-title">{displayName}</h1>
        </div>

        <div className="profile-card">
          {/* Display name */}
          <div className="profile-section">
            <div className="section-header">
              <label className="section-label">Tên hiển thị</label>
              {!isEditing && (
                <button
                  className="edit-button"
                  onClick={() => setIsEditing(true)}
                >
                  Chỉnh sửa
                </button>
              )}
            </div>

            {isEditing ? (
              <input
                type="text"
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                className="profile-input"
              />
            ) : (
              <div className="profile-display">
                <span className="display-text">{displayName}</span>
              </div>
            )}
          </div>

          {/* Email */}
          <div className="profile-section">
            <label className="section-label">Email</label>
            {isEditing ? (
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="profile-input"
              />
            ) : (
              <div className="profile-display">
                <span className="display-text">{email}</span>
              </div>
            )}
          </div>

          {/* Password */}
          <div className="password-section">
            <label className="section-label">Mật khẩu</label>
            <div className="password-display">
              <span className="password-dots">••••••••</span>
            </div>
            <button className="change-password-button">Đổi mật khẩu</button>
          </div>
        </div>

        {/* Buttons */}
        {isEditing ? (
          <div className="action-buttons">
            <button
              onClick={() => setIsEditing(false)}
              className="cancel-button"
            >
              Hủy
            </button>
            <button
              onClick={handleSave}
              className="save-button"
              disabled={loading}
            >
              {loading ? "Đang lưu..." : "Lưu thay đổi"}
            </button>
          </div>
        ) : (
          <div className="update-info-section">
            <button
              className="update-info-button"
              onClick={() => setIsEditing(true)}
            >
              Cập nhật thông tin
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;
