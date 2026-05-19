package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {

    private int numberOfDoors;

    public Car() {}

    public Car(String brand, String model, int year, double price, int mileage, String fuelType, String transmission, String imageUrl, int numberOfDoors) {
        super(brand, model, year, price, mileage, fuelType, transmission, imageUrl);
        this.numberOfDoors = numberOfDoors;
    }

    public int getNumberOfDoors() { return numberOfDoors; }
    public void setNumberOfDoors(int numberOfDoors) { this.numberOfDoors = numberOfDoors; }

    @Override
    public String getCategory() {
        return "Car";
    }
}
