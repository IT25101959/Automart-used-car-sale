package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.VehicleRepository;
import com.automart.service.VehicleService;
import com.automart.service.FileUploadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private FileUploadService fileUploadService;

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
                                @RequestParam("imageFile") MultipartFile imageFile,
                                @RequestParam(required = false, defaultValue = "4") int numberOfDoors,
                                @RequestParam(required = false, defaultValue = "false") boolean isFourWheelDrive,
                                @RequestParam(required = false, defaultValue = "0") double payloadCapacity,
                                HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";

        String savedPath = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            savedPath = fileUploadService.saveFile(imageFile, "vehicles");
        } else {
            // Default placeholder
            savedPath = "https://images.unsplash.com/photo-1583121274602-3e2820c69888?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80";
        }

        Vehicle vehicle;
        switch (type.toUpperCase()) {
            case "SUV"   -> vehicle = new SUV(brand, model, year, price, mileage, fuelType, transmission, savedPath, isFourWheelDrive);
            case "TRUCK" -> vehicle = new Truck(brand, model, year, price, mileage, fuelType, transmission, savedPath, payloadCapacity);
            default      -> vehicle = new Car(brand, model, year, price, mileage, fuelType, transmission, savedPath, numberOfDoors);
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
                                @RequestParam("imageFile") MultipartFile imageFile,
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
            
            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old local file if replaced
                if (vehicle.getImageUrl() != null && vehicle.getImageUrl().startsWith("/uploads/")) {
                    fileUploadService.deleteFile(vehicle.getImageUrl());
                }
                String savedPath = fileUploadService.saveFile(imageFile, "vehicles");
                vehicle.setImageUrl(savedPath);
            }
            
            vehicleService.saveVehicle(vehicle);
            ra.addFlashAttribute("success", "Vehicle updated.");
        }
        return "redirect:/admin";
    }

    /** GET /admin/vehicles/{id}/delete */
    @GetMapping("/{id}/delete")
    public String deleteVehicle(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle != null) {
            // Delete local file on removal
            if (vehicle.getImageUrl() != null && vehicle.getImageUrl().startsWith("/uploads/")) {
                fileUploadService.deleteFile(vehicle.getImageUrl());
            }
            vehicleService.deleteVehicle(id);
        }
        ra.addFlashAttribute("success", "Vehicle removed from inventory.");
        return "redirect:/admin";
    }
}
