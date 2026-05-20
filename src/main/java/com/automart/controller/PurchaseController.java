package com.automart.controller;

import com.automart.model.*;
import com.automart.service.PurchaseService;
import com.automart.repository.SparePartRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private SparePartRepository sparePartRepository;

    @PostMapping("/create")
    public String createPurchase(@RequestParam Long partId,
                                 @RequestParam int quantity,
                                 @RequestParam String paymentMethod,
                                 @RequestParam String fullName,
                                 @RequestParam String address,
                                 @RequestParam String city,
                                 @RequestParam String postalCode,
                                 @RequestParam String phone,
                                 @RequestParam String deliveryMethod,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            ra.addFlashAttribute("error", "Please login to purchase spare parts.");
            return "redirect:/login";
        }

        SparePart part = sparePartRepository.findById(partId).orElse(null);
        if (part == null) {
            ra.addFlashAttribute("error", "Selected spare part not found.");
            return "redirect:/spare-parts";
        }

        // --- VALIDATION (FEATURE 11) ---
        if (quantity <= 0) {
            ra.addFlashAttribute("error", "Quantity must be greater than zero.");
            return "redirect:/spare-parts/" + partId;
        }
        if (quantity > part.getStock()) {
            ra.addFlashAttribute("error", "Only " + part.getStock() + " units available in stock.");
            return "redirect:/spare-parts/" + partId;
        }
        if (fullName == null || fullName.isBlank() ||
            address == null || address.isBlank() ||
            city == null || city.isBlank() ||
            postalCode == null || postalCode.isBlank() ||
            phone == null || phone.isBlank()) {
            ra.addFlashAttribute("error", "All delivery fields are required.");
            return "redirect:/spare-parts/" + partId;
        }
        // Phone number format validation (9 to 15 digits)
        if (!phone.matches("^\\+?[0-9]{9,15}$")) {
            ra.addFlashAttribute("error", "Invalid phone number format. Please provide a valid number.");
            return "redirect:/spare-parts/" + partId;
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            ra.addFlashAttribute("error", "Please select a payment method.");
            return "redirect:/spare-parts/" + partId;
        }

        // Calculate pricing
        double subtotal = part.getPrice() * quantity;
        double deliveryFee = "Express".equalsIgnoreCase(deliveryMethod) ? 750.0 : 350.0;
        double totalPrice = subtotal + deliveryFee;

        // Compile full address
        String compiledAddress = fullName + ", " + address + ", " + city + " - " + postalCode + " (Phone: " + phone + ")";

        // Create Purchase
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setSparePart(part);
        purchase.setQuantity(quantity);
        purchase.setTotalPrice(totalPrice);
        purchase.setPaymentMethod(paymentMethod);
        purchase.setDeliveryAddress(compiledAddress);
        purchase.setFullName(fullName);
        purchase.setCity(city);
        purchase.setPostalCode(postalCode);
        purchase.setPhoneNumber(phone);
        purchase.setDeliveryMethod(deliveryMethod);
        purchase.setDeliveryFee(deliveryFee);
        purchase.setOrderDate(LocalDateTime.now());
        purchase.setStatus(OrderStatus.PENDING);

        // --- POLYMORPHIC PAYMENT INITIALIZATION (FEATURE 12) ---
        Payment payment;
        if ("Cash on Delivery".equalsIgnoreCase(paymentMethod)) {
            payment = new CODPayment(
                    "Cash on Delivery",
                    "PENDING",
                    LocalDateTime.now(),
                    "Collect LKR " + totalPrice + " upon delivery to: " + city
            );
        } else {
            // Credit Card, Debit Card, Bank Transfer are processed online
            String randomTxnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment = new OnlinePayment(
                    paymentMethod,
                    "COMPLETED",
                    LocalDateTime.now(),
                    randomTxnRef,
                    "AutoMart E-Secure Gate"
            );
        }
        purchase.setPayment(payment);

        try {
            Purchase savedPurchase = purchaseService.createPurchase(purchase);
            ra.addFlashAttribute("success", "Your order has been placed successfully!");
            return "redirect:/purchases/confirmation/" + savedPurchase.getId();
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/spare-parts/" + partId;
        }
    }

    @GetMapping("/confirmation/{id}")
    public String viewConfirmation(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Purchase purchase = purchaseService.getPurchaseById(id);
        if (purchase == null) {
            ra.addFlashAttribute("error", "Order not found.");
            return "redirect:/spare-parts";
        }

        // Safety check: ensure customers can only view their own purchases
        if (!"ADMIN".equals(user.getRole()) && !purchase.getUser().getId().equals(user.getId())) {
            ra.addFlashAttribute("error", "Access denied.");
            return "redirect:/spare-parts";
        }

        // Generate dummy order number
        String orderNumber = "AM-" + String.format("%06d", purchase.getId());
        model.addAttribute("purchase", purchase);
        model.addAttribute("orderNumber", orderNumber);
        return "purchase-confirmation";
    }

    @GetMapping("/my-purchases")
    public String myPurchases(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<Purchase> purchases = purchaseService.getPurchasesByUser(user.getId());
        model.addAttribute("purchases", purchases);
        return "purchase-history";
    }

    @GetMapping("/{id}/cancel")
    public String cancelPurchase(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Purchase purchase = purchaseService.getPurchaseById(id);
        if (purchase == null) {
            ra.addFlashAttribute("error", "Order not found.");
            return "redirect:/purchases/my-purchases";
        }

        if (!"ADMIN".equals(user.getRole()) && !purchase.getUser().getId().equals(user.getId())) {
            ra.addFlashAttribute("error", "Access denied.");
            return "redirect:/purchases/my-purchases";
        }

        boolean cancelled = purchaseService.cancelPurchase(id);
        if (cancelled) {
            ra.addFlashAttribute("success", "Order cancelled successfully and stock restored.");
        } else {
            ra.addFlashAttribute("error", "Order cannot be cancelled. It may have already been shipped or processed.");
        }
        return "redirect:/purchases/my-purchases";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status);
            purchaseService.updateStatus(id, newStatus);
            ra.addFlashAttribute("success", "Order status updated successfully.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", "Invalid order status value.");
        }
        return "redirect:/admin";
    }
}
