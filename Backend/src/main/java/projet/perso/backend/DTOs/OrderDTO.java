package projet.perso.backend.DTOs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDTO {
    private Long id;
    private String dateCreated;
    private BigDecimal total;
}
