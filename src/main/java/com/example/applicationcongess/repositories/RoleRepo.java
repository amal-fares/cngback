package com.example.applicationcongess.repositories;

import com.example.applicationcongess.enums.ERole;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.models.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepo extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
