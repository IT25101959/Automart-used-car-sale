package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TYRE")
public class TyrePart extends SparePart {

    public TyrePart() {}

    public TyrePart(String name, String brand, double price, String description, String imageUrl, String compatibility) {
        super(name, brand, price, description, imageUrl, "Tyres", 10, compatibility, "24 Months");
    }

    public TyrePart(String name, String brand, double price, String description, String imageUrl, int stockQuantity, String compatibility, String warranty) {
        super(name, brand, price, description, imageUrl, "Tyres", stockQuantity, compatibility, warranty);
    }
}
