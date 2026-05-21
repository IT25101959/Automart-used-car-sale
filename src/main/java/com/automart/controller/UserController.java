package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.*;
import com.automart.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired private UserService userService;
    @Autowired private PurchaseService purchaseService;
    @Autowired private ContactMessageService contactMessageService;
    @Autowired private FileUploadService fileUploadService;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private SellRequestRepository sellRequestRepository;

    /** GET /profile — load all tabs data for the full profile page */
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        user = userService.getUserById(user.getId());

        model.addAttribute("user", user);
        // Purchases tab
        model.addAttribute("purchases", purchaseService.getPurchasesByBuyerId(user.getId()));
        // Bookings tab
        model.addAttribute("bookings", bookingRepository.findByCustomerId(user.getId()));
        // Sell requests tab
        model.addAttribute("sellRequests", sellRequestRepository.findBySellerIdOrderByRequestDateDesc(user.getId()));
        // Reviews tab
        model.addAttribute("reviews", reviewRepository.findByUserId(user.getId()));
        // Messages tab
        model.addAttribute("messages", contactMessageService.getMessagesByUserId(user.getId()));
        return "profile";
    }

    /** POST /profile/update — update basic info */
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String phone,
                                @RequestParam(required = false) String address,
                                HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        User dbUser = userService.getUserById(user.getId());
        if (dbUser != null) {
            dbUser.setName(name);
            dbUser.setPhone(phone);
            if (dbUser instanceof Customer c && address != null) c.setAddress(address);
            userService.saveUser(dbUser);
            session.setAttribute("loggedInUser", dbUser);
            ra.addFlashAttribute("success", "Profile updated successfully.");
        }
        return "redirect:/profile";
    }

    /** POST /profile/change-password */
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("pwError", "New passwords do not match.");
            return "redirect:/profile";
        }
        if (newPassword.length() < 6) {
            ra.addFlashAttribute("pwError", "Password must be at least 6 characters.");
            return "redirect:/profile";
        }
        boolean changed = userService.changePassword(user.getId(), currentPassword, newPassword);
        if (changed) {
            ra.addFlashAttribute("success", "Password changed successfully.");
        } else {
            ra.addFlashAttribute("pwError", "Current password is incorrect.");
        }
        return "redirect:/profile";
    }

    /** POST /profile/upload-image — upload profile picture */
    @PostMapping("/profile/upload-image")
    public String uploadProfileImage(@RequestParam MultipartFile imageFile,
                                     HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        if (imageFile == null || imageFile.isEmpty()) {
            ra.addFlashAttribute("error", "Please select an image file.");
            return "redirect:/profile";
        }
        try {
            String imageUrl = fileUploadService.saveFile(imageFile, "profiles");
            User dbUser = userService.getUserById(user.getId());
            if (dbUser != null) {
                dbUser.setProfileImage(imageUrl);
                userService.saveUser(dbUser);
                session.setAttribute("loggedInUser", dbUser);
            }
            ra.addFlashAttribute("success", "Profile picture updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Image upload failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    /** GET /admin/users — admin view all users */
    @GetMapping("/admin/users")
    public String listUsers(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        model.addAttribute("users", userService.getAllUsers());
        return "admin_users";
    }

    /** GET /admin/users/{id}/delete */
    @GetMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }
}
