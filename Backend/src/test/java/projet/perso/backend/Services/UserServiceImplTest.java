package projet.perso.backend.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import projet.perso.backend.DTOs.UserDTO;
import projet.perso.backend.Exception.AppException;
import projet.perso.backend.entities.User;
import projet.perso.backend.repositories.UserRepository;
import projet.perso.backend.services.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void loadUserByUsername_shouldReturnUser_whenExists() {

        // GIVEN

        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        // WHEN

        User result = (User) userService.loadUserByUsername("test@mail.com");

        // THEN

        assertNotNull(result);
        assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenNotExists() {
        when(userRepository.findByEmail("unknown@mail.com"))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> userService.loadUserByUsername("unknown@mail.com")
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }
}
