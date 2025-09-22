package projet.perso.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projet.perso.backend.DTOs.UserLoginDTO;
import projet.perso.backend.Exception.AppException;
import projet.perso.backend.entities.User;
import projet.perso.backend.entities.UserRole;
import projet.perso.backend.repositories.UserRepository;
import projet.perso.backend.repositories.UserRoleRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public User register(String email, String password) {

        if(userRepository.findByEmail(email).isPresent()){
            throw new AppException("Email address already in use!", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(password);

        UserRole userRole = userRoleRepository.findByAuthority("USER")
                .orElseThrow(() ->
                        new AppException("Role USER not found", HttpStatus.NOT_FOUND)
                );
        Set<UserRole> authorities = new HashSet<>();

        authorities.add(userRole);

        return userRepository.save(new User(email, encodedPassword, authorities));
    }

    public UserLoginDTO login(String email, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String token = tokenService.generateJwt(auth);

            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                return new UserLoginDTO(user.getId(), user, token);
            } else {
                return new UserLoginDTO(null, null, "");
            }

        } catch (AuthenticationException e) {
            return new UserLoginDTO(null, null, "");
        }
    }
}
