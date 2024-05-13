package com.example.applicationcongess.repositories;


import com.example.applicationcongess.models.Image_justificatif;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Imagerepository extends CrudRepository<Image_justificatif, Long> {
}
