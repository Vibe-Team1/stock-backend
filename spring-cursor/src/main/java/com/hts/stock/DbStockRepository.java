package com.hts.stock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("dbStockRepository")
public interface DbStockRepository extends StockRepository, JpaRepository<Stock, Long> {

    @Override
    @Query("SELECT s FROM Stock s WHERE lower(s.name) LIKE lower(concat('%', :keyword, '%')) OR lower(s.ticker) LIKE lower(concat('%', :keyword, '%'))")
    List<Stock> searchStocks(@Param("keyword") String keyword);

    @Override
    Page<Stock> findAll(Pageable pageable);
} 