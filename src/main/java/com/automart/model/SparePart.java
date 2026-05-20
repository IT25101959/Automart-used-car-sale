package com.automart.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "part_type", discriminatorType = DiscriminatorType.STRING)
public abstract class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String brand;
    private double price;
    private String description;
    private String imageUrl;

    private int stock = 15;
    private String warranty = "12 Months Warranty";
    private String seller = "AutoMart Premium Seller";

    public SparePart() {}

    public SparePart(String name, String brand, double price, String description, String imageUrl) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public abstract String getCategory();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getWarranty() { return warranty; }
    public void setWarranty(String warranty) { this.warranty = warranty; }

    public String getSeller() { return seller; }
    public void setSeller(String seller) { this.seller = seller; }
}
