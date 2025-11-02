import React, { useState } from "react";
import "../styles/profile.css"; // 🔹 nhớ import style

const ProfilePage: React.FC = () => {
  const [displayName, setDisplayName] = useState("Kim Hồng Phạm");
  const [email, setEmail] = useState("louica0107@gmail.com");
  const [phone, setPhone] = useState("Số điện thoại...");
  const [isEditing, setIsEditing] = useState(false);

  const handleSave = () => setIsEditing(false);
  const handleCancel = () => {
    setDisplayName("Kim Hồng Phạm");
    setEmail("louica0107@gmail.com");
    setPhone("Số điện thoại...");
    setIsEditing(false);
  };

  return (
    <div className="profile-page">
      <div className="profile-container">
        {/* Header */}
        <div className="profile-header">
          <h1 className="profile-title">{displayName}</h1>
          <div className="profile-badge">
            <div className="badge-dot"></div>
            <span className="badge-text">BASIC ● Chưa tích hợp</span>
          </div>
        </div>

        {/* Card */}
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
                <div className="checkbox-indicator" />
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
                <div className="checkbox-indicator" />
                <span className="display-text">{email}</span>
              </div>
            )}
          </div>

          {/* Phone */}
          <div className="profile-section">
            <label className="section-label">Số điện thoại</label>
            {isEditing ? (
              <div>
                <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  className="profile-input"
                  placeholder="Nhập số điện thoại"
                />
                <div className="phone-actions">
                  <button className="add-phone-button">
                    <span>+</span>
                    <span>Thêm số điện thoại</span>
                  </button>
                </div>
              </div>
            ) : (
              <div className="profile-display">
                <div className="checkbox-indicator" />
                <span className="display-text">{phone}</span>
              </div>
            )}
          </div>

          {/* Password */}
          <div className="password-section">
            <label className="section-label">Mật khẩu</label>
            <div className="password-display">
              <div className="checkbox-indicator" />
              <span className="password-dots">••••••••</span>
            </div>
            <button className="change-password-button">Đổi mật khẩu</button>
          </div>
        </div>

        {/* Buttons */}
        {isEditing ? (
          <div className="action-buttons">
            <button onClick={handleCancel} className="cancel-button">
              Hủy
            </button>
            <button onClick={handleSave} className="save-button">
              Lưu thay đổi
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
