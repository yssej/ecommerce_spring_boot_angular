package projet.perso.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import projet.perso.backend.DTOs.CartDTO;
import projet.perso.backend.DTOs.CartItemDTO;
import projet.perso.backend.DTOs.OrderDTO;
import projet.perso.backend.Exception.AppException;
import projet.perso.backend.entities.Order;
import projet.perso.backend.entities.OrderItem;
import projet.perso.backend.entities.Product;
import projet.perso.backend.entities.User;
import projet.perso.backend.repositories.OrderRepository;
import projet.perso.backend.repositories.ProductRepository;
import projet.perso.backend.repositories.UserRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<OrderDTO> getOrdersByUserId(Long userId, Authentication authentication) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found.", HttpStatus.NOT_FOUND));

        if (authentication == null || !user.getEmail().equals(authentication.getName())) {
            throw new AppException("Access denied.", HttpStatus.BAD_REQUEST);
        }

        List<Order> orders = orderRepository.findAllByUserId(userId);
        List<OrderDTO> orderDTOs = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Order order : orders) {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setTotal(order.getTotal());
            String dateCreatedStr = dateFormat.format(order.getDateCreated());
            orderDTO.setDateCreated(dateCreatedStr);
            orderDTOs.add(orderDTO);
        }
        return orderDTOs;
    }

    public Order createOrderFromCart(CartDTO cart, Long userId, Authentication authentication) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (authentication == null || !user.getEmail().equals(authentication.getName())) {
            throw new AppException("Access denied.", HttpStatus.BAD_REQUEST);
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotal(cart.getTotalPrice());
        order.setDateCreated(new Date());
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemDTO cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            Product product = productRepository.findById(cartItem.getProductId()).orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }

}
