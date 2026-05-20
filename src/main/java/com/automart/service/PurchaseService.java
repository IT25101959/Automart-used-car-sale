package com.automart.service;

import com.automart.model.*;
import com.automart.repository.PurchaseRepository;
import com.automart.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Transactional
    public Purchase createPurchase(Purchase purchase) {
        SparePart part = purchase.getSparePart();
        if (part == null) {
            throw new IllegalArgumentException("Spare part not found.");
        }
        if (part.getStock() < purchase.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock available (" + part.getStock() + " units in stock).");
        }
        
        // Deduct stock
        part.setStock(part.getStock() - purchase.getQuantity());
        sparePartRepository.save(part);

        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getPurchasesByUser(Long userId) {
        return purchaseRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Transactional
    public Purchase updateStatus(Long id, OrderStatus status) {
        Purchase purchase = getPurchaseById(id);
        if (purchase != null) {
            // If the order transitions from a non-CANCELLED state to CANCELLED, restore stock
            if (status == OrderStatus.CANCELLED && purchase.getStatus() != OrderStatus.CANCELLED) {
                SparePart part = purchase.getSparePart();
                part.setStock(part.getStock() + purchase.getQuantity());
                sparePartRepository.save(part);
            }
            purchase.setStatus(status);
            return purchaseRepository.save(purchase);
        }
        return null;
    }

    @Transactional
    public boolean cancelPurchase(Long id) {
        Purchase purchase = getPurchaseById(id);
        if (purchase != null && purchase.getStatus() == OrderStatus.PENDING) {
            updateStatus(id, OrderStatus.CANCELLED);
            return true;
        }
        return false;
    }
}
