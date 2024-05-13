package com.example.applicationcongess.services;

import com.example.applicationcongess.models.Role;
import com.example.applicationcongess.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Roleservice {
    @Autowired
    RoleRepo roleRepository;

    public Role saveNewRole(Role role)
    {
        return this.roleRepository.save(role);
    }

    public Iterable<Role> getAllRoles()
    {
        return this.roleRepository.findAll();
    }
}
