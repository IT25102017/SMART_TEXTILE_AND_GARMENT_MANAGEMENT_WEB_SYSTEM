package com.lankatex.smarttextile.customer.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import com.lankatex.smarttextile.customer.entity.Customer;
import com.lankatex.smarttextile.customer.repository.CustomerRepository;
import com.lankatex.smarttextile.customer.entity.Quotation;
import com.lankatex.smarttextile.customer.repository.QuotationRepository;
import com.lankatex.smarttextile.customer.entity.CustomerOrder;
import com.lankatex.smarttextile.customer.repository.CustomerOrderRepository;
import com.lankatex.smarttextile.customer.entity.Delivery;
import com.lankatex.smarttextile.customer.repository.DeliveryRepository;
import com.lankatex.smarttextile.customer.dto.CustomerDashboardStats;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final QuotationRepository quotationRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final DeliveryRepository deliveryRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            QuotationRepository quotationRepository,
            CustomerOrderRepository customerOrderRepository,
            DeliveryRepository deliveryRepository) {
        this.customerRepository = customerRepository;
        this.quotationRepository = quotationRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public CustomerDashboardStats getDashboardStats() {
        return new CustomerDashboardStats(
                customerRepository.count(),
                quotationRepository.count(),
                customerOrderRepository.count(),
                deliveryRepository.count());
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll(Sort.by(Sort.Direction.DESC, "customerId"));
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    public Customer saveCustomer(Customer customer) {
        if (customer.getCustomerName() == null || customer.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (customer.getCustomerName().trim().length() < 2 || customer.getCustomerName().trim().length() > 100) {
            throw new IllegalArgumentException("Customer name must be between 2 and 100 characters");
        }
        if (customer.getCustomerType() == null || customer.getCustomerType().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer type is required");
        }
        if (customer.getPhone() == null || !customer.getPhone().trim().matches("^(\\+?[0-9]{1,4}[\\s-]?)?[0-9]{9,12}$")) {
            throw new IllegalArgumentException("Phone number must be a valid 9-12 digit phone number (e.g. 0771234567)");
        }
        if (customer.getEmail() == null || !customer.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Please provide a valid email address (e.g. user@example.com)");
        }
        if (customer.getBillingAddress() == null || customer.getBillingAddress().trim().length() < 5) {
            throw new IllegalArgumentException("Billing address must be at least 5 characters long");
        }
        if (customer.getDeliveryAddress() == null || customer.getDeliveryAddress().trim().length() < 5) {
            throw new IllegalArgumentException("Delivery address must be at least 5 characters long");
        }

        customer.setCustomerName(customer.getCustomerName().trim());
        customer.setCustomerType(customer.getCustomerType().trim());
        if (customer.getContactPerson() != null) {
            customer.setContactPerson(customer.getContactPerson().trim());
        }
        customer.setPhone(customer.getPhone().trim());
        customer.setEmail(customer.getEmail().trim());
        customer.setBillingAddress(customer.getBillingAddress().trim());
        customer.setDeliveryAddress(customer.getDeliveryAddress().trim());

        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public List<Quotation> getAllQuotations() {
        return quotationRepository.findAll(Sort.by(Sort.Direction.DESC, "quotationId"));
    }

    public Quotation getQuotation(Long id) {
        return quotationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found: " + id));
    }

    public Quotation saveQuotation(Quotation quotation) {
        if (quotation.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (!customerRepository.existsById(quotation.getCustomerId())) {
            throw new IllegalArgumentException("Customer with ID " + quotation.getCustomerId() + " does not exist");
        }
        if (quotation.getQuotationDate() == null) {
            throw new IllegalArgumentException("Quotation date is required");
        }
        if (quotation.getValidUntil() == null) {
            throw new IllegalArgumentException("Valid until date is required");
        }
        if (quotation.getValidUntil().isBefore(quotation.getQuotationDate())) {
            throw new IllegalArgumentException("Valid until date cannot be before quotation date");
        }
        if (quotation.getTotalValue() == null || quotation.getTotalValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total value must be greater than 0.00");
        }
        if (quotation.getStatus() == null || quotation.getStatus().trim().isEmpty()) {
            quotation.setStatus("Pending");
        } else {
            quotation.setStatus(quotation.getStatus().trim());
        }
        return quotationRepository.save(quotation);
    }

    public void deleteQuotation(Long id) {
        quotationRepository.deleteById(id);
    }

    public void archiveQuotation(Long id) {
        deleteQuotation(id);
    }

    public List<CustomerOrder> getAllCustomerOrders() {
        return customerOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderId"));
    }

    public CustomerOrder getCustomerOrder(Long id) {
        return customerOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer Order not found: " + id));
    }

    public CustomerOrder saveCustomerOrder(CustomerOrder customerOrder) {
        if (customerOrder.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (!customerRepository.existsById(customerOrder.getCustomerId())) {
            throw new IllegalArgumentException("Customer with ID " + customerOrder.getCustomerId() + " does not exist");
        }
        if (customerOrder.getQuotationId() != null && !quotationRepository.existsById(customerOrder.getQuotationId())) {
            throw new IllegalArgumentException("Quotation with ID " + customerOrder.getQuotationId() + " does not exist");
        }
        if (customerOrder.getOrderDate() == null) {
            throw new IllegalArgumentException("Order date is required");
        }
        if (customerOrder.getRequiredDeliveryDate() == null) {
            throw new IllegalArgumentException("Required delivery date is required");
        }
        if (customerOrder.getRequiredDeliveryDate().isBefore(customerOrder.getOrderDate())) {
            throw new IllegalArgumentException("Required delivery date cannot be before order date");
        }
        if (customerOrder.getPriority() == null || customerOrder.getPriority().trim().isEmpty()) {
            throw new IllegalArgumentException("Priority is required");
        }
        if (customerOrder.getStatus() == null || customerOrder.getStatus().trim().isEmpty()) {
            customerOrder.setStatus("Pending");
        } else {
            customerOrder.setStatus(customerOrder.getStatus().trim());
        }
        return customerOrderRepository.save(customerOrder);
    }

    public void deleteCustomerOrder(Long id) {
        customerOrderRepository.deleteById(id);
    }

    public void archiveCustomerOrder(Long id) {
        deleteCustomerOrder(id);
    }

    public List<Delivery> getAllDeliverys() {
        return deliveryRepository.findAll(Sort.by(Sort.Direction.DESC, "deliveryId"));
    }

    public Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found: " + id));
    }

    public Delivery saveDelivery(Delivery delivery) {
        if (delivery.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if (!customerOrderRepository.existsById(delivery.getOrderId())) {
            throw new IllegalArgumentException("Customer Order with ID " + delivery.getOrderId() + " does not exist");
        }
        if (delivery.getDeliveryDate() == null) {
            throw new IllegalArgumentException("Delivery date is required");
        }
        if (delivery.getDeliveryAddress() == null || delivery.getDeliveryAddress().trim().length() < 5) {
            throw new IllegalArgumentException("Delivery address must be at least 5 characters long");
        }
        if (delivery.getDeliveredQty() == null || delivery.getDeliveredQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Delivered quantity must be greater than 0");
        }
        if (delivery.getStatus() == null || delivery.getStatus().trim().isEmpty()) {
            delivery.setStatus("Pending");
        } else {
            delivery.setStatus(delivery.getStatus().trim());
        }
        delivery.setDeliveryAddress(delivery.getDeliveryAddress().trim());
        if (delivery.getReceiverName() != null) {
            delivery.setReceiverName(delivery.getReceiverName().trim());
        }
        return deliveryRepository.save(delivery);
    }

    public void deleteDelivery(Long id) {
        deliveryRepository.deleteById(id);
    }

    public void archiveDelivery(Long id) {
        deleteDelivery(id);
    }
}
