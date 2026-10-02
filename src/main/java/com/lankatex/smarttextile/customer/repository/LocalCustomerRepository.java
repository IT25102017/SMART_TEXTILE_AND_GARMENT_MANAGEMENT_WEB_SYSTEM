package com.lankatex.smarttextile.customer.repository;

import com.lankatex.smarttextile.customer.entity.LocalCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalCustomerRepository extends JpaRepository<LocalCustomer, Long> {
}
