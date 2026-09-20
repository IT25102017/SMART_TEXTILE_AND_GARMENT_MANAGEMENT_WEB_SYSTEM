package com.lankatex.smarttextile.customer.controller;

import com.lankatex.smarttextile.customer.dto.CustomerDashboardStats;
import com.lankatex.smarttextile.customer.entity.Customer;
import com.lankatex.smarttextile.customer.entity.CustomerOrder;
import com.lankatex.smarttextile.customer.entity.Delivery;
import com.lankatex.smarttextile.customer.entity.Quotation;
import com.lankatex.smarttextile.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/customer", "/customer/api"})
public class CustomerApiController {

    private final CustomerService service;

    public CustomerApiController(CustomerService service) {
        this.service = service;
    }

    // ==========================================
    // DASHBOARD STATS
    // ==========================================
    @GetMapping("/stats")
    public CustomerDashboardStats getDashboardStats() {
        return service.getDashboardStats();
    }

    // ==========================================
    // QUOTATIONS API
    // ==========================================
    @GetMapping("/quotations")
    public List<Quotation> getAllQuotations() {
        return service.getAllQuotations();
    }

    @GetMapping("/quotations/{id}")
    public Quotation getQuotationById(@PathVariable Long id) {
        return service.getQuotation(id);
    }

    @PostMapping({"/quotations", "/quotations/save"})
    public ResponseEntity<?> createQuotation(@RequestBody List<Quotation> quotations) {
        if (quotations == null || quotations.isEmpty()) {
            return ResponseEntity.badRequest().body("Quotation data cannot be empty");
        }
        List<Quotation> saved = quotations.stream().map(service::saveQuotation).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.size() == 1 ? saved.get(0) : saved);
    }

    @DeleteMapping({"/quotations/{id}", "/quotations/delete/{id}"})
    public ResponseEntity<Void> deleteQuotation(@PathVariable Long id) {
        service.deleteQuotation(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // CUSTOMERS API
    // ==========================================
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return service.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return service.getCustomer(id);
    }

    @PostMapping({"/customers", "/customers/save"})
    public ResponseEntity<?> createCustomer(@RequestBody List<Customer> customers) {
        if (customers == null || customers.isEmpty()) {
            return ResponseEntity.badRequest().body("Customer data cannot be empty");
        }
        List<Customer> saved = customers.stream().map(service::saveCustomer).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.size() == 1 ? saved.get(0) : saved);
    }

    @DeleteMapping({"/customers/{id}", "/customers/delete/{id}"})
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        service.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // ORDERS API
    // ==========================================
    @GetMapping("/orders")
    public List<CustomerOrder> getAllOrders() {
        return service.getAllCustomerOrders();
    }

    @GetMapping("/orders/{id}")
    public CustomerOrder getOrderById(@PathVariable Long id) {
        return service.getCustomerOrder(id);
    }

    @PostMapping({"/orders", "/orders/save"})
    public ResponseEntity<?> createOrder(@RequestBody List<CustomerOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.badRequest().body("Order data cannot be empty");
        }
        List<CustomerOrder> saved = orders.stream().map(service::saveCustomerOrder).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.size() == 1 ? saved.get(0) : saved);
    }

    @DeleteMapping({"/orders/{id}", "/orders/delete/{id}"})
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        service.deleteCustomerOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // DELIVERIES API
    // ==========================================
    @GetMapping({"/deliveries", "/deliverys"})
    public List<Delivery> getAllDeliveries() {
        return service.getAllDeliverys();
    }

    @GetMapping({"/deliveries/{id}", "/deliverys/{id}"})
    public Delivery getDeliveryById(@PathVariable Long id) {
        return service.getDelivery(id);
    }

    @PostMapping({"/deliveries", "/deliveries/save", "/deliverys", "/deliverys/save"})
    public ResponseEntity<?> createDelivery(@RequestBody List<Delivery> deliveries) {
        if (deliveries == null || deliveries.isEmpty()) {
            return ResponseEntity.badRequest().body("Delivery data cannot be empty");
        }
        List<Delivery> saved = deliveries.stream().map(service::saveDelivery).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.size() == 1 ? saved.get(0) : saved);
    }

    @DeleteMapping({"/deliveries/{id}", "/deliveries/delete/{id}", "/deliverys/{id}", "/deliverys/delete/{id}"})
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        service.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}
