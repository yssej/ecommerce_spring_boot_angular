package projet.perso.backend.DTOs;

import lombok.Data;

@Data
public class CardDTO {
    private String number;
    private String expDate;
    private String cvc;
    private String zip;
}
