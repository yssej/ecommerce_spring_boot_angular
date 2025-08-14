package projet.perso.backend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projet.perso.backend.entities.User;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserLoginDTO {
    private Long id;
    private User user;
    private String jwt;
}
