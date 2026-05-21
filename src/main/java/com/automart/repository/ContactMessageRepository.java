package com.automart.repository;

import com.automart.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findBySenderEmailOrderBySentAtDesc(String email);
    List<ContactMessage> findBySenderIdOrderBySentAtDesc(Long userId);
    List<ContactMessage> findAllByOrderBySentAtDesc();
}
