import React, { useEffect, useState } from "react";
import { FaStar, FaRegStar } from "react-icons/fa";
import { useAuthStore } from "../../store/authStore";
import { getWatchlist, addToWatchlist, removeFromWatchlist } from "../../utils/api";

interface StarButtonProps {
  assetSymbol: string;
}

const StarButton: React.FC<StarButtonProps> = ({ assetSymbol }) => {
  const { user: authUser } = useAuthStore();
  const [isInWatchlist, setIsInWatchlist] = useState(false);
  const [loading, setLoading] = useState(false);

  // --- Load watchlist
  const fetchWatchlist = async () => {
    if (!authUser?.token) return;

    try {
      const res = await getWatchlist(authUser.token);
      const list = Array.isArray(res) ? res : res.data ?? [];
      setIsInWatchlist(list.includes(assetSymbol));
    } catch (err: any) {
      console.error("Failed to load watchlist:", err.response?.data || err.message);
    }
  };

  useEffect(() => {
    fetchWatchlist();
  }, [assetSymbol, authUser?.token]);

  // --- Toggle watchlist
  const toggleWatchlist = async (e: React.MouseEvent) => {
    e.stopPropagation(); // ⚡ Ngăn click bubble lên row
    if (!authUser?.token) {
      alert("Bạn chưa đăng nhập");
      return;
    }

    setLoading(true);
    try {
      if (isInWatchlist) {
        await removeFromWatchlist(assetSymbol, authUser.token);
        setIsInWatchlist(false);
      } else {
        await addToWatchlist(assetSymbol, authUser.token);
        setIsInWatchlist(true);
      }
    } catch (err: any) {
      console.error("Watchlist update failed:", err.response?.data || err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <button
      onClick={toggleWatchlist}
      className="star-btn"
      aria-pressed={isInWatchlist}
      aria-label={isInWatchlist ? "Remove from watchlist" : "Add to watchlist"}
      disabled={loading}
    >
      {isInWatchlist ? (
        <FaStar className="star-icon filled" />
      ) : (
        <FaRegStar className="star-icon" />
      )}
    </button>
  );
};

export default StarButton;
