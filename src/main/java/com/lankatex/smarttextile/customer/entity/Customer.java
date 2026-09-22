package com.lankatex.smarttextile.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotBlank(message = "Customer type is required")
    @Column(name = "customer_type", nullable = false)
    private String customerType;

    @Size(max = 100, message = "Contact person name cannot exceed 100 characters")
    @Column(name = "contact_person")
    private String contactPerson;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+?[0-9]{1,4}[\\s-]?)?[0-9]{9,12}$", message = "Phone number must be a valid 9-12 digit phone number (e.g. 0771234567)")
    @Column(name = "phone")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address (e.g. user@example.com)")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Billing address is required")
    @Size(min = 5, max = 1000, message = "Billing address must be between 5 and 1000 characters")
    @Column(name = "billing_address", length = 1000)
    private String billingAddress;

    @NotBlank(message = "Delivery address is required")
    @Size(min = 5, max = 1000, message = "Delivery address must be between 5 and 1000 characters")
    @Column(name = "delivery_address", length = 1000)
    private String deliveryAddress;

    //new
    @Column(name = "status")
    private String status = "ACTIVE";

    @Transient
    public String getCustomerCode() {
        return customerId == null ? "CUS-NEW" : String.format("CUS-%03d", customerId);
    }
}
