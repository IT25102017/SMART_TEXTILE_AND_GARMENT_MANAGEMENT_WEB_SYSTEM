package com.lankatex.smarttextile.customer.repository;

import com.lankatex.smarttextile.customer.entity.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {
    List<DeliveryItem> findByDeliveryId(Long deliveryId);
    List<DeliveryItem> findByCustomerOrderItemId(Long customerOrderItemId);
    void deleteByDeliveryId(Long deliveryId);
}
