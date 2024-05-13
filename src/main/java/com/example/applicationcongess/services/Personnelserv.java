package com.example.applicationcongess.services;

import com.example.applicationcongess.PlayLoad.request.ChangePasswordRequest;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import lombok.AllArgsConstructor;
import org.activiti.engine.FormService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.security.Principal;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
public class Personnelserv implements IPersonnel{
    @Autowired
    PersonnelRepository personnelRepository;

    private final PasswordEncoder passwordEncoder;
    public Personnel addpersoneel (Personnel personnel){
        return personnelRepository.save((personnel));
    }
    //   public Object generateodeverif(){
  //  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         //  Object principal = authentication.getPrincipal();
           //XmlMapper xmlMapper = new XmlMapper();
           // Vous devez remplacer "YourClass.class" par la classe appropriée correspondant à la structure de l'objet XML
           //YourClass principalObject = xmlMapper.readValue(principal.toString(), YourClass.class);

          //return principal;
    //Personnel user = personnelRepository.findById(cin).orElse(null); // Remplacez "findByUsername" par la méthode appropriée pour récupérer l'utilisateur à partir de votre système d'authentification

  //  if (user != null) {
  //      String verificationCode = UUID.randomUUID().toString();
      //  user.setCode(verificationCode);
        //personnelRepository.save(user);
        //return verificationCode;
    //} else {

        //return null;
    //}

    public String Verification(String email, String code) {
        Personnel u = personnelRepository.findByEmail(email);

        if (u == null) {
            return "personnel null ";

        } else if (u.getStatusatifounon()) {
            return "le status de lutilisateur est actif pas la peine de faire une verification elle est faite ";
        } else if (!u.getStatusatifounon()) {
            if (u.getCode().equals(code)) {
                u.setStatusatifounon(true);
                u.setCode(null);
                personnelRepository.save(u);
                return "code egale code de la base";
            }
        else if (!u.getCode().equals(code)) {
            System.out.println("code incorrect");
            return "code incorrect";
        }}
        return "Une erreur inattendue s'est produite lors de la vérification.";

    }
    private final PasswordEncoder encoder;





    public ResponseEntity<String> changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long cin = userDetails.getCin();
            Personnel personne = personnelRepository.findById(cin).orElse(null);
            System.out.println(personne.getPassword());
            if (!passwordEncoder.matches(request.getCurrentPassword(), personne.getPassword())) {
                throw new IllegalStateException("Wrong password");
            }
            // check if the two new passwords are the same
            if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
                throw new IllegalStateException("Password are not the same");
            }

            // update the password
            personne.setPassword(passwordEncoder.encode(request.getNewPassword()));

            // save the new password
            personnelRepository.save(personne);

            System.out.println(personne.getPassword());
            return ResponseEntity.ok("Password changed successfully");
        } else {

            throw new IllegalStateException("User must be authenticated");
        }
    }
    @Autowired
    FormService formService;
    @Autowired
    TaskService taskService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    ProcessEngine processengine ;

    public Personnel attribuerManager(Long personnelId, Long managerId, int managerLevel) {
        Personnel personnel = personnelRepository.findById(personnelId).orElse(null);
        Personnel manager = personnelRepository.findById(managerId).orElse(null);

        if (personnel != null && manager != null) {
            personnel.setManager(manager);

            personnelRepository.save(personnel);

        }
        return personnel;
    }
}

