package com.automart.controller;

import com.automart.model.*;
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

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
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
        ra.addFlashAttribute("success", "Your sell request has been submitted! We'll review it shortly.");
        return "redirect:/";
    }

    @GetMapping("/about")
    public String aboutUs() { return "about"; }

    @GetMapping("/contact")
    public String contactUs() { return "contact"; }
}
