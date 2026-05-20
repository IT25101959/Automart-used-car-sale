package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("COD")
public class CODPayment extends Payment {

    private String deliveryInstructions;

    public CODPayment() {
        super();
    }

    public CODPayment(String paymentType, String paymentStatus, LocalDateTime transactionDate, String deliveryInstructions) {
        super();
        this.setPaymentType(paymentType);
        this.setPaymentStatus(paymentStatus);
        this.setTransactionDate(transactionDate);
        this.deliveryInstructions = deliveryInstructions;
    }

    @Override
    public String getPaymentDetails() {
        return "Cash on Delivery — Instructions: " + (deliveryInstructions != null && !deliveryInstructions.isBlank() ? deliveryInstructions : "None");
    }

    public String getDeliveryInstructions() { return deliveryInstructions; }
    public void setDeliveryInstructions(String deliveryInstructions) { this.deliveryInstructions = deliveryInstructions; }
}
