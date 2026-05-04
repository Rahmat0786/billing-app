package com.billing.billing.controller;

import com.billing.billing.dto.InvoiceResponse;
import com.billing.billing.dto.OrderRequest;
import com.billing.billing.dto.OrderResponse;
import com.billing.billing.model.OrderStatus;
import com.billing.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createOrder(request, userId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long userId) {
        return ResponseEntity.ok(billingService.getAllOrdersByUser(userId));
    }

    @GetMapping("/orders/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(billingService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getOrderById(id));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(billingService.updateOrderStatus(id, status));
    }

    @GetMapping("/invoices/order/{orderId}")
    public ResponseEntity<InvoiceResponse> getInvoiceByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(billingService.getInvoiceByOrderId(orderId));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponse>> getMyInvoices(
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long userId) {
        return ResponseEntity.ok(billingService.getAllInvoicesByUser(userId));
    }
}
