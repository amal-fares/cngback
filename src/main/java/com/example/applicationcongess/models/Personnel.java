package com.example.applicationcongess.models;

import com.example.applicationcongess.enums.Stataus_emploi;
import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor

public class Personnel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long cin ;
    String nom ;
    String prenom ;
    boolean confirmsoldeprev;
    long soldeprevisonnel;
    @Column(unique = true)
    String username ;
    Date date_naissance;
    String adresse ;
    String password;
    String email;
    String tel ;
    Date date_entree;
    Stataus_emploi status;
    float solde_conges ;
    float  jours_restants ;
    String code ;
    LocalDateTime datendcode ;
    String etatmail;
    Boolean statusatifounon ;

    String jwt ;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set <Role> roles ;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Personnel manager;
    @OneToMany(mappedBy = "manager",fetch = FetchType.EAGER)
            @JsonIgnore
    Set <Personnel> subordonnes3;
 private String poste;


  @JsonIgnore
    @OneToMany(mappedBy = "collaborateur")
    List <Demande_conge> demande_congeList ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "managerdeuxieme_id")
     Personnel managerdeuxiemeniveau;
    @OneToMany(mappedBy = "managerdeuxiemeniveau",fetch = FetchType.EAGER)
            @JsonIgnore
    Set <Personnel> subordonnes2;

    @ManyToOne
    @JoinColumn(name = "gestionnaire")

     Personnel gestionnaire;

    @OneToMany(mappedBy = "gestionnaire",fetch = FetchType.EAGER)
 @JsonIgnore
     Set <Personnel> subordonnes1 ;
    @JsonIgnore

    @OneToMany(mappedBy = "sender")
     Set <Chatroom> sentMessages;
@JsonIgnore
    // Liste de messages reçus
    @OneToMany(mappedBy = "receiver",fetch = FetchType.EAGER)
     Set <Chatroom> receivedMessages;
    public Personnel (Long cin , String username) {
        this.cin = cin;
        this.username = username;
    }
    public Personnel ( String username) {

        this.username = username;
    }
}
