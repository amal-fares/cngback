package com.example.applicationcongess.services;

import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import org.activiti.engine.*;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Service
public class checkdonneesdeform implements TaskListener {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    PersonnelRepository personnelRepository;
    @Autowired
    TaskService taskService;
    @Autowired
    Demande_congecontr demande_congecontr;
    public String resultat ;
    @Override
    public void   notify(DelegateTask delegateTask) {
        System.out.println("hhhh");
        //recuperer le process en cours d execution
        ProcessInstance processInstanceencours = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("my-process")
                .singleResult();

        System.out.println(demande_congecontr.getCurrentProcessInstanceId());


        //recuperer l utilisateur conecté pour recuperer son solde de conge
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(userDetails.getUsername());
        Personnel personnel = personnelRepository.findById(userDetails.getCin()).orElse(null);
        float soldecongeuserconnecte=personnel.getSolde_conges();
        System.out.println(processInstanceencours.getProcessInstanceId());
        //calcuer le nombre de jours
        LocalDate startDate = ((Date) runtimeService.getVariable(processInstanceencours.getProcessInstanceId(), "start")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = ((Date) runtimeService.getVariable(processInstanceencours.getProcessInstanceId(), "end")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        System.out.println(daysBetween);

        if (daysBetween < soldecongeuserconnecte && startDate.isBefore(endDate)) {
            System.out.println("vous avez le droit de conges et les jours sont valides");
            resultat="vous avez le droit de conges et les jours sont valides";

        }else {

            Task task = taskService.createTaskQuery().taskName("Remplir les champs de forumlaire de demande de conges").singleResult();

            System.out.println("nVous n avez pas le droit de congés");
            resultat="Vous n'avez pas le droit de congés ";
            runtimeService.deleteProcessInstance(processInstanceencours.getProcessInstanceId(), "Raison de l'arrêt du processus");
        }

    }
}
