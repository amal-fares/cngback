package com.example.applicationcongess.services.serviceexceptionnel;

import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;


@Service
public class mailToCollabValidatedWithJustif implements JavaDelegate {

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
        System.out.println("mail to collab without justif ");
        String proessInstanceID = demande_congecontr.getCurrentProcessInstanceId();


        Long initiateurId = (long) runtimeService.getVariable(proessInstanceID, "initiator");
        Personnel personnelsoumis=personnelRepository.findById(initiateurId).orElse(null);

        String subject = "Etat de la demande finale  ";
        String content = "Bonjour,\n\n" +
                "votre demande  est validé pour le moment par "+ personnelsoumis.getGestionnaire().getUsername() +"Vos justificatifs sont forunis et tout est bien validé et traité   " +
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

