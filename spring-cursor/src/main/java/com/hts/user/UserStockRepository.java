package com.hts.user;

import com.hts.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStockRepository extends JpaRepository<UserStock, UUID> {
    Optional<UserStock> findByUserAndStock(User user, Stock stock);
    List<UserStock> findByUserId(UUID userId);
} 