package com.lankatex.smarttextile.customer.service;

import com.lankatex.smarttextile.customer.dto.CustomerDashboardStats;
import com.lankatex.smarttextile.customer.entity.*;
import com.lankatex.smarttextile.customer.repository.*;
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
    private final LocalCustomerRepository localCustomerRepository;
    private final ExportCustomerRepository exportCustomerRepository;
    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderItemRepository customerOrderItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryItemRepository deliveryItemRepository;
    private final QualityHoldRepository qualityHoldRepository;
    public CustomerService(
            CustomerRepository customerRepository,
            LocalCustomerRepository localCustomerRepository,
            ExportCustomerRepository exportCustomerRepository,
            QuotationRepository quotationRepository,
            QuotationItemRepository quotationItemRepository,
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderItemRepository customerOrderItemRepository,
            DeliveryRepository deliveryRepository,
            DeliveryItemRepository deliveryItemRepository,
            QualityHoldRepository qualityHoldRepository) {
        this.customerRepository = customerRepository;
        this.localCustomerRepository = localCustomerRepository;
        this.exportCustomerRepository = exportCustomerRepository;
        this.quotationRepository = quotationRepository;
        this.quotationItemRepository = quotationItemRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderItemRepository = customerOrderItemRepository;
        this.deliveryRepository = deliveryRepository;
        this.deliveryItemRepository = deliveryItemRepository;
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
        if (customer.getRegistrationDate() == null) {
            customer.setRegistrationDate(LocalDate.now());
        }

        if (customer instanceof ExportCustomer ec) {
            return exportCustomerRepository.save(ec);
        } else if (customer instanceof LocalCustomer lc) {
            return localCustomerRepository.save(lc);
        } else if ("EXPORT".equalsIgnoreCase(customer.getCustomerType())) {
            ExportCustomer ec = new ExportCustomer();
            copyCustomerProperties(customer, ec);
            return exportCustomerRepository.save(ec);
        } else if ("LOCAL".equalsIgnoreCase(customer.getCustomerType())) {
            LocalCustomer lc = new LocalCustomer();
            copyCustomerProperties(customer, lc);
            return localCustomerRepository.save(lc);
        }
        return customerRepository.save(customer);
    }

    private void copyCustomerProperties(Customer src, Customer target) {
        target.setCustomerId(src.getCustomerId());
        target.setCustomerName(src.getCustomerName());
        target.setCustomerType(src.getCustomerType());
        target.setContactPerson(src.getContactPerson());
        target.setPhone(src.getPhone());
        target.setEmail(src.getEmail());
        target.setBillingAddress(src.getBillingAddress());
        target.setDeliveryAddress(src.getDeliveryAddress());
        target.setRegistrationDate(src.getRegistrationDate());
        target.setStatus(src.getStatus());
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
        List<Quotation> list = quotationRepository.findAll(Sort.by(Sort.Direction.DESC, "quotationId"));
        list.forEach(this::populateQuotationTransientItems);
        return list;
    }
    public List<Quotation> getApprovedQuotations() {
        return getAllQuotations().stream()
                .filter(q -> "APPROVED".equalsIgnoreCase(q.getStatus()))
                .toList();
    }

    public Map<Long, Quotation> getQuotationMap() {
        return getAllQuotations().stream()
                .collect(Collectors.toMap(Quotation::getQuotationId, Function.identity()));
    }
    public Quotation getQuotation(Long id) {
        Quotation q = quotationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found."));
        populateQuotationTransientItems(q);
        return q;
    }

    private void populateQuotationTransientItems(Quotation q) {
        if (q == null) return;
        List<QuotationItem> items = quotationItemRepository.findByQuotationId(q.getQuotationId());
        if (!items.isEmpty()) {
            QuotationItem first = items.get(0);
            q.setGarmentType(first.getGarmentType());
            q.setColour(first.getColour());
            q.setSize(first.getSize());
            q.setQuantity(first.getQuantity());
            q.setUnitPrice(first.getUnitPrice());
            if (first.getQuantity() != null && first.getUnitPrice() != null) {
                q.setTotalValue(first.getQuantity().multiply(first.getUnitPrice()));
            }
        }
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
        if (quotation.getQuantity() != null && quotation.getUnitPrice() != null) {
            requirePositive(quotation.getQuantity(), "Quotation quantity");
            requireNonNegative(quotation.getUnitPrice(), "Unit price");
            quotation.setTotalValue(quotation.getQuantity().multiply(quotation.getUnitPrice()));
        }
        if (quotation.getStatus() == null || quotation.getStatus().isBlank()) {
            quotation.setStatus("PENDING");
        }
        Quotation saved = quotationRepository.save(quotation);

        // Keep QUOTATION_ITEM synchronized with header line item
        if (quotation.getGarmentType() != null && !quotation.getGarmentType().isBlank()
                && quotation.getQuantity() != null && quotation.getUnitPrice() != null) {
            List<QuotationItem> items = quotationItemRepository.findByQuotationId(saved.getQuotationId());
            QuotationItem item = items.isEmpty() ? new QuotationItem() : items.get(0);
            item.setQuotationId(saved.getQuotationId());
            item.setGarmentType(quotation.getGarmentType());
            item.setColour(quotation.getColour());
            item.setSize(quotation.getSize());
            item.setQuantity(quotation.getQuantity());
            item.setUnitPrice(quotation.getUnitPrice());
            quotationItemRepository.save(item);
        }

        populateQuotationTransientItems(saved);
        return saved;
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
        List<CustomerOrder> list = customerOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderId"));
        list.forEach(this::populateOrderTransientItems);
        return list;
    }
    public Map<Long, CustomerOrder> getOrderMap() {
        return getAllCustomerOrders().stream()
                .collect(Collectors.toMap(CustomerOrder::getOrderId, Function.identity()));
    }

    public CustomerOrder getCustomerOrder(Long id) {
        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer Order not found."));
        populateOrderTransientItems(order);
        return order;
    }

    private void populateOrderTransientItems(CustomerOrder order) {
        if (order == null) return;
        List<CustomerOrderItem> items = customerOrderItemRepository.findByCustomerOrderId(order.getOrderId());
        if (!items.isEmpty()) {
            CustomerOrderItem first = items.get(0);
            order.setGarmentType(first.getGarmentType());
            order.setColour(first.getColour());
            order.setSize(first.getSize());
            order.setOrderQty(first.getQuantity());
        }
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
        CustomerOrder saved = customerOrderRepository.save(order);

        // Keep CUSTOMER_ORDER_ITEM synchronized with header line item
        if (order.getGarmentType() != null && !order.getGarmentType().isBlank()
                && order.getOrderQty() != null) {
            List<CustomerOrderItem> items = customerOrderItemRepository.findByCustomerOrderId(saved.getOrderId());
            CustomerOrderItem item = items.isEmpty() ? new CustomerOrderItem() : items.get(0);
            item.setCustomerOrderId(saved.getOrderId());
            item.setGarmentType(order.getGarmentType());
            item.setColour(order.getColour());
            item.setSize(order.getSize());
            item.setQuantity(order.getOrderQty());
            if (saved.getQuotationId() != null) {
                Quotation q = quotationRepository.findById(saved.getQuotationId()).orElse(null);
                item.setUnitPrice(q != null && q.getUnitPrice() != null ? q.getUnitPrice() : BigDecimal.ZERO);
            } else if (item.getUnitPrice() == null) {
                item.setUnitPrice(BigDecimal.ZERO);
            }
            customerOrderItemRepository.save(item);
        }

        populateOrderTransientItems(saved);
        return saved;
    }

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
        List<Delivery> list = deliveryRepository.findAll(Sort.by(Sort.Direction.DESC, "deliveryId"));
        list.forEach(this::populateDeliveryTransientItems);
        return list;
    }
    public Delivery getDelivery(Long id) {
        Delivery d = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found."));
        populateDeliveryTransientItems(d);
        return d;
    }

    private void populateDeliveryTransientItems(Delivery d) {
        if (d == null) return;
        List<DeliveryItem> items = deliveryItemRepository.findByDeliveryId(d.getDeliveryId());
        if (!items.isEmpty()) {
            BigDecimal sum = items.stream()
                    .map(DeliveryItem::getQuantityDelivered)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            d.setDeliveredQty(sum);
        }
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
        List<Delivery> deliveries = deliveryRepository.findByOrderIdOrderByDeliveryDateAsc(orderId);
        BigDecimal delivered = BigDecimal.ZERO;
        for (Delivery d : deliveries) {
            if (!"ARCHIVED".equalsIgnoreCase(d.getStatus())) {
                List<DeliveryItem> items = deliveryItemRepository.findByDeliveryId(d.getDeliveryId());
                if (!items.isEmpty()) {
                    for (DeliveryItem di : items) {
                        if (di.getQuantityDelivered() != null) {
                            delivered = delivered.add(di.getQuantityDelivered());
                        }
                    }
                } else if (d.getDeliveredQty() != null) {
                    delivered = delivered.add(d.getDeliveredQty());
                }
            }
        }
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
        if (delivery.getConfirmation() == null || delivery.getConfirmation().isBlank()) {
            delivery.setConfirmation("CONFIRMED");
        }
        Delivery saved = deliveryRepository.save(delivery);

        // Keep DELIVERY_ITEM synchronized with CustomerOrderItem
        List<CustomerOrderItem> orderItems = customerOrderItemRepository.findByCustomerOrderId(saved.getOrderId());
        if (!orderItems.isEmpty()) {
            CustomerOrderItem firstItem = orderItems.get(0);
            List<DeliveryItem> deliveryItems = deliveryItemRepository.findByDeliveryId(saved.getDeliveryId());
            DeliveryItem dItem = deliveryItems.isEmpty() ? new DeliveryItem() : deliveryItems.get(0);
            dItem.setDeliveryId(saved.getDeliveryId());
            dItem.setCustomerOrderItemId(firstItem.getCustomerOrderItemId());
            dItem.setQuantityDelivered(delivery.getDeliveredQty());
            deliveryItemRepository.save(dItem);
        }

        BigDecimal remainingAfter = getRemainingQuantity(order.getOrderId());
        if (remainingAfter.signum() == 0) {
            order.setStatus("DELIVERED");
            customerOrderRepository.save(order);
        }
        populateDeliveryTransientItems(saved);
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


    /// LINE ITEMS & SUBTYPES

    public List<QuotationItem> getQuotationItems(Long quotationId) {
        return quotationItemRepository.findByQuotationId(quotationId);
    }

    public QuotationItem saveQuotationItem(QuotationItem item) {
        requirePositive(item.getQuantity(), "Item quantity");
        requireNonNegative(item.getUnitPrice(), "Item unit price");
        return quotationItemRepository.save(item);
    }

    public void deleteQuotationItem(Long itemId) {
        quotationItemRepository.deleteById(itemId);
    }

    public List<CustomerOrderItem> getCustomerOrderItems(Long orderId) {
        return customerOrderItemRepository.findByCustomerOrderId(orderId);
    }

    public CustomerOrderItem saveCustomerOrderItem(CustomerOrderItem item) {
        requirePositive(item.getQuantity(), "Item quantity");
        requireNonNegative(item.getUnitPrice(), "Item unit price");
        return customerOrderItemRepository.save(item);
    }

    public void deleteCustomerOrderItem(Long itemId) {
        customerOrderItemRepository.deleteById(itemId);
    }

    public List<DeliveryItem> getDeliveryItems(Long deliveryId) {
        return deliveryItemRepository.findByDeliveryId(deliveryId);
    }

    public DeliveryItem saveDeliveryItem(DeliveryItem item) {
        requirePositive(item.getQuantityDelivered(), "Delivered quantity");
        return deliveryItemRepository.save(item);
    }

    public void deleteDeliveryItem(Long itemId) {
        deliveryItemRepository.deleteById(itemId);
    }

    public List<LocalCustomer> getAllLocalCustomers() {
        return localCustomerRepository.findAll();
    }

    public List<ExportCustomer> getAllExportCustomers() {
        return exportCustomerRepository.findAll();
    }

    public ExportCustomer getExportCustomer(Long id) {
        return exportCustomerRepository.findById(id).orElse(null);
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
