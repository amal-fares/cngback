package com.example.applicationcongess.services;


import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class requestrejectedtocollab  implements JavaDelegate {

    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(userDetails.getUsername());
        Personnel personnel = personnelRepository.findById(userDetails.getCin()).orElse(null);
        Personnel managerpassocieaucollabconnecte=personnel.getManager();
        Personnel obejtmanager=personnelRepository.findById(managerpassocieaucollabconnecte.getCin()).orElse(null);
        String subject = "Demande de congés soumise par un collaborateur"+ personnel.getUsername() ;
        String content = "Bonjour,\n\n" +
                "Une demande de congés a été soumise par un collaborateur "+ personnel.getUsername()+". Veuillez consulter le système pour plus de détails.\n\n" +
                "Cordialement,\n" +
                "Votre équipe de gestion des congés";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(obejtmanager.getEmail());
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

}

