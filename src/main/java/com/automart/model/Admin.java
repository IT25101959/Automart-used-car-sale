package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    private String adminLevel;

    public Admin() {
        super();
    }

    public Admin(String name, String email, String password, String phone, String adminLevel) {
        super(name, email, password, phone);
        this.adminLevel = adminLevel;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
