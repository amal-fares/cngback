
package com.example.applicationcongess.repositories;


import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Image_justificatif;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Imagerepository extends CrudRepository<Image_justificatif, Long> {

    Image_justificatif findByDemandecngjustif(Demande_conge demande_conge);
Image_justificatif findImage_justificatifByImagenId(String imagenid);
List<Image_justificatif> findImage_justificatifByDemandecngjustif(Demande_conge demande_conge);
}
