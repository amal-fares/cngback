package com.example.applicationcongess.repositories;

import com.example.applicationcongess.enums.ERole;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    Optional<Personnel> findByUsername(String username);
    Boolean existsByUsername(String username);
    Personnel findByEmail(String username);
    Boolean existsByEmail(String email);
    Personnel findPersonnelByUsername(String username);
    Personnel findByCode(String code);
    Personnel findPersonnelByCode(String code);
    Personnel findByManager(Long id );
            Personnel findPersonnelByEmail(String email );

            Personnel findByRoles(ERole erole);
            List<Personnel> findPersonnelByRoles(ERole erole);

}
