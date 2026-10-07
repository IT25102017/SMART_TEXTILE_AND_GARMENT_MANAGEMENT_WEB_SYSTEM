package com.lankatex.smarttextile.customer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing LOCAL_CUSTOMER implementing CUSTOMER ISA specialization.
 * Schema: LOCAL_CUSTOMER(CustomerID PK/FK -> CUSTOMER.CustomerID)
 */
@Entity
@Table(name = "local_customers")
@PrimaryKeyJoinColumn(name = "customer_id")
@Getter
@Setter
@NoArgsConstructor
public class LocalCustomer extends Customer {
}
