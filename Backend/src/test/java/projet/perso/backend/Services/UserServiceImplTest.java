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

    @Test
    void getUserById_shouldReturnUser_whenExistsAndAuthorized() {

        // GIVEN

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(authentication.getName())
                .thenReturn("test@mail.com");

        // WHEN

        User result = userService.getUserById(1L, authentication);

        // THEN
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@mail.com", result.getEmail());
        assertEquals(user, result);

    }

    @Test
    void getUserById_shouldThrowException_whenNotExists() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
            AppException.class, 
            () -> userService.getUserById(1L, authentication)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void getUserById_shouldThrowException_whenNotAuthorized() {

        // GIVEN

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(authentication.getName())
            .thenReturn("other@mail.com");

        // WHEN: request the same user's id but with a different authenticated email
        AppException exception = assertThrows(
            AppException.class,
            () -> userService.getUserById(1L, authentication)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void shouldUpdateEmailAndPassword_whenUserExistsAndAuthorized() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setPassword("oldHashedPassword");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(authentication.getName())
                .thenReturn("test@mail.com");
        when(passwordEncoder.encode("newPassword"))
                .thenReturn("newHashedPassword");
        // WHEN
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newMail@mail.com");
        userDTO.setPassword("newPassword");
        User result = userService.updateUserById(1L, userDTO, authentication);
        // THEN
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newMail@mail.com", result.getEmail());
        assertEquals("newHashedPassword", result.getPassword());
        verify(userRepository).save(user);

    }

    @Test 
    void updateUserById_shouldUpdateEmailAndPassword_whenValid() { 
        User user = new User(); 
        user.setId(1L); 
        user.setEmail("old@mail.com"); 
        user.setPassword("oldPass"); 
        
        UserDTO dto = new UserDTO(); 
        dto.setEmail("new@mail.com"); 
        dto.setPassword("newPass"); 
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); 
        when(authentication.getName()).thenReturn("old@mail.com"); 
        when(passwordEncoder.encode("newPass")).thenReturn("hashedPass");

        User updated = userService.updateUserById(1L, dto, authentication); 
        
        assertEquals("new@mail.com", updated.getEmail()); 
        assertEquals("hashedPass", updated.getPassword()); 
        verify(userRepository).save(user); 
    }
}
