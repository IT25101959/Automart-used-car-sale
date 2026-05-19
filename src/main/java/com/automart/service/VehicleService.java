package com.automart.service;

import com.automart.model.Vehicle;
import com.automart.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> searchVehicles(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return vehicleRepository.findByBrandContainingIgnoreCaseOrModelContainingIgnoreCase(keyword, keyword);
        }
        return vehicleRepository.findAll();
    }
}
