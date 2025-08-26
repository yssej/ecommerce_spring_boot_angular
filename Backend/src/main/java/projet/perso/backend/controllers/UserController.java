package projet.perso.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import projet.perso.backend.DTOs.UserDTO;
import projet.perso.backend.entities.User;
import projet.perso.backend.services.UserServiceImpl;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("api/v1/user")
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId, Authentication authentication) {
        return userService.getUserById(userId, authentication);
    }

    @PutMapping("/update/{userId}")
    public User updateUserById(@PathVariable Long userId, @RequestBody UserDTO userDTO, Authentication authentication) {
        return userService.updateUserById(userId, userDTO, authentication);
    }
}
