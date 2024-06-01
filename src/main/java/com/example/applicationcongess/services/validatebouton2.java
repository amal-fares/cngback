package com.example.applicationcongess.services;

import com.example.applicationcongess.controller.Demande_congecontr;
import com.example.applicationcongess.enums.Statut_conge;
import com.example.applicationcongess.models.Demande_conge;
import com.example.applicationcongess.repositories.Demande_congebRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class validatebouton2 implements JavaDelegate {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    Demande_congecontr demande_congecontr;
    @Autowired
    Demande_congebRepository demande_congebRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long idcollab = (Long) runtimeService.getVariable(demande_congecontr.getCurrentProcessInstanceId(), "initiator");


        LocalDate startDate = ((Date) runtimeService.getVariable(demande_congecontr.getCurrentProcessInstanceId(), "start")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = ((Date) runtimeService.getVariable(demande_congecontr.getCurrentProcessInstanceId(), "end")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date startAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endAsDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Demande_conge demande_conge=demande_congebRepository.demande(startAsDate,endAsDate,idcollab);


        demande_conge.setStatutconge(Statut_conge.valide2);
        demande_congebRepository.save(demande_conge);
        runtimeService.setVariable(demande_congecontr.getCurrentProcessInstanceId(), "is_validated", true );

    }
    }

