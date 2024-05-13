package com.example.applicationcongess.services;

import com.example.applicationcongess.models.Chatroom;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.ChatRoomrepo;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    @Autowired
    ChatRoomrepo chatRepo;
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    Demande_congebRepository demande_congebRepository;
    public Chatroom findchat(long idsender , long  idreceiver, long iddemande) {
        Personnel sender = personnelRepository.findById(idsender).orElse(null);
        Personnel receiver =personnelRepository.findById(idreceiver).orElse(null);

        Demande_conge demandeconge=demande_congebRepository.findById(iddemande).orElse(null);
        Chatroom chatroom = chatRepo.findBySenderAndReceiverAndAndDemandeConge(sender, receiver,demandeconge);
        Chatroom chatroom2 = chatRepo.findBySenderAndReceiverAndAndDemandeConge(receiver, sender,demandeconge);
        if (chatroom != null) {

            return chatroom;
        } else if(chatroom2!=null) {

          return chatroom2 ;
        }
            else {
                // Créez un nouveau chatroom avec l'expéditeur et le destinataire donnés
                chatroom = new Chatroom();
                chatroom.setSender(sender);
                chatroom.setReceiver(receiver);
            chatroom.setDemandeConge(demandeconge);
                return chatRepo.save(chatroom);
            }

        }
    public Chatroom getConv(Long idchatroom) {
        Chatroom ch = chatRepo.findById(idchatroom).orElse(null);
        return ch;

    }
    public void changecolor(Long id, String s){
        Chatroom ch = chatRepo.findById(id).orElse(null);
        ch.setColor(s);
        chatRepo.save(ch);
    }
}
