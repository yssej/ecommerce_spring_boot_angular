package projet.perso.backend.DTOs;

import lombok.Data;

@Data
public class PaymentMethodDTO {
    private String type;
    private CardDTO card;
}
