package com.kemboishoppingcartapp.inventoryservice.controller;

import com.kemboishoppingcartapp.inventoryservice.dto.InventoryResponse;
import com.kemboishoppingcartapp.inventoryservice.model.Inventory;
import com.kemboishoppingcartapp.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
private final InventoryService inventoryService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInstock(@RequestParam List<String> skuCode){
        return inventoryService.isInStock(skuCode);
    }
}
