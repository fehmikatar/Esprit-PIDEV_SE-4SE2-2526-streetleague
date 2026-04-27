package tn.esprit._4se2.pi.services.Favorite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.entities.Favorite;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.FavoriteRepository;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.dto.Sponsor.FavoriteDTOs;
import tn.esprit._4se2.pi.mappers.FavoriteMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteServiceI favoriteService; // Fix: use FavoriteServiceI

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        
        testProduct = Product.builder()
                .id(100L)
                .nom("Favorite Product")
                .build();
    }

    @Test
    void addToFavorites_NewFavorite_ShouldSave() {
        // Arrange
        FavoriteDTOs.FavoriteRequest request = FavoriteDTOs.FavoriteRequest.builder()
                .productId(100L)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findByIdAndDeletedFalse(100L)).thenReturn(Optional.of(testProduct));
        when(favoriteRepository.existsByUserIdAndProductId(1L, 100L)).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(i -> i.getArguments()[0]);
        when(favoriteMapper.toDTO(any(Favorite.class))).thenReturn(new FavoriteDTOs.FavoriteResponse());

        // Act
        FavoriteDTOs.FavoriteResponse response = favoriteService.addToFavorites(1L, request);

        // Assert
        assertNotNull(response);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void removeFromFavorites_ShouldCallRepositoryDelete() {
        // Arrange
        Favorite existing = new Favorite();
        when(favoriteRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(Optional.of(existing));

        // Act
        favoriteService.removeFromFavorites(1L, 100L);

        // Assert
        verify(favoriteRepository).delete(existing);
    }
}
