package com.example.applicationcongess.models;

import com.example.applicationcongess.enums.*;
import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

public class Demande_conge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_demandeconge;
    Etat etatdemande;
    @Temporal(TemporalType.DATE)

    Date date_debut ;
    @Temporal(TemporalType.DATE)
    Date date_fin ;
    float duree;

Typecongeprev typecongeprev;
Boolean justificatifs_requis;
Type_conge_exceptionnel typecongeexceptionnel;

     Date deadline;
    @OneToOne(mappedBy = "demandeConge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     Chatroom chatroom;
    @Temporal(TemporalType.DATE)
    Date date_demande_congés  ;
    Statut_conge statutconge;
    @Temporal(TemporalType.DATE)
    Date datedecision  ;
    Type_conge typeconge;
    @JsonIgnore
    @OneToMany(mappedBy = "demandecngjustif")
    List <Image_justificatif> imagesjustif = new ArrayList<>();

Boolean justificatifPresent ;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collaborateur_id")

    Personnel collaborateur ;

@JsonIgnore
 @OneToMany(mappedBy = "demande_congemes")
    List<Messages> messdemlist=new ArrayList<>();
    public Demande_conge ( Date  date_debut,Date date_fin  , Type_conge motif ,Personnel collaborateur  , Date date_demande_congés) {

        this.date_debut = date_debut;
        this.date_fin=date_fin;
        this.collaborateur=collaborateur;
        this.typeconge=motif;
        this.date_demande_congés=date_demande_congés;
    }
    public Demande_conge ( Date  date_debut,Date date_fin   ,Personnel collaborateur  ) {

        this.date_debut = date_debut;
        this.date_fin=date_fin;
        this.collaborateur=collaborateur;

}
    public Demande_conge ( Date  date_debut,Date date_fin ,Type_conge motif,Type_conge_exceptionnel type_conge_exceptionnel,Personnel collaborateur ,Boolean justificatifs_requis , Date date_demande_congés) {

        this.date_debut = date_debut;
        this.date_fin=date_fin;

        this.typeconge=motif;
        this.typecongeexceptionnel=type_conge_exceptionnel;
        this.collaborateur=collaborateur;
        this.justificatifs_requis=justificatifs_requis;
this.date_demande_congés=date_demande_congés;
    }}
