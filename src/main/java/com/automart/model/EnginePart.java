package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ENGINE")
public class EnginePart extends SparePart {

    private String compatibility;

    public EnginePart() {}

    public EnginePart(String name, String brand, double price, String description, String imageUrl, String compatibility) {
        super(name, brand, price, description, imageUrl);
        this.compatibility = compatibility;
    }

    public String getCompatibility() { return compatibility; }
    public void setCompatibility(String compatibility) { this.compatibility = compatibility; }

    @Override
    public String getCategory() {
        return "Engine Part";
    }
}
