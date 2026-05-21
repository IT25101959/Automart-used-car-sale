package com.automart.controller;

import com.automart.model.Purchase;
import com.automart.model.SparePart;
import com.automart.model.User;
import com.automart.service.PurchaseService;
import com.automart.service.SparePartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private SparePartService sparePartService;

    @PostMapping("/spare-parts/purchase")
    public String processPurchase(@RequestParam Long productId,
                                  @RequestParam int quantity,
                                  @RequestParam String customerName,
                                  @RequestParam String email,
                                  @RequestParam String phone,
                                  @RequestParam String address,
                                  @RequestParam String city,
                                  @RequestParam String postalCode,
                                  @RequestParam String paymentMethod,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        
        SparePart part = sparePartService.getSparePartById(productId);
        if (part == null) {
            ra.addFlashAttribute("error", "The selected spare part was not found.");
            return "redirect:/spare-parts";
        }

        if (part.getStockQuantity() < quantity) {
            ra.addFlashAttribute("error", "Insufficient stock available! Only " + part.getStockQuantity() + " items left.");
            return "redirect:/spare-parts/" + productId;
        }

        Purchase purchase = new Purchase();
        purchase.setProduct(part);
        purchase.setQuantity(quantity);
        purchase.setCustomerName(customerName);
        purchase.setEmail(email);
        purchase.setPhone(phone);
        purchase.setAddress(address);
        purchase.setCity(city);
        purchase.setPostalCode(postalCode);
        purchase.setPaymentMethod(paymentMethod);
        // Link to logged-in user if available
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) purchase.setBuyer(loggedInUser);
        // Starts as PENDING — admin must approve
        purchase.setStatus("PENDING");

        try {
            Purchase savedPurchase = purchaseService.placeOrder(purchase);
            ra.addFlashAttribute("success", "Purchase successful!");
            return "redirect:/spare-parts/purchase/confirmation/" + savedPurchase.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error placing order: " + e.getMessage());
            return "redirect:/spare-parts/" + productId;
        }
    }

    @GetMapping("/spare-parts/purchase/confirmation/{id}")
    public String showConfirmation(@PathVariable Long id, Model model) {
        Purchase purchase = purchaseService.getPurchaseById(id);
        if (purchase == null) {
            return "redirect:/spare-parts";
        }
        model.addAttribute("purchase", purchase);
        return "purchase_confirmation";
    }

    @GetMapping("/spare-parts/orders")
    public String myOrders(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        List<Purchase> purchases = purchaseService.getPurchasesByEmail(loggedInUser.getEmail());
        model.addAttribute("orders", purchases);
        return "my_orders";
    }

    @GetMapping("/orders/my")
    public String myOrdersRedirect(HttpSession session) {
        return "redirect:/spare-parts/orders";
    }
}
