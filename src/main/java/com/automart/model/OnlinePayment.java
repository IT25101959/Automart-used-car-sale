package com.automart.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ONLINE")
public class OnlinePayment extends Payment {

    private String transactionReference;
    private String gatewayName;

    public OnlinePayment() {
        super();
    }

    public OnlinePayment(String paymentType, String paymentStatus, LocalDateTime transactionDate, String transactionReference, String gatewayName) {
        super();
        this.setPaymentType(paymentType);
        this.setPaymentStatus(paymentStatus);
        this.setTransactionDate(transactionDate);
        this.transactionReference = transactionReference;
        this.gatewayName = gatewayName;
    }

    @Override
    public String getPaymentDetails() {
        return "Paid via " + getPaymentType() + " (Gateway: " + gatewayName + ") — Reference: " + transactionReference;
    }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getGatewayName() { return gatewayName; }
    public void setGatewayName(String gatewayName) { this.gatewayName = gatewayName; }
}
