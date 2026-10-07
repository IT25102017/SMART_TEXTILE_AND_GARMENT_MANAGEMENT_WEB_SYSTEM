package com.lankatex.smarttextile.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing EXPORT_CUSTOMER implementing CUSTOMER ISA specialization.
 * Schema: EXPORT_CUSTOMER(CustomerID PK/FK -> CUSTOMER.CustomerID, Country, ExportRegistrationDetails, ShippingPreferences)
 */
@Entity
@Table(name = "export_customers")
@PrimaryKeyJoinColumn(name = "customer_id")
@Getter
@Setter
@NoArgsConstructor
public class ExportCustomer extends Customer {

    @Column(name = "country")
    private String country;

    @Column(name = "export_registration_details", length = 1000)
    private String exportRegistrationDetails;

    @Column(name = "shipping_preferences", length = 1000)
    private String shippingPreferences;
}
