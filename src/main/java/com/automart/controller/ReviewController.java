package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.ReviewRepository;
import com.automart.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private VehicleService vehicleService;

    /** GET /reviews/new?vehicleId=X */
    @GetMapping("/new")
    public String newReviewForm(@RequestParam Long vehicleId, HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle == null) return "redirect:/vehicles";
        model.addAttribute("vehicle", vehicle);
        return "reviews/form";
    }

    /** POST /reviews/new — submit review */
    @PostMapping("/new")
    public String submitReview(@RequestParam Long vehicleId,
                               @RequestParam String content,
                               @RequestParam int rating,
                               @RequestParam(defaultValue = "false") boolean verified,
                               HttpSession session,
                               RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle == null) return "redirect:/vehicles";

        Review review;
        if (verified) {
            review = new VerifiedReview(content, rating, vehicle, user, "VRF-" + System.currentTimeMillis());
        } else {
            review = new PublicReview(content, rating, vehicle, user);
        }
        reviewRepository.save(review);
        ra.addFlashAttribute("success", "Thank you for your review!");
        return "redirect:/vehicle/" + vehicleId;
    }

    /** GET /reviews/{id}/edit */
    @GetMapping("/{id}/edit")
    public String editReview(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null || (!review.getUser().getId().equals(user.getId()) && !"ADMIN".equals(user.getRole()))) {
            return "redirect:/vehicles";
        }
        model.addAttribute("review", review);
        return "reviews/edit";
    }

    /** POST /reviews/{id}/edit */
    @PostMapping("/{id}/edit")
    public String updateReview(@PathVariable Long id,
                               @RequestParam String content,
                               @RequestParam int rating,
                               HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            review.setContent(content);
            review.setRating(rating);
            reviewRepository.save(review);
            ra.addFlashAttribute("success", "Review updated.");
            return "redirect:/vehicle/" + review.getVehicle().getId();
        }
        return "redirect:/vehicles";
    }

    /** GET /reviews/{id}/delete — admin delete */
    @GetMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null && ("ADMIN".equals(user.getRole()) || review.getUser().getId().equals(user.getId()))) {
            Long vehicleId = review.getVehicle().getId();
            reviewRepository.deleteById(id);
            ra.addFlashAttribute("success", "Review deleted.");
            return "redirect:/vehicle/" + vehicleId;
        }
        return "redirect:/vehicles";
    }
}
