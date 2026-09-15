package com.lankatex.smarttextile.inventory.service;

import com.lankatex.smarttextile.inventory.entity.GoodsReceipt;
import com.lankatex.smarttextile.inventory.repository.GoodsReceiptRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsReceiptService {

    private final GoodsReceiptRepository receiptRepository;

    public GoodsReceiptService(
            GoodsReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    public List<GoodsReceipt> getAll() {
        return receiptRepository
                .findAllByOrderByReceivedDateDesc();
    }

    public GoodsReceipt save(
            GoodsReceipt receipt) {

        if (receipt.getStatus() == null ||
                receipt.getStatus().isBlank()) {

            receipt.setStatus("RECEIVED");
        }

        return receiptRepository.save(receipt);
    }
}