import { useEffect, useState } from "react";
import { FaStar, FaRegStar } from "react-icons/fa";
import { getWatchlist, addToWatchlist, removeFromWatchlist } from "../../utils/api";

interface StarButtonProps {
  assetSymbol: string;
}

const StarButton: React.FC<StarButtonProps> = ({ assetSymbol }) => {
  const [isInWatchlist, setIsInWatchlist] = useState(false);

  // Lấy token từ localStorage
  const getToken = () => localStorage.getItem("token");

  // Load watchlist
  const fetchWatchlist = async () => {
    const token = getToken();
    if (!token) {
      console.warn("User not logged in");
      return;
    }

    try {
      const list = await getWatchlist(token);
      setIsInWatchlist(list.includes(assetSymbol));
    } catch (err: any) {
      console.error("Failed to load watchlist:", err.response?.data || err.message);
    }
  };

  useEffect(() => {
    fetchWatchlist();
  }, [assetSymbol]);

  // Toggle watchlist
  const toggleWatchlist = async () => {
    const token = getToken();
    if (!token) {
      alert("Bạn chưa đăng nhập");
      return;
    }

    try {
      if (isInWatchlist) {
        await removeFromWatchlist(assetSymbol, token);
        setIsInWatchlist(false);
      } else {
        await addToWatchlist(assetSymbol, token);
        setIsInWatchlist(true);
      }
    } catch (err: any) {
      console.error("Watchlist update failed:", err.response?.data || err.message);
    }
  };

  return (
    <button
      onClick={toggleWatchlist}
      className="star-btn"
      aria-pressed={isInWatchlist}
      aria-label={isInWatchlist ? "Remove from watchlist" : "Add to watchlist"}
    >
      {isInWatchlist ? <FaStar className="star-icon filled" /> : <FaRegStar className="star-icon" />}
    </button>
  );
};

export default StarButton;
