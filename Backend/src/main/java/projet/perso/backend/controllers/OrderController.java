package projet.perso.backend.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import projet.perso.backend.DTOs.CartDTO;
import projet.perso.backend.DTOs.OrderDTO;
import projet.perso.backend.DTOs.PaymentDTO;
import projet.perso.backend.entities.Order;
import projet.perso.backend.services.CartService;
import projet.perso.backend.services.OrderService;
import projet.perso.backend.services.PaymentService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    private final PaymentService paymentService;

    @GetMapping("/{userId}")
    public List<OrderDTO> getOrdersByUserId(@PathVariable Long userId, Authentication authentication) {
        return orderService.getOrdersByUserId(userId, authentication);
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<PaymentDTO> checkout(@PathVariable Long userId, Authentication authentication) throws StripeException {
        CartDTO cart = cartService.getCartByUserId(userId);
        BigDecimal totalPrice = cart.getTotalPrice();
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(totalPrice);

        Order createdOrder = orderService.createOrderFromCart(cart, userId, authentication);

        cartService.clearCart(userId);

        PaymentDTO paymentDTO = new PaymentDTO(paymentIntent.getClientSecret(), totalPrice, "usd", createdOrder.getId());

        return ResponseEntity.ok().body(paymentDTO);
    }
}
