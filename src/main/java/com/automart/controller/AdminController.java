package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.*;
import com.automart.service.InventoryService;
import com.automart.service.UserService;
import com.automart.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private VehicleService vehicleService;
    @Autowired private InventoryService inventoryService;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ReviewRepository reviewRepository;

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("users",        userService.getAllUsers());
        model.addAttribute("vehicles",     vehicleService.getAllVehicles());
        model.addAttribute("sellRequests", inventoryService.getAllSellRequests());
        model.addAttribute("bookings",     bookingRepository.findAll());
        model.addAttribute("reviews",      reviewRepository.findAll());
        model.addAttribute("spareParts",   inventoryService.getAllSpareParts());
        return "admin_dashboard";
    }

    @PostMapping("/sellrequest/update/{id}")
    public String updateSellRequest(@PathVariable Long id,
                                    @RequestParam String status,
                                    HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        SellRequest request = inventoryService.getSellRequestById(id);
        if (request != null) {
            request.setStatus(status);
            inventoryService.saveSellRequest(request);

            // Auto-create a Car listing when approved
            if ("APPROVED".equals(status)) {
                Car newCar = new Car(request.getBrand(), request.getModel(),
                        request.getYear(), request.getExpectedPrice(),
                        0, "Petrol", "Manual",
                        "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        4);
                newCar.setOwner(request.getSeller());
                vehicleService.saveVehicle(newCar);
                ra.addFlashAttribute("success", "Request APPROVED — vehicle added to inventory.");
            } else {
                ra.addFlashAttribute("success", "Request status updated to " + status + ".");
            }
        }
        return "redirect:/admin";
    }
}
