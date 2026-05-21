package com.automart.repository;

import com.automart.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByBrandContainingIgnoreCaseOrModelContainingIgnoreCase(String brand, String model);
}
