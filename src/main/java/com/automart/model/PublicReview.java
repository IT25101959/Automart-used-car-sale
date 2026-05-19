package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PUBLIC")
public class PublicReview extends Review {

    public PublicReview() {}

    public PublicReview(String content, int rating, Vehicle vehicle, User user) {
        super(content, rating, vehicle, user);
    }

    @Override
    public String getReviewType() {
        return "Public";
    }
}
