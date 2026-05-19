package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.UserRepository;
import com.automart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /** GET /profile — view own profile */
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        // Re-fetch fresh from DB
        user = userService.getUserById(user.getId());
        model.addAttribute("user", user);
        return "profile";
    }

    /** POST /profile/update */
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
