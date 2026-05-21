package com.automart.service;

import com.automart.model.SparePart;
import com.automart.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SparePartService {

    @Autowired
    private SparePartRepository sparePartRepository;

    public List<SparePart> getAllSpareParts() {
        return sparePartRepository.findAll();
    }

    public List<SparePart> getSparePartsByCategory(String category) {
        if (category == null || category.isBlank()) {
            return getAllSpareParts();
        }
        return sparePartRepository.findAll().stream()
                .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                .toList();
    }

    public SparePart getSparePartById(Long id) {
        return sparePartRepository.findById(id).orElse(null);
    }

    public SparePart saveSparePart(SparePart part) {
        return sparePartRepository.save(part);
    }

    public void deleteSparePart(Long id) {
        sparePartRepository.deleteById(id);
    }
}
