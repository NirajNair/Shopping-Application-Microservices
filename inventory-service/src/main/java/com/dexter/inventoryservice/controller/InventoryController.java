package com.dexter.inventoryservice.controller;

import com.dexter.inventoryservice.dto.InventoryResponse;
import com.dexter.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    // http://inventory-service/api/inventory?skuCode=1&skuCode=2&quantity=1&quantity=2
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode, @RequestParam List<Integer> quantity) {
        return inventoryService.isInStock(skuCode, quantity);
    }
}
