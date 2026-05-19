package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TRUCK")
public class Truck extends Vehicle {

    private double payloadCapacity;

    public Truck() {}

    public Truck(String brand, String model, int year, double price, int mileage, String fuelType, String transmission, String imageUrl, double payloadCapacity) {
        super(brand, model, year, price, mileage, fuelType, transmission, imageUrl);
        this.payloadCapacity = payloadCapacity;
    }

    public double getPayloadCapacity() { return payloadCapacity; }
    public void setPayloadCapacity(double payloadCapacity) { this.payloadCapacity = payloadCapacity; }

    @Override
    public String getCategory() {
        return "Truck";
    }
}
