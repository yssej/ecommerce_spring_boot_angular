package projet.perso.backend.Services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import projet.perso.backend.DTOs.UserLoginDTO;
import projet.perso.backend.Exception.AppException;
import projet.perso.backend.entities.User;
import projet.perso.backend.entities.UserRole;
import projet.perso.backend.repositories.UserRepository;
import projet.perso.backend.repositories.UserRoleRepository;
import projet.perso.backend.services.AuthenticationServiceImpl;
import projet.perso.backend.services.TokenService;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void register_shouldRegisterUser_whenValid() {
        // GIVEN
        UserRole userRole = new UserRole("USER");

        when(userRoleRepository.findByAuthority("USER"))
                .thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("thePassword"))
                .thenReturn("hashedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        User savedUser = new User("test@mail.com", "hashedPassword", Set.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // WHEN
        User result = authenticationService.register("test@mail.com", "thePassword");

        // THEN
        assertNotNull(result);
        assertEquals("test@mail.com", result.getEmail());
        assertEquals("hashedPassword", result.getPassword());

        verify(userRoleRepository).findByAuthority("USER");
        verify(passwordEncoder).encode("thePassword");
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("test@mail.com", capturedUser.getEmail());
        assertEquals("hashedPassword", capturedUser.getPassword());
        assertTrue(capturedUser.getAuthorities().contains(userRole));
    }

}
