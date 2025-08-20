package projet.perso.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projet.perso.backend.DTOs.UserDTO;
import projet.perso.backend.Exception.AppException;
import projet.perso.backend.entities.User;
import projet.perso.backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException("User not found.", HttpStatus.NOT_FOUND));
    }

    public User getUserById(Long id, Authentication authentication) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found.", HttpStatus.NOT_FOUND));
        if (!user.getEmail().equals(authentication.getName())) {
            throw new AppException("Access denied.", HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    public User updateUserById(Long id, UserDTO userDTO, Authentication authentication) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        if (!user.getEmail().equals(authentication.getName())) {
            throw new AppException("Access denied", HttpStatus.BAD_REQUEST);
        }
        String newEmail = userDTO.getEmail();
        String newPassword = userDTO.getPassword();

        if (newEmail != null && !newEmail.isEmpty()) {
            user.setEmail(newEmail);
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            String hashedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedNewPassword);
        }
        userRepository.save(user);
        return user;
    }
}
