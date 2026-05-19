package com.automart.model;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class SellRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    @Column(name = "vehicle_year")
    private int year;
    private double expectedPrice;
    private String details;
    private String status; // PENDING, APPROVED, REJECTED
    private LocalDateTime requestDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private Customer seller;

    public SellRequest() {}

    public SellRequest(String brand, String model, int year, double expectedPrice, String details, Customer seller) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.expectedPrice = expectedPrice;
        this.details = details;
        this.seller = seller;
        this.status = "PENDING";
        this.requestDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getExpectedPrice() { return expectedPrice; }
    public void setExpectedPrice(double expectedPrice) { this.expectedPrice = expectedPrice; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public Customer getSeller() { return seller; }
    public void setSeller(Customer seller) { this.seller = seller; }
}
