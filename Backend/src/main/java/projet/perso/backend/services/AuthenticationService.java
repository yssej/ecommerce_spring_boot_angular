package projet.perso.backend.services;

import projet.perso.backend.DTOs.UserLoginDTO;
import projet.perso.backend.entities.User;

public interface AuthenticationService {
    User register(String email, String password);
    UserLoginDTO login(String email, String password);
}
