package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.entities.CartItem;

public class CartItemMapper {

    public static CartItemDto toDto(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }

    public static CartItem toEntity(CartItemDto dto) {
        CartItem item = new CartItem();
        item.setId(dto.getId());
        item.setProductName(dto.getProductName());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        return item;
    }
}
