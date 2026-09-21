package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.GoodsReceipt;
import com.lankatex.smarttextile.inventory.repository.GoodsReceiptRepository;
import com.lankatex.smarttextile.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoodsReceiptService {

    private final GoodsReceiptRepository receiptRepository;

    private final StockMovementRepository movementRepository;


    public GoodsReceiptService(
            GoodsReceiptRepository receiptRepository,
            StockMovementRepository movementRepository) {

        this.receiptRepository = receiptRepository;
        this.movementRepository = movementRepository;
    }


    // =====================================================
    // READ ALL
    // =====================================================

    public List<GoodsReceipt> getAll() {

        return receiptRepository
                .findAllByOrderByReceivedDateDesc();
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
    // CREATE
    // =====================================================

    @Transactional
    public GoodsReceipt create(
            GoodsReceipt receipt) {

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
                getById(submittedReceipt.getReceiptId());


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


        /*
         * Future-proof safety check:
         * if this receipt is already referenced by a
         * stock transaction, permanent deletion is blocked.
         */
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
}