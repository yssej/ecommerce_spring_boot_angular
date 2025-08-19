package projet.perso.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projet.perso.backend.entities.Product;
import projet.perso.backend.repositories.ProductRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    public Product add(Product product) {
        return productRepository.save(product);
    }
}
