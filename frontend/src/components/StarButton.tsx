import React, { useState } from "react";
import { FaStar, FaRegStar } from "react-icons/fa";
import axios from "axios";

const StarButton = ({ assetId }: { assetId: string }) => {
  const [isWatched, setIsWatched] = useState(false);

  const toggleWatchlist = async (e: React.MouseEvent) => {
    e.stopPropagation(); // tránh trigger click mở StockDetailPage
    try {
      if (isWatched) {
        await axios.delete(`/api/watchlist/${assetId}`);
      } else {
        await axios.post(`/api/watchlist`, { assetId });
      }
      setIsWatched(!isWatched);
    } catch (err) {
      console.error("Watchlist update failed:", err);
    }
  };

  return (
    <button onClick={toggleWatchlist} className="text-yellow-500">
      {isWatched ? <FaStar /> : <FaRegStar />}
    </button>
  );
};

export default StarButton;
