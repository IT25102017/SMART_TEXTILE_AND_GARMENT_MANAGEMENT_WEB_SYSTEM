package com.lankatex.smarttextile.inventory.repository;

import com.lankatex.smarttextile.inventory.entity.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsReceiptRepository
        extends JpaRepository<GoodsReceipt, Long> {

    List<GoodsReceipt> findAllByOrderByReceivedDateDesc();
}