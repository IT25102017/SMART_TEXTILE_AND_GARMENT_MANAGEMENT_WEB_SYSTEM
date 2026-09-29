package com.lankatex.smarttextile.customer.repository;


import com.lankatex.smarttextile.customer.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByOrderIdOrderByDeliveryDateAsc(Long orderId);
}
