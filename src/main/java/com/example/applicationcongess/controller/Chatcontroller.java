package com.example.applicationcongess.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.applicationcongess.models.*;
import com.example.applicationcongess.repositories.*;
import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Controller
@CrossOrigin(origins = "*")
public class Chatcontroller {
    @Autowired
    MessagesRepository messagesRepository;
    @Autowired
    Demande_congebRepository demande_congebRepository;
    @Autowired
    ChatRoomrepo chatRoomrepo;
    @Autowired
    Imagerepository imagerepository;
    @Autowired
    ChatRoomrepo cr;
    @Autowired
    ChatMessagerepo chatMessagerepo;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    final  Cloudinary cloudinary;
@Autowired
    RuntimeService runtimeService;
@Autowired
Demande_congecontr demande_congecontr;
    private Map<String, String> valuesMap = new HashMap<>();
    @MessageExceptionHandler
    public void handleException(Throwable exception) {
        System.out.println(exception);
    }


    @MessageMapping("/sendmsg")
    @SendTo("/topic/messages")
    public ChatMessage chat(@RequestBody ChatMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        Chatroom ch = cr.findById(message.getIdchat()).orElse(null);
        message.setChat(ch);
        chatMessagerepo.save(message);



        return new ChatMessage(message.getMessageId(),message.getText(), message.getUsername(), message.getAvatar(),message.getSender(),message.getIdchat(),message.getChat());


        }

    @MessageMapping("/notification")

    public String notifierValidation(String message , String  iduser ) {
        messagingTemplate.convertAndSendToUser(iduser, "/topic/notification", message);
        return message
                ;
    }

    public Chatcontroller() {
        valuesMap.put("cloud_name", "dcbg7hmvc");
        valuesMap.put("api_key", "152545221988441");
        valuesMap.put("api_secret", "5Q-oCmrUKORJIYJe87lbFt43rbo");
        cloudinary = new Cloudinary(valuesMap);
    }

    @MessageMapping("/send-image")
    @SendTo("/topic/messages" )
    public Image_justificatif sendimage (@Payload MultipartFile multipartFile, @Header("idDemande") Long idDemande, @Header("chatroomId") Long chatroomId) throws IOException {

        Demande_conge dem=demande_congebRepository.findById(idDemande).orElse(null);
        File file = convert(multipartFile);
        Map result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        file.delete();
        BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
        Image_justificatif media = new Image_justificatif((String)
                result.get("original_filename")
                , (String) result.get("url"),
                (String) result.get("public_id"));
        media.setDemandecngjustif(dem);
        media.setChatroom(chatRoomrepo.findById(chatroomId).orElse(null));
        imagerepository.save(media);
        return media ;
    }
    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file;
    }




    @MessageMapping("/refus")

    public String notifierrefus(String message , String  iduser ) {

        messagingTemplate.convertAndSendToUser(iduser, "/topic/refus", message);
        return message
                ;
    }
    @MessageMapping("/ajoutjustif")

    public String ajoutjustif(String message , String  iduser ) {
        runtimeService.setVariable(demande_congecontr.getCurrentProcessInstanceId(), "hasReminded", true);

        messagingTemplate.convertAndSendToUser(iduser, "/topic/ajoutjustif", message);
        return message
                ;
    }
    @MessageMapping("/congesprev")

    public String notifcongesprev(String message , String  iduser ) {

        messagingTemplate.convertAndSendToUser(iduser, "/topic/congesprev", message);
        return message
                ;
    }
    @MessageMapping("/traitement")

    public String notiftraitement(String message , String  iduser ) {

        messagingTemplate.convertAndSendToUser(iduser, "/topic/traitement", message);
        return message;
    }
}


