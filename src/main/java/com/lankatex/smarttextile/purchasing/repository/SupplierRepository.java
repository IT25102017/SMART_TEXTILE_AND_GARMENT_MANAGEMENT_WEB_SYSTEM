package com.lankatex.smarttextile.purchasing.repository;

import com.lankatex.smarttextile.purchasing.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}