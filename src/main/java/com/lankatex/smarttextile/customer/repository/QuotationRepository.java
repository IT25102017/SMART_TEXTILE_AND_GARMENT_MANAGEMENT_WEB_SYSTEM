package com.lankatex.smarttextile.customer.repository;


import com.lankatex.smarttextile.customer.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
}
