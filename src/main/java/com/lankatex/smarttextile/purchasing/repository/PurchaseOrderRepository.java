package com.lankatex.smarttextile.purchasing.repository;

import com.lankatex.smarttextile.purchasing.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}