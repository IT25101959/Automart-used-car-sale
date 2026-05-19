package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    private String address;

    public Customer() {
        super();
    }

    public Customer(String name, String email, String password, String phone, String address) {
        super(name, email, password, phone);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}
