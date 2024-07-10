package com.example.applicationcongess.services.serviceexceptionnel;

import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import com.example.applicationcongess.repositories.PersonnelRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class mailToManagerRefusedWithJustif implements JavaDelegate {
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    Demande_congebRepository demande_congebRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        System.out.println("mail to manager  refused with justif ");
        String proessInstanceID = demande_congecontr.getCurrentProcessInstanceId();

        runtimeService.setVariable(demande_congecontr.getCurrentProcessInstanceId(), "mail",true);
         runtimeService.getVariable(demande_congecontr.getCurrentProcessInstanceId(), "id_demande_conge");

        Long initiateurId = (long) runtimeService.getVariable(proessInstanceID, "initiator");
        Personnel personnelsoumis=personnelRepository.findById(initiateurId).orElse(null);

       personnelsoumis.setEtatmail("mailmaangerrefused");
        personnelRepository.save(personnelsoumis);
        Personnel manager= personnelsoumis.getManager();
        manager.setEtatmail("mailmaangerrefused");
        personnelRepository.save(manager);
        String subject = "Validation effectuéé par un gestionnaire et refusée ";
        String content = "Bonjour,\n\n" +
                "La demande  soumise par "+ personnelsoumis.getUsername()+" est rejettée par  "+ personnelsoumis.getGestionnaire().getUsername() +
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
