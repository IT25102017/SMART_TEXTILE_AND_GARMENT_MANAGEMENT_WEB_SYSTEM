package com.lankatex.smarttextile.customer.repository;

import com.lankatex.smarttextile.customer.entity.CustomerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderItemRepository extends JpaRepository<CustomerOrderItem, Long> {
    List<CustomerOrderItem> findByCustomerOrderId(Long customerOrderId);
    void deleteByCustomerOrderId(Long customerOrderId);
}
