import React, { useState } from 'react';

interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
  onLogin: (email: string, password: string) => void;
}

export const LoginModal: React.FC<LoginModalProps> = ({ isOpen, onClose, onLogin }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleConfirm = () => {
    if (email && password) {
      onLogin(email, password);
      setEmail('');
      setPassword('');
    } else {
      alert('Vui lòng nhập email và mật khẩu!');
    }
  };

  const handleCancel = () => {
    setEmail('');
    setPassword('');
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="modal active" id="loginModal">
      <div className="modal-content">
        <div className="modal-header">
          <h2 className="modal-title">Đăng Nhập</h2>
          <button className="close-modal" onClick={handleCancel}>&times;</button>
        </div>
        <div className="form-group">
          <label htmlFor="loginEmail">Email</label>
          <input
            type="email"
            id="loginEmail"
            placeholder="email@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
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
          <button className="btn btn-secondary" onClick={handleCancel}>Hủy</button>
          <button className="btn btn-primary" onClick={handleConfirm}>Đăng nhập</button>
        </div>
        <div style={{ textAlign: 'center', marginTop: '15px' }}>
          <a
            href="#"
            style={{ color: 'var(--primary-color)', textDecoration: 'none' }}
            onClick={(e) => {
              e.preventDefault();
              alert('Tính năng quên mật khẩu đang được phát triển...');
            }}
          >
            Quên mật khẩu?
          </a>
        </div>
      </div>
    </div>
  );
};