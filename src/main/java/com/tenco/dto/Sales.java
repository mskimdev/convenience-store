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
    private int productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private LocalDateTime soldAt;
}
