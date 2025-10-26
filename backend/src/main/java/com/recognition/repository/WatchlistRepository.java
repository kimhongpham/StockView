package com.recognition.repository;

import com.recognition.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, UUID> {

    List<Watchlist> findByUserId(UUID userId);

    boolean existsByUserIdAndSymbol(UUID userId, String symbol);

    void deleteByUserIdAndSymbol(UUID userId, String symbol);
}
