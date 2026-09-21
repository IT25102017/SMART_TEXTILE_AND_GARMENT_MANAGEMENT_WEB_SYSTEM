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
    // READ ALL GOODS RECEIPTS
    // =====================================================

    public List<GoodsReceipt> getAll() {

        return receiptRepository
                .findAllByOrderByReceivedDateDesc();
    }


    // =====================================================
    // READ ONE GOODS RECEIPT
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
    // Archived Purchase Orders are excluded from forms.
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
    // Archived Suppliers are excluded from forms.
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

            receipt.setStatus("RECEIVED");
        }


        return receiptRepository.save(receipt);
    }


    // =====================================================
    // UPDATE
    //
    // Purchase Order ID and Supplier ID are preserved
    // because they identify the original purchasing record.
    // =====================================================

    @Transactional
    public GoodsReceipt update(
            GoodsReceipt submittedReceipt) {

        if (submittedReceipt.getReceiptId() == null) {

            throw new IllegalArgumentException(
                    "Receipt ID is required for update."
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
         * An archived receipt must stay archived until
         * the Restore action is used.
         */
        if (!"ARCHIVED".equalsIgnoreCase(
                existing.getStatus())) {

            String newStatus =
                    submittedReceipt.getStatus();

            if (newStatus == null ||
                    newStatus.isBlank()) {

                newStatus = "RECEIVED";
            }

            existing.setStatus(newStatus);
        }


        return receiptRepository.save(existing);
    }


    // =====================================================
    // ARCHIVE
    // =====================================================

    @Transactional
    public void archive(Long id) {

        GoodsReceipt receipt =
                getById(id);

        receipt.setStatus("ARCHIVED");

        receiptRepository.save(receipt);
    }


    // =====================================================
    // RESTORE
    // =====================================================

    @Transactional
    public void restore(Long id) {

        GoodsReceipt receipt =
                getById(id);


        /*
         * Restoring is allowed only when the linked
         * Purchase Order and Supplier are still valid.
         */
        validatePurchasingRelationship(
                receipt.getPoId(),
                receipt.getSupplierId()
        );


        receipt.setStatus("RECEIVED");

        receiptRepository.save(receipt);
    }


    // =====================================================
    // SAFE DELETE
    // =====================================================

    @Transactional
    public void delete(Long id) {

        GoodsReceipt receipt =
                getById(id);


        boolean usedInStockMovement =
                movementRepository
                        .existsByReferenceTypeIgnoreCaseAndReferenceId(
                                "GOODS_RECEIPT",
                                id
                        );


        if (usedInStockMovement) {

            throw new IllegalArgumentException(
                    "Cannot permanently delete this goods receipt because it is already used in inventory transactions. Please Archive it instead."
            );
        }


        receiptRepository.delete(receipt);

        receiptRepository.flush();
    }


    // =====================================================
    // PURCHASING RELATIONSHIP VALIDATION
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
                    "Archived Purchase Orders cannot be used for a Goods Receipt."
            );
        }


        if ("ARCHIVED".equalsIgnoreCase(
                supplier.getStatus())) {

            throw new IllegalArgumentException(
                    "Archived Suppliers cannot be used for a Goods Receipt."
            );
        }


        if (purchaseOrder.getSupplierId() == null ||
                !purchaseOrder.getSupplierId()
                        .equals(supplierId)) {

            throw new IllegalArgumentException(
                    "The selected Supplier does not match the Supplier assigned to this Purchase Order."
            );
        }
    }
}