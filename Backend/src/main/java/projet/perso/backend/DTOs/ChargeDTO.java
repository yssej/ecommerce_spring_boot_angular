package projet.perso.backend.DTOs;

import lombok.Data;

@Data
public class ChargeDTO {
    private String id;
    private Long amount;
    private String currency;
    private String status;

    public ChargeDTO(String id, Long amount, String currency, String status) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }
}
