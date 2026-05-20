package com.automart.controller;

import com.automart.model.Payment;
import com.automart.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Payments are managed inline with purchases. This serves as a placeholder for extension.
    @GetMapping
    public String index(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "redirect:/purchases/my-purchases";
    }
}
