package com.lankatex.smarttextile.purchasing.controller;

import com.lankatex.smarttextile.inventory.entity.Material;
import com.lankatex.smarttextile.inventory.service.MaterialService;
import com.lankatex.smarttextile.purchasing.entity.PurchaseOrder;
import com.lankatex.smarttextile.purchasing.entity.PurchaseOrderItem;
import com.lankatex.smarttextile.purchasing.entity.PurchaseRequest;
import com.lankatex.smarttextile.purchasing.entity.Supplier;
import com.lankatex.smarttextile.purchasing.service.PurchasingService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/purchasing")
public class PurchasingController {

    private final PurchasingService service;
    private final MaterialService materialService;

    public PurchasingController(
            PurchasingService service,
            MaterialService materialService) {

        this.service = service;
        this.materialService = materialService;
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    @GetMapping
    public String dashboard(Model model) {

        model.addAttribute(
                "stats",
                service.getDashboardStats()
        );

        return "purchasing/dashboard";
    }

    // =====================================================
    // SUPPLIER
    // =====================================================

    @GetMapping("/suppliers")
    public String suppliers(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "supplier",
                editId == null
                        ? new Supplier()
                        : service.getSupplier(editId)
        );

        model.addAttribute(
                "suppliersList",
                service.getAllSuppliers()
        );

        return "purchasing/suppliers";
    }

    @GetMapping("/suppliers/new")
    public String newSupplier(Model model) {

        model.addAttribute(
                "supplier",
                new Supplier()
        );

        model.addAttribute(
                "isEdit",
                false
        );

        return "purchasing/supplier-form";
    }

    @GetMapping("/suppliers/edit/{id}")
    public String editSupplier(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "supplier",
                service.getSupplier(id)
        );

        model.addAttribute(
                "isEdit",
                true
        );

