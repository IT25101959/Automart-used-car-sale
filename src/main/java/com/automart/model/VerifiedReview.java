package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VERIFIED")
public class VerifiedReview extends Review {

    private String purchaseId;

    public VerifiedReview() {}

    public VerifiedReview(String content, int rating, Vehicle vehicle, User user, String purchaseId) {
        super(content, rating, vehicle, user);
        this.purchaseId = purchaseId;
    }

    public String getPurchaseId() { return purchaseId; }
    public void setPurchaseId(String purchaseId) { this.purchaseId = purchaseId; }

    @Override
    public String getReviewType() {
        return "Verified Buyer";
    }
}
