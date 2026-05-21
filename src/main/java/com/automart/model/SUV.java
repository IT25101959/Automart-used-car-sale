package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUV")
public class SUV extends Vehicle {

    private boolean isFourWheelDrive;

    public SUV() {}

    public SUV(String brand, String model, int year, double price, int mileage, String fuelType, String transmission, String imageUrl, boolean isFourWheelDrive) {
        super(brand, model, year, price, mileage, fuelType, transmission, imageUrl);
        this.isFourWheelDrive = isFourWheelDrive;
    }

    public boolean isFourWheelDrive() { return isFourWheelDrive; }
    public void setFourWheelDrive(boolean fourWheelDrive) { isFourWheelDrive = fourWheelDrive; }

    @Override
    public String getCategory() {
        return "SUV";
    }
}
