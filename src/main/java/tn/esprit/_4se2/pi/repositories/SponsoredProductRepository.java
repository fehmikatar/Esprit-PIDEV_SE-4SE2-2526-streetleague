package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.SponsoredProduct;

import java.util.List;

@Repository
public interface SponsoredProductRepository extends JpaRepository<SponsoredProduct, Long> {
    List<SponsoredProduct> findByIsActiveTrue();
    List<SponsoredProduct> findBySponsorId(Long sponsorId);
    List<SponsoredProduct> findByProductId(Long productId);
}