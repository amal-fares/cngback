package com.example.applicationcongess.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class Justificatifs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_justificatif;
    String desription ;
    Date date_envoi ;
    @Lob
    private byte[] nature_fichier;
    @JsonIgnore
    @ManyToOne
    private Demande_conge demande_cngjustif;
}
