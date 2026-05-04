package com.billing.product;

import com.billing.product.model.Product;
import com.billing.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            productRepository.save(Product.builder()
                    .name("Laptop Pro").description("High-performance laptop for professionals")
                    .price(new BigDecimal("999.99")).quantity(50).category("Electronics").createdBy(1L).build());
            productRepository.save(Product.builder()
                    .name("Wireless Mouse").description("Ergonomic wireless mouse")
                    .price(new BigDecimal("29.99")).quantity(200).category("Electronics").createdBy(1L).build());
            productRepository.save(Product.builder()
                    .name("Office Chair").description("Comfortable ergonomic office chair")
                    .price(new BigDecimal("299.99")).quantity(30).category("Furniture").createdBy(1L).build());
            productRepository.save(Product.builder()
                    .name("Notebook Set").description("Premium notebook set for professionals")
                    .price(new BigDecimal("12.99")).quantity(500).category("Stationery").createdBy(1L).build());
            productRepository.save(Product.builder()
                    .name("Coffee Maker").description("Automatic coffee maker with timer")
                    .price(new BigDecimal("79.99")).quantity(75).category("Appliances").createdBy(1L).build());
        }
    }
}
