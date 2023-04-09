package com.dexter.inventoryservice.service;

import com.dexter.inventoryservice.model.Inventory;
import com.dexter.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode) {
        Optional<Inventory> inventoryStock = inventoryRepository.findBySkuCode(skuCode);
        log.info("Stock checked!");
        return inventoryStock.filter(inventory -> inventory.getQuantity() > 0).isPresent();

    }
}
