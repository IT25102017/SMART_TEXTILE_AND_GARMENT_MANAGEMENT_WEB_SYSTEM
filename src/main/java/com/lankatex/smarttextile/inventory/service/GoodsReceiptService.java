package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.GoodsReceipt;
import com.lankatex.smarttextile.inventory.repository.GoodsReceiptRepository;
import com.lankatex.smarttextile.inventory.repository.StockMovementRepository;

import com.lankatex.smarttextile.purchasing.entity.PurchaseOrder;
import com.lankatex.smarttextile.purchasing.entity.Supplier;
import com.lankatex.smarttextile.purchasing.repository.PurchaseOrderRepository;
import com.lankatex.smarttextile.purchasing.repository.SupplierRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GoodsReceiptService {

    private final GoodsReceiptRepository receiptRepository;

    private final StockMovementRepository movementRepository;

    private final PurchaseOrderRepository purchaseOrderRepository;

    private final SupplierRepository supplierRepository;


    public GoodsReceiptService(
            GoodsReceiptRepository receiptRepository,
            StockMovementRepository movementRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            SupplierRepository supplierRepository) {

        this.receiptRepository = receiptRepository;
        this.movementRepository = movementRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
    }


    // =====================================================
    // READ ALL
    // =====================================================

    public List<GoodsReceipt> getAll() {

        return receiptRepository.findAll();
    }


    // =====================================================
    // READ ONE
    // =====================================================

    public GoodsReceipt getById(Long id) {

        return receiptRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Goods receipt not found."
                        )
                );
    }


    // =====================================================
    // AVAILABLE PURCHASE ORDERS
    //
    // Used by the Goods Receipt form.
    // Archived Purchase Orders are excluded.
    // =====================================================

    public List<PurchaseOrder> getAvailablePurchaseOrders() {

        return purchaseOrderRepository
                .findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "poId"
                        )
                )
                .stream()
                .filter(order ->
                        order.getStatus() == null ||
                                !"ARCHIVED".equalsIgnoreCase(
                                        order.getStatus()
                                )
                )
                .toList();
    }


    // =====================================================
    // ACTIVE SUPPLIERS
    //
    // Used by the Goods Receipt form.
    // Archived Suppliers are excluded.
    // =====================================================

    public List<Supplier> getActiveSuppliers() {

        return supplierRepository
                .findAll(
                        Sort.by(
                                Sort.Direction.ASC,
                                "supplierName"
                        )
                )
                .stream()
                .filter(supplier ->
                        supplier.getStatus() == null ||
                                !"ARCHIVED".equalsIgnoreCase(
                                        supplier.getStatus()
                                )
                )
                .toList();
    }


    // =====================================================
    // PURCHASE ORDER LOOKUP MAP
    //
    // Used by the Goods Receipt list page.
    // =====================================================

    public Map<Long, PurchaseOrder> getPurchaseOrderMap() {

        return purchaseOrderRepository
                .findAll()
                .stream()
                .collect(
                        Collectors.toMap(
                                PurchaseOrder::getPoId,
                                Function.identity()
                        )
                );
    }


    // =====================================================
    // SUPPLIER LOOKUP MAP
    //
    // Used by the Goods Receipt list page.
    // =====================================================

    public Map<Long, Supplier> getSupplierMap() {

        return supplierRepository
                .findAll()
                .stream()
                .collect(
                        Collectors.toMap(
                                Supplier::getSupplierId,
                                Function.identity()
                        )
                );
    }


    // =====================================================
    // CREATE
    // =====================================================

    @Transactional
    public GoodsReceipt create(
            GoodsReceipt receipt) {

        validatePurchasingRelationship(
                receipt.getPoId(),
                receipt.getSupplierId()
        );


        if (receipt.getReceivedDate() == null) {

            throw new IllegalArgumentException(
                    "Received date is required."
            );
        }


        if (receipt.getStatus() == null ||
                receipt.getStatus().isBlank()) {

            receipt.setStatus(
                    "RECEIVED"
            );
        }


        return receiptRepository.save(
                receipt
        );
    }


    // =====================================================
    // UPDATE
    //
    // Purchase Order and Supplier are intentionally
    // preserved because they represent the original
    // purchasing relationship of this receipt.
    //
    // Historical receipts remain editable even when the
    // original Purchase Order or Supplier is later archived
    // or removed from the Purchasing module.
    // =====================================================

    @Transactional
    public GoodsReceipt update(
            GoodsReceipt submittedReceipt) {

        if (submittedReceipt.getReceiptId() == null) {

            throw new IllegalArgumentException(
                    "Goods Receipt ID is required for update."
            );
        }


        GoodsReceipt existing =
                getById(
                        submittedReceipt.getReceiptId()
                );


        if (submittedReceipt.getReceivedDate() == null) {

            throw new IllegalArgumentException(
                    "Received date is required."
            );
        }


        /*
         * Preserve original Purchase Order and Supplier.
         */
        Long originalPoId =
                existing.getPoId();

        Long originalSupplierId =
                existing.getSupplierId();


        existing.setPoId(
                originalPoId
        );

        existing.setSupplierId(
                originalSupplierId
        );


        existing.setReceivedDate(
                submittedReceipt.getReceivedDate()
        );

        existing.setReceivedBy(
                submittedReceipt.getReceivedBy()
        );

        existing.setRemarks(
                submittedReceipt.getRemarks()
        );


        /*
         * Archived records stay archived until the user
         * explicitly restores them from the list page.
         */
        if (!"ARCHIVED".equalsIgnoreCase(
                existing.getStatus())) {

            String submittedStatus =
                    submittedReceipt.getStatus();


            if (submittedStatus == null ||
                    submittedStatus.isBlank()) {

                existing.setStatus(
                        "RECEIVED"
                );

            } else {

                existing.setStatus(
                        submittedStatus
                );
            }
        }


        return receiptRepository.save(
                existing
        );
    }


    // =====================================================
    // ARCHIVE
    // =====================================================

    @Transactional
    public void archive(Long id) {

        GoodsReceipt receipt =
                getById(id);


        receipt.setStatus(
                "ARCHIVED"
        );


        receiptRepository.save(
                receipt
        );
    }


    // =====================================================
    // RESTORE
    //
    // Historical purchasing references are preserved.
    //
    // Restore only changes the Goods Receipt status.
    // The original Purchase Order or Supplier does not
    // need to still exist in the Purchasing module.
    // =====================================================

    @Transactional
    public void restore(Long id) {

        GoodsReceipt receipt =
                getById(id);


        receipt.setStatus(
                "RECEIVED"
        );


        receiptRepository.save(
                receipt
        );
    }


    // =====================================================
    // SAFE HARD DELETE
    //
    // A Goods Receipt cannot be permanently deleted when
    // Stock Movement records already reference it.
    // =====================================================

    @Transactional
    public void delete(Long id) {

        GoodsReceipt receipt =
                getById(id);


        boolean usedByStockMovement =
                movementRepository
                        .existsByReferenceTypeIgnoreCaseAndReferenceId(
                                "GOODS_RECEIPT",
                                id
                        );


        if (usedByStockMovement) {

            throw new IllegalArgumentException(
                    "Cannot permanently delete this Goods Receipt because it is already referenced by Stock Movement history. Please Archive it instead."
            );
        }


        receiptRepository.delete(
                receipt
        );


        receiptRepository.flush();
    }


    // =====================================================
    // PURCHASING RELATIONSHIP VALIDATION
    //
    // Used when creating a NEW Goods Receipt.
    //
    // Rules:
    // 1. Purchase Order is required.
    // 2. Supplier is required.
    // 3. Purchase Order must exist.
    // 4. Supplier must exist.
    // 5. Purchase Order must not be archived.
    // 6. Supplier must not be archived.
    // 7. Purchase Order must belong to the selected Supplier.
    // =====================================================

    private void validatePurchasingRelationship(
            Long poId,
            Long supplierId) {

        if (poId == null) {

            throw new IllegalArgumentException(
                    "Purchase Order is required."
            );
        }


        if (supplierId == null) {

            throw new IllegalArgumentException(
                    "Supplier is required."
            );
        }


        PurchaseOrder purchaseOrder =
                purchaseOrderRepository
                        .findById(poId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Selected Purchase Order does not exist."
                                )
                        );


        Supplier supplier =
                supplierRepository
                        .findById(supplierId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Selected Supplier does not exist."
                                )
                        );


        if ("ARCHIVED".equalsIgnoreCase(
                purchaseOrder.getStatus())) {

            throw new IllegalArgumentException(
                    "Archived Purchase Orders cannot be used for a new Goods Receipt."
            );
        }


        if ("ARCHIVED".equalsIgnoreCase(
                supplier.getStatus())) {

            throw new IllegalArgumentException(
                    "Archived Suppliers cannot be used for a new Goods Receipt."
            );
        }


        if (purchaseOrder.getSupplierId() == null ||
                !purchaseOrder
                        .getSupplierId()
                        .equals(supplierId)) {

            throw new IllegalArgumentException(
                    "The selected Supplier does not match the selected Purchase Order."
            );
        }
    }
}