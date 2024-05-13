package com.example.applicationcongess.PlayLoad.request;

import com.example.applicationcongess.enums.Stataus_emploi;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Messages;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Updateuser {

    String nom ;
    String prenom ;
    @Column(unique = true)
    String username ;
    Date date_naissance;
    String adresse ;

    String email;
    String tel ;
















}
