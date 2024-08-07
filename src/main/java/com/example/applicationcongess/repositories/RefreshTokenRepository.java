package com.example.applicationcongess.repositories;

import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByPersonnel(Personnel personnel);
    RefreshToken findByPersonnel(Personnel personnel);
}
