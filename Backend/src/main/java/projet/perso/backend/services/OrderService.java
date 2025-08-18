package projet.perso.backend.services;

import org.springframework.security.core.Authentication;
import projet.perso.backend.DTOs.CartDTO;
import projet.perso.backend.DTOs.OrderDTO;
import projet.perso.backend.entities.Order;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getOrdersByUserId(Long userId, Authentication authentication);
    Order createOrderFromCart(CartDTO cart, Long userId, Authentication authentication);
}
