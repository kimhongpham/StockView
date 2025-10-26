import React, { useState, useEffect } from 'react';
import { fetchAssetById, fetchMarketAssets } from '../../utils/api';

interface AddStockModalProps {
  isOpen: boolean;
  onClose: () => void;
  onAddStock: (asset: any) => void;
}

export const AddStockModal: React.FC<AddStockModalProps> = ({ isOpen, onClose, onAddStock }) => {
  const [stockCode, setStockCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [topStocks, setTopStocks] = useState<any[]>([]);

  // Lấy danh sách top stocks khi modal mở
  useEffect(() => {
    if (!isOpen) return;
    const loadTopStocks = async () => {
      try {
        const stocks = await fetchMarketAssets("stocks");
        setTopStocks(stocks);
      } catch (err) {
        console.error('Không tải được top stocks', err);
      }
    };
    loadTopStocks();
  }, [isOpen]);

  const handleConfirm = async () => {
    if (!stockCode.trim()) {
      alert('Vui lòng nhập mã cổ phiếu!');
      return;
    }

    try {
      setLoading(true);
      const asset = await fetchAssetById(stockCode.trim().toUpperCase());
      onAddStock(asset);
      setStockCode('');
      onClose();
    } catch (err) {
      alert('Không tìm thấy cổ phiếu!');
    } finally {
      setLoading(false);
    }
  };

  const handleAddFromTop = (asset: any) => {
    onAddStock(asset);
    onClose();
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
          <label htmlFor="stockCode">Nhập Mã Cổ Phiếu</label>
          <input
            type="text"
            id="stockCode"
            placeholder="VD: VNM, HPG, FPT..."
            value={stockCode}
            onChange={(e) => setStockCode(e.target.value)}
          />
          <button className="btn btn-primary" onClick={handleConfirm} disabled={loading}>
            {loading ? 'Đang tải...' : 'Thêm'}
          </button>
        </div>

        <div className="top-stocks">
          <h3>Top Stocks</h3>
          <ul>
            {topStocks.map(stock => (
              <li key={stock.symbol}>
                <button className="btn btn-secondary" onClick={() => handleAddFromTop(stock)}>
                  {stock.symbol} - {stock.name}
                </button>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};
