package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Sponsor.CategoryDTO;
import tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.entities.ProductVariant;

import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(ProductDTOs.ProductRequest request) {
        if (request == null)
            return null;

        Product.ProductStatus mappedStatus = Product.ProductStatus.EN_STOCK;
        if (request.getStatus() != null) {
            try {
                mappedStatus = Product.ProductStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Default gracefully
            }
        }

        java.util.List<tn.esprit._4se2.pi.entities.ProductImage> productImages = new java.util.ArrayList<>();
        if (request.getImages() != null) {
            for (String img : request.getImages()) {
                if (img != null && img.startsWith("data:image")) {
                    productImages.add(new tn.esprit._4se2.pi.entities.ProductImage(null, img));
                } else if (img != null && !img.trim().isEmpty()) {
                    productImages.add(new tn.esprit._4se2.pi.entities.ProductImage(img, null));
                }
            }
        }

        return Product.builder()
                .nom(request.getNom())
                .marque(request.getMarque())
                .description(request.getDescription())
                .prix(request.getPrix())
                .stock(request.getStock())
                .images(productImages)
                .status(mappedStatus)
                .build();
    }

    public ProductDTOs.ProductResponse toDTO(Product product) {
        if (product == null)
            return null;

        CategoryDTO categoryDTO = null;
        if (product.getCategory() != null) {
            categoryDTO = CategoryDTO.builder()
                    .id(product.getCategory().getId())
                    .nom(product.getCategory().getNom())
                    .build();
        }

        java.util.List<String> dtoImages = new java.util.ArrayList<>();
        if (product.getImages() != null) {
            for (tn.esprit._4se2.pi.entities.ProductImage pi : product.getImages()) {
                if (pi.getUploadImage() != null && !pi.getUploadImage().trim().isEmpty()) {
                    dtoImages.add(pi.getUploadImage());
                } else if (pi.getImageUrl() != null && !pi.getImageUrl().trim().isEmpty()) {
                    dtoImages.add(pi.getImageUrl());
                }
            }
        }

        return ProductDTOs.ProductResponse.builder()
                .id(product.getId())
                .nom(product.getNom())
                .marque(product.getMarque())
                .description(product.getDescription())
                .prix(product.getPrix())
                .stock(product.getStock())
                .images(dtoImages)
                .category(categoryDTO)
                .variants(product.getVariants().stream()
                        .map(this::toVariantDTO)
                        .collect(Collectors.toList()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deleted(product.getDeleted())
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .build();
    }

    public ProductDTOs.ProductVariantDTO toVariantDTO(ProductVariant variant) {
        if (variant == null)
            return null;

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
        if (dto == null)
            return null;

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
