package com.example.applicationcongess.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@ToString
public class Chatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long chatroomId;

    @JsonIgnore
    @ManyToOne
    Personnel sender;
    @JsonIgnore
    @ManyToOne
    Personnel receiver;


    String color = "#EC407A";

    @OneToOne
@JsonIgnore
     Demande_conge demandeConge;
    @OneToMany(cascade = CascadeType.ALL , mappedBy = "chat")
    List<ChatMessage> messages;
    @OneToMany(mappedBy = "chat1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Messages> messages1; // Messages dans la salle de chat

}
