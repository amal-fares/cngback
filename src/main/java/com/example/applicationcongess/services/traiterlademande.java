package com.example.applicationcongess.services;


import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.enums.Statut_conge;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class traiterlademande implements TaskListener {
    @Autowired
    Demande_congebRepository demande_congebRepository;
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    RuntimeService runtimeService;
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("traiterdemande");
        String proessInstanceID=demande_congecontr.getCurrentProcessInstanceId();
        Long idcollab = (Long) runtimeService.getVariable(proessInstanceID, "initiator");


        LocalDate startDate = ((Date) runtimeService.getVariable(proessInstanceID, "start")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = ((Date) runtimeService.getVariable(proessInstanceID, "end")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date startAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endAsDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Demande_conge demande_conge=demande_congebRepository.demande(startAsDate,endAsDate,idcollab);
        int nombreutilisateurchevauchant=demande_congebRepository.planningequipe(demande_conge.getDate_fin(),demande_conge.getDate_debut(),demande_conge.getCollaborateur().getCin());
        if (nombreutilisateurchevauchant>3){
             System.out.println("impossible de prendre le congé , les periodes de conges de collab se chevauchent  ");
             demande_conge.setStatutconge(Statut_conge.rejette);
             demande_congebRepository.save(demande_conge);
            runtimeService.setVariable(proessInstanceID, "is_validated", true );

}else
         {

             demande_conge.setStatutconge(Statut_conge.valide1);
             demande_congebRepository.save(demande_conge);
             runtimeService.setVariable(proessInstanceID, "is_validated", false );
             System.out.println(demande_conge.getStatutconge());
         }
    }
}
