package com.billing.billing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String invoiceNumber;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;
    private BigDecimal grandTotal;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private LocalDateTime issuedAt;
    private LocalDateTime dueDate;

    @PrePersist
    protected void onCreate() {
        invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        issuedAt = LocalDateTime.now();
        dueDate = issuedAt.plusDays(30);
        if (status == null) status = InvoiceStatus.ISSUED;
        if (taxRate == null) taxRate = new BigDecimal("0.18");
    }
}
