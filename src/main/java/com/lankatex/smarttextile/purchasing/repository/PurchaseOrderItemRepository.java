package com.lankatex.smarttextile.purchasing.repository;

import com.lankatex.smarttextile.purchasing.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
}
