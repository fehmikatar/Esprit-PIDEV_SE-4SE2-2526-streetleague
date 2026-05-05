package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.entities.Cart;
import tn.esprit._4se2.pi.entities.CartItem;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartDto toDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setItems(cart.getItems()
                .stream()
                .map(CartItemMapper::toDto)
                .collect(Collectors.toList()));
        dto.setStatus(cart.getStatus());
        dto.setClientName(cart.getClientName());
        dto.setClientEmail(cart.getClientEmail());
        return dto;
    }

    public static Cart toEntity(CartDto dto) {
        Cart cart = new Cart();
        cart.setId(dto.getId());
        cart.setItems(dto.getItems()
                .stream()
                .map(itemDto -> {
                    CartItem item = CartItemMapper.toEntity(itemDto);
                    item.setCart(cart);
                    return item;
                })
                .collect(Collectors.toList()));
        cart.setStatus(dto.getStatus());
        cart.setClientName(dto.getClientName());
        cart.setClientEmail(dto.getClientEmail());
        return cart;
    }
}
