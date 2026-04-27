package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Category;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
<<<<<<< Updated upstream
	Optional<Category> findByNomIgnoreCase(String nom);
=======

    Optional<Category> findByNom(String nom);

    List<Category> findByParentCategoryIsNull();

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.parentCategory IS NULL")
    List<Category> findAllWithSubCategories();

    List<Category> findDistinctByNomContainingIgnoreCaseOrProducts_NomContainingIgnoreCase(
            String categoryKeyword,
            String productKeyword
    );

    boolean existsByNom(String nom);
>>>>>>> Stashed changes
}
