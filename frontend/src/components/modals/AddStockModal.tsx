import React, { useState } from 'react';

interface AddStockModalProps {
  isOpen: boolean;
  onClose: () => void;
  onAddStock: (stockCode: string) => void;
}

export const AddStockModal: React.FC<AddStockModalProps> = ({ isOpen, onClose, onAddStock }) => {
  const [stockCode, setStockCode] = useState('');

  const handleConfirm = () => {
    if (stockCode.trim()) {
      onAddStock(stockCode);
      setStockCode('');
    } else {
      alert('Vui lòng nhập mã cổ phiếu!');
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal active" id="addStockModal">
      <div className="modal-content">
        <div className="modal-header">
          <h2 className="modal-title">Thêm Cổ Phiếu Vào Watchlist</h2>
          <button className="close-modal" onClick={onClose}>&times;</button>
        </div>
        <div className="form-group">
          <label htmlFor="stockCode">Mã Cổ Phiếu</label>
          <input
            type="text"
            id="stockCode"
            placeholder="VD: VNM, HPG, FPT..."
            value={stockCode}
            onChange={(e) => setStockCode(e.target.value)}
          />
        </div>
        <div className="form-actions">
          <button className="btn btn-secondary" onClick={onClose}>Hủy</button>
          <button className="btn btn-primary" onClick={handleConfirm}>Thêm</button>
        </div>
      </div>
    </div>
  );
};