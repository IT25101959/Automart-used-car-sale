package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.SparePartRepository;
import com.automart.service.InventoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/spare-parts")
public class SparePartController {

    @Autowired
    private SparePartRepository sparePartRepository;

    @Autowired
    private InventoryService inventoryService;

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
                             @RequestParam(required = false) String imageUrl,
                             @RequestParam(required = false) String compatibility,
                             @RequestParam(required = false) String color,
                             HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";

        SparePart part;
        if ("ENGINE".equalsIgnoreCase(type)) {
            part = new EnginePart(name, brand, price, description, imageUrl, compatibility);
        } else {
            part = new BodyPart(name, brand, price, description, imageUrl, color);
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
                             @RequestParam(required = false) String imageUrl,
                             HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        SparePart part = sparePartRepository.findById(id).orElse(null);
        if (part != null) {
            part.setName(name);
            part.setBrand(brand);
            part.setPrice(price);
            part.setDescription(description);
            if (imageUrl != null && !imageUrl.isBlank()) part.setImageUrl(imageUrl);
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
        sparePartRepository.deleteById(id);
        ra.addFlashAttribute("success", "Part deleted.");
        return "redirect:/spare-parts";
    }

    /** GET /spare-parts/{id} — view spare part detail page */
    @GetMapping("/{id}")
    public String viewPartDetails(@PathVariable Long id, HttpSession session, Model model) {
        SparePart part = sparePartRepository.findById(id).orElse(null);
        if (part == null) return "redirect:/spare-parts";
        
        model.addAttribute("part", part);
        
        // Pre-fill delivery details if user is logged in
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            model.addAttribute("prefillName", user.getName());
            model.addAttribute("prefillPhone", user.getPhone());
            if (user instanceof Customer) {
                model.addAttribute("prefillAddress", ((Customer) user).getAddress());
            } else {
                model.addAttribute("prefillAddress", "");
            }
        } else {
            model.addAttribute("prefillName", "");
            model.addAttribute("prefillPhone", "");
            model.addAttribute("prefillAddress", "");
        }
        return "spare_part_details";
    }
}
