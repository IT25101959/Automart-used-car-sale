package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TOOL")
public class ToolPart extends SparePart {

    public ToolPart() {}

    public ToolPart(String name, String brand, double price, String description, String imageUrl, String compatibility) {
        super(name, brand, price, description, imageUrl, "Tools", 10, compatibility, "Lifetime");
    }

    public ToolPart(String name, String brand, double price, String description, String imageUrl, int stockQuantity, String compatibility, String warranty) {
        super(name, brand, price, description, imageUrl, "Tools", stockQuantity, compatibility, warranty);
    }
}
