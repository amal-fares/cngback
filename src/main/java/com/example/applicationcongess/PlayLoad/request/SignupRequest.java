package com.example.applicationcongess.PlayLoad.request;

import com.example.applicationcongess.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String  nom;
    private String prenom ;
    private String email;
    private String adresse ;
    private List<String> roles;
    private String password;
    String username ;


}