package com.automart.service;

import com.automart.model.Purchase;
import com.automart.model.SparePart;
import com.automart.repository.PurchaseRepository;
import com.automart.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Transactional
    public Purchase placeOrder(Purchase purchase) {
        SparePart part = sparePartRepository.findById(purchase.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product selection"));

        if (part.getStockQuantity() < purchase.getQuantity()) {
            throw new IllegalStateException("Insufficient stock available!");
        }

        // Decrement stock
        part.setStockQuantity(part.getStockQuantity() - purchase.getQuantity());
        sparePartRepository.save(part);

        // Update purchase details
        purchase.setProduct(part);
        // Let's compute delivery fee (e.g. standard flat fee of $15 or free above $150 or similar, but the user requested:
        // "details page with large images, dynamic quantity, and shipping calculators, plus shipping forms and order database persistence."
        // We'll compute checkout totals in the UI dynamically. In the backend, we calculate standard total cost:
        // totalCost = (price * quantity) + deliveryFee. Let's make delivery fee $15 flat or $0 if total is > $200. Or we can just sum whatever cost is passed or calculate it.
        // Let's calculate: base = part.getPrice() * purchase.getQuantity(); delivery = base > 200 ? 0.0 : 15.0;
        // purchase.setTotalCost(base + delivery);
        double base = part.getPrice() * purchase.getQuantity();
        double delivery = base > 200 ? 0.0 : 15.0;
        purchase.setTotalCost(base + delivery);
        
        purchase.setPurchaseDate(LocalDateTime.now());
        if (purchase.getStatus() == null) {
            purchase.setStatus("CONFIRMED");
        }

        return purchaseRepository.save(purchase);
    }

    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    public List<Purchase> getPurchasesByEmail(String email) {
        if (email == null || email.isBlank()) {
            return List.of();
        }
        return purchaseRepository.findByEmailOrderByPurchaseDateDesc(email);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAllByOrderByPurchaseDateDesc();
    }

    /** Get purchases for a specific logged-in user (by buyer FK) */
    public List<Purchase> getPurchasesByBuyerId(Long buyerId) {
        return purchaseRepository.findByBuyerIdOrderByPurchaseDateDesc(buyerId);
    }

    /** Admin: update purchase status */
    public Purchase updateStatus(Long id, String status) {
        Purchase purchase = purchaseRepository.findById(id).orElse(null);
        if (purchase != null) {
            purchase.setStatus(status);
            return purchaseRepository.save(purchase);
        }
        return null;
    }

    /** Admin: reject a purchase with a reason */
    public Purchase rejectPurchase(Long id, String reason) {
        Purchase purchase = purchaseRepository.findById(id).orElse(null);
        if (purchase != null) {
            purchase.setStatus("REJECTED");
            purchase.setRejectionReason(reason);
            // Restore stock on rejection
            SparePart part = purchase.getProduct();
            if (part != null) {
                part.setStockQuantity(part.getStockQuantity() + purchase.getQuantity());
                sparePartRepository.save(part);
            }
            return purchaseRepository.save(purchase);
        }
        return null;
    }
}
