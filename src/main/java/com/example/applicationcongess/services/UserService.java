package com.example.applicationcongess.services;

import com.example.applicationcongess.PlayLoad.request.Updateuser;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {
    @Autowired
    PersonnelRepository personnelRepository;
@Autowired
    JavaMailSender mailSender;

    public Personnel getbyUsername(String username){
        System.out.println(username);
        return personnelRepository.findPersonnelByUsername(username);
    }
    public List<Personnel> retrieveAllUsers() {
        List<Personnel> users = new ArrayList<>();
        personnelRepository.findAll().forEach(users::add);
        return users;
    }

    public Personnel retrieveUser(Long idUser) {
        return personnelRepository.findById(idUser).orElse(null);
    }
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);


        helper.setTo(to);
        helper.setSubject(subject);
        helper .setText(body);
        mailSender.send(message);
    }
    @Autowired
    PasswordEncoder encoder;
    public void removeUser(Long idUser) {
        personnelRepository.deleteById(idUser);
    }
    public Personnel updateUser(long  iduser, Updateuser updateuser) throws MessagingException {
        Optional<Personnel> optionalPersonnel = personnelRepository.findById(iduser);
            Personnel personnelToUpdate = optionalPersonnel.get();
            personnelToUpdate.setUsername(updateuser.getUsername());
            personnelToUpdate.setEmail(updateuser.getEmail());
            personnelToUpdate.setNom(updateuser.getNom());
            personnelToUpdate.setPrenom(updateuser.getPrenom());
            personnelToUpdate.setDate_naissance(updateuser.getDate_naissance());
            personnelToUpdate.setAdresse(updateuser.getAdresse());
            personnelToUpdate.setTel(updateuser.getTel());


            Personnel updatedPersonnel = personnelRepository.save(personnelToUpdate);
            System.out.println(updatedPersonnel.getEmail());
            sendEmail(personnelToUpdate.getEmail(), "Modification des détails utilisateur", "Bonjour " + updatedPersonnel.getUsername() + "\n Vos données ont été modifiées avec succès !");



     return updatedPersonnel;
    }
    public String SendCode(String email) throws MessagingException {
        String resultat="";
        if(personnelRepository.findPersonnelByEmail(email)==null){

        }else{
            String code =   RandomStringUtils.randomAlphanumeric(5);;
            Personnel u =personnelRepository.findPersonnelByEmail(email);
            u.setCode(code);
            u.setDatendcode( LocalDateTime.now().plusMinutes(5));
            sendEmail(u.getEmail(),"RESETCODE","votre code de verification est :"+code+"\nNB:Le code ne fonctionne pas apres 5 minutes");
            personnelRepository.save(u);
            resultat+="succes";

        }
        return resultat;
    }

    public String ResetPassword(String code, String newPassword) {
        String resultat="";
            Personnel u = personnelRepository.findByCode(code);
        if (u == null ){
resultat+="resultat incorret";
        }
        else if(u.getDatendcode().isBefore(LocalDateTime.now())) {
            resultat+="date expiré";

        }
        else{
            u.setPassword(encoder.encode(newPassword));
            u.setCode(null);
            u.setDatendcode(null);
            personnelRepository.save(u);
        }
        resultat+="OK";
        return  resultat;
    }

}