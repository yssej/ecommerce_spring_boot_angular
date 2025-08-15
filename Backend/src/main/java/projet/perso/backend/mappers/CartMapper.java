package projet.perso.backend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import projet.perso.backend.DTOs.CartDTO;
import projet.perso.backend.DTOs.CartItemDTO;
import projet.perso.backend.entities.Cart;
import projet.perso.backend.entities.CartItem;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CartMapper {
    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(target = "id", source = "cart.id")
    @Mapping(target = "cartItems", source = "cartItems")
    CartDTO cartToCartDTO(Cart cart, BigDecimal totalPrice, List<CartItemDTO> cartItems);

    @Mapping(target = "subTotal", expression = "java(cartItem.getSubTotal())")
    CartItemDTO cartItemToCartItemDTO(CartItem cartItem);
}
