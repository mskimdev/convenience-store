package com.tenco.dto;


import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Product {
    private int id;
    private String barcode;
    private String name;
    private String category;
    private BigDecimal price;
    private BigDecimal cost;
    private int stock;
    private int minStock;
    private Date expireDate;
    private boolean isActive;
}
