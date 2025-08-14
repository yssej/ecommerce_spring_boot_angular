package projet.perso.backend.DTOs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subTotal;
    private ProductDTO product;
}
