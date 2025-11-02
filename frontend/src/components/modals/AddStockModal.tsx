import React, { useState, useEffect } from 'react'
import { fetchAssetById, fetchMarketAssets } from '../../utils/api'
import { X, Search, Plus, Loader2 } from 'lucide-react'

interface AddStockModalProps {
  isOpen: boolean
  onClose: () => void
  onAddStock: (asset: any) => void
}

export const AddStockModal: React.FC<AddStockModalProps> = ({
  isOpen,
  onClose,
  onAddStock,
}) => {
  const [stockCode, setStockCode] = useState('')
  const [loading, setLoading] = useState(false)
  const [topStocks, setTopStocks] = useState<any[]>([])

  useEffect(() => {
    if (!isOpen) return
    const loadTopStocks = async () => {
      try {
        const stocks = await fetchMarketAssets('stocks')
        setTopStocks(stocks)
      } catch (err) {
        console.error('Không tải được top stocks', err)
      }
    }
    loadTopStocks()
  }, [isOpen])

  const handleConfirm = async () => {
    if (!stockCode.trim()) {
      alert('Vui lòng nhập mã cổ phiếu!')
      return
    }
    try {
      setLoading(true)
      const asset = await fetchAssetById(stockCode.trim().toUpperCase())
      onAddStock(asset)
      setStockCode('')
      onClose()
    } catch (err) {
      alert('Không tìm thấy cổ phiếu!')
    } finally {
      setLoading(false)
    }
  }

  const handleAddFromTop = (asset: any) => {
    onAddStock(asset)
    onClose()
  }

  if (!isOpen) return null

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2 className="modal-title">Thêm cổ phiếu vào Watchlist</h2>
          <button className="close-btn" onClick={onClose}>
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="modal-body">
          <div className="search-section">
            <label htmlFor="stockCode">Nhập mã cổ phiếu</label>
            <div className="input-group">
              <Search className="w-4 h-4 text-gray-400" />
              <input
                id="stockCode"
                type="text"
                placeholder="VD: VNM, HPG, FPT..."
                value={stockCode}
                onChange={(e) => setStockCode(e.target.value)}
              />
              <button
                className="btn-primary"
                onClick={handleConfirm}
                disabled={loading}
              >
                {loading ? (
                  <Loader2 className="animate-spin w-4 h-4" />
                ) : (
                  <Plus className="w-4 h-4" />
                )}
                <span>{loading ? 'Đang tải...' : 'Thêm'}</span>
              </button>
            </div>
          </div>

          <div className="top-stocks">
            <h3>Top cổ phiếu phổ biến</h3>
            <ul>
              {topStocks.slice(0, 10).map((stock) => (
                <li key={stock.symbol}>
                  <button
                    className="top-stock-item"
                    onClick={() => handleAddFromTop(stock)}
                  >
                    <span className="symbol">{stock.symbol}</span>
                    <span className="name">{stock.name}</span>
                  </button>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}
