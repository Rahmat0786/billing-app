package com.billing.billing.dto;

import com.billing.billing.model.InvoiceStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private String orderNumber;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;
    private BigDecimal grandTotal;
    private InvoiceStatus status;
    private LocalDateTime issuedAt;
    private LocalDateTime dueDate;
}
