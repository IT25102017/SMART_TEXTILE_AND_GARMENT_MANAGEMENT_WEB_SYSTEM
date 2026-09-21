package com.lankatex.smarttextile.customer.controller;

import com.lankatex.smarttextile.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import com.lankatex.smarttextile.customer.entity.Customer;
import com.lankatex.smarttextile.customer.entity.Quotation;
import com.lankatex.smarttextile.customer.entity.CustomerOrder;
import com.lankatex.smarttextile.customer.entity.Delivery;

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
        return "customer/dashboard";
    }

    @GetMapping("/customers")
    public String customers(@RequestParam(required = false) Long editId, Model model) {
        if (!model.containsAttribute("customer")) {
            model.addAttribute("customer", editId == null ? new Customer() : service.getCustomer(editId));
        }
        model.addAttribute("customersList", service.getAllCustomers());
        return "customer/customers";
    }

    @GetMapping({"/customers/data", "/customers/json"})
    @ResponseBody
    public List<Customer> getCustomersJson() {
        return service.getAllCustomers();
    }

    @PostMapping("/customers/save")
    public String saveCustomer(@Valid @ModelAttribute Customer customer, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("customer", customer);
            return "redirect:/customer/customers";
        }
        try {
            service.saveCustomer(customer);
            redirectAttributes.addFlashAttribute("message", "Customer saved successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("customer", customer);
        }
        return "redirect:/customer/customers";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("message", "Customer deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete customer because related orders or quotations exist. Please remove them first.");
        }
        return "redirect:/customer/customers";
    }

    @GetMapping("/quotations")
    public String quotations(@RequestParam(required = false) Long editId, Model model) {
        if (!model.containsAttribute("quotation")) {
            model.addAttribute("quotation", editId == null ? new Quotation() : service.getQuotation(editId));
        }
        model.addAttribute("quotationsList", service.getAllQuotations());
        return "customer/quotations";
    }

    @GetMapping({"/quotations/data", "/quotations/json"})
    @ResponseBody
    public List<Quotation> getQuotationsJson() {
        return service.getAllQuotations();
    }

    @PostMapping("/quotations/save")
    public String saveQuotation(@Valid @ModelAttribute Quotation quotation, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("quotation", quotation);
            return "redirect:/customer/quotations";
        }
        try {
            service.saveQuotation(quotation);
            redirectAttributes.addFlashAttribute("message", "Quotation saved successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("quotation", quotation);
        }
        return "redirect:/customer/quotations";
    }

    @GetMapping({"/quotations/delete/{id}", "/quotations/archive/{id}"})
    public String deleteQuotation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteQuotation(id);
            redirectAttributes.addFlashAttribute("message", "Quotation deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete quotation because it is linked to an existing customer order. Please remove the order first.");
        }
        return "redirect:/customer/quotations";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) Long editId, Model model) {
        if (!model.containsAttribute("customerOrder")) {
            model.addAttribute("customerOrder", editId == null ? new CustomerOrder() : service.getCustomerOrder(editId));
        }
        model.addAttribute("ordersList", service.getAllCustomerOrders());
        return "customer/orders";
    }

    @GetMapping({"/orders/data", "/orders/json"})
    @ResponseBody
    public List<CustomerOrder> getOrdersJson() {
        return service.getAllCustomerOrders();
    }

    @PostMapping("/orders/save")
    public String saveCustomerOrder(@Valid @ModelAttribute CustomerOrder customerOrder, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("customerOrder", customerOrder);
            return "redirect:/customer/orders";
        }
        try {
            service.saveCustomerOrder(customerOrder);
            redirectAttributes.addFlashAttribute("message", "Customer Order saved successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("customerOrder", customerOrder);
        }
        return "redirect:/customer/orders";
    }

    @GetMapping({"/orders/delete/{id}", "/orders/archive/{id}"})
    public String deleteCustomerOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteCustomerOrder(id);
            redirectAttributes.addFlashAttribute("message", "Customer Order deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete customer order because it is linked to an existing delivery. Please remove the delivery first.");
        }
        return "redirect:/customer/orders";
    }

    @GetMapping("/deliverys")
    public String deliverys(@RequestParam(required = false) Long editId, Model model) {
        if (!model.containsAttribute("delivery")) {
            model.addAttribute("delivery", editId == null ? new Delivery() : service.getDelivery(editId));
        }
        model.addAttribute("deliverysList", service.getAllDeliverys());
        return "customer/deliverys";
    }

    @GetMapping({"/deliverys/data", "/deliverys/json", "/deliveries/data", "/deliveries/json"})
    @ResponseBody
    public List<Delivery> getDeliveriesJson() {
        return service.getAllDeliverys();
    }

    @PostMapping("/deliverys/save")
    public String saveDelivery(@Valid @ModelAttribute Delivery delivery, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("delivery", delivery);
            return "redirect:/customer/deliverys";
        }
        try {
            service.saveDelivery(delivery);
            redirectAttributes.addFlashAttribute("message", "Delivery saved successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("delivery", delivery);
        }
        return "redirect:/customer/deliverys";
    }

    @GetMapping({"/deliverys/delete/{id}", "/deliverys/archive/{id}"})
    public String deleteDelivery(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteDelivery(id);
            redirectAttributes.addFlashAttribute("message", "Delivery deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete delivery: " + ex.getMessage());
        }
        return "redirect:/customer/deliverys";
    }
}
