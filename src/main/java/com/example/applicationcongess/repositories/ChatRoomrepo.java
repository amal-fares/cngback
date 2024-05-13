package com.example.applicationcongess.repositories;

import com.example.applicationcongess.models.ChatMessage;
import com.example.applicationcongess.models.Chatroom;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomrepo extends JpaRepository<Chatroom, Long> {
    Chatroom findBySenderAndReceiverAndAndDemandeConge(Personnel sender, Personnel receiverPersonnel, Demande_conge demandeconge);
Chatroom getChatroomByChatroomId(long idchatroom);
}
