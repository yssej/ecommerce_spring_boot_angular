package projet.perso.backend.services;

import projet.perso.backend.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long productId);
    Product add(Product product);
}
