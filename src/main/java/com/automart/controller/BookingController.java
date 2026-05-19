package com.automart.controller;

import com.automart.model.Booking;
import com.automart.model.Customer;
import com.automart.model.User;
import com.automart.model.Vehicle;
import com.automart.repository.BookingRepository;
import com.automart.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleService vehicleService;

    /** GET /bookings — list current user's bookings */
    @GetMapping
    public String myBookings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<Booking> bookings;
        if ("ADMIN".equals(user.getRole())) {
            bookings = bookingRepository.findAll();
        } else {
            bookings = bookingRepository.findByCustomerId(user.getId());
        }
        model.addAttribute("bookings", bookings);
        return "bookings/list";
    }

    /** GET /bookings/new?vehicleId=X — show booking form */
    @GetMapping("/new")
    public String newBookingForm(@RequestParam(required = false) Long vehicleId,
                                 HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        model.addAttribute("selectedVehicleId", vehicleId);
        return "bookings/form";
    }

    /** POST /bookings/new — create booking */
    @PostMapping("/new")
    public String createBooking(@RequestParam Long vehicleId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookingDate,
                                HttpSession session,
                                RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle == null) { ra.addFlashAttribute("error", "Vehicle not found."); return "redirect:/bookings/new"; }

        Booking booking = new Booking(bookingDate, (Customer) user, vehicle);
        bookingRepository.save(booking);
        ra.addFlashAttribute("success", "Booking created! We will confirm shortly.");
        return "redirect:/bookings";
    }

    /** GET /bookings/{id}/edit — edit form */
    @GetMapping("/{id}/edit")
    public String editBookingForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) return "redirect:/bookings";
        model.addAttribute("booking", booking);
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        return "bookings/edit";
    }

    /** POST /bookings/{id}/edit — update booking */
    @PostMapping("/{id}/edit")
    public String updateBooking(@PathVariable Long id,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookingDate,
                                HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            booking.setBookingDate(bookingDate);
            bookingRepository.save(booking);
            ra.addFlashAttribute("success", "Booking updated successfully.");
        }
        return "redirect:/bookings";
    }

    /** GET /bookings/{id}/cancel — cancel booking */
    @GetMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
            ra.addFlashAttribute("success", "Booking cancelled.");
        }
        return "redirect:/bookings";
    }

    /** GET /bookings/{id}/confirm — admin confirms a booking */
    @GetMapping("/{id}/confirm")
    public String confirmBooking(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus("CONFIRMED");
            bookingRepository.save(booking);
            ra.addFlashAttribute("success", "Booking confirmed.");
        }
        return "redirect:/admin";
    }
}
