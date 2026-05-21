package com.automart.controller;

import com.automart.model.*;
import com.automart.service.ContactMessageService;
import com.automart.service.InventoryService;
import com.automart.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired private VehicleService vehicleService;
    @Autowired private InventoryService inventoryService;
    @Autowired private ContactMessageService contactMessageService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        java.util.List<com.automart.model.SparePart> allParts = inventoryService.getAllSpareParts();
        model.addAttribute("spareParts", allParts.size() > 6 ? allParts.subList(0, 6) : allParts);
        return "index";
    }

    @GetMapping("/vehicles")
    public String vehicleListing(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("vehicles", vehicleService.searchVehicles(search));
        model.addAttribute("search", search);
        return "vehicles";
    }

    @GetMapping("/vehicle/{id}")
    public String vehicleDetails(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle == null) return "redirect:/vehicles";
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("reviews", inventoryService.getReviewsForVehicle(id));
        return "vehicle_details";
    }

    @GetMapping("/sell")
    public String sellVehiclePage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        return "sell_vehicle";
    }

    @PostMapping("/sell")
    public String submitSellRequest(@RequestParam String brand,
                                    @RequestParam String model,
                                    @RequestParam int year,
                                    @RequestParam double expectedPrice,
                                    @RequestParam String details,
                                    HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user instanceof Customer customer) {
            SellRequest req = new SellRequest(brand, model, year, expectedPrice, details, customer);
            inventoryService.saveSellRequest(req);
        }
        ra.addFlashAttribute("success", "Sell request submitted successfully! We'll review it within 24 hours.");
        return "redirect:/sell";
    }

    @GetMapping("/about")
    public String aboutUs() { return "about"; }

    @GetMapping("/contact")
    public String contactUs() { return "contact"; }

    /** POST /contact — save support message to database */
    @PostMapping("/contact")
    public String submitContact(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String subject,
                                @RequestParam String message,
                                HttpSession session, RedirectAttributes ra) {
        User loggedIn = (User) session.getAttribute("loggedInUser");
        ContactMessage msg = new ContactMessage(name, email, subject, message, loggedIn);
        contactMessageService.saveMessage(msg);
        ra.addFlashAttribute("success", "Your message has been sent! We'll reply within 24 hours.");
        return "redirect:/contact";
    }
}
