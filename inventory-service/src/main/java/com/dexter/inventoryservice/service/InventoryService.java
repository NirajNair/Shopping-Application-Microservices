package com.dexter.inventoryservice.service;

import com.dexter.inventoryservice.dto.InventoryResponse;
import com.dexter.inventoryservice.model.Inventory;
import com.dexter.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodeList, List<Integer> quantity) {
        List<Inventory> inventoryList = inventoryRepository.findBySkuCodeIn(skuCodeList);
        return IntStream.range(0, inventoryList.size())
                .mapToObj(i -> {
                    return InventoryResponse.builder()
                           .skuCode(inventoryList.get(i).getSkuCode())
                           .isInStock(inventoryList.get(i).getQuantity() >= quantity.get(i))
                           .build();
                })
                .collect(Collectors.toList());


//                .map(inventory -> {
//                    InventoryResponse.builder()Integer                      .skuCode(inventory.getSkuCode())
//                            .isInStock(inventory.getQuantity()>)
//                })
    }
}
