package projet.perso.backend.services;

import projet.perso.backend.DTOs.CartDTO;
import projet.perso.backend.DTOs.CartItemDTO;
import projet.perso.backend.entities.Cart;

import java.util.List;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    int getNumberOfItemsInCart(Long userId);
    CartDTO addItemToCart(Long userId, Long productId, int quantity);
    CartDTO removeItemFromCart(Long userId, Long productId);
    Cart getCartEntityByUserId(Long userId);
    void clearCart(Long userId);
}
