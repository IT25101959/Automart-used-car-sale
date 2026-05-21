package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BODY")
public class BodyPart extends SparePart {

    private String color;

    public BodyPart() {}

    public BodyPart(String name, String brand, double price, String description, String imageUrl, String color) {
        super(name, brand, price, description, imageUrl, "Body Parts", 10, "All Standard Vehicles", "6 Months");
        this.color = color;
    }

    public BodyPart(String name, String brand, double price, String description, String imageUrl, int stockQuantity, String compatibility, String warranty, String color) {
        super(name, brand, price, description, imageUrl, "Body Parts", stockQuantity, compatibility, warranty);
        this.color = color;
    }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
