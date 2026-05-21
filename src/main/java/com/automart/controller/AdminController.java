package com.automart.controller;

import com.automart.model.*;
import com.automart.repository.*;
import com.automart.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private VehicleService vehicleService;
    @Autowired private InventoryService inventoryService;
    @Autowired private PurchaseService purchaseService;
    @Autowired private ContactMessageService contactMessageService;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ReviewRepository reviewRepository;

    /** Check that session user is an admin */
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }

    /** Main admin dashboard — loads only count data for the 6 category cards */
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("users",        userService.getAllUsers());
        model.addAttribute("vehicles",     vehicleService.getAllVehicles());
        model.addAttribute("sellRequests", inventoryService.getAllSellRequests());
        model.addAttribute("bookings",     bookingRepository.findAll());
        model.addAttribute("reviews",      reviewRepository.findAll());
        model.addAttribute("spareParts",   inventoryService.getAllSpareParts());
        model.addAttribute("purchases",    purchaseService.getAllPurchases());
        model.addAttribute("messages",     contactMessageService.getAllMessages());
        return "admin_dashboard";
    }

    /** Sub-page: Vehicle Inventory */
    @GetMapping("/inventory")
    public String inventory(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        return "admin_inventory";
    }

    /** Sub-page: Spare Parts + Orders */
    @GetMapping("/spare-parts")
    public String spareParts(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("spareParts", inventoryService.getAllSpareParts());
        model.addAttribute("purchases",  purchaseService.getAllPurchases());
        return "admin_spare_parts";
    }

    /** Sub-page: Reviews + Support Messages */
    @GetMapping("/reviews")
    public String reviews(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("reviews",  reviewRepository.findAll());
        model.addAttribute("messages", contactMessageService.getAllMessages());
        return "admin_reviews";
    }

    /** Sub-page: Bookings Schedule */
    @GetMapping("/bookings")
    public String bookings(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("bookings", bookingRepository.findAll());
        return "admin_bookings";
    }

    /** Sub-page: Sell Requests */
    @GetMapping("/sell-requests")
    public String sellRequests(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("sellRequests", inventoryService.getAllSellRequests());
        return "admin_sell_requests";
    }

    // ─── SELL REQUEST MANAGEMENT ─────────────────────────────────────────────

    /** Approve or reject a sell request; supports optional rejection reason */
    @PostMapping("/sellrequest/update/{id}")
    public String updateSellRequest(@PathVariable Long id,
                                    @RequestParam String status,
                                    @RequestParam(required = false) String rejectionReason,
                                    HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        SellRequest request = inventoryService.getSellRequestById(id);
        if (request != null) {
            request.setStatus(status);
            if ("REJECTED".equals(status) && rejectionReason != null && !rejectionReason.isBlank()) {
                request.setRejectionReason(rejectionReason);
            }
            inventoryService.saveSellRequest(request);

            if ("APPROVED".equals(status)) {
                Car newCar = new Car(request.getBrand(), request.getModel(),
                        request.getYear(), request.getExpectedPrice(),
                        0, "Petrol", "Manual",
                        "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                        4);
                newCar.setOwner(request.getSeller());
                vehicleService.saveVehicle(newCar);
                ra.addFlashAttribute("success", "Request APPROVED — vehicle added to inventory.");
            } else {
                ra.addFlashAttribute("success", "Sell request updated to: " + status);
            }
        }
        return "redirect:/admin/sell-requests";
    }

    // ─── PURCHASE ORDER MANAGEMENT ───────────────────────────────────────────

    /** Change purchase status (Accept, Ship, Deliver, etc.) */
    @PostMapping("/purchase/status/{id}")
    public String updatePurchaseStatus(@PathVariable Long id,
                                       @RequestParam String status,
                                       HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        purchaseService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Order #" + id + " status updated to: " + status);
        return "redirect:/admin/spare-parts";
    }

    /** Reject a purchase with a reason — restores stock automatically */
    @PostMapping("/purchase/reject/{id}")
    public String rejectPurchase(@PathVariable Long id,
                                 @RequestParam String rejectionReason,
                                 HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        purchaseService.rejectPurchase(id, rejectionReason);
        ra.addFlashAttribute("success", "Order #" + id + " has been rejected. Stock restored.");
        return "redirect:/admin/spare-parts";
    }

    // ─── CONTACT MESSAGE MANAGEMENT ──────────────────────────────────────────

    /** Admin replies to a customer support message */
    @PostMapping("/messages/reply/{id}")
    public String replyToMessage(@PathVariable Long id,
                                 @RequestParam String reply,
                                 HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        contactMessageService.replyToMessage(id, reply);
        ra.addFlashAttribute("success", "Reply sent and message marked as Resolved.");
        return "redirect:/admin/reviews";
    }

    /** Mark a message resolved without reply */
    @PostMapping("/messages/resolve/{id}")
    public String resolveMessage(@PathVariable Long id,
                                 HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        contactMessageService.markResolved(id);
        ra.addFlashAttribute("success", "Message marked as Resolved.");
        return "redirect:/admin/reviews";
    }

    // ─── USER MANAGEMENT ─────────────────────────────────────────────────────

    /** Ban a user account */
    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        userService.banUser(id);
        ra.addFlashAttribute("success", "User banned successfully.");
        return "redirect:/admin/users";
    }

    /** Unban a user account */
    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        userService.unbanUser(id);
        ra.addFlashAttribute("success", "User unbanned successfully.");
        return "redirect:/admin/users";
    }
}