        return "purchasing/supplier-form";
    }

    @PostMapping("/suppliers/save")
    public String saveSupplier(
            @ModelAttribute Supplier supplier,
            RedirectAttributes redirectAttributes) {

        try {

            service.saveSupplier(supplier);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Supplier saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/purchasing/suppliers";
    }

    @PostMapping("/suppliers/archive/{id}")
    public String archiveSupplier(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.archiveSupplier(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Supplier archived successfully."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot archive Supplier."
            );
        }

        return "redirect:/purchasing/suppliers";
    }

    @PostMapping("/suppliers/restore/{id}")
    public String restoreSupplier(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.restoreSupplier(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Supplier restored successfully."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot restore Supplier."
            );
        }

        return "redirect:/purchasing/suppliers";
    }

    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deleteSupplier(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Supplier permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/purchasing/suppliers";
    }

    // =====================================================
    // PURCHASE REQUEST
    // =====================================================

    @GetMapping("/requests")
    public String requests(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "purchaseRequest",
                editId == null
                        ? new PurchaseRequest()
                        : service.getPurchaseRequest(editId)
        );

        model.addAttribute(
                "requestsList",
                service.getAllPurchaseRequests()
        );

        return "purchasing/requests";
    }

    @GetMapping("/requests/new")
    public String newPurchaseRequest(Model model) {

        model.addAttribute(
                "purchaseRequest",
                new PurchaseRequest()
        );

        model.addAttribute(
                "isEdit",
                false
        );

        return "purchasing/request-form";
    }

    @GetMapping("/requests/edit/{id}")
    public String editPurchaseRequest(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "purchaseRequest",
                service.getPurchaseRequest(id)
        );

        model.addAttribute(
                "isEdit",
                true
        );

        return "purchasing/request-form";
    }

    @PostMapping("/requests/save")
    public String savePurchaseRequest(
            @ModelAttribute PurchaseRequest purchaseRequest,
            RedirectAttributes redirectAttributes) {

        try {

            service.savePurchaseRequest(
                    purchaseRequest
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Request saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/purchasing/requests";
    }

    @PostMapping("/requests/archive/{id}")
    public String archivePurchaseRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.archivePurchaseRequest(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Request archived successfully."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot archive Purchase Request."
            );
        }

        return "redirect:/purchasing/requests";
    }

    @PostMapping("/requests/restore/{id}")
    public String restorePurchaseRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.restorePurchaseRequest(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Request restored to Pending status."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot restore Purchase Request."
            );
        }

        return "redirect:/purchasing/requests";
    }

    @PostMapping("/requests/delete/{id}")
    public String deletePurchaseRequest(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deletePurchaseRequest(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Request permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/purchasing/requests";
    }

    // =====================================================
    // PURCHASE ORDER
    // =====================================================

    @GetMapping("/orders")
    public String orders(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute(
                "purchaseOrder",
                editId == null
                        ? new PurchaseOrder()
                        : service.getPurchaseOrder(editId)
        );

        model.addAttribute(
                "ordersList",
                service.getAllPurchaseOrders()
        );

        return "purchasing/orders";
    }

    @GetMapping("/orders/new")
    public String newPurchaseOrder(Model model) {

        model.addAttribute(
                "purchaseOrder",
                new PurchaseOrder()
        );

        model.addAttribute(
                "suppliersList",
                service.getAllSuppliers()
        );

        model.addAttribute(
                "requestsList",
                service.getAllPurchaseRequests()
        );

        model.addAttribute(
                "isEdit",
                false
        );

        return "purchasing/order-form";
    }

    @GetMapping("/orders/edit/{id}")
    public String editPurchaseOrder(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "purchaseOrder",
                service.getPurchaseOrder(id)
        );

        model.addAttribute(
                "suppliersList",
                service.getAllSuppliers()
        );

        model.addAttribute(
                "requestsList",
                service.getAllPurchaseRequests()
        );

        model.addAttribute(
                "isEdit",
                true
        );

        return "purchasing/order-form";
    }

    @PostMapping("/orders/save")
    public String savePurchaseOrder(
            @ModelAttribute PurchaseOrder purchaseOrder,
            RedirectAttributes redirectAttributes) {

        try {

            service.savePurchaseOrder(
                    purchaseOrder
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Order saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/purchasing/orders";
    }

    @PostMapping("/orders/archive/{id}")
    public String archivePurchaseOrder(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.archivePurchaseOrder(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Order archived successfully."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot archive Purchase Order."
            );
        }

        return "redirect:/purchasing/orders";
    }

    @PostMapping("/orders/restore/{id}")
    public String restorePurchaseOrder(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.restorePurchaseOrder(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Order restored to Pending status."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot restore Purchase Order."
            );
        }

        return "redirect:/purchasing/orders";
    }

    @PostMapping("/orders/delete/{id}")
    public String deletePurchaseOrder(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deletePurchaseOrder(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Order permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/purchasing/orders";
    }

    // =====================================================
    // PURCHASE ORDER ITEM
    // =====================================================

    @GetMapping("/items")
    public String items(
            @RequestParam(required = false) Long editId,
            Model model) {

        List<PurchaseOrder> purchaseOrders =
                service.getAllPurchaseOrders();

        List<Material> materials =
                materialService.getActiveMaterials(null);

        Map<Long, PurchaseOrder> orderMap =
                new HashMap<>();

        for (PurchaseOrder order : purchaseOrders) {
            orderMap.put(
                    order.getPoId(),
                    order
            );
        }

        Map<Long, Material> materialMap =
                new HashMap<>();

        for (Material material : materials) {
            materialMap.put(
                    material.getMaterialId(),
                    material
            );
        }

        model.addAttribute(
                "purchaseOrderItem",
                editId == null
                        ? new PurchaseOrderItem()
                        : service.getPurchaseOrderItem(editId)
        );

        model.addAttribute(
                "itemsList",
                service.getAllPurchaseOrderItems()
        );

        model.addAttribute(
                "orderMap",
                orderMap
        );

        model.addAttribute(
                "materialMap",
                materialMap
        );

        return "purchasing/items";
    }

    @GetMapping("/items/new")
    public String newPurchaseOrderItem(Model model) {

        model.addAttribute(
                "purchaseOrderItem",
                new PurchaseOrderItem()
        );

        model.addAttribute(
                "purchaseOrders",
                service.getAllPurchaseOrders()
        );

        model.addAttribute(
                "materials",
                materialService.getActiveMaterials(null)
        );

        model.addAttribute(
                "isEdit",
                false
        );

        return "purchasing/item-form";
    }

    @GetMapping("/items/edit/{id}")
    public String editPurchaseOrderItem(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "purchaseOrderItem",
                service.getPurchaseOrderItem(id)
        );

        model.addAttribute(
                "purchaseOrders",
                service.getAllPurchaseOrders()
        );

        model.addAttribute(
                "materials",
                materialService.getActiveMaterials(null)
        );

        model.addAttribute(
                "isEdit",
                true
        );

        return "purchasing/item-form";
    }

    @PostMapping("/items/save")
    public String savePurchaseOrderItem(
            @ModelAttribute PurchaseOrderItem purchaseOrderItem,
            RedirectAttributes redirectAttributes) {

        try {

            service.savePurchaseOrderItem(
                    purchaseOrderItem
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Order Item saved successfully."
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/purchasing/items";
    }

    @PostMapping("/items/delete/{id}")
    public String deletePurchaseOrderItem(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            service.deletePurchaseOrderItem(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Purchase Order Item permanently deleted."
            );

        } catch (Exception ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete Purchase Order Item."
            );
        }

        return "redirect:/purchasing/items";
    }
}