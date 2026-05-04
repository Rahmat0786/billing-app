package com.billing.billing.service;

import com.billing.billing.client.ProductServiceClient;
import com.billing.billing.dto.*;
import com.billing.billing.exception.BadRequestException;
import com.billing.billing.exception.ResourceNotFoundException;
import com.billing.billing.model.*;
import com.billing.billing.repository.InvoiceRepository;
import com.billing.billing.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public OrderResponse createOrder(OrderRequest req, Long userId) {
        Order order = Order.builder()
                .userId(userId)
                .userEmail(req.getUserEmail())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : req.getItems()) {
            ProductInfo product = productServiceClient.getProductById(itemReq.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId());
            }
            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();
            orderItems.add(item);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Update stock for each product
        for (OrderItemRequest itemReq : req.getItems()) {
            productServiceClient.updateStock(itemReq.getProductId(), itemReq.getQuantity());
        }

        // Generate invoice
        BigDecimal taxRate = new BigDecimal("0.18");
        BigDecimal taxAmount = totalAmount.multiply(taxRate);
        BigDecimal grandTotal = totalAmount.add(taxAmount);

        Invoice invoice = Invoice.builder()
                .order(savedOrder)
                .userId(userId)
                .totalAmount(totalAmount)
                .taxAmount(taxAmount)
                .taxRate(taxRate)
                .grandTotal(grandTotal)
                .build();
        invoiceRepository.save(invoice);

        return toOrderResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return toOrderResponse(order);
    }

    public List<OrderResponse> getAllOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toOrderResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toOrderResponse).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        return toOrderResponse(orderRepository.save(order));
    }

    public InvoiceResponse getInvoiceByOrderId(Long orderId) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for order id: " + orderId));
        return toInvoiceResponse(invoice);
    }

    public List<InvoiceResponse> getAllInvoicesByUser(Long userId) {
        return invoiceRepository.findByUserId(userId).stream()
                .map(this::toInvoiceResponse).collect(Collectors.toList());
    }

    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .userEmail(order.getUserEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private InvoiceResponse toInvoiceResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .orderNumber(invoice.getOrder() != null ? invoice.getOrder().getOrderNumber() : null)
                .userId(invoice.getUserId())
                .totalAmount(invoice.getTotalAmount())
                .taxAmount(invoice.getTaxAmount())
                .taxRate(invoice.getTaxRate())
                .grandTotal(invoice.getGrandTotal())
                .status(invoice.getStatus())
                .issuedAt(invoice.getIssuedAt())
                .dueDate(invoice.getDueDate())
                .build();
    }
}
