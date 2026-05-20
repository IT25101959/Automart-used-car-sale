package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.SparePartRepository;
import com.automart.service.InventoryService;
import com.automart.service.FileUploadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/spare-parts")
public class SparePartController {

    @Autowired
    private SparePartRepository sparePartRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private FileUploadService fileUploadService;

    /** GET /spare-parts */
    @GetMapping
    public String listParts(@RequestParam(required = false) String search, Model model) {
        List<SparePart> parts;
        if (search != null && !search.isBlank()) {
            parts = sparePartRepository.findAll().stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase())
                            || p.getBrand().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        } else {
            parts = inventoryService.getAllSpareParts();
        }
        model.addAttribute("spareParts", parts);
        model.addAttribute("search", search);
        return "spare_parts";
    }

    /** GET /spare-parts/new — admin add form */
    @GetMapping("/new")
    public String newPartForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        return "spare_parts_form";
    }

    /** POST /spare-parts/new */
    @PostMapping("/new")
    public String createPart(@RequestParam String type,
                             @RequestParam String name,
                             @RequestParam String brand,
                             @RequestParam double price,
                             @RequestParam String description,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             @RequestParam(required = false) String compatibility,
                             @RequestParam(required = false) String color,
                             HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";

        String savedPath = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            savedPath = fileUploadService.saveFile(imageFile, "spareparts");
        } else {
            // Default placeholder
            savedPath = "https://images.unsplash.com/photo-1486006920555-c77dce18193b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80";
        }

        SparePart part;
        if ("ENGINE".equalsIgnoreCase(type)) {
            part = new EnginePart(name, brand, price, description, savedPath, compatibility);
        } else {
            part = new BodyPart(name, brand, price, description, savedPath, color);
        }
        sparePartRepository.save(part);
        ra.addFlashAttribute("success", "Spare part added successfully.");
        return "redirect:/spare-parts";
    }

    /** GET /spare-parts/{id}/edit */
    @GetMapping("/{id}/edit")
    public String editPartForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        SparePart part = sparePartRepository.findById(id).orElse(null);
        if (part == null) return "redirect:/spare-parts";
        model.addAttribute("part", part);
        return "spare_parts_edit";
    }

    /** POST /spare-parts/{id}/edit */
    @PostMapping("/{id}/edit")
    public String updatePart(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam String brand,
                             @RequestParam double price,
                             @RequestParam String description,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        SparePart part = sparePartRepository.findById(id).orElse(null);
        if (part != null) {
            part.setName(name);
            part.setBrand(brand);
            part.setPrice(price);
            part.setDescription(description);
            
            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old local file if replaced
                if (part.getImageUrl() != null && part.getImageUrl().startsWith("/uploads/")) {
                    fileUploadService.deleteFile(part.getImageUrl());
                }
                String savedPath = fileUploadService.saveFile(imageFile, "spareparts");
                part.setImageUrl(savedPath);
            }
            
            sparePartRepository.save(part);
        }
        ra.addFlashAttribute("success", "Part updated.");
        return "redirect:/spare-parts";
    }

    /** GET /spare-parts/{id}/delete */
    @GetMapping("/{id}/delete")
    public String deletePart(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        SparePart part = sparePartRepository.findById(id).orElse(null);
        if (part != null) {
            // Delete local file on removal
            if (part.getImageUrl() != null && part.getImageUrl().startsWith("/uploads/")) {
                fileUploadService.deleteFile(part.getImageUrl());
            }
            sparePartRepository.deleteById(id);
        }
        ra.addFlashAttribute("success", "Part deleted.");
        return "redirect:/spare-parts";
    }
}
