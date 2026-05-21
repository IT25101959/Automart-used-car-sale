package com.automart.repository;

import com.automart.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByEmailOrderByPurchaseDateDesc(String email);
    List<Purchase> findByBuyerIdOrderByPurchaseDateDesc(Long buyerId);
    List<Purchase> findAllByOrderByPurchaseDateDesc();
}
