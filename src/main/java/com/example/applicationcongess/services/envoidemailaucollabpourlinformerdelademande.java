package com.example.applicationcongess.services;


import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class envoidemailaucollabpourlinformerdelademande implements TaskListener {
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void notify(DelegateTask delegateTask) {
        try {
            System.out.println("mail au collab");
            String proessInstanceID = demande_congecontr.getCurrentProcessInstanceId();


            Long initiateurId = (long) runtimeService.getVariable(proessInstanceID, "initiator");
            Personnel personnelto = personnelRepository.findById(initiateurId).orElse(null);
            String subject = "suivre la demande";
            String content = "Bonjour,\n\n" +
                    "votre demande est auprés de votre manager  elle va etre traité " + personnelto.getManager().getUsername() + ". Veuillez consulter le système pour plus de détails.\n\n" +
                    "Cordialement,\n" +
                    "Votre équipe de gestion des congés";
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(personnelto.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch ( Exception e ){
       System.out.println ( "mail ");}
    }


}
