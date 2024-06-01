package com.example.applicationcongess.services.serviceexceptionnel;

import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import com.example.applicationcongess.services.UserDetailsImpl;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import  com.example.applicationcongess.services.checkdonneesdeform;
@Service
public class mailtomanageresxrp  implements JavaDelegate {
    @Autowired
    checkdonneesdeform checkdonneesdeform;
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    TaskService taskService;
    public String resultat;
    @Autowired
    RuntimeService runtimeService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("mail au manager a propos le conge exceptionnel ");
        ProcessInstance processInstanceencours = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("my-process")
                .singleResult();

        if (checkdonneesdeform.resultat.equals("Vous n'avez pas le droit de congés")) {
            Task task = taskService.createTaskQuery().taskName("Remplir les champs de forumlaire de demande de conges").singleResult();

            System.out.println("nVous n avez pas le droit de congés");
            resultat="Vous n'avez pas le droit de congés ";
            runtimeService.deleteProcessInstance(processInstanceencours.getProcessInstanceId(), "Raison de l'arrêt du processus");
        }
        else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            System.out.println(userDetails.getUsername());
            Personnel personnel = personnelRepository.findById(userDetails.getCin()).orElse(null);


            Personnel obejtmanager = personnelRepository.findById(personnel.getManager().getCin()).orElse(null);
            String subject = "Demande de congés  Exceptionnel soumise par un membre de votre équipe " + personnel.getUsername();
            String content = "Bonjour,\n\n" +
                    "Une demande de congés a été soumise par  " + personnel.getUsername() + ". Veuillez consulter le système pour plus de détails.\n\n" +
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
    }

