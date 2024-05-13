package com.example.applicationcongess.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@Getter
@Setter
public class Image_justificatif {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String senderemail;
    private String replymessage;
    private String imagenUrl;
    private String imagenId;

    Date date_envoi ;
    @Lob
    private byte[] nature_fichier;
    @JsonIgnore
    @ManyToOne
    private Demande_conge demande_cngjustif;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chatroom chatroom;



    public Image_justificatif() {
    }

    public Image_justificatif(String name, String imagenUrl, String imagenId) {
        this.name = name;
        this.imagenUrl = imagenUrl;
        this.imagenId = imagenId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getImagenId() {
        return imagenId;
    }





    public void setImagenId(String imagenId) {
        this.imagenId = imagenId;
    }
}

