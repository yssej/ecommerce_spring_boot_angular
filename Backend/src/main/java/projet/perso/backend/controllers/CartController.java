package projet.perso.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projet.perso.backend.DTOs.CartDTO;
import projet.perso.backend.Exception.AppException;
import projet.perso.backend.services.CartServiceImpl;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("api/v1/cart")
public class CartController {

    private final CartServiceImpl cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getCartByUserId(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        CartDTO cartDTO = cartService.getCartByUserId(userId);
        if (cartDTO != null) {
            response.put("cart", cartDTO);
            response.put("numberOfItemsInCart", cartService.getNumberOfItemsInCart(userId));
            return ResponseEntity.ok().body(response);
        } else {
            throw new AppException("User's cart not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{userId}/{productId}/{quantity}")
    public ResponseEntity<CartDTO> addItemToCart(@PathVariable Long userId, @PathVariable Long productId, @PathVariable int quantity) {
        CartDTO cartDTO = cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok().body(cartDTO);
    }

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        CartDTO cartDTO = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok().body(cartDTO);
    }
}

