package com.lankatex.smarttextile.customer.repository;

import com.lankatex.smarttextile.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
