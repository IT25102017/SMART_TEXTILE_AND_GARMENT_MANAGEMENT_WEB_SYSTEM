package com.lankatex.smarttextile.purchasing.repository;

import com.lankatex.smarttextile.purchasing.entity.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
}