package com.automart.repository;

import com.automart.model.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long> {
    List<SparePart> findByCategoryIgnoreCase(String category);
    List<SparePart> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    List<SparePart> findByCategoryIgnoreCaseAndNameContainingIgnoreCase(String category, String name);
}
