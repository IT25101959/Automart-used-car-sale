package com.automart.controller;

import com.automart.model.User;
import com.automart.model.Customer;
import com.automart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        User user = userService.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin";
            }
            return "redirect:/";
        }
        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(Customer customer, Model model) {
        if (userService.getUserByEmail(customer.getEmail()) != null) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }
        userService.saveUser(customer);
        return "redirect:/login?registered";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
