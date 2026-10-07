package com.lankatex.smarttextile.purchasing.service;

import com.lankatex.smarttextile.purchasing.dto.PurchasingDashboardStats;
import com.lankatex.smarttextile.purchasing.entity.PurchaseOrder;
import com.lankatex.smarttextile.purchasing.entity.PurchaseOrderItem;
import com.lankatex.smarttextile.purchasing.entity.PurchaseRequest;
import com.lankatex.smarttextile.purchasing.entity.Supplier;
import com.lankatex.smarttextile.purchasing.repository.PurchaseOrderItemRepository;
import com.lankatex.smarttextile.purchasing.repository.PurchaseOrderRepository;
import com.lankatex.smarttextile.purchasing.repository.PurchaseRequestRepository;
import com.lankatex.smarttextile.purchasing.repository.SupplierRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PurchasingService {

    private final SupplierRepository supplierRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;


    public PurchasingService(
            SupplierRepository supplierRepository,
            PurchaseRequestRepository purchaseRequestRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            PurchaseOrderItemRepository purchaseOrderItemRepository) {

        this.supplierRepository = supplierRepository;
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
    }


    // =====================================================
    // DASHBOARD
    // =====================================================

    public PurchasingDashboardStats getDashboardStats() {

        return new PurchasingDashboardStats(
                supplierRepository.count(),
                purchaseRequestRepository.count(),
                purchaseOrderRepository.count(),
                purchaseOrderItemRepository.count()
        );
    }


    // =====================================================
    // SUPPLIER CRUD
    // =====================================================

    public List<Supplier> getAllSuppliers() {

        // Active + Archived suppliers දෙකම show කරනවා.
        // Archived record Restore කරන්න ඒක අවශ්‍යයි.
        return supplierRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "supplierId"
                )
        );
    }


    public Supplier getSupplier(Long id) {

        return supplierRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Supplier not found: " + id
                        )
                );
    }


    // CREATE / UPDATE
    public Supplier saveSupplier(Supplier supplier) {

        if (supplier.getSupplierName() == null
                || supplier.getSupplierName().isBlank()) {

            throw new IllegalArgumentException(
                    "Supplier name is required"
            );
        }

        if (supplier.getStatus() == null
                || supplier.getStatus().isBlank()) {

            supplier.setStatus("Active");
        }

        return supplierRepository.save(supplier);
    }


    // ARCHIVE
    // Active -> Archived
    public void archiveSupplier(Long id) {

        Supplier supplier = getSupplier(id);

        supplier.setStatus("Archived");

        supplierRepository.save(supplier);
    }


    // RESTORE
    // Archived -> Active
    public void restoreSupplier(Long id) {

        Supplier supplier = getSupplier(id);

        supplier.setStatus("Active");

        supplierRepository.save(supplier);
    }


    // HARD DELETE
    public void deleteSupplier(Long id) {

        Supplier supplier = getSupplier(id);

        try {

            supplierRepository.delete(supplier);

            // Execute DELETE immediately
            supplierRepository.flush();

        } catch (DataIntegrityViolationException ex) {

            throw new IllegalArgumentException(
                    "Cannot permanently delete this Supplier because it may already be used by a Purchase Order. Archive it instead."
            );
        }
    }


    // =====================================================
    // PURCHASE REQUEST CRUD
    // =====================================================

    public List<PurchaseRequest> getAllPurchaseRequests() {

        return purchaseRequestRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "requestId"
                )
        );
    }


    public PurchaseRequest getPurchaseRequest(Long id) {

        return purchaseRequestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Purchase Request not found: " + id
                        )
                );
    }


    // CREATE / UPDATE
    public PurchaseRequest savePurchaseRequest(
            PurchaseRequest purchaseRequest) {

        if (purchaseRequest.getReason() == null
                || purchaseRequest.getReason().isBlank()) {

            throw new IllegalArgumentException(
                    "Request reason is required"
            );
        }

        if (purchaseRequest.getStatus() == null
                || purchaseRequest.getStatus().isBlank()) {

            purchaseRequest.setStatus("Pending");
        }

        return purchaseRequestRepository.save(
                purchaseRequest
        );
    }


    // ARCHIVE
    public void archivePurchaseRequest(Long id) {

        PurchaseRequest purchaseRequest =
                getPurchaseRequest(id);

        purchaseRequest.setStatus("Archived");

        purchaseRequestRepository.save(
                purchaseRequest
        );
    }


    // RESTORE
    //
    // Current database එකේ previous status save කරන
    // වෙනම field එකක් නැති නිසා Archived record එක
    // Pending status එකට restore කරනවා.
    public void restorePurchaseRequest(Long id) {

        PurchaseRequest purchaseRequest =
                getPurchaseRequest(id);

        purchaseRequest.setStatus("Pending");

        purchaseRequestRepository.save(
                purchaseRequest
        );
    }


    // HARD DELETE
    public void deletePurchaseRequest(Long id) {

        PurchaseRequest purchaseRequest =
                getPurchaseRequest(id);

        try {

            purchaseRequestRepository.delete(
                    purchaseRequest
            );

            purchaseRequestRepository.flush();

        } catch (DataIntegrityViolationException ex) {

            throw new IllegalArgumentException(
                    "Cannot permanently delete this Purchase Request because it may already be used by a Purchase Order. Archive it instead."
            );
        }
    }


    // =====================================================
    // PURCHASE ORDER CRUD
    // =====================================================

    public List<PurchaseOrder> getAllPurchaseOrders() {

        return purchaseOrderRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "poId"
                )
        );
    }


    public PurchaseOrder getPurchaseOrder(Long id) {

        return purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Purchase Order not found: " + id
                        )
                );
    }


    // CREATE / UPDATE
    public PurchaseOrder savePurchaseOrder(
            PurchaseOrder purchaseOrder) {

        if (purchaseOrder.getSupplierId() == null) {

            throw new IllegalArgumentException(
                    "Supplier ID is required"
            );
        }

        if (purchaseOrder.getStatus() == null
                || purchaseOrder.getStatus().isBlank()) {

            purchaseOrder.setStatus("Pending");
        }

        return purchaseOrderRepository.save(
                purchaseOrder
        );
    }


    // ARCHIVE
    public void archivePurchaseOrder(Long id) {

        PurchaseOrder purchaseOrder =
                getPurchaseOrder(id);

        purchaseOrder.setStatus("Archived");

        purchaseOrderRepository.save(
                purchaseOrder
        );
    }


    // RESTORE
    public void restorePurchaseOrder(Long id) {

        PurchaseOrder purchaseOrder =
                getPurchaseOrder(id);

        purchaseOrder.setStatus("Pending");

        purchaseOrderRepository.save(
                purchaseOrder
        );
    }


    // HARD DELETE
    public void deletePurchaseOrder(Long id) {

        PurchaseOrder purchaseOrder =
                getPurchaseOrder(id);

        try {

            purchaseOrderRepository.delete(
                    purchaseOrder
            );

            purchaseOrderRepository.flush();

        } catch (DataIntegrityViolationException ex) {

            throw new IllegalArgumentException(
                    "Cannot permanently delete this Purchase Order because Purchase Order Items may already use it. Delete child items first or Archive the order."
            );
        }
    }


    // =====================================================
    // PURCHASE ORDER ITEM CRUD
    // =====================================================

    public List<PurchaseOrderItem> getAllPurchaseOrderItems() {

        return purchaseOrderItemRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "poItemId"
                )
        );
    }


    public PurchaseOrderItem getPurchaseOrderItem(
            Long id) {

        return purchaseOrderItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Purchase Order Item not found: " + id
                        )
                );
    }


    // CREATE / UPDATE
    public PurchaseOrderItem savePurchaseOrderItem(
            PurchaseOrderItem purchaseOrderItem) {

        if (purchaseOrderItem.getPoId() == null) {

            throw new IllegalArgumentException(
                    "PO ID is required"
            );
        }

        if (purchaseOrderItem.getMaterialId() == null) {

            throw new IllegalArgumentException(
                    "Material ID is required"
            );
        }

        if (purchaseOrderItem.getQuantity() == null
                || purchaseOrderItem.getQuantity().signum() <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }

        if (purchaseOrderItem.getUnitPrice() == null
                || purchaseOrderItem.getUnitPrice().signum() < 0) {

            throw new IllegalArgumentException(
                    "Unit Price cannot be negative"
            );
        }

        return purchaseOrderItemRepository.save(
                purchaseOrderItem
        );
    }


    // HARD DELETE
    //
    // PurchaseOrderItem entity එකේ status field එකක් නැහැ.
    // ඒ නිසා Archive / Restore apply වෙන්නේ නැහැ.
    public void deletePurchaseOrderItem(Long id) {

        PurchaseOrderItem purchaseOrderItem =
                getPurchaseOrderItem(id);

        purchaseOrderItemRepository.delete(
                purchaseOrderItem
        );

        purchaseOrderItemRepository.flush();
    }
}