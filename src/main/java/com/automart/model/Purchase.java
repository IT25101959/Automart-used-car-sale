package com.automart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "spare_part_id", nullable = false)
    private SparePart sparePart;

    private int quantity;
    private double totalPrice;
    private String paymentMethod;
    
    @Column(length = 500)
    private String deliveryAddress;
    
    private String fullName;
    private String city;
    private String postalCode;
    private String phoneNumber;
    private String deliveryMethod;
    private double deliveryFee;
    
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public Purchase() {}

    public Purchase(User user, SparePart sparePart, int quantity, double totalPrice, String paymentMethod,
                    String deliveryAddress, String fullName, String city, String postalCode, String phoneNumber,
                    String deliveryMethod, double deliveryFee, LocalDateTime orderDate, OrderStatus status) {
        this.user = user;
        this.sparePart = sparePart;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.fullName = fullName;
        this.city = city;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.deliveryMethod = deliveryMethod;
        this.deliveryFee = deliveryFee;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SparePart getSparePart() { return sparePart; }
    public void setSparePart(SparePart sparePart) { this.sparePart = sparePart; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public LocalDateTime getEstimatedDeliveryDate() {
        if (orderDate == null) return null;
        if ("Express".equalsIgnoreCase(deliveryMethod)) {
            return orderDate.plusDays(2);
        } else {
            return orderDate.plusDays(5);
        }
    }
}
