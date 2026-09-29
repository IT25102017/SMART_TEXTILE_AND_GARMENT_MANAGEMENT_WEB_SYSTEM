package com.lankatex.smarttextile.customer.service;

import com.lankatex.smarttextile.customer.dto.CustomerDashboardStats;
import com.lankatex.smarttextile.customer.entity.Customer;
import com.lankatex.smarttextile.customer.entity.CustomerOrder;
import com.lankatex.smarttextile.customer.entity.Delivery;
import com.lankatex.smarttextile.customer.entity.Quotation;
import com.lankatex.smarttextile.customer.repository.CustomerOrderRepository;
import com.lankatex.smarttextile.customer.repository.CustomerRepository;
import com.lankatex.smarttextile.customer.repository.DeliveryRepository;
import com.lankatex.smarttextile.customer.repository.QuotationRepository;
import com.lankatex.smarttextile.quality.entity.QualityHold;
import com.lankatex.smarttextile.quality.repository.QualityHoldRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;



@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final QuotationRepository quotationRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final DeliveryRepository deliveryRepository;
    private final QualityHoldRepository qualityHoldRepository;
    public CustomerService(
            CustomerRepository customerRepository,
            QuotationRepository quotationRepository,
            CustomerOrderRepository customerOrderRepository,
            DeliveryRepository deliveryRepository,
            QualityHoldRepository qualityHoldRepository) {
        this.customerRepository = customerRepository;
        this.quotationRepository = quotationRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.deliveryRepository = deliveryRepository;
        this.qualityHoldRepository = qualityHoldRepository;
    }

    /// DASHBOARD

    public CustomerDashboardStats getDashboardStats() {
        List<CustomerOrder> orders = getAllCustomerOrders();
        long pending = orders.stream().filter(o -> "PENDING".equalsIgnoreCase(o.getStatus())).count();
        long active = orders.stream().filter(this::isActiveOrder).count();
        long delayed = orders.stream().filter(this::isDelayedOrder).count();
        long delivered = orders.stream().filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus())).count();

        return new CustomerDashboardStats(
                customerRepository.count(),
                quotationRepository.count(),
                pending,
                active,
                delayed,
                delivered);


    }

    public List<CustomerOrder> getDelayedOrders() {
        return getAllCustomerOrders().stream()
                .filter(this::isDelayedOrder)
                .toList();
    }

    private boolean isActiveOrder(CustomerOrder order) {
        String s = normalize(order.getStatus());
        return Set.of("APPROVED", "IN_PRODUCTION", "QUALITY_CHECKING", "READY").contains(s);
    }

    private boolean isDelayedOrder(CustomerOrder order) {
        if (order.getRequiredDeliveryDate() == null) return false;
        String s = normalize(order.getStatus());
        return order.getRequiredDeliveryDate().isBefore(LocalDate.now())
                && !Set.of("DELIVERED", "CANCELLED", "ARCHIVED").contains(s);
    }

    /// CUSTOMERS

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll(Sort.by(Sort.Direction.DESC, "customerId"));
    }
    public List<Customer> getActiveCustomers() {
        return getAllCustomers().stream()
                .filter(c -> !"ARCHIVED".equalsIgnoreCase(c.getStatus()))
                .toList();
    }

    public Map<Long, Customer> getCustomerMap() {
        return customerRepository.findAll().stream()
                .collect(Collectors.toMap(Customer::getCustomerId, Function.identity()));
    }
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));
    }

    public Customer saveCustomer(Customer customer) {
        if (customer.getCustomerName() == null || customer.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (customer.getStatus() == null || customer.getStatus().isBlank()) {
            customer.setStatus("ACTIVE");
        }
        return customerRepository.save(customer);
    }

    public void archiveCustomer(Long id) {
        Customer customer = getCustomer(id);
        customer.setStatus("ARCHIVED");
        customerRepository.save(customer);
    }

    public void restoreCustomer(Long id) {
        Customer customer = getCustomer(id);
        customer.setStatus("ACTIVE");
        customerRepository.save(customer);
    }

    /// QUOTATIONS

    public List<Quotation> getAllQuotations() {
        return quotationRepository.findAll(Sort.by(Sort.Direction.DESC, "quotationId"));
    }
    public List<Quotation> getApprovedQuotations() {
        return getAllQuotations().stream()
                .filter(q -> "APPROVED".equalsIgnoreCase(q.getStatus()))
                .toList();
    }

    public Map<Long, Quotation> getQuotationMap() {
        return quotationRepository.findAll().stream()
                .collect(Collectors.toMap(Quotation::getQuotationId, Function.identity()));
    }
    public Quotation getQuotation(Long id) {
        return quotationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found."));
    }

    public Quotation saveQuotation(Quotation quotation) {
        Customer customer = getCustomer(quotation.getCustomerId());
        if ("ARCHIVED".equalsIgnoreCase(customer.getStatus())) {
            throw new IllegalArgumentException("Archived customers cannot be used for a quotation.");
        }
        if (quotation.getQuotationDate() == null || quotation.getValidUntil() == null) {
            throw new IllegalArgumentException("Quotation date and valid-until date are required.");
        }
        if (quotation.getValidUntil().isBefore(quotation.getQuotationDate())) {
            throw new IllegalArgumentException("Valid-until date cannot be before quotation date.");
        }
        requirePositive(quotation.getQuantity(), "Quotation quantity");
        requireNonNegative(quotation.getUnitPrice(), "Unit price");
        quotation.setTotalValue(quotation.getQuantity().multiply(quotation.getUnitPrice()));
        if (quotation.getStatus() == null || quotation.getStatus().isBlank()) {
            quotation.setStatus("PENDING");
        }
        return quotationRepository.save(quotation);
    }

    public void approveQuotation(Long id, Long approvedBy) {
        Quotation quotation = getQuotation(id);
        quotation.setStatus("APPROVED");
        quotation.setApprovedBy(approvedBy);
        quotationRepository.save(quotation);
    }
    public void rejectQuotation(Long id, Long approvedBy) {
        Quotation quotation = getQuotation(id);
        quotation.setStatus("REJECTED");
        quotation.setApprovedBy(approvedBy);
        quotationRepository.save(quotation);
    }

    public void archiveQuotation(Long id) {
        Quotation quotation = getQuotation(id);
        quotation.setStatus("ARCHIVED");
        quotationRepository.save(quotation);
    }

    public void restoreQuotation(Long id) {
        Quotation quotation = getQuotation(id);
        quotation.setStatus("PENDING");
        quotationRepository.save(quotation);
    }

    /// CUSTOMER ORDERS

    public List<CustomerOrder> getAllCustomerOrders() {
        return customerOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderId"));
    }
    public Map<Long, CustomerOrder> getOrderMap() {
        return customerOrderRepository.findAll().stream()
                .collect(Collectors.toMap(CustomerOrder::getOrderId, Function.identity()));
    }

    public CustomerOrder getCustomerOrder(Long id) {
        return customerOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer Order not found."));
    }
    public CustomerOrder buildOrderFromQuotation(Long quotationId) {
        Quotation q = getQuotation(quotationId);
        if (!"APPROVED".equalsIgnoreCase(q.getStatus())) {
            throw new IllegalArgumentException("Only an approved quotation can be converted to an order.");
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomerId(q.getCustomerId());
        order.setQuotationId(q.getQuotationId());
        order.setOrderDate(LocalDate.now());
        order.setRequiredDeliveryDate(q.getRequestedDeliveryDate());
        order.setPriority("NORMAL");
        order.setGarmentType(q.getGarmentType());
        order.setColour(q.getColour());
        order.setSize(q.getSize());
        order.setOrderQty(q.getQuantity());
        order.setStatus("PENDING");
        return order;
    }

    public CustomerOrder saveCustomerOrder(CustomerOrder order) {
        Customer customer = getCustomer(order.getCustomerId());
        if ("ARCHIVED".equalsIgnoreCase(customer.getStatus())) {
            throw new IllegalArgumentException("Archived customers cannot be used for a new order.");
        }

        if (order.getQuotationId() != null) {
            Quotation q = getQuotation(order.getQuotationId());
            if (!"APPROVED".equalsIgnoreCase(q.getStatus())) {
                throw new IllegalArgumentException("Selected quotation must be APPROVED.");
            }
            if (!Objects.equals(q.getCustomerId(), order.getCustomerId())) {
                throw new IllegalArgumentException("Quotation customer does not match the selected customer.");
            }
            if (order.getGarmentType() == null || order.getGarmentType().isBlank()) {
                order.setGarmentType(q.getGarmentType());
                order.setColour(q.getColour());
                order.setSize(q.getSize());
                order.setOrderQty(q.getQuantity());
            }
        }

        if (order.getRequiredDeliveryDate() == null) {
            throw new IllegalArgumentException("Required delivery date is required.");
        }
        requirePositive(order.getOrderQty(), "Order quantity");
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("PENDING");
        }
        return customerOrderRepository.save(order);}

    public void approveOrder(Long id, Long approvedBy) {
        if (approvedBy == null) {
            throw new IllegalArgumentException("Approver User ID is required.");
        }
        CustomerOrder order = getCustomerOrder(id);
        order.setStatus("APPROVED");
        order.setApprovedBy(approvedBy);
        customerOrderRepository.save(order);
    }

    public void rejectOrder(Long id, Long approvedBy) {
        CustomerOrder order = getCustomerOrder(id);
        order.setStatus("CANCELLED");
        order.setApprovedBy(approvedBy);
        customerOrderRepository.save(order);
    }

    public void updateOrderStatus(Long id, String status) {
        String newStatus = normalize(status);
        Set<String> allowed = Set.of("IN_PRODUCTION", "QUALITY_CHECKING", "READY", "CANCELLED");
        if (!allowed.contains(newStatus)) {
            throw new IllegalArgumentException("Invalid manual order status.");
        }
        CustomerOrder order = getCustomerOrder(id);
        order.setStatus(newStatus);
        customerOrderRepository.save(order);
    }

    /// DELIVERIES

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll(Sort.by(Sort.Direction.DESC, "deliveryId"));
    }
    public Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found."));
    }
    public List<CustomerOrder> getDeliverableOrders() {
        return getAllCustomerOrders().stream()
                .filter(o -> "READY".equalsIgnoreCase(o.getStatus()))
                .filter(o -> !hasActiveQualityHold(o.getOrderId()))
                .filter(o -> getRemainingQuantity(o.getOrderId()).signum() > 0)
                .toList();
    }

    public BigDecimal getRemainingQuantity(Long orderId) {
        CustomerOrder order = getCustomerOrder(orderId);
        if (order.getOrderQty() == null) return BigDecimal.ZERO;
        BigDecimal delivered = deliveryRepository.findByOrderIdOrderByDeliveryDateAsc(orderId).stream()
                .filter(d -> !"ARCHIVED".equalsIgnoreCase(d.getStatus()))
                .map(Delivery::getDeliveredQty)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return order.getOrderQty().subtract(delivered).max(BigDecimal.ZERO);
    }

    public Map<Long, BigDecimal> getRemainingQuantityMap() {
        Map<Long, BigDecimal> map = new LinkedHashMap<>();
        for (CustomerOrder order : getAllCustomerOrders()) {
            map.put(order.getOrderId(), getRemainingQuantity(order.getOrderId()));
        }
        return map;
    }

    public Delivery saveDelivery(Delivery delivery) {
        if (delivery.getDeliveryId() != null) {
            throw new IllegalArgumentException("Confirmed delivery records cannot be edited. Archive and create a correction instead.");
        }
        CustomerOrder order = getCustomerOrder(delivery.getOrderId());
        if (!"READY".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException("Only READY customer orders can be delivered.");
        }
        if (hasActiveQualityHold(order.getOrderId())) {
            throw new IllegalArgumentException("Delivery is blocked because an active Quality Hold exists for this order.");
        }
        requirePositive(delivery.getDeliveredQty(), "Delivered quantity");
        BigDecimal remaining = getRemainingQuantity(order.getOrderId());
        if (delivery.getDeliveredQty().compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Delivered quantity cannot exceed the remaining order quantity of " + remaining + ".");
        }

        if (delivery.getDeliveryAddress() == null || delivery.getDeliveryAddress().isBlank()) {
            Customer customer = getCustomer(order.getCustomerId());
            delivery.setDeliveryAddress(customer.getDeliveryAddress());
        }
        if (delivery.getDeliveryDate() == null) {
            delivery.setDeliveryDate(LocalDate.now());
        }
        delivery.setStatus("CONFIRMED");
        Delivery saved = deliveryRepository.save(delivery);
        BigDecimal remainingAfter = getRemainingQuantity(order.getOrderId());
        if (remainingAfter.signum() == 0) {
            order.setStatus("DELIVERED");
            customerOrderRepository.save(order);
        }
        return saved;
    }

    public void archiveDelivery(Long id) {
        Delivery delivery = getDelivery(id);
        delivery.setStatus("ARCHIVED");
        deliveryRepository.save(delivery);
        CustomerOrder order = getCustomerOrder(delivery.getOrderId());
        if (!"CANCELLED".equalsIgnoreCase(order.getStatus())) {
            order.setStatus(getRemainingQuantity(order.getOrderId()).signum() == 0 ? "DELIVERED" : "READY");
            customerOrderRepository.save(order);
        }
    }

    public void restoreDelivery(Long id) {
        Delivery delivery = getDelivery(id);
        CustomerOrder order = getCustomerOrder(delivery.getOrderId());
        if (hasActiveQualityHold(order.getOrderId())) {
            throw new IllegalArgumentException("Cannot restore delivery while an active Quality Hold exists.");
        }
        BigDecimal remainingWithoutArchived = getRemainingQuantity(order.getOrderId());
        if (delivery.getDeliveredQty().compareTo(remainingWithoutArchived) > 0) {
            throw new IllegalArgumentException("Restoring this delivery would exceed the order quantity.");
        }
        delivery.setStatus("CONFIRMED");
        deliveryRepository.save(delivery);
        order.setStatus(getRemainingQuantity(order.getOrderId()).signum() == 0 ? "DELIVERED" : "READY");
        customerOrderRepository.save(order);
    }


    /// QUALITY HOLD INTEGRATION

    public boolean hasActiveQualityHold(Long orderId) {
        return qualityHoldRepository.findAll().stream()
                .filter(h -> Objects.equals(h.getOrderId(), orderId))
                .anyMatch(this::isBlockingHold);
    }
    private boolean isBlockingHold(QualityHold hold) {
        String s = normalize(hold.getStatus());
        return !Set.of("RELEASED", "RESOLVED", "ARCHIVED", "CLOSED").contains(s);
    }
    private void requirePositive(BigDecimal value, String label) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(label + " must be greater than zero.");
        }
    }
    private void requireNonNegative(BigDecimal value, String label) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException(label + " cannot be negative.");
        }
    }
    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase().replace(' ', '_');
    }


 }
