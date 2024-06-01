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
public class mailToManagerValidatedWithoutJustif implements JavaDelegate {
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

        System.out.println("mail to manager without justif ");
        String proessInstanceID = demande_congecontr.getCurrentProcessInstanceId();


        Long initiateurId = (long) runtimeService.getVariable(proessInstanceID, "initiator");
        Personnel personnelsoumis=personnelRepository.findById(initiateurId).orElse(null);
        Personnel manager= personnelsoumis.getManager();
        String subject = "suivre la demande";
        String content = "Bonjour,\n\n" +
                "La demande  soumise par "+ personnelsoumis.getUsername()+" est valide pour le moment par "+ personnelsoumis.getGestionnaire().getUsername() +" mais il manque les justificatif .Il faut les fournir dans les plus bref délais  " +
                "Cordialement,\n" +
                "Votre équipe de gestion des congés";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(manager.getEmail());
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);


    }



}
