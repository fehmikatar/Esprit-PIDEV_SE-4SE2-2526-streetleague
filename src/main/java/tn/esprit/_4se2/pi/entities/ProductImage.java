package tn.esprit._4se2.pi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Column(name = "image_url")
    private String imageUrl;

    @Lob
    @Column(name = "upload_image", columnDefinition = "LONGTEXT")
    private String uploadImage;
}
