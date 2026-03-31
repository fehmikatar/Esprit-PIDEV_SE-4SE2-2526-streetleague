package tn.esprit._4se2.pi.mappers;


import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.CategoryDTO;
import tn.esprit._4se2.pi.dto.ProductDTOs;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.entities.ProductVariant;

import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(ProductDTOs.ProductRequest request) {
        if (request == null) return null;

        return Product.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .prix(request.getPrix())
                .stock(request.getStock())
                .images(request.getImages())
                .build();
    }

    public ProductDTOs.ProductResponse toDTO(Product product) {
        if (product == null) return null;

        CategoryDTO categoryDTO = null;
        if (product.getCategory() != null) {
            categoryDTO = CategoryDTO.builder()
                    .id(product.getCategory().getId())
                    .nom(product.getCategory().getNom())
                    .build();
        }

        return ProductDTOs.ProductResponse.builder()
                .id(product.getId())
                .nom(product.getNom())
                .description(product.getDescription())
                .prix(product.getPrix())
                .stock(product.getStock())
                .images(product.getImages())
                .category(categoryDTO)
                .variants(product.getVariants().stream()
                        .map(this::toVariantDTO)
                        .collect(Collectors.toList()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deleted(product.getDeleted())
                .build();
    }

    public ProductDTOs.ProductVariantDTO toVariantDTO(ProductVariant variant) {
        if (variant == null) return null;

        return ProductDTOs.ProductVariantDTO.builder()
                .id(variant.getId())
                .size(variant.getSize())
                .color(variant.getColor())
                .sku(variant.getSku())
                .stock(variant.getStock())
                .priceAdjustment(variant.getPriceAdjustment())
                .build();
    }

    public ProductVariant toVariantEntity(ProductDTOs.ProductVariantDTO dto, Product product) {
        if (dto == null) return null;

        return ProductVariant.builder()
                .size(dto.getSize())
                .color(dto.getColor())
                .sku(dto.getSku())
                .stock(dto.getStock())
                .priceAdjustment(dto.getPriceAdjustment())
                .product(product)
                .build();
    }
}
