package com.example.applicationcongess.repositories;

import com.example.applicationcongess.models.ChatMessage;
import com.example.applicationcongess.models.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessagerepo extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findChatMessageByChat(Chatroom chatroom);
}
