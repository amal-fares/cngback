package com.example.applicationcongess.services;

import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.enums.ERole;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class mailtocollabrefusedfin implements JavaDelegate {
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    RuntimeService runtimeService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("mailtocollbrefuseddfin");

        String proessInstanceID = demande_congecontr.getCurrentProcessInstanceId();


        Long initiateurId = (long) runtimeService.getVariable(proessInstanceID, "initiator");
        Personnel personnelsoumis = personnelRepository.findById(initiateurId).orElse(null);
        if (personnelsoumis.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.Role_manager2))) {
            System.out.println("je suis le manager n+2");
        } else {
            String subject = "Suivi de l etat de  la demande";
            String content = "Bonjour,\n\n" +
                    "votre demande est traite par   vote maanger n+2 " + personnelsoumis.getManagerdeuxiemeniveau().getUsername() + " Elle  est traitée et rejettée " +
                    "Cordialement,\n" +
                    "Votre équipe de gestion des congés";
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(personnelsoumis.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        }
    }

    }

