package com.lankatex.smarttextile.customer.controller;

import com.lankatex.smarttextile.customer.entity.Customer;
import com.lankatex.smarttextile.customer.entity.CustomerOrder;
import com.lankatex.smarttextile.customer.entity.Delivery;
import com.lankatex.smarttextile.customer.entity.Quotation;
import com.lankatex.smarttextile.customer.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) {
        this.service = service;
    }
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", service.getDashboardStats());
        model.addAttribute("delayedOrders", service.getDelayedOrders());
        model.addAttribute("customerMap", service.getCustomerMap());
        return "customer/dashboard";
    }

    /// CUSTOMERS

    @GetMapping("/customers")
    public String customers(@RequestParam(required = false) Long editId, Model model) {
        Customer customer = editId == null ? new Customer() : service.getCustomer(editId);
        if (customer.getStatus() == null) customer.setStatus("ACTIVE");
        model.addAttribute("customer", customer);
        model.addAttribute("customersList", service.getAllCustomers());
        return "customer/customers";
    }
    @PostMapping("/customers/save")
    public String saveCustomer(@ModelAttribute Customer customer, RedirectAttributes ra) {
        try {
            service.saveCustomer(customer);
            ra.addFlashAttribute("message", "Customer saved successfully.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customer/customers";
    }
    @PostMapping("/customers/archive/{id}")
    public String archiveCustomer(@PathVariable Long id, RedirectAttributes ra) {
        service.archiveCustomer(id);
        ra.addFlashAttribute("message", "Customer archived.");
        return "redirect:/customer/customers";
    }

    @PostMapping("/customers/restore/{id}")
    public String restoreCustomer(@PathVariable Long id, RedirectAttributes ra) {
        service.restoreCustomer(id);
        ra.addFlashAttribute("message", "Customer restored.");
        return "redirect:/customer/customers";
    }

    /// QUOTATIONS

    @GetMapping("/quotations")
    public String quotations(@RequestParam(required = false) Long editId, Model model) {
        Quotation quotation = editId == null ? new Quotation() : service.getQuotation(editId);
        if (editId == null) {
            quotation.setQuotationDate(LocalDate.now());
            quotation.setStatus("PENDING");
        }
        model.addAttribute("quotation", quotation);
        model.addAttribute("quotationsList", service.getAllQuotations());
        model.addAttribute("customers", service.getActiveCustomers());
        model.addAttribute("customerMap", service.getCustomerMap());
        return "customer/quotations";
    }

    @PostMapping("/quotations/save")
    public String saveQuotation(@ModelAttribute Quotation quotation, RedirectAttributes ra) {
        try {
            service.saveQuotation(quotation);
            ra.addFlashAttribute("message", "Quotation saved successfully.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customer/quotations";
    }

    @PostMapping("/quotations/approve/{id}")
    public String approveQuotation(@PathVariable Long id,
                                   @RequestParam(required = false) Long approvedBy,
                                   RedirectAttributes ra) {
        service.approveQuotation(id, approvedBy);
        ra.addFlashAttribute("message", "Quotation approved.");
        return "redirect:/customer/quotations";
    }

    @PostMapping("/quotations/reject/{id}")
    public String rejectQuotation(@PathVariable Long id,
                                  @RequestParam(required = false) Long approvedBy,
                                  RedirectAttributes ra) {
        service.rejectQuotation(id, approvedBy);
        ra.addFlashAttribute("message", "Quotation rejected.");
        return "redirect:/customer/quotations";
    }

    @PostMapping("/quotations/archive/{id}")
    public String archiveQuotation(@PathVariable Long id, RedirectAttributes ra) {
        service.archiveQuotation(id);
        ra.addFlashAttribute("message", "Quotation archived.");
        return "redirect:/customer/quotations";
    }
    @PostMapping("/quotations/restore/{id}")
    public String restoreQuotation(@PathVariable Long id, RedirectAttributes ra) {
        service.restoreQuotation(id);
        ra.addFlashAttribute("message", "Quotation restored to PENDING.");
        return "redirect:/customer/quotations";
    }

    ///CUSTOMER ORDERS
    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) Long editId,
                         @RequestParam(required = false) Long quotationId,
                         Model model) {
        CustomerOrder order;
        if (editId != null) {
            order = service.getCustomerOrder(editId);
        } else if (quotationId != null) {
            order = service.buildOrderFromQuotation(quotationId);
        } else {
            order = new CustomerOrder();
            order.setOrderDate(LocalDate.now());
            order.setPriority("NORMAL");
            order.setStatus("PENDING");
        }
        model.addAttribute("customerOrder", order);
        model.addAttribute("ordersList", service.getAllCustomerOrders());
        model.addAttribute("customers", service.getActiveCustomers());
        model.addAttribute("approvedQuotations", service.getApprovedQuotations());
        model.addAttribute("customerMap", service.getCustomerMap());
        model.addAttribute("quotationMap", service.getQuotationMap());
        model.addAttribute("remainingQtyMap", service.getRemainingQuantityMap());
        return "customer/orders";
    }
    @PostMapping("/orders/save")
    public String saveOrder(@ModelAttribute CustomerOrder order, RedirectAttributes ra) {
        try {
            service.saveCustomerOrder(order);
            ra.addFlashAttribute("message", "Customer Order saved successfully.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customer/orders";
    }
    @PostMapping("/orders/approve/{id}")
    public String approveOrder(@PathVariable Long id, @RequestParam Long approvedBy, RedirectAttributes ra) {
        try {
            service.approveOrder(id, approvedBy);
            ra.addFlashAttribute("message", "Customer Order approved.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customer/orders";
    }

    @PostMapping("/orders/reject/{id}")
    public String rejectOrder(@PathVariable Long id,
                              @RequestParam(required = false) Long approvedBy,
                              RedirectAttributes ra) {
        service.rejectOrder(id, approvedBy);
        ra.addFlashAttribute("message", "Customer Order cancelled/rejected.");
        return "redirect:/customer/orders";
    }
    @PostMapping("/orders/status/{id}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    RedirectAttributes ra) {
        try {
            service.updateOrderStatus(id, status);
            ra.addFlashAttribute("message", "Order status updated.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customer/orders";
    }

    /// DELIVERIES

    @GetMapping("/deliveries")
    public String deliveries(Model model) {
        Delivery delivery = new Delivery();
        delivery.setDeliveryDate(LocalDate.now());
        model.addAttribute("delivery", delivery);
        model.addAttribute("deliveriesList", service.getAllDeliveries());
        model.addAttribute("deliverableOrders", service.getDeliverableOrders());
        model.addAttribute("orderMap", service.getOrderMap());
        model.addAttribute("customerMap", service.getCustomerMap());
        model.addAttribute("remainingQtyMap", service.getRemainingQuantityMap());
        return "customer/deliveries";
    }

    @PostMapping("/deliveries/save")
    public String saveDelivery(@ModelAttribute Delivery delivery, RedirectAttributes ra) {
        try {
            service.saveDelivery(delivery);
            ra.addFlashAttribute("message", "Delivery recorded successfully.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customer/deliveries";
    }

    @PostMapping("/deliveries/archive/{id}")
    public String archiveDelivery(@PathVariable Long id, RedirectAttributes ra) {
        service.archiveDelivery(id);
        ra.addFlashAttribute("message", "Delivery archived.");
        return "redirect:/customer/deliveries";
    }

    @PostMapping("/deliveries/restore/{id}")
    public String restoreDelivery(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.restoreDelivery(id);
            ra.addFlashAttribute("message", "Delivery restored.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customer/deliveries";
    }



}
