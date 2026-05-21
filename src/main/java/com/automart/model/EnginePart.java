package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ENGINE")
public class EnginePart extends SparePart {

    public EnginePart() {}

    public EnginePart(String name, String brand, double price, String description, String imageUrl, String compatibility) {
        super(name, brand, price, description, imageUrl, "Engine Parts", 10, compatibility, "12 Months");
    }

    public EnginePart(String name, String brand, double price, String description, String imageUrl, int stockQuantity, String compatibility, String warranty) {
        super(name, brand, price, description, imageUrl, "Engine Parts", stockQuantity, compatibility, warranty);
    }
}
