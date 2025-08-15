package projet.perso.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import projet.perso.backend.DTOs.CartDTO;
import projet.perso.backend.DTOs.CartItemDTO;
import projet.perso.backend.DTOs.ProductDTO;
import projet.perso.backend.Exception.AppException;
import projet.perso.backend.entities.Cart;
import projet.perso.backend.entities.CartItem;
import projet.perso.backend.entities.Product;
import projet.perso.backend.entities.User;
import projet.perso.backend.mappers.CartMapper;
import projet.perso.backend.repositories.CartItemRepository;
import projet.perso.backend.repositories.CartRepository;
import projet.perso.backend.repositories.ProductRepository;
import projet.perso.backend.repositories.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class CartServiceImpl implements  CartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartDTO getCartByUserId(Long userId) {
        Optional<Cart> userCart = cartRepository.findByUserId(userId);
        if (userCart.isPresent()) {
            Cart cart = userCart.get();

            CartDTO cartDTO = new CartDTO();
            cartDTO.setId(cart.getId());
            cartDTO.setUserId(cart.getUserId());

            List<CartItemDTO> cartItemDTOs = getCartItemDTO(cart);

            Map<Long, CartItemDTO> cartItemMap = new HashMap<>();

            for (CartItemDTO cartItemDTO : cartItemDTOs) {
                Long productId = cartItemDTO.getProductId();
                if (cartItemMap.containsKey(productId)) {
                    CartItemDTO existingItem = cartItemMap.get(productId);
                    existingItem.setQuantity(existingItem.getQuantity() + cartItemDTO.getQuantity());
                    existingItem.setSubTotal(existingItem.getSubTotal().add(cartItemDTO.getSubTotal()));
                } else {
                    cartItemMap.put(productId, cartItemDTO);
                }
            }

            List<CartItemDTO> consolidatedCartItems = new ArrayList<>(cartItemMap.values());
            cartDTO.setCartItems(consolidatedCartItems);

            for (CartItemDTO consolidatedCartItem : consolidatedCartItems) {
                Long productId = consolidatedCartItem.getProductId();
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));

                ProductDTO productDTO = new ProductDTO();
                productDTO.setImgUrl(product.getImgUrl());
                consolidatedCartItem.setProduct(productDTO);
            }

            BigDecimal totalPrice = consolidatedCartItems.stream()
                    .map(CartItemDTO::getSubTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            cartDTO.setTotalPrice(totalPrice);

            return cartDTO;
        } else {
            return null;
        }
    }

    public int getNumberOfItemsInCart(Long userId) {
        Optional<Cart> userCart = cartRepository.findByUserId(userId);
        if (userCart.isPresent()) {
            Cart cart = userCart.get();
            return cart.getCartItems().stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();
        } else {
            return 0;
        }
    }

    private static List<CartItemDTO> getCartItemDTO(Cart cart) {
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setProductId(cartItem.getProductId());
            cartItemDTO.setProductName(cartItem.getProductName());
            cartItemDTO.setQuantity(cartItem.getQuantity());
            cartItemDTO.setPrice(cartItem.getPrice());

            cartItemDTO.setSubTotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemDTOs.add(cartItemDTO);
        }
        return cartItemDTOs;
    }

    public CartDTO addItemToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));

        BigDecimal itemPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        Optional<Cart> optionalCart = cartRepository.findByUserId(userId);
        Cart userCart = optionalCart.orElse(new Cart());
        if (optionalCart.isEmpty()) {
            userCart.setUserId(userId);
            cartRepository.save(userCart);
        }

        CartItem newItem = new CartItem();
        newItem.setProductId(product.getId());
        newItem.setProductName(product.getName());
        newItem.setQuantity(quantity);
        newItem.setPrice(itemPrice);
        newItem.setCart(userCart);
        newItem.setSubTotal(itemPrice);

        cartItemRepository.save(newItem);

        userCart.getCartItems().add(newItem);

        BigDecimal totalPrice = userCart.getCartItems().stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        userCart.setTotalPrice(totalPrice);
        cartRepository.save(userCart);

        List<CartItemDTO> cartItemDTOs = userCart.getCartItems().stream()
                .map(item -> {
                    CartItemDTO itemDTO = new CartItemDTO();
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setProductName(item.getProductName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setSubTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                            .setScale(2, RoundingMode.HALF_UP));
                    return itemDTO;
                }).collect(Collectors.toList());

        return CartMapper.INSTANCE.cartToCartDTO(userCart, totalPrice, cartItemDTOs);
    }

    public CartDTO removeItemFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));

        Cart userCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Cart not found", HttpStatus.NOT_FOUND));

        CartItem cartItemToRemove = userCart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new AppException("Cart item not found", HttpStatus.NOT_FOUND));

        userCart.getCartItems().remove(cartItemToRemove);
        cartItemRepository.delete(cartItemToRemove);

        BigDecimal totalPrice = userCart.getCartItems().stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCart.setTotalPrice(totalPrice);
        cartRepository.save(userCart);

        List<CartItemDTO> cartItemDTOs = getCartItemDTO(userCart);
        return CartMapper.INSTANCE.cartToCartDTO(userCart, totalPrice, cartItemDTOs);
    }

    public Cart getCartEntityByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() ->
                new AppException("Cart not found for user id: " + userId, HttpStatus.NOT_FOUND));
    }

    public void clearCart(Long userId) {
        Cart cart = getCartEntityByUserId(userId);
        if (cart != null) {
            cart.getCartItems().clear();
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart);
        }
    }
    
}
