package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.VehicleRepository;
import com.automart.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }

    /** GET /admin/vehicles/new — show add vehicle form */
    @GetMapping("/new")
    public String newVehicleForm(HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        return "vehicle_form";
    }

    /** POST /admin/vehicles/new — create vehicle */
    @PostMapping("/new")
    public String createVehicle(@RequestParam String type,
                                @RequestParam String brand,
                                @RequestParam String model,
                                @RequestParam int year,
                                @RequestParam double price,
                                @RequestParam int mileage,
                                @RequestParam String fuelType,
                                @RequestParam String transmission,
                                @RequestParam(required = false) String imageUrl,
                                @RequestParam(required = false, defaultValue = "4") int numberOfDoors,
                                @RequestParam(required = false, defaultValue = "false") boolean isFourWheelDrive,
                                @RequestParam(required = false, defaultValue = "0") double payloadCapacity,
                                HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";

        Vehicle vehicle;
        switch (type.toUpperCase()) {
            case "SUV"   -> vehicle = new SUV(brand, model, year, price, mileage, fuelType, transmission, imageUrl, isFourWheelDrive);
            case "TRUCK" -> vehicle = new Truck(brand, model, year, price, mileage, fuelType, transmission, imageUrl, payloadCapacity);
            default      -> vehicle = new Car(brand, model, year, price, mileage, fuelType, transmission, imageUrl, numberOfDoors);
        }
        vehicleService.saveVehicle(vehicle);
        ra.addFlashAttribute("success", "Vehicle added to inventory.");
        return "redirect:/admin";
    }

    /** GET /admin/vehicles/{id}/edit */
    @GetMapping("/{id}/edit")
    public String editVehicleForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle == null) return "redirect:/admin";
        model.addAttribute("vehicle", vehicle);
        return "vehicle_edit";
    }

    /** POST /admin/vehicles/{id}/edit */
    @PostMapping("/{id}/edit")
    public String updateVehicle(@PathVariable Long id,
                                @RequestParam String brand,
                                @RequestParam String model,
                                @RequestParam int year,
                                @RequestParam double price,
                                @RequestParam int mileage,
                                @RequestParam String fuelType,
                                @RequestParam String transmission,
                                @RequestParam(required = false) String imageUrl,
                                HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle != null) {
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setYear(year);
            vehicle.setPrice(price);
            vehicle.setMileage(mileage);
            vehicle.setFuelType(fuelType);
            vehicle.setTransmission(transmission);
            if (imageUrl != null && !imageUrl.isBlank()) vehicle.setImageUrl(imageUrl);
            vehicleService.saveVehicle(vehicle);
            ra.addFlashAttribute("success", "Vehicle updated.");
        }
        return "redirect:/admin";
    }

    /** GET /admin/vehicles/{id}/delete */
    @GetMapping("/{id}/delete")
    public String deleteVehicle(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        vehicleService.deleteVehicle(id);
        ra.addFlashAttribute("success", "Vehicle removed from inventory.");
        return "redirect:/admin";
    }
}
