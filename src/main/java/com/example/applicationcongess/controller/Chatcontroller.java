package com.example.applicationcongess.controller;

import com.example.applicationcongess.models.ChatMessage;
import com.example.applicationcongess.models.Chatroom;
import com.example.applicationcongess.models.Image_justificatif;
import com.example.applicationcongess.models.Messages;
import com.example.applicationcongess.repositories.ChatMessagerepo;
import com.example.applicationcongess.repositories.ChatRoomrepo;
import com.example.applicationcongess.repositories.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Controller
@CrossOrigin(origins = "*")
public class Chatcontroller {
    @Autowired
    MessagesRepository messagesRepository;



    @MessageMapping("/send")
    @SendTo("/topic/public")
    public String   sendMessage(@Payload String   chatMessage) {
        System.out.println("mess");

       String ch="jdjjdj";

        System.out.println("mess");
        return ch ;
    }
    @MessageExceptionHandler
    public void handleException(Throwable exception) {
        System.out.println(exception);
    }

    @MessageMapping("/chat.reply")
    @SendTo("/topic/public")
    public String replyToMessage(String message) {


        System.out.println("message");
        return message;
    }

    @Autowired
    ChatRoomrepo cr;
@Autowired
    ChatMessagerepo chatMessagerepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    // mapped to handle chat messages to the /sendmsg destination
    @MessageMapping("/sendmsg")
    // the return value is broadcast to all subscribers of /chat/messages
    @SendTo("/topic/messages")
    public ChatMessage chat( @RequestBody ChatMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        Chatroom ch = cr.findById(message.getIdchat()).orElse(null);
        message.setChat(ch);
        chatMessagerepo.save(message);



        return new ChatMessage(message.getMessageId(),message.getText(), message.getUsername(), message.getAvatar(),message.getSender(),message.getIdchat(),message.getChat());
    }
    // Map pour stocker les sessions WebSocket des utilisateurs connectés
    private Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    // Méthode pour ajouter une session WebSocket associée à un utilisateur
    public void addUserSession(String username, WebSocketSession session) {
        userSessions.put(username, session);
    }

    // Méthode pour supprimer la session WebSocket associée à un utilisateur
    public void removeUserSession(String username) {
        userSessions.remove(username);
    }

    // Méthode pour récupérer la session WebSocket d'un utilisateur spécifié par son nom d'utilisateur
    public WebSocketSession getUserSession(String username) {
        return userSessions.get(username);
    }
}


