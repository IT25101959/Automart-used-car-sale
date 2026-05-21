package com.automart.service;

import com.automart.model.ContactMessage;
import com.automart.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    /** Save a new contact message */
    public ContactMessage saveMessage(ContactMessage message) {
        return contactMessageRepository.save(message);
    }

    /** Get all messages ordered by newest first (for admin) */
    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAllByOrderBySentAtDesc();
    }

    /** Get messages for a specific user (for profile page) */
    public List<ContactMessage> getMessagesByUserId(Long userId) {
        return contactMessageRepository.findBySenderIdOrderBySentAtDesc(userId);
    }

    /** Get messages by sender email (fallback for guest lookup) */
    public List<ContactMessage> getMessagesByEmail(String email) {
        return contactMessageRepository.findBySenderEmailOrderBySentAtDesc(email);
    }

    /** Find a single message by ID */
    public ContactMessage getById(Long id) {
        return contactMessageRepository.findById(id).orElse(null);
    }

    /** Admin replies to a message and marks it RESOLVED */
    public ContactMessage replyToMessage(Long id, String replyText) {
        ContactMessage msg = getById(id);
        if (msg != null) {
            msg.setAdminReply(replyText);
            msg.setRepliedAt(LocalDateTime.now());
            msg.setStatus("RESOLVED");
            return contactMessageRepository.save(msg);
        }
        return null;
    }

    /** Mark message as resolved without a reply */
    public void markResolved(Long id) {
        ContactMessage msg = getById(id);
        if (msg != null) {
            msg.setStatus("RESOLVED");
            contactMessageRepository.save(msg);
        }
    }
}
