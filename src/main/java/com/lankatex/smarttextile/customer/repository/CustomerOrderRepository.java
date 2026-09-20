package com.lankatex.smarttextile.customer.repository;


import com.lankatex.smarttextile.customer.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
}
