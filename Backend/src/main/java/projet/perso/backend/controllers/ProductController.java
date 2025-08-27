package projet.perso.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projet.perso.backend.DTOs.ProductDTO;
import projet.perso.backend.Exception.AppException;
import projet.perso.backend.entities.Product;
import projet.perso.backend.services.ProductServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductServiceImpl productService;

    @GetMapping("all")
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @PostMapping("add")
    public Product add(@RequestBody ProductDTO productDTO) {
        if (
                productDTO.getName() == null ||
                        productDTO.getName().isEmpty() ||
                        productDTO.getDescription() == null || productDTO.getDescription().isEmpty() ||
                        productDTO.getImgUrl() == null || productDTO.getImgUrl().isEmpty() ||
                        productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {

            throw new AppException("All fields are required.", HttpStatus.BAD_REQUEST);
        }
        Product newProduct = new Product();
        newProduct.setName(productDTO.getName());
        newProduct.setDescription(productDTO.getDescription());
        newProduct.setPrice(productDTO.getPrice());
        newProduct.setImgUrl(productDTO.getImgUrl());

        return productService.add(newProduct);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        Optional<Product> productOptional = productService.getProductById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return ResponseEntity.ok(product);
        } else {
            throw new AppException("Product not found", HttpStatus.NOT_FOUND);
        }
    }
}
