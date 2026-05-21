package com.automart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderName;
    private String senderEmail;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String adminReply;

    // OPEN, RESOLVED
    private String status;

    private LocalDateTime sentAt;
    private LocalDateTime repliedAt;

    // Optional FK to User (null for guest messages)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User sender;

    public ContactMessage() {
        this.status = "OPEN";
        this.sentAt = LocalDateTime.now();
    }

    public ContactMessage(String senderName, String senderEmail, String subject, String message, User sender) {
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.message = message;
        this.sender = sender;
        this.status = "OPEN";
        this.sentAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.repliedAt = repliedAt; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
}
