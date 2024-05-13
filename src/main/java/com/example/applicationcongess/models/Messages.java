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
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long messageId;

    String body;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_conge_id") // Assurez-vous que ce nom correspond dans votre base de données
    private Demande_conge demande_congemes;

    @JsonIgnore
    @ManyToOne
    Chatroom chat1;

}
