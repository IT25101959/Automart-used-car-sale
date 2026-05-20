package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.SparePartRepository;
import com.automart.service.InventoryService;
import com.automart.service.FileUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String listParts(Model model) {

        List<SparePart> parts = sparePartRepository.findAll();

        model.addAttribute("spareParts", parts);

        return "spare_parts";
    }

    /** GET /spare-parts/{id} */
    @GetMapping("/details/{id}")
    public String viewPartDetails(@PathVariable Long id, Model model) {

        SparePart part = sparePartRepository.findById(id).orElse(null);

        if (part == null) {
            return "redirect:/spare-parts";
        }

        model.addAttribute("part", part);

        return "spare_part_details";
    }

    /** GET /spare-parts/add */
    @GetMapping("/add")
    public String addPartForm(Model model) {

        model.addAttribute("part", new EnginePart());

        return "spare_part_form";
    }

    /** POST /spare-parts/add */
    @PostMapping("/add")
    public String savePart(
            @RequestParam String name,
            @RequestParam String brand,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam String category,
            @RequestParam(required = false) MultipartFile imageFile,
            RedirectAttributes ra
    ) {

        SparePart part;

        if (category.equalsIgnoreCase("Engine Parts")) {

            part = new EnginePart();

        } else if (category.equalsIgnoreCase("Body Parts")) {

            part = new BodyPart();

        } else {

            part = new EnginePart();
        }

        part.setName(name);
        part.setBrand(brand);
        part.setDescription(description);
        part.setPrice(price);
        part.setCategory(category);

        // Upload image if exists
        if (imageFile != null && !imageFile.isEmpty()) {

            try {

                String imageUrl = fileUploadService.saveFile(imageFile, "spareparts");

                part.setImageUrl(imageUrl);

            } catch (Exception e) {

                ra.addFlashAttribute("error", "Image upload failed.");

                return "redirect:/spare-parts/add";
            }
        }

        // Default stock
        part.setStockQuantity(100);

        sparePartRepository.save(part);

        ra.addFlashAttribute("success", "Spare part added successfully.");

        return "redirect:/spare-parts";
    }

    /** GET /spare-parts/edit/{id} */
    @GetMapping("/edit/{id}")
    public String editPartForm(@PathVariable Long id, Model model) {

        SparePart part = sparePartRepository.findById(id).orElse(null);

        if (part == null) {

            return "redirect:/spare-parts";
        }

        model.addAttribute("part", part);

        return "spare_parts_edit";
    }

    /** POST /spare-parts/update/{id} */
    @PostMapping("/update/{id}")
    public String updatePart(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String brand,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam String category,
            RedirectAttributes ra
    ) {

        SparePart part = sparePartRepository.findById(id).orElse(null);

        if (part == null) {

            return "redirect:/spare-parts";
        }

        part.setName(name);
        part.setBrand(brand);
        part.setDescription(description);
        part.setPrice(price);
        part.setCategory(category);

        sparePartRepository.save(part);

        ra.addFlashAttribute("success", "Spare part updated successfully.");

        return "redirect:/spare-parts";
    }

    /** GET /spare-parts/delete/{id} */
    @GetMapping("/delete/{id}")
    public String deletePart(@PathVariable Long id,
                             RedirectAttributes ra) {

        sparePartRepository.deleteById(id);

        ra.addFlashAttribute("success", "Spare part deleted successfully.");

        return "redirect:/spare-parts";
    }
}