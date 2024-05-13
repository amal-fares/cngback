package com.example.applicationcongess.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity

public class ChatMessage {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long messageId;

        private String text;
        private String username;
        private String avatar;

        Long idchat;

        Long sender;


        @JsonIgnore
        @ManyToOne
        Chatroom chat;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }


    }

