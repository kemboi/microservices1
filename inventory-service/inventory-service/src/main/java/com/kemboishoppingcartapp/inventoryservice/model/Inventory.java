package com.kemboishoppingcartapp.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_inventory")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "inventory_GEN")
    private Long id;
    private String skuCode;
    private int quantity;

}
