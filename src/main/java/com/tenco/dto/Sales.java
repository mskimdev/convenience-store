package com.tenco.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Sales {
    private int id;
    private int product_id;
    private int quantity;
    private BigDecimal unit_price;
    private LocalDateTime sold_at;
}
