package projet.perso.backend.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import projet.perso.backend.DTOs.UserDTO;
import projet.perso.backend.entities.User;

public interface UserService {
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
    User getUserById(Long id, Authentication authentication);
    User updateUserById(Long id, UserDTO userDTO, Authentication authentication);

}
