package com.automart.repository;

import com.automart.model.SellRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SellRequestRepository extends JpaRepository<SellRequest, Long> {
    List<SellRequest> findBySellerIdOrderByRequestDateDesc(Long sellerId);
}
