package com.automart.service;

import com.automart.model.*;
import com.automart.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    
    @Autowired
    private SparePartRepository sparePartRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private SellRequestRepository sellRequestRepository;

    // Spare Parts
    public List<SparePart> getAllSpareParts() { return sparePartRepository.findAll(); }
    public SparePart saveSparePart(SparePart part) { return sparePartRepository.save(part); }
    public void deleteSparePart(Long id) { sparePartRepository.deleteById(id); }

    // Reviews
    public List<Review> getReviewsForVehicle(Long vehicleId) { return reviewRepository.findByVehicleId(vehicleId); }
    public Review saveReview(Review review) { return reviewRepository.save(review); }

    // Bookings
    public List<Booking> getAllBookings() { return bookingRepository.findAll(); }
    public Booking saveBooking(Booking booking) { return bookingRepository.save(booking); }
    public void deleteBooking(Long id) { bookingRepository.deleteById(id); }

    // Sell Requests
    public List<SellRequest> getAllSellRequests() { return sellRequestRepository.findAll(); }
    public SellRequest saveSellRequest(SellRequest request) { return sellRequestRepository.save(request); }
    public SellRequest getSellRequestById(Long id) { return sellRequestRepository.findById(id).orElse(null); }
}
