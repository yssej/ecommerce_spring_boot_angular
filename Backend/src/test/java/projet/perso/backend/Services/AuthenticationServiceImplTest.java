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

    @Test
    void register_shouldThrowException_whenUserRoleNotFound() {
        // GIVEN
        when(userRoleRepository.findByAuthority("USER"))
                .thenReturn(Optional.empty()); // Le rôle USER n'existe pas

        // WHEN + THEN
        assertThrows(NoSuchElementException.class, () -> {
            authenticationService.register("test@mail.com", "pass123");
        });

        verify(userRoleRepository).findByAuthority("USER");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExist() {
        // GIVEN
        String email = "test@mail.com";
        String password = "password123";

        // Le repository retourne un User → donc email existe
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(new User()));

        // WHEN + THEN
        AppException exception = assertThrows(
                AppException.class,
                () -> authenticationService.register(email, password)
        );

        assertEquals("Email address already in use!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // On s’assure que save() n’a PAS été appelé
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnUserLogiDTO_whenCredentialsAreValid() {
        // GIVEN
        String email = "test@mail.com";
        String password = "password";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)))
                .thenReturn(authentication);
        when(tokenService.generateJwt(authentication))
                .thenReturn("jwtToken");
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));


        UserLoginDTO result = authenticationService.login(email, password);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(user, result.getUser());
        assertEquals("jwtToken", result.getJwt());
    }

    @Test
    void login_shouldReturnEmptyDTO_whenAuthenticationFails() {
        // GIVEN
        String email = "wrong@mail.com";
        String password = "badPassword";

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        )).thenThrow(new AuthenticationException("Invalid credentials") {});

        // WHEN
        UserLoginDTO result = authenticationService.login(email, password);

        // THEN
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUser());
        assertEquals("", result.getJwt());
    }

    @Test
    void login_shouldReturnEmptyDTO_whenUserNotFound() {
        // GIVEN
        String email = "wrong@mail.com";
        String password = "badPassword";

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        )).thenReturn(auth);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());
        when(tokenService.generateJwt(auth))
                .thenReturn("jwtToken123");

        // WHEN
        UserLoginDTO result = authenticationService.login(email, password);

        // THEN
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUser());
        assertEquals("", result.getJwt());
    }

}
